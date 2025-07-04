// RUN_PIPELINE_TILL: BACKEND
//KT-597 Type inference failed

fun <T> Array<T>?.get(i: Int) : T {
    if (this != null)
        return <!DEBUG_INFO_SMARTCAST!>this<!>.get(i) // <- inferred type is Any? but &T was excepted
    else throw NullPointerException()
}

operator fun Int?.inc() : Int {
    if (this != null)
        return <!DEBUG_INFO_SMARTCAST!>this<!>.inc()
    else
        throw NullPointerException()
}

fun test() {
   var i : Int? = 10
   var i_inc = i++ // <- expected Int?, but returns Any?
}

/* GENERATED_FIR_TAGS: assignment, equalityExpression, funWithExtensionReceiver, functionDeclaration, ifExpression,
incrementDecrementExpression, integerLiteral, localProperty, nullableType, operator, propertyDeclaration, smartcast,
thisExpression, typeParameter */
