package it.unisa.ascetic;

import it.unisa.ascetic.analysis.code_smell_detection.BeanDetectionTest;
import it.unisa.ascetic.analysis.code_smell_detection.blob.StructuralBlobStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.blob.TextualBlobStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.StructuralFeatureEnvyStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.StructuralMisplacedClassStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.StructuralPromiscuousPackageStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategyTest;
import it.unisa.ascetic.refactoring.SplitClassTest;
import it.unisa.ascetic.refactoring.SplitPackagesTest;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.repository.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnectorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({SQLiteConnectorTest.class,
        DBCreationTest.class,
        InstanceVariableListProxyTest.class,
        UsedInstanceVariableListProxyTest.class,
        CalledMethodsListProxyTest.class,
        MethodListProxyTest.class,
        ClassListProxyTest.class,
        InstanceVariableRepositoryTest.class,
        MethodRepositoryTest.class,
        ClassRepositoryTest.class,
        PackageRepositoryTest.class,
        TextualFeatureEnvyStrategyTest.class,
        StructuralFeatureEnvyStrategyTest.class,
        TextualMisplacedClassStrategyTest.class,
        StructuralMisplacedClassStrategyTest.class,
        TextualBlobStrategyTest.class,
        StructuralBlobStrategyTest.class,
        TextualPromiscuousPackageStrategyTest.class,
        StructuralPromiscuousPackageStrategyTest.class,
        SplitPackagesTest.class,
        SplitClassTest.class,
        BeanDetectionTest.class
})

public class JUnitTestSuite {
}
