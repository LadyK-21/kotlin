// RUN_PIPELINE_TILL: BACKEND
fun test(x: Any) {
  if (x !is String) return

  class LocalOuter {
    fun foo(y: Any) {
      if (y !is String) return
      class Local {
        init {
          x.length
          y.length
        }
      }
    }
  }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, ifExpression, init, isExpression, localClass, smartcast */
