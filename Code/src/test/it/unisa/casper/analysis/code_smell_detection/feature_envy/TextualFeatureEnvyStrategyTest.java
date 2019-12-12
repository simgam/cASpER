package it.unisa.casper.analysis.code_smell_detection.feature_envy;

import it.unisa.casper.analysis.code_smell.FeatureEnvyCodeSmell;
import it.unisa.casper.analysis.code_smell_detection.Helper.CosineSimilarityStub;
import it.unisa.casper.storage.beans.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextualFeatureEnvyStrategyTest {

    private InstanceVariableBeanList instances;
    private MethodBeanList methods, list;
    private MethodBean metodo, smelly, noSmelly;
    private ClassBean classe, classeE;
    private ClassBeanList classes;
    private PackageBean pack;
    private List<PackageBean> listPackage = new ArrayList<PackageBean>();

    @Before
    public void setUp() {
        String filename = System.getProperty("user.home") + File.separator + ".casper" + File.separator + "stopwordlist.txt";
        File stopwordlist = new File(filename);
        stopwordlist.delete();

        MethodBeanList vuota = new MethodList();
        HashMap<String, ClassBean> nulla = new HashMap<String, ClassBean>();
        classes = new ClassList();
        pack = new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                "   private final String operatore;" +
                "private final String unformattedNumber;\n" +
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
                "   public String getOperatore() {\n" +
                "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                "   }\n" +
                "   public String reverceNumber() {\n" +
                "      return getNumber()+getPrefix()+getAreaCode();\n" +
                "   }\n" +
                "   public String italianNumber() {\n" +
                "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                "   }\n" +
                "   public boolean pushNumber(){\n" +
                "        if(getAreaCode().equals(getPrefix()))\n" +
                "               if(getPrefix().equals(getNumber())){return false;}\n" +
                "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                "                        return true;\n" +
                "                      }\n" +
                "            else{return true;}\n" +
                "        return false;\n" +
                "   }" +
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
                "   public String getMobilePhoneNumber(Phone p) {\n" +
                "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                "    }" +
                "}")
                .setClassList(classes).build();

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("unformattedNumber", "String", "", "private final"));
        instances.getList().add(new InstanceVariableBean("operatore", "String", "", "private final"));
        methods = new MethodList();
        MethodBeanList called = new MethodList();
        classeE = new ClassBean.Builder("feature_envy.package.Phone", "public class Phone {\n" +
                "   private final String operatore;" +
                "private final String unformattedNumber;\n" +
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
                "   public String getOperatore() {\n" +
                "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                "   }\n" +
                "   public String reverceNumber() {\n" +
                "      return getNumber()+getPrefix()+getAreaCode();\n" +
                "   }\n" +
                "   public String italianNumber() {\n" +
                "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                "   }\n" +
                "   public boolean pushNumber(){\n" +
                "        if(getAreaCode().equals(getPrefix()))\n" +
                "               if(getPrefix().equals(getNumber())){return false;}\n" +
                "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                "                        return true;\n" +
                "                      }\n" +
                "            else{return true;}\n" +
                "        return false;\n" +
                "   }" +
                "}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(12)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                        "   private final String operatore;" +
                        "private final String unformattedNumber;\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }" +
                        "}" +
                        "\n" +
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
                        "   public String getMobilePhoneNumber(Phone p) {\n" +
                        "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                        "    }" +
                        "}")
                        .build())
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
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getAreaCode", "return unformattedNumber.substring(0,3);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getPrefix", "return unformattedNumber.substring(3,6);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .build();
        classeE.addMethodBeanList(metodo);
        called.getList().add(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.getOperatore", "return operatore+getAreaCode()+getPrefix();")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(called)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.italianNumber", "return \"39+\"+getNumber()+getPrefix()+getAreaCode();")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(called)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);

        noSmelly = new MethodBean.Builder("feature_envy.package.Phone.getNumber", "return unformattedNumber.substring(6,10);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(noSmelly);
        called.getList().add(noSmelly);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.reverceNumber", "return getNumber()+getPrefix()+getAreaCode();")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(called)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classeE.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Phone.pushNumber", "if(getAreaCode().equals(getPrefix()))\n" +
                "               if(getPrefix().equals(getNumber())){return false;}\n" +
                "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                "                        return true;\n" +
                "                      }\n" +
                "            else{return true;}\n" +
                "        return false;")
                .setReturnType(new ClassBean.Builder("boolean", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(called)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("feature_envy.package.Phone", "private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }").build())
                .setVisibility("public")
                .build();
        classeE.addMethodBeanList(metodo);
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
                "   public String getMobilePhoneNumber(Phone p) {\n" +
                "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                "    }" +
                "}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(15)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("feature_envy.package", "public class Phone {\n" +
                        "   private final String unformattedNumber;\n" +
                        "   private final String operatore;" +
                        "public Phone(String unformattedNumber) {\n" +
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
                        "   public String getOperatore() {\n" +
                        "         return operatore+getAreaCode()+getPrefix()+getNumber();\n" +
                        "   }\n" +
                        "   public String reverceNumber() {\n" +
                        "      return getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public String italianNumber() {\n" +
                        "         return \"39+\"+getNumber()+getPrefix()+getAreaCode();\n" +
                        "   }\n" +
                        "   public boolean pushNumber(){\n" +
                        "        if(getAreaCode().equals(getPrefix()))\n" +
                        "               if(getPrefix().equals(getNumber())){return false;}\n" +
                        "                  else{ unformattedNumber.replace(getNumber(),\"lol\");\n" +
                        "                        return true;\n" +
                        "                      }\n" +
                        "            else{return true;}\n" +
                        "        return false;\n" +
                        "   }" +
                        "}" +
                        "\n" +
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
                        "   public String getMobilePhoneNumber(Phone p) {\n" +
                        "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                        "    }" +
                        "}")
                        .build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\feature_envy\\package")
                .setAffectedSmell()
                .build();

        metodo = new MethodBean.Builder("feature_envy.package.Customer.Customer", "this.name=name;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
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
                        "   public String getMobilePhoneNumber(Phone p) {\n" +
                        "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                        "    }" +
                        "}")
                        .build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("feature_envy.package.Customer.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
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
                        "   public String getMobilePhoneNumber(Phone p) {\n" +
                        "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                        "    }" +
                        "}")
                        .build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        hash = new HashMap<String, ClassBean>();
        hash.put("p", new ClassBean.Builder("Phone", "").build());
        smelly = new MethodBean.Builder("feature_envy.package.Customer.getMobilePhoneNumber", "return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(new InstanceVariableList())
                .setMethodsCalls(called)
                .setParameters(hash)
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
                        "   public String getMobilePhoneNumber(Phone p) {\n" +
                        "      return tel.getAreaCode()+tel.getPrefix()+tel.getNumber();\n" +
                        "    }" +
                        "}")
                        .build())
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
        TextualFeatureEnvyStrategy analisi = new TextualFeatureEnvyStrategy(listPackage, 0.0); //soglia default
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyNearThreshold() {
        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = smelly.getTextContent();
        String[] document2 = new String[2];
        document2[0] = "class";
        document2[1] = smelly.getEnviedClass().getTextContent();

        TextualFeatureEnvyStrategy analisi = new TextualFeatureEnvyStrategy(listPackage, CosineSimilarityStub.computeSimilarity(document1, document2) - 0.1);
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyMinThreshold() {
        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = smelly.getTextContent();
        String[] document2 = new String[2];
        document2[0] = "class";
        document2[1] = smelly.getEnviedClass().getTextContent();

        TextualFeatureEnvyStrategy analisi = new TextualFeatureEnvyStrategy(listPackage, CosineSimilarityStub.computeSimilarity(document1, document2));
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertFalse(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

    @Test
    public void isSmellyFalse() {
        TextualFeatureEnvyStrategy analisi = new TextualFeatureEnvyStrategy(listPackage, 0.0); //soglia default
        FeatureEnvyCodeSmell smell = new FeatureEnvyCodeSmell(analisi, "Textual");
        boolean risultato = noSmelly.isAffected(smell);
        assertFalse(noSmelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

}