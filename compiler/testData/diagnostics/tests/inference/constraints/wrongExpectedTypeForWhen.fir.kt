// RUN_PIPELINE_TILL: FRONTEND
fun foo() {
    var f: Int = <!INITIALIZER_TYPE_MISMATCH!>if (true) <!ARGUMENT_TYPE_MISMATCH!>{ x: Long ->  }<!> else { x: Long ->  }<!>
}

class A {
    var x: Int
        get(): Int = <!RETURN_TYPE_MISMATCH!>if (true) { <!ARGUMENT_TYPE_MISMATCH!>{42}<!> } else { <!ARGUMENT_TYPE_MISMATCH!>{24}<!> }<!>
        set(i: Int) {}
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, getter, ifExpression, integerLiteral, lambdaLiteral,
localProperty, propertyDeclaration, setter */
