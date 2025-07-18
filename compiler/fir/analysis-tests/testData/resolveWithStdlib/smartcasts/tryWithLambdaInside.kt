// RUN_PIPELINE_TILL: BACKEND
// DUMP_CFG
// FULL_JDK

fun <T> List<T>.notInPlaceFilter(block: (T) -> Boolean): List<T> = this

fun foo() {}

fun testInPlace(list: List<Boolean>) =
    try {
        list.filter { it }
    } finally {}

fun testNotInPlace(list: List<Boolean>) =
    try {
        list.notInPlaceFilter { it }
    } finally {}

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, functionalType, lambdaLiteral, nullableType,
thisExpression, tryExpression, typeParameter */
