// RUN_PIPELINE_TILL: BACKEND
// Issue: KT-18583

sealed class Maybe<T> {
    class Nope<T>(val reasonForLog: String, val reasonForUI: String) : Maybe<T>()
    class Yeah<T>(val meat: T) : Maybe<T>()

    fun unwrap() = when (this) {
        is Nope -> throw Exception("")
        is Yeah -> meat
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, isExpression, nestedClass, nullableType,
primaryConstructor, propertyDeclaration, sealed, smartcast, stringLiteral, typeParameter, whenExpression,
whenWithSubject */
