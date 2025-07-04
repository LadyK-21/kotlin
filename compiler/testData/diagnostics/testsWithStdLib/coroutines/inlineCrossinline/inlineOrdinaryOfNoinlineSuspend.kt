// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_PARAMETER -NOTHING_TO_INLINE
// SKIP_TXT
// WITH_COROUTINES
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*
import helpers.*

interface SuspendRunnable {
    suspend fun run()
}

// Function is NOT suspend
// parameter is noinline
// parameter is suspend
// Block is NOT allowed to be called inside the body of owner inline function
// Block is allowed to be called from nested classes/lambdas (as common crossinlines)
// It is possible to call startCoroutine on the parameter
// suspend calls possible inside lambda matching to the parameter

inline fun test(noinline c: suspend () -> Unit)  {
    <!ILLEGAL_SUSPEND_FUNCTION_CALL!>c<!>()
    val o = object : SuspendRunnable {
        override suspend fun run() {
            c()
        }
    }
    val l: suspend () -> Unit = { c() }
    c.startCoroutine(EmptyContinuation)
}

suspend fun calculate() = "OK"

fun box() {
    test {
        calculate()
    }
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, assignment, classDeclaration, companionObject, functionDeclaration,
functionalType, inline, interfaceDeclaration, lambdaLiteral, localProperty, noinline, nullableType, objectDeclaration,
override, primaryConstructor, propertyDeclaration, safeCall, stringLiteral, suspend, thisExpression, typeParameter */
