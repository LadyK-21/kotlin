// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: BACKEND
// MODULE: m1-common
// FILE: common.kt
@<!UNRESOLVED_REFERENCE!>NonExistingClass<!>
expect fun foo()

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt
actual fun foo() {}

/* GENERATED_FIR_TAGS: actual, expect, functionDeclaration */
