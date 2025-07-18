// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-36188

interface SomeRandomBase<K> {
    fun child(props: Int = 20)
}

interface SomeRandomOverride<J> : SomeRandomBase<J> {
    override abstract fun child(props: Int)
}

open class Keker<P> {
    open fun child(props: Int = 10) {}
}

class Implementation<P>() : Keker<P>(), SomeRandomOverride<P> {
    override fun child(<!MULTIPLE_DEFAULTS_INHERITED_FROM_SUPERTYPES_DEPRECATION_WARNING!>props: Int<!>) {}
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, integerLiteral, interfaceDeclaration, nullableType,
override, primaryConstructor, typeParameter */
