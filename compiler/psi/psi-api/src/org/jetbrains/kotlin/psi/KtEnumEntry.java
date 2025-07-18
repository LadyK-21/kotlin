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

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtStubBasedElementTypes;
import org.jetbrains.kotlin.name.ClassId;
import org.jetbrains.kotlin.psi.psiUtil.KtPsiUtilKt;
import org.jetbrains.kotlin.psi.stubs.KotlinClassStub;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KtEnumEntry extends KtClass {
    public KtEnumEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtEnumEntry(@NotNull KotlinClassStub stub) {
        super(stub, KtStubBasedElementTypes.ENUM_ENTRY);
    }

    @NotNull
    @Override
    public List<KtSuperTypeListEntry> getSuperTypeListEntries() {
        KtInitializerList initializerList = getInitializerList();
        if (initializerList == null) {
            return Collections.emptyList();
        }
        return initializerList.getInitializers();
    }

    public boolean hasInitializer() {
        return !getSuperTypeListEntries().isEmpty();
    }

    @Nullable
    @Override
    public ClassId getClassId() {
        return null;
    }

    @Nullable
    public KtInitializerList getInitializerList() {
        return getStubOrPsiChild(KtStubBasedElementTypes.INITIALIZER_LIST);
    }

    @Override
    public boolean isEquivalentTo(@Nullable PsiElement another) {
        if (this == another) return true;
        if (!(another instanceof KtEnumEntry)) return false;
        KtEnumEntry anotherEnumEntry = (KtEnumEntry) another;

        if (!Objects.equals(getName(), anotherEnumEntry.getName())) return false;

        KtClassOrObject thisContainingClass = KtPsiUtilKt.getContainingClassOrObject(this);
        if (thisContainingClass == null) return false;

        KtClassOrObject anotherContainingClass = KtPsiUtilKt.getContainingClassOrObject(anotherEnumEntry);
        return thisContainingClass.isEquivalentTo(anotherContainingClass);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitEnumEntry(this, data);
    }
}
