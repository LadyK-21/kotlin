/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.classpathDiff.impl

import java.io.File

/** Information about the location of a .class file. */
internal class ClassFile(

    /** Directory or jar containing the .class file. */
    val classRoot: File,

    /**
     * The relative path from [classRoot] to the .class file.
     *
     * Any '\' characters in the path will be replaced with '/' to create [unixStyleRelativePath].
     */
    relativePath: String
) {

    /** The Unix-style relative path (with '/' as separators) from [classRoot] to the .class file. */
    val unixStyleRelativePath: String = File(relativePath).invariantSeparatorsPath
}

/** Information about the location of a .class file ([ClassFile]) and how to load its contents. */
internal class ClassFileWithContentsProvider(
    val classFile: ClassFile,
    val contentsProvider: () -> ByteArray
) {
    fun loadContents() = ClassFileWithContents(classFile, contentsProvider.invoke())
}

/** Information about the location of a .class file ([ClassFile]) and its contents. */
internal class ClassFileWithContents(
    @Suppress("unused") val classFile: ClassFile,
    val contents: ByteArray
) {
    val classInfo: BasicClassInfo by lazy {
        BasicClassInfo.compute(contents)
    }
}
