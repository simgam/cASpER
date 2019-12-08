package it.unisa.casper;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {

    /**
     * Metodo main.
     *
     * @param args array riga di comando
     */
    public static void main(String[] args) {

        Result result = (Result) JUnitCore.runClasses(JUnitTestSuite.class);

        for (Failure failure : ((org.junit.runner.Result) result).getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(((org.junit.runner.Result) result).wasSuccessful());
    }

}
