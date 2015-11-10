/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.test

import com.intellij.ide.startup.impl.StartupManagerImpl
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.actions.internal.KotlinInternalMode
import org.jetbrains.kotlin.idea.references.BuiltInsReferenceResolver
import org.jetbrains.kotlin.test.KotlinTestUtils

public abstract class KotlinLightPlatformCodeInsightFixtureTestCase: LightPlatformCodeInsightFixtureTestCase() {
    private var kotlinInternalModeOriginalValue: Boolean = false

    override fun setUp() {
        super.setUp()
        (StartupManager.getInstance(getProject()) as StartupManagerImpl).runPostStartupActivities()
        VfsRootAccess.allowRootAccess(KotlinTestUtils.getHomeDirectory())
        invalidateLibraryCache(project)

        kotlinInternalModeOriginalValue = KotlinInternalMode.enabled
        KotlinInternalMode.enabled = true
    }

    override fun tearDown() {
        KotlinInternalMode.enabled = kotlinInternalModeOriginalValue
        VfsRootAccess.disallowRootAccess(KotlinTestUtils.getHomeDirectory())

        val builtInsSources = BuiltInsReferenceResolver.getInstance(getProject()).builtInsSources!!
        val fileManager = (PsiManager.getInstance(getProject()) as PsiManagerEx).getFileManager()

        super.tearDown()

        // Restore mapping between PsiFiles and VirtualFiles dropped in FileManager.cleanupForNextTest(),
        // otherwise built-ins psi elements will become invalid in next test.
        for (source in builtInsSources) {
            val provider = source.getViewProvider()
            fileManager.setViewProvider(provider.getVirtualFile(), provider)
        }
    }
}