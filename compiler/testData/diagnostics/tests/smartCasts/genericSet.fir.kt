// RUN_PIPELINE_TILL: BACKEND
class Wrapper<T>(var x: T)

inline fun <reified T> change(w: Wrapper<T>, x: Any?) {
    if (x is T) {
        w.x = x
    }
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, ifExpression, inline, isExpression,
nullableType, primaryConstructor, propertyDeclaration, reified, smartcast, typeParameter */
