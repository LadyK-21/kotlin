/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.backend.jvm

import org.jetbrains.kotlin.backend.jvm.localClassType
import org.jetbrains.kotlin.codegen.ClassBuilderMode
import org.jetbrains.kotlin.codegen.serialization.JvmSerializationBindings
import org.jetbrains.kotlin.codegen.serialization.JvmSignatureSerializer
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.config.JvmDefaultMode
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.constant.KClassValue
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.backend.ConstValueProviderImpl
import org.jetbrains.kotlin.fir.backend.Fir2IrComponents
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.utils.*
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.java.hasJvmFieldAnnotation
import org.jetbrains.kotlin.fir.languageVersionSettings
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.serialization.*
import org.jetbrains.kotlin.fir.serialization.constant.ConstValueProvider
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.load.kotlin.NON_EXISTENT_CLASS_NAME
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.jvm.JvmProtoBuf
import org.jetbrains.kotlin.metadata.jvm.deserialization.ClassMapperLite
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmFlags
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.metadata.serialization.MutableVersionRequirementTable
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.JvmStandardClassIds.JVM_DEFAULT_WITHOUT_COMPATIBILITY_CLASS_ID
import org.jetbrains.kotlin.name.JvmStandardClassIds.JVM_DEFAULT_WITH_COMPATIBILITY_CLASS_ID
import org.jetbrains.kotlin.serialization.DescriptorSerializer
import org.jetbrains.kotlin.types.AbstractTypeApproximator
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.Method

