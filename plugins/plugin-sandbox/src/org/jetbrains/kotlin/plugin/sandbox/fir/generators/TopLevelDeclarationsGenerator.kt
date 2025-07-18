/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.plugin.sandbox.fir.generators

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.constructStarProjectedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.plugin.sandbox.fir.SANDBOX_ANNOTATIONS_PACKAGE

/*
 * Generates `dummyClassName(value: ClassName): String` function for each class annotated with @DummyFunction
 */
@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class TopLevelDeclarationsGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        private val DUMMY_FUNCTION_CLASS_ID = ClassId(SANDBOX_ANNOTATIONS_PACKAGE, Name.identifier("DummyFunction"))
        private val PREDICATE = LookupPredicate.create { annotated(DUMMY_FUNCTION_CLASS_ID.asSingleFqName()) }
    }

    private val predicateBasedProvider = session.predicateBasedProvider
    private val matchedClasses by lazy {
        predicateBasedProvider.getSymbolsByPredicate(PREDICATE).filterIsInstance<FirRegularClassSymbol>()
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        if (context != null) return emptyList()
        val matchedClassSymbol = findMatchedClassForFunction(callableId) ?: return emptyList()
        val containingFileName = matchedClassSymbol.getAnnotationByClassId(DUMMY_FUNCTION_CLASS_ID, session)?.let {
            val argument = (it as? FirAnnotationCall)?.arguments?.firstOrNull() as? FirLiteralExpression ?: return@let null
            argument.value as? String
        }
        val function = createTopLevelFunction(
            Key,
            callableId,
            session.builtinTypes.stringType.coneType,
            containingFileName = containingFileName,
        ) {
            valueParameter(Name.identifier("value"), matchedClassSymbol.constructStarProjectedType())
        }
        return listOf(function.symbol)
    }

    private fun findMatchedClassForFunction(callableId: CallableId): FirRegularClassSymbol? {
        // We generate only top-level functions
        if (callableId.classId != null) return null
        return matchedClasses
            .filter { it.classId.packageFqName == callableId.packageName }
            .firstOrNull { callableId.callableName.identifier == it.classId.toDummyCallableName() }
    }

    private fun ClassId.toDummyCallableName(): String {
        return "dummy${shortClassName.identifier}"
    }

    override fun getTopLevelCallableIds(): Set<CallableId> {
        return matchedClasses.mapTo(mutableSetOf()) {
            val classId = it.classId
            CallableId(classId.packageFqName, Name.identifier(classId.toDummyCallableName()))
        }
    }

    object Key : GeneratedDeclarationKey()

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(PREDICATE)
    }
}
