// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: +UNUSED_TYPEALIAS_PARAMETER
typealias Test<T, <!UNUSED_TYPEALIAS_PARAMETER!>X<!>> = List<T>
typealias Test2<T, <!UNUSED_TYPEALIAS_PARAMETER!>X<!>> = Test<T, X>

/* GENERATED_FIR_TAGS: nullableType, typeAliasDeclaration, typeAliasDeclarationWithTypeParameter, typeParameter */
