// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
open class A : <!CYCLIC_INHERITANCE_HIERARCHY!>B.BB<!>() {
    open class AA
}
open class B : <!CYCLIC_INHERITANCE_HIERARCHY!>A.AA<!>() {
    open class BB
}

/* GENERATED_FIR_TAGS: classDeclaration, nestedClass */
