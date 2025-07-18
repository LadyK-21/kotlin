// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL

import kotlin.reflect.*

class Foo(val prop: Any) {
    fun func() {}
}

fun n01() = Foo::prop
fun n02() = Foo::func
fun n03() = Foo::class
fun n04(p: KProperty0<Int>) = p.get()
fun n05(p: KMutableProperty0<String>) = p.set("")
fun n07(p: KFunction<String>) = p.name
fun n08(p: KProperty1<String, Int>) = p.get("")
fun n09(p: KProperty2<String, String, Int>) = p.get("", "")
fun n10() = (Foo::func).invoke(Foo(""))
fun n11() = (Foo::func)(Foo(""))

fun y01() = Foo::prop.<!NO_REFLECTION_IN_CLASS_PATH!>getter<!>
fun y02() = Foo::class.<!NO_REFLECTION_IN_CLASS_PATH!>members<!>
fun y03() = Foo::class.simpleName
fun y04() = Foo::class.<!UNRESOLVED_REFERENCE!>properties<!>
fun y05() = Foo::prop.<!NO_REFLECTION_IN_CLASS_PATH!>getter<!>(Foo(42))

fun <T : Any> kclass(k: KClass<*>, kt: KClass<T>) {
    k.simpleName
    k.qualifiedName
    k.<!NO_REFLECTION_IN_CLASS_PATH!>members<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>constructors<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>nestedClasses<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>objectInstance<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>typeParameters<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>supertypes<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>visibility<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isFinal<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isOpen<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isAbstract<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isSealed<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isData<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isInner<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isCompanion<!>

    k.<!NO_REFLECTION_IN_CLASS_PATH!>annotations<!>
    k.isInstance(42)

    k == kt
    k.hashCode()
    k.toString()
}

typealias TA<T> = KClass<T>

fun <T : Any> kclassTa(k: TA<*>, kt: TA<T>) {
    k.simpleName
    k.qualifiedName
    k.<!NO_REFLECTION_IN_CLASS_PATH!>members<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>constructors<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>nestedClasses<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>objectInstance<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>typeParameters<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>supertypes<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>visibility<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isFinal<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isOpen<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isAbstract<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isSealed<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isData<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isInner<!>
    k.<!NO_REFLECTION_IN_CLASS_PATH!>isCompanion<!>

    k.<!NO_REFLECTION_IN_CLASS_PATH!>annotations<!>
    k.isInstance(42)

    k == kt
    k.hashCode()
    k.toString()
}

fun ktype(t: KType, t2: KType) {
    t.classifier
    t.arguments
    t.isMarkedNullable
    t.annotations

    t == t2
    t.hashCode()
    t.toString()

    KTypeProjection.Companion.covariant(t)
    KTypeProjection.STAR
    KTypeProjection(KVariance.IN, t)
}

/* GENERATED_FIR_TAGS: callableReference, capturedType, classDeclaration, classReference, equalityExpression,
functionDeclaration, integerLiteral, nullableType, primaryConstructor, propertyDeclaration, starProjection,
stringLiteral, typeAliasDeclaration, typeAliasDeclarationWithTypeParameter, typeConstraint, typeParameter */
