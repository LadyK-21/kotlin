// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

fun foo() {
    val a = object {
        val b = object {
            val c = 42
        }
    }

    checkSubtype<Int>(a.b.c)
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, integerLiteral, localProperty, nullableType, propertyDeclaration, typeParameter,
typeWithExtension */
