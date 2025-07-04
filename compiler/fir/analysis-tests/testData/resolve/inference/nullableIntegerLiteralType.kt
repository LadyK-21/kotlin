// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-37639

fun takeInt(x: Int) {}

fun test_1(b: Boolean) {
    val x = if (b) 1 else null
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>x<!>)
}

fun test_2(b: Boolean, y: Int) {
    val x = if (b) y else null
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>x<!>)
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, localProperty, nullableType,
propertyDeclaration */
