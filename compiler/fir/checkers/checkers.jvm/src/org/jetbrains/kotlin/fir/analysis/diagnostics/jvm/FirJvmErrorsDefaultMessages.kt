/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.diagnostics.jvm

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.NOT_RENDERED
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.TO_STRING
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.NAME
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.diagnostics.rendering.toDeprecationWarningMessage
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.DECLARATION_FQ_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.DECLARATION_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.OPTIONAL_SENTENCE
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_TYPE
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.STRING_TARGETS
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.SYMBOL
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.SYMBOL_WITH_CONTAINING_DECLARATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ACCIDENTAL_OVERRIDE_CLASH_BY_JVM_SIGNATURE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ANNOTATION_TARGETS_ONLY_IN_JAVA
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.CONCURRENT_HASH_MAP_CONTAINS_OPERATOR_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.DANGEROUS_CHARACTERS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.DELEGATION_BY_IN_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.DEPRECATED_JAVA_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ENUM_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.EXTERNAL_DECLARATION_CANNOT_BE_ABSTRACT
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.EXTERNAL_DECLARATION_CANNOT_BE_INLINED
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.EXTERNAL_DECLARATION_CANNOT_HAVE_BODY
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.EXTERNAL_DECLARATION_IN_INTERFACE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.FIELD_IN_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.FUNCTION_DELEGATE_MEMBER_NAME_CLASH
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.IDENTITY_SENSITIVE_OPERATIONS_WITH_VALUE_TYPE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ILLEGAL_JAVA_LANG_RECORD_SUPERTYPE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ILLEGAL_JVM_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.IMPLEMENTATION_BY_DELEGATION_WITH_DIFFERENT_GENERIC_SIGNATURE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INAPPLICABLE_JVM_EXPOSE_BOXED_WITH_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INAPPLICABLE_JVM_FIELD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INAPPLICABLE_JVM_FIELD_WARNING
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INAPPLICABLE_JVM_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INCOMPATIBLE_ANNOTATION_TARGETS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INLINE_FROM_HIGHER_PLATFORM
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INNER_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.INTERFACE_CANT_CALL_DEFAULT_METHOD_VIA_SUPER
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_CLASS_INHERITS_KT_PRIVATE_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_CLASS_ON_COMPANION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_FIELD_SHADOWED_BY_KOTLIN_PROPERTY
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_MODULE_DOES_NOT_READ_UNNAMED_MODULE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_SAM_INTERFACE_CONSTRUCTOR_REFERENCE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JAVA_TYPE_MISMATCH
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_DEFAULT_WITHOUT_COMPATIBILITY_NOT_IN_ENABLE_MODE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_DEFAULT_WITH_COMPATIBILITY_NOT_IN_NO_COMPATIBILITY_MODE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_BE_THE_SAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_BE_THE_SAME_AS_JVM_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_EXPOSE_LOCALS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_EXPOSE_OPEN_ABSTRACT
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_EXPOSE_REIFIED
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_EXPOSE_SUSPEND
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_CANNOT_EXPOSE_SYNTHETIC
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_EXPOSE_BOXED_REQUIRES_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_INLINE_WITHOUT_VALUE_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_PACKAGE_NAME_CANNOT_BE_EMPTY
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_PACKAGE_NAME_MUST_BE_VALID_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_PACKAGE_NAME_NOT_SUPPORTED_IN_FILES_WITH_CLASSES
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_RECORD_EXTENDS_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_RECORD_NOT_LAST_VARARG_PARAMETER
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_RECORD_NOT_VAL_PARAMETER
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_RECORD_WITHOUT_PRIMARY_CONSTRUCTOR_PARAMETERS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_SERIALIZABLE_LAMBDA_ON_INLINED_FUNCTION_LITERALS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_STATIC_NOT_IN_OBJECT_OR_CLASS_COMPANION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_STATIC_NOT_IN_OBJECT_OR_COMPANION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_STATIC_ON_CONST_OR_JVM_FIELD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_STATIC_ON_EXTERNAL_IN_INTERFACE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_STATIC_ON_NON_PUBLIC_MEMBER
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.JVM_SYNTHETIC_ON_DELEGATE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.LOCAL_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.MISSING_BUILT_IN_DECLARATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NON_DATA_CLASS_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NON_FINAL_JVM_RECORD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NON_SOURCE_REPEATED_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NOT_YET_SUPPORTED_LOCAL_INLINE_FUNCTION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NO_REFLECTION_IN_CLASS_PATH
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NULLABILITY_MISMATCH_BASED_ON_EXPLICIT_TYPE_ARGUMENTS_FOR_JAVA
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_ABSTRACT
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_ANNOTATION_CLASS_CONSTRUCTOR_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_INTERFACE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_LOCAL
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_PRIVATE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.OVERRIDE_CANNOT_BE_STATIC
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.POSITIONED_VALUE_ARGUMENT_FOR_JAVA_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.PROPERTY_HIDES_JAVA_FIELD
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REDUNDANT_REPEATABLE_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATABLE_ANNOTATION_HAS_NESTED_CLASS_NAMED_CONTAINER_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATABLE_CONTAINER_HAS_NON_DEFAULT_PARAMETER_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATABLE_CONTAINER_HAS_SHORTER_RETENTION_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATABLE_CONTAINER_MUST_HAVE_VALUE_ARRAY_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATABLE_CONTAINER_TARGET_SET_NOT_A_SUBSET_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.REPEATED_ANNOTATION_WITH_CONTAINER
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SPREAD_ON_SIGNATURE_POLYMORPHIC_CALL_ERROR
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.STRICTFP_ON_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SUBCLASS_CANT_CALL_COMPANION_PROTECTED_NON_STATIC
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SUSPENSION_POINT_INSIDE_CRITICAL_SECTION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_BLOCK_ON_JAVA_VALUE_BASED_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_BLOCK_ON_VALUE_CLASS_OR_PRIMITIVE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_IN_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_IN_INTERFACE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_ON_ABSTRACT
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_ON_INLINE
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_ON_SUSPEND
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNCHRONIZED_ON_VALUE_CLASS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.SYNTHETIC_PROPERTY_WITHOUT_JAVA_ORIGIN
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.THROWS_IN_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.TYPE_MISMATCH_WHEN_FLEXIBILITY_CHANGES
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.UPPER_BOUND_CANNOT_BE_ARRAY
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.UPPER_BOUND_VIOLATED_BASED_ON_JAVA_ANNOTATIONS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.UPPER_BOUND_VIOLATED_IN_TYPEALIAS_EXPANSION_BASED_ON_JAVA_ANNOTATIONS
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.USELESS_JVM_EXPOSE_BOXED
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.VALUE_CLASS_WITHOUT_JVM_INLINE_ANNOTATION
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.WRONG_NULLABILITY_FOR_JAVA_OVERRIDE

