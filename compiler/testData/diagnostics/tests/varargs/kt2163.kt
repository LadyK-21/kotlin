// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

fun printAll(vararg a : Any) {}

fun main(args: Array<String>) {
    printAll(*args) // Shouldn't be an error
}

/* GENERATED_FIR_TAGS: functionDeclaration, vararg */
