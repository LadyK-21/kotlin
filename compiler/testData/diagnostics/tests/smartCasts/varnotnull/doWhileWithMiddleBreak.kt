// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
fun x(): Boolean { return true }

public fun foo(pp: String?): Int {
    var p = pp
    do {
        p!!.length
        if (p == "abc") break
        p = null
    } while (!x())
    // Smart cast is NOT possible here
    return p<!UNSAFE_CALL!>.<!>length
}

/* GENERATED_FIR_TAGS: assignment, break, checkNotNullCall, doWhileLoop, equalityExpression, functionDeclaration,
ifExpression, localProperty, nullableType, propertyDeclaration, smartcast, stringLiteral */
