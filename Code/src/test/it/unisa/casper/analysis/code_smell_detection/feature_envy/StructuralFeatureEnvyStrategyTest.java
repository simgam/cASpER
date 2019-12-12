package it.unisa.casper.analysis.code_smell_detection.feature_envy;

import it.unisa.casper.analysis.code_smell.FeatureEnvyCodeSmell;
import it.unisa.casper.analysis.code_smell_detection.Helper.CKMetricsStub;
import it.unisa.casper.storage.beans.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StructuralFeatureEnvyStrategyTest {

    private InstanceVariableBeanList instances;
    private MethodBeanList methods;
    private MethodBean metodo, smelly, noSmelly;
    private ClassBean classe, classeE;
    private ClassBeanList classes;
    private PackageBean pack;
    private List<PackageBean> listPackage = new ArrayList<PackageBean>();

    @Before
    public void setUp() {
        classes = new ClassList();
        pack = new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                "   private final String unformattedNumber;\n" +
                "   }\n" +
                "   public Phone(String unformattedNumber) {\n" +
                "      this.unformattedNumber = unformattedNumber;\n" +
                "   public String getAreaCode() {\n" +
                "      return unformattedNumber.substring(0,3);\n" +
                "   }\n" +
                "   public String getPrefix() {\n" +
                "      return unformattedNumber.substring(3,6);\n" +
                "   }\n" +
                "   public String getNumber() {\n" +
                "      return unformattedNumber.substring(6,10);\n" +
                "   }\n" +
                "}\n" +
                "public class Customer{\n" +
                "\n" +
                "   private String name;\n" +
                "\n" +
                "   public Customer(String name)\n" +
                "   {    this.name=name;\n" +
                "   }\n" +
                "\n" +
                "   public String getName()\n" +
                "   {    return name;\n" +
                "   }\n" +
                "\n" +
                "   public Phone getMobilePhoneNumber() {\n" +
                "      Phone tel=new Phone(3333333333);\n" +
                "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                "      tel=new Phone(4444444444);\n" +
                "      s+=tel.getPrefix() + \"-\" ;\n" +
                "      tel=new Phone(5555555555);\n" +
                "      s+=tel.getNumber();\n" +
                "      tel=new Phone(6666666666);\n" +
                "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber());\n" +
                "   }\n" +
                "}").setClassList(classes).build();

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("unformattedNumber", "String", "", "private final"));
        methods = new MethodList();
        MethodBeanList called = new MethodList();
        classeE = new ClassBean.Builder("feature_envy.package.Phone", "public class Phone {\n" +
                "   private final String unformattedNumber;\n" +
                "   public Phone(String unformattedNumber) {\n" +
                "      this.unformattedNumber = unformattedNumber;\n" +
                "   }\n" +
                "   public String getAreaCode() {\n" +
                "      return unformattedNumber.substring(0,3);\n" +
                "   }\n" +
                "   public String getPrefix() {\n" +
                "      return unformattedNumber.substring(3,6);\n" +
                "   }\n" +
                "   public String getNumber() {\n" +
                "      return unformattedNumber.substring(6,10);\n" +
                "   }\n" +
                "}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(12)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                        "   private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }\n" +
                        "}\n" +
                        "public class Customer{\n" +
                        "\n" +
                        "   private String name;\n" +
                        "\n" +
                        "   public Customer(String name)\n" +
                        "   {    this.name=name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public String getName()\n" +
                        "   {    return name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public Phone getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return new Phonete(l.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber());\n" +
                        "   }\n" +
                        "}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(10)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\feature_envy\\package")
                .setAffectedSmell()
                .build();

        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("unformattedNumber", new ClassBean.Builder("String", "").build());

        metodo = new MethodBean.Builder("feature_envy.package.Phone.Phone", "this.unformattedNumber = unformattedNumber;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(new MethodList())
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getAreaCode", "return unformattedNumber.substring(0,3);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(new MethodList())
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getPrefix", "return unformattedNumber.substring(3,6);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(new MethodList())
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);
        called.getList().add(metodo);

        noSmelly = new MethodBean.Builder("feature_envy.package.Phone.getNumber", "return unformattedNumber.substring(6,10);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(new MethodList())
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(noSmelly);
        called.getList().add(noSmelly);
        pack.addClassList(classeE);

        instances = new InstanceVariableList();
        methods = new MethodList();
        instances.getList().add(new InstanceVariableBean("name", "String", "", "private"));
        classe = new ClassBean.Builder("feature_envy.package.Customer", "private String name;\n" +
                "\n" +
                "   public Customer(String name)\n" +
                "   {    this.name=name;\n" +
                "   }\n" +
                "\n" +
                "   public String getName()\n" +
                "   {    return name;\n" +
                "   }\n" +
                "\n" +
                "   public Phone getMobilePhoneNumber() {\n" +
                "      Phone tel=new Phone(3333333333);\n" +
                "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                "      tel=new Phone(4444444444);\n" +
                "      s+=tel.getPrefix() + \"-\" ;\n" +
                "      tel=new Phone(5555555555);\n" +
                "      s+=tel.getNumber();\n" +
                "      tel=new Phone(6666666666);\n" +
                "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber());\n" +
                "   }")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(15)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                        "   private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }\n" +
                        "}\n" +
                        "public class Customer{\n" +
                        "\n" +
                        "   private String name;\n" +
                        "\n" +
                        "   public Customer(String name)\n" +
                        "   {    this.name=name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public String getName()\n" +
                        "   {    return name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public Phone getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber());\n" +
                        "   }\n" +
                        "}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\feature_envy\\package")
                .setAffectedSmell()
                .build();

        metodo = new MethodBean.Builder("feature_envy.package.Customer.Customer", "this.name=name;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(new MethodList())
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Customer", "private String name;\n" +
                        "\n" +
                        "   public Customer(String name)\n" +
                        "   {    this.name=name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public String getName()\n" +
                        "   {    return name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public Phone getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber());\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Customer.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Customer", "private String name;\n" +
                        "\n" +
                        "   public Customer(String name)\n" +
                        "   {    this.name=name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public String getName()\n" +
                        "   {    return name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public Phone getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber());\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        smelly = new MethodBean.Builder("feature_envy.package.Customer.getMobilePhoneNumber", "Phone tel=new Phone(3333333333);\n" +
                "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                "      tel=new Phone(4444444444);\n" +
                "      s+=tel.getPrefix() + \"-\" ;\n" +
                "      tel=new Phone(5555555555);\n" +
                "      s+=tel.getNumber();\n" +
                "      tel=new Phone(6666666666);\n" +
                "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber());")
                .setReturnType(new ClassBean.Builder("Phone", "public class Phone {\n" +
                        "   private final String unformattedNumber;\n" +
                        "   public Phone(String unformattedNumber) {\n" +
                        "      this.unformattedNumber = unformattedNumber;\n" +
                        "   }\n" +
                        "   public String getAreaCode() {\n" +
                        "      return unformattedNumber.substring(0,3);\n" +
                        "   }\n" +
                        "   public String getPrefix() {\n" +
                        "      return unformattedNumber.substring(3,6);\n" +
                        "   }\n" +
                        "   public String getNumber() {\n" +
                        "      return unformattedNumber.substring(6,10);\n" +
                        "   }\n" +
                        "}").build())
                .setInstanceVariableList(null)
                .setMethodsCalls(called)
                .setParameters(new HashMap<String, ClassBean>())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Customer", "private String name;\n" +
                        "\n" +
                        "   public Customer(String name)\n" +
                        "   {    this.name=name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public String getName()\n" +
                        "   {    return name;\n" +
                        "   }\n" +
                        "\n" +
                        "   public Phone getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return new Phone(tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber());\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .setEnviedClass(classeE)
                .build();

        classe.addMethodBeanList(smelly);
        pack.addClassList(classe);
        listPackage.add(pack);
    }

    @Test
    public void isSmellyTrue() {
        StructuralFeatureEnvyStrategy analisi = new StructuralFeatureEnvyStrategy(listPackage, 0); //soglia default
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyNearThreshold() {
        StructuralFeatureEnvyStrategy analisi = new StructuralFeatureEnvyStrategy(listPackage, (int) CKMetricsStub.getNumberOfDependencies(smelly, smelly.getEnviedClass()) - 1);
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyMinThreshold() {
        StructuralFeatureEnvyStrategy analisi = new StructuralFeatureEnvyStrategy(listPackage, (int) CKMetricsStub.getNumberOfDependencies(smelly, smelly.getEnviedClass()));
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertFalse(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

    @Test
    public void isSmellyFalse() {
        StructuralFeatureEnvyStrategy analisi = new StructuralFeatureEnvyStrategy(listPackage, 0); //soglia default
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Structural");
        boolean risultato = noSmelly.isAffected(smell);
        assertFalse(noSmelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

}