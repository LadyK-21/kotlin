// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
data class A(val x: Int) {
    fun toArray(): IntArray =
            intArrayOf(x)

    override fun <!RETURN_TYPE_MISMATCH_ON_OVERRIDE!>toString<!>() =
            toArray().takeWhile { it != -1 } // .joinToString()
}

/* GENERATED_FIR_TAGS: classDeclaration, data, equalityExpression, functionDeclaration, integerLiteral, lambdaLiteral,
override, primaryConstructor, propertyDeclaration */
