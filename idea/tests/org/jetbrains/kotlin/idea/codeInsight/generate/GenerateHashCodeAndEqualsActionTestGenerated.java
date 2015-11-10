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

package org.jetbrains.kotlin.idea.codeInsight.generate;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/codeInsight/generate/equalsWithHashCode")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class GenerateHashCodeAndEqualsActionTestGenerated extends AbstractGenerateHashCodeAndEqualsActionTest {
    public void testAllFilesPresentInEqualsWithHashCode() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/codeInsight/generate/equalsWithHashCode"), Pattern.compile("^(.+)\\.kt$"), true);
    }

    @TestMetadata("annotation.kt")
    public void testAnnotation() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/annotation.kt");
        doTest(fileName);
    }

    @TestMetadata("dataClass.kt")
    public void testDataClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/dataClass.kt");
        doTest(fileName);
    }

    @TestMetadata("enum.kt")
    public void testEnum() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/enum.kt");
        doTest(fileName);
    }

    @TestMetadata("genericClass.kt")
    public void testGenericClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/genericClass.kt");
        doTest(fileName);
    }

    @TestMetadata("genericClassWithIsCheck.kt")
    public void testGenericClassWithIsCheck() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/genericClassWithIsCheck.kt");
        doTest(fileName);
    }

    @TestMetadata("interface.kt")
    public void testInterface() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/interface.kt");
        doTest(fileName);
    }

    @TestMetadata("multipleVars.kt")
    public void testMultipleVars() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/multipleVars.kt");
        doTest(fileName);
    }

    @TestMetadata("multipleVarsNullable.kt")
    public void testMultipleVarsNullable() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/multipleVarsNullable.kt");
        doTest(fileName);
    }

    @TestMetadata("multipleVarsWithSuperClass.kt")
    public void testMultipleVarsWithSuperClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/multipleVarsWithSuperClass.kt");
        doTest(fileName);
    }

    @TestMetadata("nameClash.kt")
    public void testNameClash() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/nameClash.kt");
        doTest(fileName);
    }

    @TestMetadata("noVars.kt")
    public void testNoVars() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/noVars.kt");
        doTest(fileName);
    }

    @TestMetadata("noVarsForced.kt")
    public void testNoVarsForced() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/noVarsForced.kt");
        doTest(fileName);
    }

    @TestMetadata("noVarsForcedWithSuperClass.kt")
    public void testNoVarsForcedWithSuperClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/noVarsForcedWithSuperClass.kt");
        doTest(fileName);
    }

    @TestMetadata("object.kt")
    public void testObject() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/object.kt");
        doTest(fileName);
    }

    @TestMetadata("singleVar.kt")
    public void testSingleVar() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/singleVar.kt");
        doTest(fileName);
    }

    @TestMetadata("singleVarNullable.kt")
    public void testSingleVarNullable() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/singleVarNullable.kt");
        doTest(fileName);
    }

    @TestMetadata("singleVarWithIsCheck.kt")
    public void testSingleVarWithIsCheck() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/singleVarWithIsCheck.kt");
        doTest(fileName);
    }

    @TestMetadata("singleVarWithJavaSuperClass.kt")
    public void testSingleVarWithJavaSuperClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/singleVarWithJavaSuperClass.kt");
        doTest(fileName);
    }

    @TestMetadata("singleVarWithSuperClass.kt")
    public void testSingleVarWithSuperClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/testData/codeInsight/generate/equalsWithHashCode/singleVarWithSuperClass.kt");
        doTest(fileName);
    }
}
