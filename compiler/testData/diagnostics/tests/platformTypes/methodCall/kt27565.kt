// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// LANGUAGE: +SamConversionForKotlinFunctions
// DIAGNOSTICS: -UNUSED_PARAMETER
// ISSUE: KT-27565

// FILE: Runnable.java

public interface Runnable {
    void run();
}

// FILE: k.kt

fun fail() {
    foo({ }, { })
    foo(::bar, { })
}

fun foo(f: Runnable, selector: () -> Unit) {}
fun foo(func1: () -> Unit, func2: () -> Unit) {}

fun bar() {}

/* GENERATED_FIR_TAGS: callableReference, functionDeclaration, functionalType, javaType, lambdaLiteral */
