// RUN_PIPELINE_TILL: FRONTEND
fun interface Bar {
    fun invoke(): String
}

operator fun Bar.plus(b: Bar): String = invoke() + b.invoke()

fun box(): String {
    return { "O" } <!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>+<!> { "K" }
}

/* GENERATED_FIR_TAGS: additiveExpression, funInterface, funWithExtensionReceiver, functionDeclaration,
interfaceDeclaration, lambdaLiteral, operator, samConversion, stringLiteral */
