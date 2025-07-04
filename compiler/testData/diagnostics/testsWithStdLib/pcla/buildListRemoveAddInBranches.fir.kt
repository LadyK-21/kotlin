// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-55168
fun foo(arg: Boolean) = buildList {
    if (arg) {
        removeLast()
    } else {
        add(<!ARGUMENT_TYPE_MISMATCH!>42<!>)
    }
}

fun bar(arg: Boolean) = buildList {
    if (!arg) {
        add(42)
    } else {
        removeLast()
    }
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, lambdaLiteral */
