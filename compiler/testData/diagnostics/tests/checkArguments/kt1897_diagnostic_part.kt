// RUN_PIPELINE_TILL: FRONTEND
//KT-1897 When call cannot be resolved to any function, save information about types of arguments

package a

fun bar() {}

fun foo(i: Int, s: String) {}

fun test() {

    bar(<!TOO_MANY_ARGUMENTS, UNRESOLVED_REFERENCE!>xx<!>)

    bar <!TOO_MANY_ARGUMENTS!>{ }<!>

    foo(<!TYPE_MISMATCH!>""<!>, <!CONSTANT_EXPECTED_TYPE_MISMATCH!>1<!>, <!TOO_MANY_ARGUMENTS, UNRESOLVED_REFERENCE!>xx<!>)

    foo(<!NAMED_PARAMETER_NOT_FOUND!>r<!> = <!UNRESOLVED_REFERENCE!>xx<!>, i = <!TYPE_MISMATCH!>""<!>, s = "")

    foo(i = 1, <!ARGUMENT_PASSED_TWICE!>i<!> = 1, s = <!CONSTANT_EXPECTED_TYPE_MISMATCH!>11<!>)

    foo(<!TYPE_MISMATCH!>""<!>, s = <!CONSTANT_EXPECTED_TYPE_MISMATCH!>2<!>)

    foo(i = <!TYPE_MISMATCH!>""<!>, s = <!CONSTANT_EXPECTED_TYPE_MISMATCH!>2<!>, <!TOO_MANY_ARGUMENTS!>33<!>)

    foo(<!TYPE_MISMATCH!>""<!>, <!CONSTANT_EXPECTED_TYPE_MISMATCH!>1<!>) <!TOO_MANY_ARGUMENTS!>{}<!>

    foo(<!TYPE_MISMATCH!>""<!>, <!CONSTANT_EXPECTED_TYPE_MISMATCH!>1<!>) <!TOO_MANY_ARGUMENTS!>{}<!> <!MANY_LAMBDA_EXPRESSION_ARGUMENTS!>{}<!>
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, lambdaLiteral, stringLiteral */
