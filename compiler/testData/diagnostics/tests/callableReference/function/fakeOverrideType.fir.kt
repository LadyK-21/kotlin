// RUN_PIPELINE_TILL: FRONTEND
// CHECK_TYPE

import kotlin.reflect.KFunction2

open class A {
    fun foo(s: String): String = s
}

class B : A() {
}


fun test() {
    B::foo checkType { _<KFunction2<B, String, String>>() }

    (B::hashCode)(<!ARGUMENT_TYPE_MISMATCH!>"No."<!>)
}

/* GENERATED_FIR_TAGS: callableReference, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, lambdaLiteral, nullableType, stringLiteral, typeParameter, typeWithExtension */