object FirJvmErrorsDefaultMessages : BaseDiagnosticRendererFactory() {

    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("FIR") { map ->
        map.put(
            JAVA_TYPE_MISMATCH,
            "Java type mismatch: expected ''{0}'' but found ''{1}''. Use explicit cast.",
            RENDER_TYPE,
            RENDER_TYPE,
        )

        map.put(
            RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS,
            "Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type ''{0}''.{2}",
            RENDER_TYPE,
            NOT_RENDERED,
            OPTIONAL_SENTENCE,
        )
        map.put(
            NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS,
            "Java type mismatch: inferred type is ''{0}'', but ''{1}'' was expected.{2}",
            RENDER_TYPE,
            RENDER_TYPE,
            OPTIONAL_SENTENCE,
        )
        map.put(
            NULLABILITY_MISMATCH_BASED_ON_EXPLICIT_TYPE_ARGUMENTS_FOR_JAVA,
            "Java type mismatch because of nullable type argument ''{0}'', as not-null ''{1}'' was expected.{2}",
            RENDER_TYPE,
            RENDER_TYPE,
            OPTIONAL_SENTENCE,
        )

        map.put(
            TYPE_MISMATCH_WHEN_FLEXIBILITY_CHANGES,
            "Argument type mismatch: actual type is ''{0}'', but ''{1}'' was expected."
                .toDeprecationWarningMessage(LanguageFeature.ProhibitReturningIncorrectNullabilityValuesFromSamConstructorLambdaOfJdkInterfaces),
            RENDER_TYPE,
            RENDER_TYPE,
        )

        map.put(
            JAVA_CLASS_ON_COMPANION,
            "The resulting type of this ''javaClass'' call is ''{0}'' and not ''{1}''. Use ''::class.java'' to access type ''{1}''.",
            RENDER_TYPE,
            RENDER_TYPE,
        )

        map.put(
            WRONG_NULLABILITY_FOR_JAVA_OVERRIDE,
            "Override ''{0}'' has incorrect nullability in its signature compared to the overridden declaration ''{1}''.",
            SYMBOL,
            SYMBOL,
        )

        map.put(
            ACCIDENTAL_OVERRIDE_CLASH_BY_JVM_SIGNATURE,
            "This function accidentally overrides both {0} and {1} {2} from JVM point of view because of mixed Java/Kotlin hierarchy.\n" +
                    "This situation provokes a JVM clash and is forbidden. To fix it, delete either this or one of the overridden functions.",
            SYMBOL_WITH_CONTAINING_DECLARATION,
            STRING,
            SYMBOL_WITH_CONTAINING_DECLARATION,
        )

        map.put(
            IMPLEMENTATION_BY_DELEGATION_WITH_DIFFERENT_GENERIC_SIGNATURE,
            "The function {0} from an interface is generic, but the function {1} from a delegate is not.\n" +
                    "Such an implementation can provoke runtime errors.",
            SYMBOL_WITH_CONTAINING_DECLARATION,
            SYMBOL_WITH_CONTAINING_DECLARATION,
        )

        map.put(NOT_YET_SUPPORTED_LOCAL_INLINE_FUNCTION, "Local inline functions are not yet supported.")

        map.put(
            UPPER_BOUND_VIOLATED_BASED_ON_JAVA_ANNOTATIONS,
            "Type argument is not within its bounds: must be subtype of ''{0}''.",
            RENDER_TYPE,
            RENDER_TYPE
        )
        map.put(
            UPPER_BOUND_VIOLATED_IN_TYPEALIAS_EXPANSION_BASED_ON_JAVA_ANNOTATIONS,
            "Type argument is not within its bounds: must be subtype of ''{0}''.",
            RENDER_TYPE,
            RENDER_TYPE
        )
        map.put(
            PROPERTY_HIDES_JAVA_FIELD,
            "This property hides Java field ''{0}'' thus making it inaccessible.",
            SYMBOL,
        )

        map.put(UPPER_BOUND_CANNOT_BE_ARRAY, "Upper bound of type parameter cannot be an array.")
        map.put(STRICTFP_ON_CLASS, "'@Strictfp' annotation on classes is not yet supported.")
        map.put(SYNCHRONIZED_ON_ABSTRACT, "'@Synchronized' annotation cannot be used on abstract functions.")
        map.put(SYNCHRONIZED_ON_INLINE, "'@Synchronized' annotation has no effect on inline functions.")
        map.put(SYNCHRONIZED_ON_VALUE_CLASS, "'@Synchronized' annotation has no effect on value classes.")
        map.put(
            SYNCHRONIZED_BLOCK_ON_JAVA_VALUE_BASED_CLASS,
            "Synchronizing on an instance of Java value-based class ''{0}'' will produce a runtime exception in future JVM releases.",
            RENDER_TYPE,
        )
        map.put(SYNCHRONIZED_BLOCK_ON_VALUE_CLASS_OR_PRIMITIVE, "Synchronizing on ''{0}'' is forbidden.", RENDER_TYPE)
        map.put(SYNCHRONIZED_ON_SUSPEND, "'@Synchronized' annotation is not applicable to 'suspend' functions and lambdas.")
        map.put(SYNCHRONIZED_IN_INTERFACE, "'@Synchronized' annotation cannot be used on interface members.")
        map.put(SYNCHRONIZED_IN_ANNOTATION, "'@Synchronized' annotation cannot be used on annotation parameters.")
        map.put(OVERLOADS_WITHOUT_DEFAULT_ARGUMENTS, "'@JvmOverloads' annotation has no effect for methods without default arguments.")
        map.put(OVERLOADS_ABSTRACT, "'@JvmOverloads' annotation cannot be used on abstract methods.")
        map.put(OVERLOADS_INTERFACE, "'@JvmOverloads' annotation cannot be used on interface methods.")
        map.put(OVERLOADS_PRIVATE, "'@JvmOverloads' annotation has no effect on private declarations.")
        map.put(OVERLOADS_LOCAL, "'@JvmOverloads' annotation cannot be used on local declarations.")
        map.put(
            OVERLOADS_ANNOTATION_CLASS_CONSTRUCTOR_ERROR,
            "'@JvmOverloads' annotation cannot be used on constructors of annotation classes."
        )
        map.put(DEPRECATED_JAVA_ANNOTATION, "This annotation is deprecated in Kotlin. Use ''@{0}'' instead.", TO_STRING)
        map.put(POSITIONED_VALUE_ARGUMENT_FOR_JAVA_ANNOTATION, "Only named arguments are available for Java annotations.")
        map.put(JVM_PACKAGE_NAME_CANNOT_BE_EMPTY, "'@JvmPackageName' annotation value cannot be empty.")
        map.put(
            JVM_PACKAGE_NAME_MUST_BE_VALID_NAME,
            "'@JvmPackageName' annotation value must be a valid dot-qualified name of a package."
        )
        map.put(
            JVM_PACKAGE_NAME_NOT_SUPPORTED_IN_FILES_WITH_CLASSES,
            "'@JvmPackageName' annotation is not supported for files with class declarations."
        )

        map.put(LOCAL_JVM_RECORD, "Local '@JvmRecord' classes are prohibited.")
        map.put(NON_FINAL_JVM_RECORD, "'@JvmRecord' class must be final.")
        map.put(ENUM_JVM_RECORD, "'@JvmRecord' class cannot be an enum.")
        map.put(
            JVM_RECORD_WITHOUT_PRIMARY_CONSTRUCTOR_PARAMETERS,
            "Primary constructor with parameters is required for '@JvmRecord' class."
        )
        map.put(JVM_RECORD_NOT_VAL_PARAMETER, "Constructor parameter of '@JvmRecord' class must be a 'val'.")
        map.put(JVM_RECORD_NOT_LAST_VARARG_PARAMETER, "Only the last constructor parameter of '@JvmRecord' can be a vararg.")
        map.put(JVM_RECORD_EXTENDS_CLASS, "Record cannot extend a class.", RENDER_TYPE)
        map.put(INNER_JVM_RECORD, "'@JvmRecord' class cannot be inner.")
        map.put(FIELD_IN_JVM_RECORD, "Non-constructor properties with backing field in '@JvmRecord' class are prohibited.")
        map.put(DELEGATION_BY_IN_JVM_RECORD, "Delegation is prohibited for '@JvmRecord' classes.")
        map.put(NON_DATA_CLASS_JVM_RECORD, "Only data classes are allowed to be marked as '@JvmRecord'.")
        map.put(ILLEGAL_JAVA_LANG_RECORD_SUPERTYPE, "Classes cannot have explicit 'java.lang.Record' supertype.")

        map.put(
            JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE,
            "Symbol is declared in module ''{0}'', which the current module does not depend on.", STRING,
        )
        map.put(
            JAVA_MODULE_DOES_NOT_READ_UNNAMED_MODULE,
            "Symbol is declared in an unnamed module which is not read by current module."
        )
        map.put(
            JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE,
            "Symbol is declared in module ''{0}'' which does not export package ''{1}''.", STRING, STRING,
        )

        map.put(OVERRIDE_CANNOT_BE_STATIC, "Override member cannot be '@JvmStatic' in an object.")
        map.put(
            JVM_STATIC_NOT_IN_OBJECT_OR_CLASS_COMPANION,
            "Only members in named objects and companion objects of classes can be annotated with '@JvmStatic'."
        )
        map.put(
            JVM_STATIC_NOT_IN_OBJECT_OR_COMPANION,
            "Only members in named objects and companion objects can be annotated with '@JvmStatic'."
        )
        map.put(
            JVM_STATIC_ON_NON_PUBLIC_MEMBER,
            "Only public members in interface companion objects can be annotated with '@JvmStatic'."
        )
        map.put(
            JVM_STATIC_ON_CONST_OR_JVM_FIELD,
            "'@JvmStatic' annotation is useless for const or '@JvmField' properties.",
        )
        map.put(
            JVM_STATIC_ON_EXTERNAL_IN_INTERFACE,
            "'@JvmStatic' annotation cannot be used on 'external' members of interface companions."
        )

        map.put(INAPPLICABLE_JVM_NAME, "'@JvmName' annotation is not applicable to this declaration.")
        map.put(ILLEGAL_JVM_NAME, "Illegal JVM name.")

        map.put(FUNCTION_DELEGATE_MEMBER_NAME_CLASH, "Spread operator is prohibited for arguments to signature-polymorphic calls.")

        map.put(VALUE_CLASS_WITHOUT_JVM_INLINE_ANNOTATION, "Value classes without '@JvmInline' annotation are not yet supported.")
        map.put(JVM_INLINE_WITHOUT_VALUE_CLASS, "'@JvmInline' annotation is applicable only to value classes.")

        map.put(INAPPLICABLE_JVM_EXPOSE_BOXED_WITH_NAME, "'@JvmExposeBoxed' with name is applicable only to functions, getters and setters.")
        map.put(
            USELESS_JVM_EXPOSE_BOXED,
            "Useless '@JvmExposeBoxed', it is a callable declaration with no inline value class in its signature."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_EXPOSE_SUSPEND,
            "Suspend functions cannot be exposed by @JvmExposeBoxed."
        )
        map.put(
            JVM_EXPOSE_BOXED_REQUIRES_NAME,
            "This declaration requires a name in the '@JvmExposeBoxed' annotation."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_BE_THE_SAME,
            "The name in '@JvmExposeBoxed' cannot coincide with that of the declaration."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_BE_THE_SAME_AS_JVM_NAME,
            "The name in '@JvmExposeBoxed' cannot coincide with the one in '@JvmName'."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_EXPOSE_OPEN_ABSTRACT,
            "'@JvmExposeBoxed' cannot expose functions which are open or abstract, or member of an interface."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_EXPOSE_SYNTHETIC,
            "'@JvmExposeBoxed' cannot expose synthetic functions."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_EXPOSE_LOCALS,
            "'@JvmExposeBoxed' cannot expose local functions."
        )
        map.put(
            JVM_EXPOSE_BOXED_CANNOT_EXPOSE_REIFIED,
            "'@JvmExposeBoxed' cannot expose inline functions with reified type parameters."
        )

        map.put(
            JVM_DEFAULT_WITHOUT_COMPATIBILITY_NOT_IN_ENABLE_MODE,
            "Usage of '@JvmDefaultWithoutCompatibility' is only allowed with '-jvm-default=enable' option."
        )
        map.put(
            JVM_DEFAULT_WITH_COMPATIBILITY_NOT_IN_NO_COMPATIBILITY_MODE,
            "Usage of '@JvmDefaultWithCompatibility' is only allowed with '-jvm-default=no-compatibility' option."
        )

        map.put(EXTERNAL_DECLARATION_IN_INTERFACE, "Members of interfaces cannot be external.")
        map.put(EXTERNAL_DECLARATION_CANNOT_BE_ABSTRACT, "External declaration cannot be abstract.")
        map.put(EXTERNAL_DECLARATION_CANNOT_HAVE_BODY, "External declaration cannot have a body.")
        map.put(EXTERNAL_DECLARATION_CANNOT_BE_INLINED, "Inline functions cannot be external.")

        map.put(INAPPLICABLE_JVM_FIELD, "{0}.", STRING)
        map.put(INAPPLICABLE_JVM_FIELD_WARNING, "{0}.".toDeprecationWarningMessage(LanguageFeature.ForbidJvmAnnotationsOnAnnotationParameters), STRING)

        map.put(JVM_SYNTHETIC_ON_DELEGATE, "'@JvmSynthetic' annotation cannot be used on delegated properties.")

        map.put(
            NON_SOURCE_REPEATED_ANNOTATION,
            "Repeatable annotations with non-SOURCE retention are supported only in Kotlin 1.6 and later."
        )
        map.put(
            REPEATED_ANNOTATION_WITH_CONTAINER,
            "Repeated annotation ''@{0}'' cannot be used on a declaration that is annotated with its container annotation ''@{1}''.",
            TO_STRING,
            TO_STRING
        )

        map.put(
            INTERFACE_CANT_CALL_DEFAULT_METHOD_VIA_SUPER,
            "Calling JVM-default members via super is supported only in Kotlin 2.1 and later, or with -jvm-default=enable/no-compatibility.",
        )
        map.put(
            SUBCLASS_CANT_CALL_COMPANION_PROTECTED_NON_STATIC,
            "Using protected members that are not '@JvmStatic' in the superclass companion is not yet supported."
        )

        map.put(
            REPEATABLE_CONTAINER_MUST_HAVE_VALUE_ARRAY_ERROR,
            "Container annotation ''{0}'' must have a property ''value'' of type ''Array<{1}>''.",
            TO_STRING,
            TO_STRING
        )
        map.put(
            REPEATABLE_CONTAINER_HAS_NON_DEFAULT_PARAMETER_ERROR,
            "Container annotation ''{0}'' does not have a default value for ''{1}''.",
            TO_STRING,
            TO_STRING
        )
        map.put(
            REPEATABLE_CONTAINER_HAS_SHORTER_RETENTION_ERROR,
            "Container annotation ''{0}'' has shorter retention (''{1}'') than the repeatable annotation ''{2}'' (''{3}'').",
            TO_STRING,
            TO_STRING,
            TO_STRING,
            TO_STRING
        )
        map.put(
            REPEATABLE_CONTAINER_TARGET_SET_NOT_A_SUBSET_ERROR,
            "Target set of container annotation ''{0}'' must be a subset of the target set of contained annotation ''{1}''.",
            TO_STRING,
            TO_STRING
        )
        map.put(
            REPEATABLE_ANNOTATION_HAS_NESTED_CLASS_NAMED_CONTAINER_ERROR,
            "Repeatable annotation cannot have a nested class named 'Container'. This name is reserved for auto-generated container class."
        )
        map.put(
            SUSPENSION_POINT_INSIDE_CRITICAL_SECTION,
            "The ''{0}'' suspension point is inside a critical section.",
            DECLARATION_NAME
        )
        map.put(
            INLINE_FROM_HIGHER_PLATFORM,
            "Cannot inline bytecode built with {0} into bytecode that is being built with {1}. Specify proper ''-jvm-target'' option.",
            STRING,
            STRING
        )
        map.put(
            CONCURRENT_HASH_MAP_CONTAINS_OPERATOR_ERROR,
            "Method 'contains' from ConcurrentHashMap might have unexpected semantics: it calls 'containsValue' instead of 'containsKey'. " +
                    "Use the explicit form of the call to 'containsKey'/'containsValue'/'contains' or cast the value to 'kotlin.collections.Map' instead. " +
                    "See https://youtrack.jetbrains.com/issue/KT-18053 for more details."
        )
        map.put(
            SPREAD_ON_SIGNATURE_POLYMORPHIC_CALL_ERROR,
            "Spread operator is prohibited for arguments to signature-polymorphic calls."
        )
        map.put(
            JAVA_SAM_INTERFACE_CONSTRUCTOR_REFERENCE,
            "Java SAM interface constructor references are prohibited."
        )
        map.put(
            REDUNDANT_REPEATABLE_ANNOTATION,
            "Remove the ''{0}'' annotation, as it is redundant in presence of ''{1}''.",
            TO_STRING,
            TO_STRING,
        )
        map.put(THROWS_IN_ANNOTATION, "'@Throws' annotation cannot be used on annotation parameters.")
        map.put(
            JVM_SERIALIZABLE_LAMBDA_ON_INLINED_FUNCTION_LITERALS,
            "'@JvmSerializableLambda' is not applicable to inlined function literals."
        )
        map.put(
            INCOMPATIBLE_ANNOTATION_TARGETS,
            "Incompatible annotation targets: Java {0} missing, corresponding to Kotlin {1}.",
            STRING_TARGETS,
            STRING_TARGETS
        )
        map.put(
            ANNOTATION_TARGETS_ONLY_IN_JAVA,
            "No Kotlin '@Target' annotation specified (implicitly targeting everything), but one exists for Java."
        )
        map.put(
            NO_REFLECTION_IN_CLASS_PATH,
            "Call uses reflection API which is not found in compilation classpath. " +
                    "Make sure you have kotlin-reflect.jar in the classpath."
        )

        map.put(
            SYNTHETIC_PROPERTY_WITHOUT_JAVA_ORIGIN,
            "This synthetic property is based on the getter function ''{0}'' from Kotlin. " +
                    "In the future, synthetic properties will be available only if the base getter function came from Java. " +
                    "Consider replacing this property access with a ''{1}()'' function call.",
            SYMBOL,
            NAME
        )

        map.put(
            JAVA_FIELD_SHADOWED_BY_KOTLIN_PROPERTY,
            "This variable access is resolved to Java field, but it is clashed with Kotlin property ''{0}'' with backing field " +
                    "which leads to the incorrect bytecode generation and failure at runtime. So such calls are prohibited until " +
                    "corresponding bug will be fixed. See https://youtrack.jetbrains.com/issue/KT-56386 for more details.",
            SYMBOL
        )

        map.put(
            JAVA_CLASS_INHERITS_KT_PRIVATE_CLASS,
            "Java class ''{0}'' declaring this member directly or indirectly extends the private Kotlin class ''{1}''.",
            TO_STRING,
            RENDER_TYPE
        )

        map.put(
            MISSING_BUILT_IN_DECLARATION,
            "Cannot access built-in declaration ''{0}''. Ensure that you have a dependency on the Kotlin standard library.",
            DECLARATION_FQ_NAME
        )
        map.put(
            DANGEROUS_CHARACTERS,
            "Name contains character(s) that can cause problems on Windows: {0}",
            STRING
        )

        map.put(
            IDENTITY_SENSITIVE_OPERATIONS_WITH_VALUE_TYPE,
            "Identity-sensitive operation on an instance of value type ''{0}'' may cause unexpected behavior or errors.",
            RENDER_TYPE,
        )
    }
}
