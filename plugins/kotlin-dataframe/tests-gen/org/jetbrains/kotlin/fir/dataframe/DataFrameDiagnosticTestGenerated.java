

package org.jetbrains.kotlin.fir.dataframe;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.fir.dataframe.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("testData/diagnostics")
@TestDataPath("$PROJECT_ROOT")
public class DataFrameDiagnosticTestGenerated extends AbstractDataFrameDiagnosticTest {
  @Test
  public void testAllFilesPresentInDiagnostics() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("testData/diagnostics"), Pattern.compile("^(.+)\\.kt$"), null, true);
  }

  @Test
  @TestMetadata("disableInterpretation.kt")
  public void testDisableInterpretation() {
    runTest("testData/diagnostics/disableInterpretation.kt");
  }

  @Test
  @TestMetadata("HistoryItem.kt")
  public void testHistoryItem() {
    runTest("testData/diagnostics/HistoryItem.kt");
  }

  @Test
  @TestMetadata("renameToCamelCase.kt")
  public void testRenameToCamelCase() {
    runTest("testData/diagnostics/renameToCamelCase.kt");
  }

  @Test
  @TestMetadata("selectDuringTyping.kt")
  public void testSelectDuringTyping() {
    runTest("testData/diagnostics/selectDuringTyping.kt");
  }

  @Test
  @TestMetadata("structuralCast.kt")
  public void testStructuralCast() {
    runTest("testData/diagnostics/structuralCast.kt");
  }

  @Test
  @TestMetadata("targetOfCastIsNotDataSchema.kt")
  public void testTargetOfCastIsNotDataSchema() {
    runTest("testData/diagnostics/targetOfCastIsNotDataSchema.kt");
  }

  @Test
  @TestMetadata("toDataFrame_java.kt")
  public void testToDataFrame_java() {
    runTest("testData/diagnostics/toDataFrame_java.kt");
  }
}
