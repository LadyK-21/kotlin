/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir.providers.impl.BridgeProvider

import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.sir.*
import org.jetbrains.kotlin.sir.providers.*
import org.jetbrains.kotlin.sir.providers.source.kaSymbolOrNull
import org.jetbrains.kotlin.sir.util.isNever
import org.jetbrains.kotlin.sir.util.isVoid
import org.jetbrains.kotlin.sir.util.name
import org.jetbrains.kotlin.sir.util.swiftIdentifier

private const val exportAnnotationFqName = "kotlin.native.internal.ExportedBridge"
private const val cinterop = "kotlinx.cinterop.*"
private const val convertBlockPtrToKotlinFunction = "kotlinx.cinterop.internal.convertBlockPtrToKotlinFunction"
private const val stdintHeader = "stdint.h"
private const val foundationHeader = "Foundation/Foundation.h"

public class SirBridgeProviderImpl(private val session: SirSession, private val typeNamer: SirTypeNamer) : SirBridgeProvider {
    override fun generateTypeBridge(
        kotlinFqName: List<String>,
        swiftFqName: String,
        swiftSymbolName: String,
    ): SirTypeBindingBridge? {
        val annotationName = "kotlin.native.internal.objc.BindClassToObjCName"
        val kotlinFqName = kotlinFqName.joinToString(".")
        return SirTypeBindingBridge(
            name = swiftFqName,
            kotlinFileAnnotation = "$annotationName($kotlinFqName::class, \"$swiftSymbolName\")"
        )
    }

    override fun generateFunctionBridge(
        baseBridgeName: String,
        explicitParameters: List<SirParameter>,
        returnType: SirType,
        kotlinFqName: List<String>,
        selfParameter: SirParameter?,
        extensionReceiverParameter: SirParameter?,
        errorParameter: SirParameter?
    ): BridgeFunctionProxy? = session.withSessions {
        val covariantTypes = listOfNotNull(returnType, errorParameter?.type)
        val contravariantTypes = (explicitParameters + listOfNotNull(selfParameter, extensionReceiverParameter))
            .map { it.type }

        if ((covariantTypes + contravariantTypes).any { !isSupported(it) })
            return@withSessions null

        // If any of the parameters is never - there should be no ability to call this function - therefore we can skip the bridge generation
        if (contravariantTypes.any { it.isNever })
            return@withSessions null

        BridgeFunctionDescriptor(
            baseBridgeName = baseBridgeName,
            parameters = explicitParameters.mapIndexed { index, value -> bridgeParameter(value, index) },
            returnType = bridgeType(returnType),
            kotlinFqName = kotlinFqName,
            selfParameter = selfParameter?.let { bridgeParameter(it, 0) },
            extensionReceiverParameter = extensionReceiverParameter?.let { bridgeParameter(it, 0) },
            errorParameter = errorParameter?.let {
                BridgeParameter(
                    name = it.name!!.let(::createBridgeParameterName),
                    bridge = Bridge.AsOutError
                )
            },
            typeNamer = typeNamer
        )
    }
}

internal fun SirAndKaSession.isSupported(type: SirType): Boolean = when (type) {
    is SirNominalType -> {
        val declarationSupported = when (val declaration = type.typeDeclaration) {
            is SirTypealias -> isSupported(declaration.type)
            else -> type.typeDeclaration.kaSymbolOrNull<KaNamedClassSymbol>()?.sirAvailability(useSiteSession)?.let { it is SirAvailability.Available } != false
        }
        declarationSupported && type.typeArguments.all(::isSupported)
    }
    is SirFunctionalType -> isSupported(type.returnType) && type.parameterTypes.all(::isSupported)
    is SirExistentialType -> type.protocols.all { it.kaSymbolOrNull<KaClassSymbol>()?.sirAvailability(useSiteSession) is SirAvailability.Available != false }
    else -> false
}

public interface BridgeFunctionBuilder {
    public val baseBridgeName: String
    public val kotlinFqName: List<String>
    public val typeNamer: SirTypeNamer

    public val parameters: List<Any>
    public val returnType: Any
    public val selfParameter: Any?
    public val extensionReceiverParameter: Any?
    public val errorParameter: Any?

    public fun buildCall(args: String): String
    public val argNames: List<String>
    public val name: String
}

public interface BridgeFunctionProxy {
    public fun createSirBridge(kotlinCall: BridgeFunctionBuilder.() -> String): SirBridge
    public fun createSwiftInvocation(resultTransformer: ((String) -> String)?): List<String>
}

