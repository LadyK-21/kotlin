// RUN_PIPELINE_TILL: FRONTEND
// JDK_KIND: MODIFIED_MOCK_JDK
// CHECK_TYPE
// SKIP_TXT
// WITH_STDLIB

fun foo(s: String) {
    s.isBlank()
    s.lines().checkType { _<List<String>>() }
    s.repeat(1)

    // We don't have `strip` extension, so leave it for a while in gray list
    s.<!UNRESOLVED_REFERENCE!>strip<!>()
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType, infix,
integerLiteral, lambdaLiteral, nullableType, typeParameter, typeWithExtension */
