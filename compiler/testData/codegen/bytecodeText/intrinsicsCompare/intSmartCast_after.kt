// LANGUAGE: +ProperIeee754Comparisons
fun equals3(a: Int?, b: Int?) = a != null && b != null && a == b

fun equals4(a: Int?, b: Int?) = if (a is Int && b is Int) a == b else null!!

fun equals5(a: Any?, b: Any?) = if (a is Int && b is Int) a == b else null!!

fun less3(a: Int?, b: Int?) = a != null && b != null && a < b

fun less4(a: Int?, b: Int?) = if (a is Int && b is Int) a < b else true

fun less5(a: Any?, b: Any?) = if (a is Int && b is Int) a < b else true

// 3 Intrinsics\.areEqual
// 0 Intrinsics\.compare
// 0 IFGE
// 3 IF_ICMPGE
