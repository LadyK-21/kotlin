// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-74389
// WITH_STDLIB

@file:OptIn(ExperimentalJsExport::class)

import kotlin.js.Promise

@JsExport
fun foo(): Promise<*> = null!!

<!NON_EXPORTABLE_TYPE!>@JsExport
fun bar(): Promise<Unit><!> = null!!

@JsExport
class Box<T>

@JsExport
fun box(): Box<*> = null!!