open class FirJvmSerializerExtension(
    override val session: FirSession,
    private val bindings: JvmSerializationBindings,
    private val localDelegatedProperties: List<FirProperty>,
    override val scopeSession: ScopeSession,
    private val globalBindings: JvmSerializationBindings,
    private val useTypeTable: Boolean,
    private val moduleName: String,
    private val classBuilderMode: ClassBuilderMode,
    private val isParamAssertionsDisabled: Boolean,
    private val unifiedNullChecks: Boolean,
    override val metadataVersion: BinaryVersion,
    private val jvmDefaultMode: JvmDefaultMode,
    final override val stringTable: FirElementAwareStringTable,
    override val constValueProvider: ConstValueProvider?,
    override val additionalMetadataProvider: FirAdditionalMetadataProvider?,
) : FirSerializerExtension() {
    private val signatureSerializer = FirJvmSignatureSerializer(stringTable)

    constructor(
        session: FirSession,
        bindings: JvmSerializationBindings,
        state: GenerationState,
        localDelegatedProperties: List<FirProperty>,
        approximator: AbstractTypeApproximator,
        components: Fir2IrComponents,
        stringTable: FirElementAwareStringTable
    ) : this(
        session,
        bindings,
        localDelegatedProperties,
        components.scopeSession,
        state.globalSerializationBindings,
        state.config.useTypeTableInSerializer,
        state.moduleName,
        state.classBuilderMode,
        state.config.isParamAssertionsDisabled,
        state.config.unifiedNullChecks,
        state.config.metadataVersion,
        state.config.jvmDefaultMode,
        stringTable,
        ConstValueProviderImpl(components),
        components.annotationsFromPluginRegistrar.createAdditionalMetadataProvider()
    )

    override val localClassIdOracle: LocalClassIdOracle
        get() = object : LocalClassIdOracle() {
            override fun getLocalClassId(klass: KClassValue.Value.LocalClass): ClassId? {
                val irClass = klass.irClass as? IrClass ?: return null
                val type = irClass.localClassType ?: return null
                val fqName = FqName(type.internalName.replace('/', '.'))
                // Note that this ClassId cannot be used for anything other than mapping it back to the JVM type, which is exactly the only
                // way it's being used -- kotlin-reflect uses it to find the java.lang.Class object if requested.
                // For this reason, the relative class name in this ClassId does not make sense, and for example, in case of an inner class
                // in a local class, will contain something like "foo$Local$Inner".
                return ClassId(fqName.parent(), FqName.topLevel(fqName.shortName()), isLocal = true)
            }
        }

    override fun shouldUseTypeTable(): Boolean = useTypeTable

    protected open val isOptionalAnnotationClassSerialization: Boolean get() = false

    override fun serializeClass(
        klass: FirClass,
        proto: ProtoBuf.Class.Builder,
        versionRequirementTable: MutableVersionRequirementTable,
        childSerializer: FirElementSerializer
    ) {
        if (moduleName != JvmProtoBufUtil.DEFAULT_MODULE_NAME) {
            proto.setExtension(JvmProtoBuf.classModuleName, stringTable.getStringIndex(moduleName))
        }
        for (localVariable in localDelegatedProperties) {
            proto.addExtension(JvmProtoBuf.classLocalVariable, childSerializer.propertyProto(localVariable)?.build() ?: continue)
        }
        writeVersionRequirementForJvmDefaultIfNeeded(klass, proto, versionRequirementTable)

        if (jvmDefaultMode.isEnabled && klass is FirRegularClass && klass.classKind == ClassKind.INTERFACE) {
            proto.setExtension(JvmProtoBuf.jvmClassFlags, JvmFlags.getClassFlags(true, isInCompatibilityMode(klass)))
        }

        serializeAnnotations(klass, proto::addAnnotation)
    }

    private fun isInCompatibilityMode(klass: FirRegularClass): Boolean =
        (jvmDefaultMode == JvmDefaultMode.ENABLE && !klass.hasAnnotation(JVM_DEFAULT_WITHOUT_COMPATIBILITY_CLASS_ID, session)) ||
                (jvmDefaultMode == JvmDefaultMode.NO_COMPATIBILITY && klass.hasAnnotation(JVM_DEFAULT_WITH_COMPATIBILITY_CLASS_ID, session))

    override fun serializeScript(
        script: FirScript,
        proto: ProtoBuf.Class.Builder,
        versionRequirementTable: MutableVersionRequirementTable,
        childSerializer: FirElementSerializer,
    ) {
        processScriptOrSnippet(proto, childSerializer)
    }

    override fun serializeSnippet(
        snippet: FirReplSnippet,
        proto: ProtoBuf.Class.Builder,
        versionRequirementTable: MutableVersionRequirementTable,
        childSerializer: FirElementSerializer,
    ) {
        processScriptOrSnippet(proto, childSerializer)
    }

    private fun processScriptOrSnippet(
        proto: ProtoBuf.Class.Builder,
        childSerializer: FirElementSerializer,
    ) {
        if (moduleName != JvmProtoBufUtil.DEFAULT_MODULE_NAME) {
            proto.setExtension(JvmProtoBuf.classModuleName, stringTable.getStringIndex(moduleName))
        }
        for (localVariable in localDelegatedProperties) {
            proto.addExtension(JvmProtoBuf.classLocalVariable, childSerializer.propertyProto(localVariable)?.build() ?: continue)
        }
    }

    // Interfaces which have @JvmDefault members somewhere in the hierarchy need the compiler 1.2.40+
    // so that the generated bridges in subclasses would call the super members correctly
    private fun writeVersionRequirementForJvmDefaultIfNeeded(
        klass: FirClass,
        builder: ProtoBuf.Class.Builder,
        versionRequirementTable: MutableVersionRequirementTable
    ) {
        if (klass is FirRegularClass && klass.classKind == ClassKind.INTERFACE) {
            if (jvmDefaultMode == JvmDefaultMode.NO_COMPATIBILITY) {
                builder.addVersionRequirement(
                    DescriptorSerializer.writeVersionRequirement(
                        1,
                        4,
                        0,
                        ProtoBuf.VersionRequirement.VersionKind.COMPILER_VERSION,
                        versionRequirementTable
                    )
                )
            }
        }
    }

    override fun serializePackage(
        packageFqName: FqName,
        proto: ProtoBuf.Package.Builder,
        versionRequirementTable: MutableVersionRequirementTable?,
        childSerializer: FirElementSerializer
    ) {
        if (moduleName != JvmProtoBufUtil.DEFAULT_MODULE_NAME) {
            proto.setExtension(JvmProtoBuf.packageModuleName, stringTable.getStringIndex(moduleName))
        }
        for (localVariable in localDelegatedProperties) {
            proto.addExtension(JvmProtoBuf.packageLocalVariable, childSerializer.propertyProto(localVariable)?.build() ?: continue)
        }
    }

    override fun serializeFlexibleType(
        type: ConeFlexibleType,
        lowerProto: ProtoBuf.Type.Builder,
        upperProto: ProtoBuf.Type.Builder
    ) {
        lowerProto.flexibleTypeCapabilitiesId = stringTable.getStringIndex(JvmProtoBufUtil.PLATFORM_TYPE_ID)

        if (type is ConeRawType) {
            lowerProto.setExtension(JvmProtoBuf.isRaw, true)

            // we write this Extension for compatibility with old compiler
            upperProto.setExtension(JvmProtoBuf.isRaw, true)
        }
    }

    override fun serializeTypeAnnotations(annotations: List<FirAnnotation>, proto: ProtoBuf.Type.Builder) {
        for (annotation in annotations) {
            proto.addExtensionOrNull(JvmProtoBuf.typeAnnotation, annotationSerializer.serializeAnnotation(annotation))
        }
    }


    override fun serializeTypeParameter(typeParameter: FirTypeParameter, proto: ProtoBuf.TypeParameter.Builder) {
        for (annotation in typeParameter.nonSourceAnnotations(session)) {
            proto.addExtensionOrNull(JvmProtoBuf.typeParameterAnnotation, annotationSerializer.serializeAnnotation(annotation))
        }
    }

    override fun serializeConstructor(
        constructor: FirConstructor, proto: ProtoBuf.Constructor.Builder, childSerializer: FirElementSerializer
    ) {
        val method = getBinding(METHOD_FOR_FIR_FUNCTION, constructor)
        if (method != null) {
            val signature = signatureSerializer.methodSignature(constructor, null, method)
            if (signature != null) {
                proto.setExtension(JvmProtoBuf.constructorSignature, signature)
            }
        }

        serializeAnnotations(constructor, proto::addAnnotation)
    }

    override fun serializeFunction(
        function: FirFunction,
        proto: ProtoBuf.Function.Builder,
        versionRequirementTable: MutableVersionRequirementTable?,
        childSerializer: FirElementSerializer
    ) {
        val method = getBinding(METHOD_FOR_FIR_FUNCTION, function)
        if (method != null) {
            val signature = signatureSerializer.methodSignature(function, (function as? FirSimpleFunction)?.name, method)
            if (signature != null) {
                proto.setExtension(JvmProtoBuf.methodSignature, signature)
            }
        }

        if (function.needsInlineParameterNullCheckRequirement()) {
            versionRequirementTable?.writeInlineParameterNullCheckRequirement(proto::addVersionRequirement)
        }

        serializeAnnotations(function, proto::addAnnotation)
        function.receiverParameter?.let { serializeAnnotations(it, proto::addExtensionReceiverAnnotation) }
    }

    private fun MutableVersionRequirementTable.writeInlineParameterNullCheckRequirement(add: (Int) -> Unit) {
        if (unifiedNullChecks) {
            // Since Kotlin 1.4, we generate a call to Intrinsics.checkNotNullParameter in inline functions which causes older compilers
            // (earlier than 1.3.50) to crash because a functional parameter in this position can't be inlined
            add(DescriptorSerializer.writeVersionRequirement(1, 3, 50, ProtoBuf.VersionRequirement.VersionKind.COMPILER_VERSION, this))
        }
    }

    private fun FirFunction.needsInlineParameterNullCheckRequirement(): Boolean =
        this is FirSimpleFunction && isInline && !isSuspend && !isParamAssertionsDisabled &&
                !Visibilities.isPrivate(visibility) &&
                (valueParameters.any { it.returnTypeRef.coneType.isSomeFunctionType(session) } ||
                        receiverParameter?.typeRef?.coneType?.isSomeFunctionType(session) == true)

    override fun serializeProperty(
        property: FirProperty,
        proto: ProtoBuf.Property.Builder,
        versionRequirementTable: MutableVersionRequirementTable?,
        childSerializer: FirElementSerializer
    ) {
        val getter = property.getter
        val setter = property.setter
        val getterMethod = if (getter == null) null else getBinding(METHOD_FOR_FIR_FUNCTION, getter)
        val setterMethod = if (setter == null) null else getBinding(METHOD_FOR_FIR_FUNCTION, setter)

        val field = getBinding(FIELD_FOR_PROPERTY, property)
        val syntheticMethod = getBinding(SYNTHETIC_METHOD_FOR_FIR_VARIABLE, property)
        val delegateMethod = getBinding(DELEGATE_METHOD_FOR_FIR_VARIABLE, property)
        assert(property.delegate != null || delegateMethod == null) { "non-delegated property ${property.render()} has delegate method" }

        val signature = signatureSerializer.propertySignature(
            property.name,
            field?.second,
            field?.first?.descriptor,
            if (syntheticMethod != null) signatureSerializer.methodSignature(null, null, syntheticMethod) else null,
            if (delegateMethod != null) signatureSerializer.methodSignature(null, null, delegateMethod) else null,
            if (getterMethod != null) signatureSerializer.methodSignature(null, null, getterMethod) else null,
            if (setterMethod != null) signatureSerializer.methodSignature(null, null, setterMethod) else null,
            requiresFieldSignature = field?.first?.descriptor?.let { signatureSerializer.requiresPropertySignature(property, it) } ?: false
        )

        if (signature != null) {
            proto.setExtension(JvmProtoBuf.propertySignature, signature)
        }

        if (property.isJvmFieldPropertyInInterfaceCompanion() && versionRequirementTable != null) {
            proto.setExtension(JvmProtoBuf.flags, JvmFlags.getPropertyFlags(true))
        }

        if (getter?.needsInlineParameterNullCheckRequirement() == true || setter?.needsInlineParameterNullCheckRequirement() == true) {
            versionRequirementTable?.writeInlineParameterNullCheckRequirement(proto::addVersionRequirement)
        }

        serializeAnnotations(getter, proto::addGetterAnnotation)
        serializeAnnotations(setter, proto::addSetterAnnotation)
        serializeAnnotations(property, proto::addAnnotation)
        property.receiverParameter?.let { serializeAnnotations(it, proto::addExtensionReceiverAnnotation) }
        property.backingField?.let { field ->
            serializeAnnotations(field, proto::addBackingFieldAnnotation) { it != AnnotationUseSiteTarget.PROPERTY_DELEGATE_FIELD }
            serializeAnnotations(field, proto::addDelegateFieldAnnotation) { it == AnnotationUseSiteTarget.PROPERTY_DELEGATE_FIELD }
        }
    }

    private fun FirProperty.isJvmFieldPropertyInInterfaceCompanion(): Boolean {
        if (!hasJvmFieldAnnotation(session)) return false

        val containerSymbol = dispatchReceiverType?.classLikeLookupTagIfAny?.toRegularClassSymbol(session)
        // Note: companions are anyway forbidden in local classes
        if (containerSymbol == null || !containerSymbol.isCompanion || containerSymbol.isLocal) {
            return false
        }

        val grandParent = containerSymbol.classId.outerClassId?.let {
            session.getRegularClassSymbolByClassId(it)?.fir
        }
        return grandParent != null &&
                (grandParent.classKind == ClassKind.INTERFACE || grandParent.classKind == ClassKind.ANNOTATION_CLASS)
    }

    override fun getClassSupertypes(klass: FirClass): List<FirTypeRef> {
        if (classBuilderMode == ClassBuilderMode.KAPT3) {
            // In K1, error supertypes are filtered on the frontend level in `DescriptorResolver.addValidSupertype`. Doing the same in FIR
            // would be incorrect because there would be no errors reported on the corresponding FIR types. And not filtering them at all
            // would lead to differences in metadata in generated stubs. So we fix this difference during metadata serialization.
            return super.getClassSupertypes(klass)
                .filterNot { it.coneType is ConeErrorType }
                .ifEmpty { listOf(session.builtinTypes.anyType) }
        }

        return super.getClassSupertypes(klass)
    }

    override fun serializeValueParameter(parameter: FirValueParameter, proto: ProtoBuf.ValueParameter.Builder) {
        serializeAnnotations(parameter, proto::addAnnotation)
    }

    override fun serializeErrorType(type: ConeErrorType, builder: ProtoBuf.Type.Builder) {
        if (classBuilderMode === ClassBuilderMode.KAPT3) {
            builder.className = stringTable.getStringIndex(NON_EXISTENT_CLASS_NAME)
            return
        }

        super.serializeErrorType(type, builder)
    }

    override fun serializeEnumEntry(enumEntry: FirEnumEntry, proto: ProtoBuf.EnumEntry.Builder) {
        serializeAnnotations(enumEntry, proto::addAnnotation)
    }

    private fun <K : Any, V : Any> getBinding(slice: JvmSerializationBindings.SerializationMappingSlice<K, V>, key: K): V? =
        bindings.get(slice, key) ?: globalBindings.get(slice, key)

    private fun serializeAnnotations(
        declaration: FirAnnotationContainer?,
        addAnnotation: (ProtoBuf.Annotation) -> Unit,
        matchUseSiteTarget: ((AnnotationUseSiteTarget?) -> Boolean)? = null,
    ) {
        if (session.languageVersionSettings.supportsFeature(LanguageFeature.AnnotationsInMetadata) ||
            declaration in localDelegatedProperties ||
            isOptionalAnnotationClassSerialization
        ) {
            for (annotation in declaration?.allRequiredAnnotations(session, additionalMetadataProvider).orEmpty()) {
                if (matchUseSiteTarget == null || matchUseSiteTarget(annotation.useSiteTarget)) {
                    addAnnotation(annotationSerializer.serializeAnnotation(annotation) ?: continue)
                }
            }
        }
    }

    companion object {
        val METHOD_FOR_FIR_FUNCTION: JvmSerializationBindings.SerializationMappingSlice<FirFunction, Method> =
            JvmSerializationBindings.SerializationMappingSlice.create()
        val FIELD_FOR_PROPERTY: JvmSerializationBindings.SerializationMappingSlice<FirProperty, Pair<Type, String>> =
            JvmSerializationBindings.SerializationMappingSlice.create()
        val SYNTHETIC_METHOD_FOR_FIR_VARIABLE: JvmSerializationBindings.SerializationMappingSlice<FirVariable, Method> =
            JvmSerializationBindings.SerializationMappingSlice.create()
        val DELEGATE_METHOD_FOR_FIR_VARIABLE: JvmSerializationBindings.SerializationMappingSlice<FirVariable, Method> =
            JvmSerializationBindings.SerializationMappingSlice.create()
    }
}

