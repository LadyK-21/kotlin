// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
typealias S = String

fun test1(x: Any) = x is S
fun test2(x: Any) = x as S
fun test3(x: Any) = x as? S

/* GENERATED_FIR_TAGS: asExpression, functionDeclaration, isExpression, nullableType, typeAliasDeclaration */
