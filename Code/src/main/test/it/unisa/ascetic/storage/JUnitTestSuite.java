package it.unisa.ascetic.storage;

import it.unisa.ascetic.analysis.code_smell_detection.blob.TextualBlobStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.StructuralFeatureEnvyStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.StructuralMisplacedClassStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategyTest;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategyTest;
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
        TextualPromiscuousPackageStrategyTest.class
})

public class JUnitTestSuite {
}
