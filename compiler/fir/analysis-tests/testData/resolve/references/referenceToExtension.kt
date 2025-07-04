// RUN_PIPELINE_TILL: FRONTEND
class GenericTest {
    class A<T>

    class B<T> {
        val memberVal: A<T> = A()
        fun memberFun(): A<T> = A()
    }

    val <T> B<T>.extensionVal: A<T>
        get() = A()

    fun <T> B<T>.extensionFun(): A<T> = A()

    fun test_1() {
        val memberValRef = B<*>::memberVal
        val memberFunRef = B<*>::memberFun
    }

    fun test_2() {
        val extensionValRef = B<*>::<!EXTENSION_IN_CLASS_REFERENCE_NOT_ALLOWED!>extensionVal<!>
        val extensionFunRef = B<*>::<!EXTENSION_IN_CLASS_REFERENCE_NOT_ALLOWED!>extensionFun<!>
    }
}

class NoGenericTest {
    class A

    class B {
        val memberVal: A = A()
        fun memberFun(): A = A()
    }

    val B.extensionVal: A
        get() = A()

    fun B.extensionFun(): A = A()

    fun test_1() {
        val extensionValRef = B::<!EXTENSION_IN_CLASS_REFERENCE_NOT_ALLOWED!>extensionVal<!>
        val extensionFunRef = B::<!EXTENSION_IN_CLASS_REFERENCE_NOT_ALLOWED!>extensionFun<!>
    }

    fun test_2() {
        val memberValRef = B::memberVal
        val memberFunRef = B::memberFun
    }
}

/* GENERATED_FIR_TAGS: callableReference, capturedType, classDeclaration, funWithExtensionReceiver, functionDeclaration,
getter, localProperty, nestedClass, nullableType, outProjection, propertyDeclaration, propertyWithExtensionReceiver,
starProjection, typeParameter */
