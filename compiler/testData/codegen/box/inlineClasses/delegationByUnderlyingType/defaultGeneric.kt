// WITH_STDLIB
// LANGUAGE: +InlineClassImplementationByDelegation, +GenericInlineClassParameter

interface I {
    fun ok(): String = "OK"
}

inline class IC<T: I>(val i: T): I by i

fun box(): String {
    val i = object : I {}
    var res = IC(i).ok()
    if (res != "OK") return "FAIL: $res"
    val ic: I = IC(i)
    res = ic.ok()
    return res
}
