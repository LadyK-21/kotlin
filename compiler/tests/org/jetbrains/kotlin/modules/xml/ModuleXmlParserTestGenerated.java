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

package org.jetbrains.kotlin.modules.xml;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/modules.xml")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class ModuleXmlParserTestGenerated extends AbstractModuleXmlParserTest {
    public void testAllFilesPresentInModules_xml() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/modules.xml"), Pattern.compile("^(.+)\\.xml$"), true);
    }

    @TestMetadata("allOnce.xml")
    public void testAllOnce() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/allOnce.xml");
        doTest(fileName);
    }

    @TestMetadata("comments.xml")
    public void testComments() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/comments.xml");
        doTest(fileName);
    }

    @TestMetadata("empty.xml")
    public void testEmpty() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/empty.xml");
        doTest(fileName);
    }

    @TestMetadata("emptyModule.xml")
    public void testEmptyModule() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/emptyModule.xml");
        doTest(fileName);
    }

    @TestMetadata("manyTimes.xml")
    public void testManyTimes() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/manyTimes.xml");
        doTest(fileName);
    }

    @TestMetadata("onlySources.xml")
    public void testOnlySources() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/onlySources.xml");
        doTest(fileName);
    }

    @TestMetadata("twoModules.xml")
    public void testTwoModules() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/twoModules.xml");
        doTest(fileName);
    }

    @TestMetadata("typeTestModule.xml")
    public void testTypeTestModule() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("compiler/testData/modules.xml/typeTestModule.xml");
        doTest(fileName);
    }
}