private class BridgeFunctionDescriptor(
    override val baseBridgeName: String,
    override val parameters: List<BridgeParameter>,
    override val returnType: Bridge,
    override val kotlinFqName: List<String>,
    override val selfParameter: BridgeParameter?,
    override val extensionReceiverParameter: BridgeParameter?,
    override val errorParameter: BridgeParameter?,
    override val typeNamer: SirTypeNamer,
) : BridgeFunctionBuilder, BridgeFunctionProxy {
    val kotlinBridgeName = bridgeDeclarationName(baseBridgeName, parameters, typeNamer)
    val cBridgeName = kotlinBridgeName

    val allParameters
        get() = listOfNotNull(selfParameter) + parameters + listOfNotNull(errorParameter)

    override val name
        get() = kotlinFqName.joinToString(separator = ".") { it.kotlinIdentifier }

    override val argNames get() = this.parameters.map { "__${it.name}".kotlinIdentifier }

    override fun buildCall(args: String): String {
        return if (selfParameter == null) {
            if (extensionReceiverParameter == null) {
                "$name$args"
            } else {
                "__${extensionReceiverParameter.name}.$safeImportName$args"
            }
        } else {
            val memberName = kotlinFqName.last().kotlinIdentifier
            if (extensionReceiverParameter == null) {
                "__${selfParameter.name}.$memberName$args"
            } else {
                "__${selfParameter.name}.run { __${extensionReceiverParameter.name}.$memberName$args }"
            }
        }
    }

    override fun createSirBridge(kotlinCall: BridgeFunctionBuilder.() -> String) =
        SirFunctionBridge(
            name = baseBridgeName,
            KotlinFunctionBridge(
                createKotlinBridge(typeNamer, kotlinCall),
                listOf(exportAnnotationFqName, cinterop, convertBlockPtrToKotlinFunction) + additionalImports()
            ),
            CFunctionBridge(listOf(cDeclaration()), listOf(foundationHeader, stdintHeader))
        )

    override fun createSwiftInvocation(resultTransformer: ((String) -> String)?): List<String> = buildList {
        val descriptor = this@BridgeFunctionDescriptor
        val errorParameter = descriptor.errorParameter

        if (errorParameter != null) {
            add("var ${errorParameter.name}: UnsafeMutableRawPointer? = nil")
            add("let _result = ".takeIf { resultTransformer != null }.orEmpty() + descriptor.swiftInvoke(typeNamer))
            val error = errorParameter.bridge.inSwiftSources.kotlinToSwift(typeNamer, errorParameter.name)
            add("guard ${errorParameter.name} == nil else { throw KotlinError(wrapped: $error) }")
            resultTransformer?.let { add(it(descriptor.returnType.inSwiftSources.kotlinToSwift(typeNamer, "_result"))) }
        } else {
            add((resultTransformer ?: { it })(descriptor.swiftCall(typeNamer)))
        }
    }
}

// TODO: we need to mangle C name in more elegant way. KT-64970
// problems with this approach are:
// 1. there can be limit for declaration names in Clang compiler
// 1. this name will be UGLY in the debug session
private fun bridgeDeclarationName(bridgeName: String, parameterBridges: List<BridgeParameter>, typeNamer: SirTypeNamer): String {
    val nameSuffixForOverloadSimulation = parameterBridges.joinToString(separator = "_") {
        typeNamer.swiftFqName(it.bridge.swiftType)
            .replace(".", "_")
            .replace(",", "_")
            .replace("<", "_")
            .replace(">", "_")
    }
    val suffixString = if (parameterBridges.isNotEmpty()) "__TypesOfArguments__${nameSuffixForOverloadSimulation}__" else ""
    val result = "${bridgeName}${suffixString}".cIdentifier
    return result
}

private fun BridgeFunctionDescriptor.createKotlinBridge(
    typeNamer: SirTypeNamer,
    buildCallSite: BridgeFunctionDescriptor.() -> String,
) = buildList {
    add("@${exportAnnotationFqName.substringAfterLast('.')}(\"${cBridgeName}\")")
    add(
        "public fun $kotlinBridgeName(${
            allParameters.filter { it.isRenderable }.joinToString { "${it.name.kotlinIdentifier}: ${it.bridge.kotlinType.repr}" }
        }): ${returnType.kotlinType.repr} {"
    )
    val indent = "    "

    allParameters.forEach {
        val parameterName = "__${it.name}".kotlinIdentifier
        add("${indent}val $parameterName = ${it.bridge.inKotlinSources.swiftToKotlin(typeNamer, it.name.kotlinIdentifier)}")
    }
    val callSite = buildCallSite()
    if (returnType.swiftType.isVoid && errorParameter == null) {
        add("${indent}$callSite")
    } else {
        val resultName = "_result"
        if (errorParameter != null) {
            add(
                """
            try {
                val $resultName = $callSite
                return ${returnType.inKotlinSources.kotlinToSwift(typeNamer, resultName)}
            } catch (error: Throwable) {
                __${errorParameter.name}.value = StableRef.create(error).asCPointer()
                return ${returnType.kotlinType.defaultValue}
            }
            """.trimIndent().prependIndent(indent)
            )
        } else {
            add("${indent}val $resultName = $callSite")
            add("${indent}return ${returnType.inKotlinSources.kotlinToSwift(typeNamer, resultName)}")
        }
    }
    add("}")
}

private fun BridgeFunctionDescriptor.swiftInvoke(typeNamer: SirTypeNamer): String {
    val parameters = allParameters.filter { it.isRenderable }.joinToString {
        // We fix ugly `self` escaping here. This is the only place we'd otherwise need full support for swift's contextual keywords
        it.bridge.inSwiftSources.swiftToKotlin(typeNamer, it.name.takeIf { it == "self" } ?: it.name.swiftIdentifier)
    }
    return "$cBridgeName($parameters)"
}

private fun BridgeFunctionDescriptor.swiftCall(typeNamer: SirTypeNamer): String {
    return returnType.inSwiftSources.kotlinToSwift(typeNamer, swiftInvoke(typeNamer))
}

private fun BridgeFunctionDescriptor.cDeclaration() = buildString {
    append(
        returnType.cType.render(buildString {
            append(cBridgeName)
            append("(")
            allParameters.filter { it.isRenderable }.joinTo(this) { it.bridge.cType.render(it.name.cIdentifier) }
            append(')')
        })
    )
    if (returnType.swiftType.isNever) append(" __attribute((noreturn))")
    append(";")
}

private fun BridgeFunctionDescriptor.additionalImports(): List<String> {
    if (extensionReceiverParameter != null && selfParameter == null && kotlinFqName.size > 1) {
        return listOf("$name as $safeImportName")
    }
    return emptyList()
}

private val BridgeFunctionDescriptor.safeImportName: String
    get() = kotlinFqName.run { if (size <= 1) single() else joinToString("_") { it.replace("_", "__") } }
