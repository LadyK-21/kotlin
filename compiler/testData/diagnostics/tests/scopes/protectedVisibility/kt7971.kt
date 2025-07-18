// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE
// FILE: module1/AbstractModule.java
package module1;

public abstract class AbstractModule<S> {
    protected <T> S bind(Class<T> clazz) { return null; }
}

// FILE: module2/main.kt
package module2

import module1.*
import checkType
import _

fun <T> javaClass(): Class<T> = null!!

public class AppServiceModule : AbstractModule<String>() {
    inline fun <reified T> AbstractModule<Int>.bind() {
        val x = <!PROTECTED_CALL_FROM_PUBLIC_INLINE_ERROR!>bind<!>(javaClass<T>())

        x checkType { _<String>() } // check that Class receiver is used instead of extension one
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, flexibleType, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, inline, javaType, lambdaLiteral, localProperty, nullableType, propertyDeclaration, reified,
typeParameter, typeWithExtension */
