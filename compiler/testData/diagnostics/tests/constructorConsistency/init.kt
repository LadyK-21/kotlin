// RUN_PIPELINE_TILL: BACKEND
class My {
    val x: String

    init {
        x = <!DEBUG_INFO_LEAKING_THIS!>foo<!>()
    }

    fun foo(): String = x
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, init, propertyDeclaration */
