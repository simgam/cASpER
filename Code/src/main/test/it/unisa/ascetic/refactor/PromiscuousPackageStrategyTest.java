package it.unisa.ascetic.refactor;

import com.intellij.openapi.application.PathManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import it.unisa.ascetic.refactor.manipulator.PromiscuousPackageRefactoringStrategy;

import java.io.File;

public class PromiscuousPackageStrategyTest extends LightCodeInsightFixtureTestCase {//NON SO PERCHE' NON ESISTE LA CLASSE DA ESTENDERE

    @Override
    protected String getTestDataPath() {
        return new File(getSourceRoot(), "testData").getPath();
    }

    private static File getSourceRoot() {
        String testOutput = PathManager.getJarPathForClass(PromiscuousPackageRefactoringStrategy.class);
        return new File(testOutput, "../../../..");
    }

    public void testExtractClass() {
        myFixture.configureByFile("before" + getTestName(false) + ".java");


        /*
            FACCIAMO ROBA PER RICAVARCI I PARAMETRI DA DARE AL METODO DA TESTARE
         */

        myFixture.checkResultByFile("after" + getTestName(false) + ".java");
    }
}
