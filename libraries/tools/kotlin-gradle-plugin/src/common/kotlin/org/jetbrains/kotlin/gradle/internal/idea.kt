/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.internal

import org.gradle.api.Project
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.jetbrains.kotlin.gradle.utils.ConfigurationCacheOpaqueValueSource

/**
 * Returns [true] only when Gradle build is invoked during IDEA Project Sync
 * i.e. regular Task Execution via IDEA it will be [false].
 */
internal val Project.isInIdeaSync
    get() = providers.of(IsInIdeaSyncValueSource::class.java) {}

/**
 * Returns [true] when Gradle build is invoked in any sort of IDEA environment: sync or task execution
 */
internal val Project.isInIdeaEnvironment
    get() = providers.of(IsInIdeaEnvironmentValueSource::class.java) {}.map { it.value }

private abstract class IsInIdeaSyncValueSource : ValueSource<Boolean, ValueSourceParameters.None> {
    override fun obtain(): Boolean {
        // "idea.sync.active" was introduced in 2019.1
        if (System.getProperty("idea.sync.active")?.toBoolean() == true) return true

        // Before 2019.1 there is "idea.active" that was true only on sync,
        // but since 2019.1 "idea.active" present in task execution too.
        // So let's check the IDEA version
        val majorIdeaVersion = System.getProperty("idea.version")
            ?.split(".")
            ?.getOrNull(0)
        val isBeforeIdea2019 = majorIdeaVersion == null || majorIdeaVersion.toInt() < 2019

        return isBeforeIdea2019 && System.getProperty("idea.active")?.toBoolean() == true
    }
}

private abstract class IsInIdeaEnvironmentValueSource : ConfigurationCacheOpaqueValueSource<Boolean>() {
    override fun obtainValue(): Boolean = System.getProperty("idea.version") != null
}