// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// Related issue: KT-28370

fun test1(s1: String?) {
    var s: String? = null
    s = ""
    try {
        s = ""
        requireNotNull(s1)
    }
    catch (e: Exception) {
        return
    }
    finally {
        s.length
    }
    s.length
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, localProperty, nullableType, propertyDeclaration, smartcast,
stringLiteral, tryExpression */