class FirJvmSignatureSerializer(stringTable: FirElementAwareStringTable) : JvmSignatureSerializer<FirFunction, FirProperty>(stringTable) {
    // We don't write those signatures which can be trivially reconstructed from already serialized data
    // TODO: make JvmStringTable implement NameResolver and use JvmProtoBufUtil#getJvmMethodSignature instead
    override fun requiresFunctionSignature(descriptor: FirFunction, desc: String): Boolean {
        val sb = StringBuilder()
        sb.append("(")
        val receiverTypeRef = descriptor.receiverParameter?.typeRef
        if (receiverTypeRef != null) {
            val receiverDesc = mapTypeDefault(receiverTypeRef) ?: return true
            sb.append(receiverDesc)
        }

        for (valueParameter in descriptor.valueParameters) {
            val paramDesc = mapTypeDefault(valueParameter.returnTypeRef) ?: return true
            sb.append(paramDesc)
        }

        sb.append(")")

        val returnTypeRef = descriptor.returnTypeRef
        val returnTypeDesc = (mapTypeDefault(returnTypeRef)) ?: return true
        sb.append(returnTypeDesc)

        return sb.toString() != desc
    }

    override fun requiresPropertySignature(descriptor: FirProperty, desc: String): Boolean {
        return desc != mapTypeDefault(descriptor.returnTypeRef)
    }

    private fun mapTypeDefault(typeRef: FirTypeRef): String? {
        val classId = typeRef.coneType.classId
        return if (classId == null) null else ClassMapperLite.mapClass(classId.asString())
    }
}
