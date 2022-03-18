package it.unisa.casper.analysis.code_smell_detection.promiscuous_package;

import it.unisa.casper.analysis.code_smell.PromiscuousPackageCodeSmell;
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

public class StructuralPromiscuousPackageStrategyTest {

    private List<PackageBean> systemPackage = new ArrayList<PackageBean>();
    private MethodBeanList methods;
    private MethodBean metodo;
    private ClassBean classe;
    private PackageBean smelly, noSmelly;

    @Before
    public void setUp() {
        InstanceVariableBeanList instances;
        MethodBeanList vuota = new MethodList();
        HashMap<String, ClassBean> nulla = new HashMap<String, ClassBean>();

        noSmelly = new PackageBean.Builder("promiscuous_package.package2", "public class BankAccount {\n" +
                "\n" +
                "    private double balance;\n" +
                "\n" +
                "    public BankAccount(double balance) {\n" +
                "        this.balance = balance;\n" +
                "    }\n" +
                "\n" +
                "    public double getBalance() {\n" +
                "        return balance;\n" +
                "    }\n" +
                "\n" +
                "}")
                .setClassList(new ClassList())
                .build();

        String testo = "public class Ristorante {\n" +
                "\n" +
                "\tpublic String nome_Ristorante;\n" +
                "\n" +
                "\tpublic Ristorante(String nome_Ristorante) {\n" +
                "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getNome_Ristorante() {\n" +
                "\t\treturn nome_Ristorante;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setNome_Ristorante(String nome_Ristorante) {\n" +
                "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                "\t}\n" +
                "\n" +
                "}" +
                "public class Phone {\n" +
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
                "}" +
                "public class Cliente {\n" +
                "\n" +
                "\tprivate String name;\n" +
                "\tprivate int età;\n" +
                "\n" +
                "\tpublic Cliente(String name, int età) {\n" +
                "\t\tthis.name = name;\n" +
                "\t\tthis.età = età;\n" +
                "\t}\n" +
                "\tpublic String getName() {\n" +
                "\t\treturn name;\n" +
                "\t}\n" +
                "\tpublic int getEtà() {\n" +
                "\t\treturn età;\n" +
                "\t}\n" +
                "\t\n" +
                "}";
        smelly = new PackageBean.Builder("promiscuous_package.package", testo)
                .setClassList(new ClassList())
                .build();

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("età", "int", "", "private "));
        methods = new MethodList();
        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());
        hash.put("età", new ClassBean.Builder("int", "").build());

        classe = new ClassBean.Builder("promiscuous_package.package.Cliente", "private String name;\n" +
                "\tprivate int età;\n" +
                "\n" +
                "\tpublic Cliente(String name, int età) {\n" +
                "\t\tthis.name = name;\n" +
                "\t\tthis.età = età;\n" +
                "\t}\n" +
                "\tpublic String getName() {\n" +
                "\t\treturn name;\n" +
                "\t}\n" +
                "\tpublic int getEtà() {\n" +
                "\t\treturn età;\n" +
                "\t}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(12)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("promiscuous_package.package", testo).build())
                .setEnviedPackage(null)
                .setEntityClassUsage(2)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\promiscuous_package\\package")
                .setAffectedSmell()
                .build();

        metodo = new MethodBean.Builder("promiscuous_package.package.Cliente.Cliente", "this.name = name;\n" +
                "\t\tthis.età = età;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Cliente", "private String name;\n" +
                        "\tprivate int età;\n" +
                        "\n" +
                        "\tpublic Cliente(String name, int età) {\n" +
                        "\t\tthis.name = name;\n" +
                        "\t\tthis.età = età;\n" +
                        "\t}\n" +
                        "\tpublic String getName() {\n" +
                        "\t\treturn name;\n" +
                        "\t}\n" +
                        "\tpublic int getEtà() {\n" +
                        "\t\treturn età;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        instances.getList().remove(new InstanceVariableBean("età", "int", "", "private "));
        metodo = new MethodBean.Builder("promiscuous_package.package.Cliente.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Cliente", "private String name;\n" +
                        "\tprivate int età;\n" +
                        "\n" +
                        "\tpublic Cliente(String name, int età) {\n" +
                        "\t\tthis.name = name;\n" +
                        "\t\tthis.età = età;\n" +
                        "\t}\n" +
                        "\tpublic String getName() {\n" +
                        "\t\treturn name;\n" +
                        "\t}\n" +
                        "\tpublic int getEtà() {\n" +
                        "\t\treturn età;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        instances.getList().remove(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("età", "int", "", "private "));
        metodo = new MethodBean.Builder("promiscuous_package.package.Cliente.getEtà", "return età;")
                .setReturnType(new ClassBean.Builder("int", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Cliente", "private String name;\n" +
                        "\tprivate int età;\n" +
                        "\n" +
                        "\tpublic Cliente(String name, int età) {\n" +
                        "\t\tthis.name = name;\n" +
                        "\t\tthis.età = età;\n" +
                        "\t}\n" +
                        "\tpublic String getName() {\n" +
                        "\t\treturn name;\n" +
                        "\t}\n" +
                        "\tpublic int getEtà() {\n" +
                        "\t\treturn età;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);
        smelly.addClassList(classe);

        methods = new MethodList();
        classe = new ClassBean.Builder("promiscuous_package.package.Phone", "private final String unformattedNumber;\n" +
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
                "   }")
                .setInstanceVariables(new InstanceVariableList())
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(11)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("promiscuous_package.package", testo).build())
                .setEnviedPackage(null)
                .setEntityClassUsage(3)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\promiscuous_package\\package")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("unformattedNumber", new ClassBean.Builder("String", "").build());
        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("unformattedNumber", "String", "", "private"));
        metodo = new MethodBean.Builder("promiscuous_package.package.Phone.Phone", "this.unformattedNumber = unformattedNumber;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Phone", "private final String unformattedNumber;\n" +
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

        metodo = new MethodBean.Builder("promiscuous_package.package.Phone.getAreaCode", "return unformattedNumber.substring(0,3);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Phone", "private final String unformattedNumber;\n" +
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

        metodo = new MethodBean.Builder("promiscuous_package.package.Phone.getPrefix", "return unformattedNumber.substring(3,6);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Phone", "private final String unformattedNumber;\n" +
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

        metodo = new MethodBean.Builder("promiscuous_package.package.Phone.getNumber", "return unformattedNumber.substring(6,10);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Phone", "private final String unformattedNumber;\n" +
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
        smelly.addClassList(classe);

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("nome_Ristorante", "String", "", "private "));
        methods = new MethodList();
        classe = new ClassBean.Builder("promiscuous_package.package.Ristorante", "public String nome_Ristorante;\n" +
                "\n" +
                "\tpublic Ristorante(String nome_Ristorante) {\n" +
                "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getNome_Ristorante() {\n" +
                "\t\treturn nome_Ristorante;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setNome_Ristorante(String nome_Ristorante) {\n" +
                "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                "\t}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(12)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("promiscuous_package.package", testo).build())
                .setEnviedPackage(null)
                .setEntityClassUsage(2)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\promiscuous_package\\package\\")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("nome_Ristorante", new ClassBean.Builder("String", "").build());
        metodo = new MethodBean.Builder("promiscuous_package.package.Ristorante.Ristorante", "this.nome_Ristorante = nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Ristorante", "public String nome_Ristorante;\n" +
                        "\n" +
                        "\tpublic Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic String getNome_Ristorante() {\n" +
                        "\t\treturn nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic void setNome_Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("promiscuous_package.package.Ristorante.getNome_Ristorante", "return nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Ristorante", "public String nome_Ristorante;\n" +
                        "\n" +
                        "\tpublic Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic String getNome_Ristorante() {\n" +
                        "\t\treturn nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic void setNome_Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        instances.getList().remove(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("età", "int", "", "private "));
        metodo = new MethodBean.Builder("promiscuous_package.package.Ristorante.setNome_Ristorante", "this.nome_Ristorante = nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.Ristorante", "public String nome_Ristorante;\n" +
                        "\n" +
                        "\tpublic Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic String getNome_Ristorante() {\n" +
                        "\t\treturn nome_Ristorante;\n" +
                        "\t}\n" +
                        "\n" +
                        "\tpublic void setNome_Ristorante(String nome_Ristorante) {\n" +
                        "\t\tthis.nome_Ristorante = nome_Ristorante;\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);
        smelly.addClassList(classe);

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("balance", "double", "", "private "));
        methods = new MethodList();
        classe = new ClassBean.Builder("promiscuous_package.package.BankAccount", "private double balance;\n" +
                "\n" +
                "    public BankAccount(double balance) {\n" +
                "        this.balance = balance;\n" +
                "    }\n" +
                "\n" +
                "    public double getBalance() {\n" +
                "        return balance;\n" +
                "    }\n")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(9)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("promiscuous_package.package", testo).build())
                .setEnviedPackage(null)
                .setEntityClassUsage(1)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\promiscuous_package\\package\\")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("nome_Ristorante", new ClassBean.Builder("String", "").build());
        metodo = new MethodBean.Builder("promiscuous_package.package.BankAccount.BankAccount", "this.balance = balance;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.BankAccount", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }\n").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("promiscuous_package.package.BankAccount.getBalance", "return balance;")
                .setReturnType(new ClassBean.Builder("double", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package.BankAccount", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }\n").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("balance", "double", "", "private "));
        methods = new MethodList();
        classe = new ClassBean.Builder("promiscuous_package.package2.BankAccount", "private double balance;\n" +
                "\n" +
                "    public BankAccount(double balance) {\n" +
                "        this.balance = balance;\n" +
                "    }\n" +
                "\n" +
                "    public double getBalance() {\n" +
                "        return balance;\n" +
                "    }\n")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(9)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("promiscuous_package.package2", "public class BankAccount {\n" +
                        "\n" +
                        "    private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }\n" +
                        "\n" +
                        "}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(1)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\promiscuous_package\\package2\\")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("nome_Ristorante", new ClassBean.Builder("String", "").build());
        metodo = new MethodBean.Builder("promiscuous_package.package2.BankAccount.BankAccount", "this.balance = balance;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package2.BankAccount", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }\n").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("promiscuous_package.package2.BankAccount.getBalance", "return balance;")
                .setReturnType(new ClassBean.Builder("double", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("promiscuous_package.package2.BankAccount", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }\n").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);
        noSmelly.addClassList(classe);
        systemPackage.add(noSmelly);
        systemPackage.add(smelly);
    }

    @Test
    public void isSmellyTrue() {
        StructuralPromiscuousPackageStrategy analisi = new StructuralPromiscuousPackageStrategy(systemPackage, 0.5, 0.5); // soglie default
        PromiscuousPackageCodeSmell smell = new PromiscuousPackageCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyNearThreshold() {
        StructuralPromiscuousPackageStrategy analisi = new StructuralPromiscuousPackageStrategy(systemPackage, CKMetricsStub.computeMediumIntraConnectivity(smelly), CKMetricsStub.computeMediumInterConnectivity(smelly, systemPackage) - 0.1);
        PromiscuousPackageCodeSmell smell = new PromiscuousPackageCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyMinThreshold() {
        StructuralPromiscuousPackageStrategy analisi = new StructuralPromiscuousPackageStrategy(systemPackage, CKMetricsStub.computeMediumIntraConnectivity(smelly), CKMetricsStub.computeMediumInterConnectivity(smelly, systemPackage));
        PromiscuousPackageCodeSmell smell = new PromiscuousPackageCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertFalse(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

    @Test
    public void isSmellyFalse() {
        StructuralPromiscuousPackageStrategy analisi = new StructuralPromiscuousPackageStrategy(systemPackage, 0.5, 0.5); // soglie default
        PromiscuousPackageCodeSmell smell = new PromiscuousPackageCodeSmell(analisi, "Structural");
        boolean risultato = noSmelly.isAffected(smell);
        assertFalse(noSmelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

}