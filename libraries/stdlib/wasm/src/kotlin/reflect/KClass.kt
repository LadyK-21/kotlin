/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect

/**
 * Represents a class and provides introspection capabilities.
 * Instances of this class are obtainable by the `::class` syntax.
 * See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/reflection.html#class-references)
 * for more information.
 *
 * @param T the type of the class.
 */
public actual interface KClass<T : Any> : KClassifier {
    /**
     * The simple name of the class as it was declared in the source code,
     * or `null` if the class has no name (if, for example, it is a class of an anonymous object).
     */
    public actual val simpleName: String?

    /**
     * The fully qualified dot-separated name of the class,
     * or `null` if the class is local or a class of an anonymous object.
     *
     * By default, this property is not supported in Kotlin/Wasm and using it will result in a compilation error.
     * To enable fully qualified names, `-Xwasm-kclass-fqn` compiler flag should be specified.
     * That, however, may increase the size of the linked executable.
     */
    public actual val qualifiedName: String?

    /**
     * Returns `true` if [value] is an instance of this class on a given platform.
     */
    @SinceKotlin("1.1")
    public actual fun isInstance(value: Any?): Boolean

    actual override fun equals(other: Any?): Boolean // KT-24971

    actual override fun hashCode(): Int // KT-24971
}
