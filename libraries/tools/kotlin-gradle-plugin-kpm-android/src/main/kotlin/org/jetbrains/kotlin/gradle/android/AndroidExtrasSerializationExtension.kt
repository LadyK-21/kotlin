/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.android

import org.jetbrains.kotlin.gradle.kpm.idea.serialize.IdeaKpmExtrasSerializationExtension
import org.jetbrains.kotlin.gradle.kpm.idea.serialize.IdeaKpmExtrasSerializer.Companion.javaIoSerializable

val AndroidExtrasSerializationExtension = IdeaKpmExtrasSerializationExtension {
    register(androidDslKey, javaIoSerializable())
}
