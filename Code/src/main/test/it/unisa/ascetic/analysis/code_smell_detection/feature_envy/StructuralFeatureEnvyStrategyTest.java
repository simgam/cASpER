package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;

import it.unisa.ascetic.storage.beans.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class StructuralFeatureEnvyStrategyTest {

    private InstanceVariableBeanList instances;
    private MethodBeanList methods, list;
    private MethodBean metodo, smelly, noSmelly;
    private ClassBean classe;
    private ClassBeanList classes;
    private PackageBean pack;

    @Before
    public void setUp() throws Exception {
        classes = new ClassList();
        pack = new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
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
                "   public String getMobilePhoneNumber() {\n" +
                "      Phone tel=new Phone(3333333333);\n" +
                "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                "      tel=new Phone(4444444444);\n" +
                "      s+=tel.getPrefix() + \"-\" ;\n" +
                "      tel=new Phone(5555555555);\n" +
                "      s+=tel.getNumber();\n" +
                "      tel=new Phone(6666666666);\n" +
                "      return tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber();\n" +
                "   }\n" +
                "}").setClassList(classes).build();

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("unformattedNumber", "String", "", "private final"));
        methods = new MethodList();
        MethodBeanList called = new MethodList();
        classe = new ClassBean.Builder("feature_envy.package.Phone", "public class Phone {\n" +
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
                .setImports(null)
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
                        "   public String getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber();\n" +
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
                .setReturnType(null)
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
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
        classe.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getAreaCode", "return unformattedNumber.substring(0,3);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(null)
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
        classe.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getPrefix", "return unformattedNumber.substring(3,6);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(null)
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
        classe.addMethodBeanList(metodo);
        called.getList().add(metodo);

        noSmelly = new MethodBean.Builder("feature_envy.package.Phone.getNumber", "return unformattedNumber.substring(6,10);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(null)
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
        classe.addMethodBeanList(noSmelly);
        called.getList().add(noSmelly);
        pack.addClassList(classe);

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
                "   public String getMobilePhoneNumber() {\n" +
                "      Phone tel=new Phone(3333333333);\n" +
                "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                "      tel=new Phone(4444444444);\n" +
                "      s+=tel.getPrefix() + \"-\" ;\n" +
                "      tel=new Phone(5555555555);\n" +
                "      s+=tel.getNumber();\n" +
                "      tel=new Phone(6666666666);\n" +
                "      return tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber();\n" +
                "   }")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(null)
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
                        "   public String getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber();\n" +
                        "   }\n" +
                        "}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\feature_envy\\package")
                .setAffectedSmell()
                .build();

        metodo = new MethodBean.Builder("feature_envy.package.Customer.Customer", "this.name=name;")
                .setReturnType(null)
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(null)
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
                        "   public String getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber();\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Customer.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(null)
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
                        "   public String getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber();\n" +
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
                "      return tel.getAreaCode() + \") \" +\n" +
                "             tel.getPrefix() + \"-\" +\n" +
                "             tel.getNumber();")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(null)
                .setMethodsCalls(called)
                .setParameters(null)
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
                        "   public String getMobilePhoneNumber() {\n" +
                        "      Phone tel=new Phone(3333333333);\n" +
                        "      string s=\"(\" + tel.getAreaCode()+ \") \" ;\n" +
                        "      tel=new Phone(4444444444);\n" +
                        "      s+=tel.getPrefix() + \"-\" ;\n" +
                        "      tel=new Phone(5555555555);\n" +
                        "      s+=tel.getNumber();\n" +
                        "      tel=new Phone(6666666666);\n" +
                        "      return tel.getAreaCode() + \") \" +\n" +
                        "             tel.getPrefix() + \"-\" +\n" +
                        "             tel.getNumber();\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();

        classe.addMethodBeanList(smelly);
        pack.addClassList(classe);

    }

    @Test
    public void isSmellyTrue() {

        List<PackageBean> list = new ArrayList<PackageBean>();
        list.add(pack);
        StructuralFeatureEnvyStrategy analisi = new StructuralFeatureEnvyStrategy(list,0);
        it.unisa.ascetic.analysis.code_smell.FeatureEnvyCodeSmell smell = new it.unisa.ascetic.analysis.code_smell.FeatureEnvyCodeSmell(analisi,"Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

}