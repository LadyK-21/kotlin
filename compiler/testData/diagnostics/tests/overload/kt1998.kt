// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// KT-1998 Strange "Overload resolution ambiguity"

object A {
    val c : String = "test"

    fun f(b: B): String {
        return b.c // Test no "Overload resolution ambiguity" is reported here
    }
}

class B

val B.c : String
    get() = "test"

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, getter, objectDeclaration, propertyDeclaration,
propertyWithExtensionReceiver, stringLiteral */
