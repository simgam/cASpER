package it.unisa.casper.refactor;

import it.unisa.casper.refactor.splitting_algorithm.GameTheorySplitClasses;
import it.unisa.casper.refactor.splitting_algorithm.MethodByMethodMatrixConstruction;
import it.unisa.casper.refactor.splitting_algorithm.PayoffPair;
import it.unisa.casper.refactor.splitting_algorithm.SplitClasses;
import it.unisa.casper.storage.beans.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.util.logging.Logger;
import static org.junit.Assert.*;


public class GameTheorySplitClassesTest {
    private MethodBeanList methods, called1, called2, called3, called4;
    private MethodBean metodo;
    private ClassBean classe, noSmelly, smelly;
    private ClassBeanList classes;
    private PackageBean pack;


    @Before
    public void setUp() throws Exception {
        MethodBeanList vuota = new MethodList();
        HashMap<String, ClassBean> nulla = new HashMap<String, ClassBean>();
        classes = new ClassList();
        pack = new PackageBean.Builder("blob.package", "public class BankAccount {\n" +
                "\n" +
                "    private double balance;\n" +
                "\n" +
                "    public BankAccount(double balance, int accountNumber) {\n" +
                "        this.balance = balance;\n" +
                "        this.accountNumber = accountNumber;\n" +
                "    }\n" +
                "\n" +
                "    public double withdraw(String b) {\n" +
                "            BankAccount new= BankAccount(b);\n" +
                "            b.getBalance() - 1000;\n" +
                "            return new;\n" +
                "        }" +
                "\n" +
                "}" +
                "public class Cliente {\n" +
                "\n" +
                "\tprivate String name;\n" +
                "\tprivate int età;\n" +
                "\t\n" +
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
                "}\n" +
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
                "public class Ristorante {\n" +
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
                "import java.util.ArrayList;\n" +
                "import java.util.Scanner;\n" +
                "\n" +
                "public class Prodotto {\n" +
                "\n" +
                "\tpublic String uno;\n" +
                "\tpublic String due;\n" +
                "\tpublic double tre;\n" +
                "\n" +
                "    public double withdraw(String b) {\n" +
                "            BankAccount new= BankAccount(b);\n" +
                "            b.getBalance() - 1000;\n" +
                "            return new;\n" +
                "        }" +
                "\n" +
                "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                "          return \"(\" +\n" +
                "             mobilePhone.getAreaCode() + \") \" +\n" +
                "             mobilePhone.getPrefix() + \"-\" +\n" +
                "             mobilePhone.getNumber();\n" +
                "       }\n" +
                "\n" +
                "\tpublic String nuovoNomeRistorante() {\n" +
                "\t\tScanner in= new Scanner(System.in);\n" +
                "\t\tString ristorante=in.nextLine();\n" +
                "\t\tRistorante r= new Ristorante(ristorante);\n" +
                "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                "\t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tpublic Cliente scorriListaClienti() {\n" +
                "\t\t\n" +
                "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Ugo\",51);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Maria\",16);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Lucia\",20);\n" +
                "\t\tclienti.add(c);\n" +
                "\n" +
                "\t\tint contatore=0;\n" +
                "\n" +
                "\t\tfor(int i=0;i<4;i++) {\n" +
                "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\treturn clienti.get(contatore);\n" +
                "\t}\n" +
                "}\n").setClassList(classes).build();

        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("uno", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("due", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("tre", "String", "", "private "));
        List<String> imports = new ArrayList<String>();
        imports.add("import java.util.Scanner;");
        imports.add("import java.util.ArrayList;");

        called1 = new MethodList();
        called2 = new MethodList();
        called3 = new MethodList();
        called4 = new MethodList();

        methods = new MethodList();
        InstanceVariableBeanList instancesBank = new InstanceVariableList();
        instancesBank.getList().add(new InstanceVariableBean("balance", "double", "", "private "));
        noSmelly = new ClassBean.Builder("blob.package.BankAccount", "private double balance;\n" +
                "\n" +
                "    public BankAccount(double balance) {\n" +
                "        this.balance = balance;\n" +
                "    }\n" +
                "\n" +
                "    public double getBalance() {\n" +
                "        return balance;\n" +
                "    }")
                .setInstanceVariables(instancesBank)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(10)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("blob.package", "public class BankAccount {\n" +
                        "\n" +
                        "    private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance, int accountNumber) {\n" +
                        "        this.balance = balance;\n" +
                        "        this.accountNumber = accountNumber;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "}" +
                        "public class Cliente {\n" +
                        "\n" +
                        "\tprivate String name;\n" +
                        "\tprivate int età;\n" +
                        "\t\n" +
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
                        "}\n" +
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
                        "public class Ristorante {\n" +
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
                        "import java.util.ArrayList;\n" +
                        "import java.util.Scanner;\n" +
                        "\n" +
                        "public class Prodotto {\n" +
                        "\n" +
                        "\tpublic String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}\n" +
                        "}\n").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(2)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\blob\\package")
                .setAffectedSmell()
                .build();

        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("balance", new ClassBean.Builder("Double", "").build());
        metodo = new MethodBean.Builder("blob.package.BankAccount.BankAccount", "this.balance = balance;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instancesBank)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("blob.package.BankAccount ", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        noSmelly.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("blob.package.BankAccount.getBalance", "return balance;")
                .setReturnType(new ClassBean.Builder("Double", "").build())
                .setInstanceVariableList(instancesBank)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.BankAccount", "private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance) {\n" +
                        "        this.balance = balance;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double getBalance() {\n" +
                        "        return balance;\n" +
                        "    }").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        noSmelly.addMethodBeanList(metodo);
        called4.getList().add(metodo);
        pack.addClassList(noSmelly);

        methods = new MethodList();
        classe = new ClassBean.Builder("blob.package.Phone", "private final String unformattedNumber;\n" +
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
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(11)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("blob.package", "public class BankAccount {\n" +
                        "\n" +
                        "    private double balance;\n" +
                        "\n" +
                        "    public BankAccount(double balance, int accountNumber) {\n" +
                        "        this.balance = balance;\n" +
                        "        this.accountNumber = accountNumber;\n" +
                        "    }\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "}" +
                        "public class Cliente {\n" +
                        "\n" +
                        "\tprivate String name;\n" +
                        "\tprivate int età;\n" +
                        "\t\n" +
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
                        "}\n" +
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
                        "public class Ristorante {\n" +
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
                        "import java.util.ArrayList;\n" +
                        "import java.util.Scanner;\n" +
                        "\n" +
                        "public class Prodotto {\n" +
                        "\n" +
                        "\tpublic String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}\n" +
                        "}\n").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(4)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\blob\\package")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("unformattedNumber", new ClassBean.Builder("String", "").build());

        metodo = new MethodBean.Builder("blob.package.Phone.Phone", "this.unformattedNumber = unformattedNumber;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("blob.package.Phone ", "private final String unformattedNumber;\n" +
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

        metodo = new MethodBean.Builder("blob.package.Phone.getAreaCode", "return unformattedNumber.substring(0,3);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Phone", "private final String unformattedNumber;\n" +
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
        called3.getList().add(metodo);

        metodo = new MethodBean.Builder("blob.package.Phone.getPrefix", "return unformattedNumber.substring(3,6);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Phone", "private final String unformattedNumber;\n" +
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
        called3.getList().add(metodo);

        metodo = new MethodBean.Builder("blob.package.Phone.getNumber", "return unformattedNumber.substring(6,10);")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Phone", "private final String unformattedNumber;\n" +
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
        called3.getList().add(metodo);
        pack.addClassList(classe);

        methods = new MethodList();
        classe = new ClassBean.Builder("blob.package.Cliente", "private String name;\n" +
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
                .setBelongingPackage(new PackageBean.Builder("package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(8)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\blob\\package\\")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());
        hash.put("età", new ClassBean.Builder("int", "").build());
        metodo = new MethodBean.Builder("blob.package.Cliente.Cliente", "this.name = name;\n" +
                "\t\tthis.età = età;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("blob.package.Cliente", "private String name;\n" +
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
        called1.getList().add(metodo);

        instances.getList().remove(new InstanceVariableBean("età", "int", "", "private "));
        metodo = new MethodBean.Builder("blob.package.Cliente.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Cliente", "private String name;\n" +
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
        metodo = new MethodBean.Builder("blob.package.Cliente.getEtà", "return età;")
                .setReturnType(new ClassBean.Builder("int", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Cliente", "private String name;\n" +
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
        called1.getList().add(metodo);
        pack.addClassList(classe);

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("nome_Ristorante", "String", "", "private "));
        methods = new MethodList();
        classe = new ClassBean.Builder("blob.package.Ristorante", "public String nome_Ristorante;\n" +
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
                .setBelongingPackage(new PackageBean.Builder("blob.package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(2)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\blob\\package\\")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("nome_Ristorante", new ClassBean.Builder("String", "").build());
        metodo = new MethodBean.Builder("blob.package.Ristorante.Ristorante", "this.nome_Ristorante = nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("blob.package.Ristorante", "").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        classe.addMethodBeanList(metodo);
        called2.getList().add(metodo);

        metodo = new MethodBean.Builder("blob.package.Ristorante.getNome_Ristorante", "return nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("Ristorante", "public String nome_Ristorante;\n" +
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
        called2.getList().add(metodo);

        instances.getList().remove(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("età", "int", "", "private "));
        metodo = new MethodBean.Builder("blob.package.Cliente.setNome_Ristorante", "this.nome_Ristorante = nome_Ristorante;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuota)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Ristorante", "public String nome_Ristorante;\n" +
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
        pack.addClassList(classe);

        methods = new MethodList();
        smelly = new ClassBean.Builder("blob.package.Prodotto", "public String uno;\n" +
                "\tpublic String due;\n" +
                "\tpublic double tre;\n" +
                "\n" +
                "    public double withdraw(String b) {\n" +
                "            BankAccount new= BankAccount(b);\n" +
                "            b.getBalance() - 1000;\n" +
                "            return new;\n" +
                "        }" +
                "\n" +
                "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                "          return \"(\" +\n" +
                "             mobilePhone.getAreaCode() + \") \" +\n" +
                "             mobilePhone.getPrefix() + \"-\" +\n" +
                "             mobilePhone.getNumber();\n" +
                "       }\n" +
                "\n" +
                "\tpublic String nuovoNomeRistorante() {\n" +
                "\t\tScanner in= new Scanner(System.in);\n" +
                "\t\tString ristorante=in.nextLine();\n" +
                "\t\tRistorante r= new Ristorante(ristorante);\n" +
                "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                "\t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tpublic Cliente scorriListaClienti() {\n" +
                "\t\t\n" +
                "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Ugo\",51);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Maria\",16);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Lucia\",20);\n" +
                "\t\tclienti.add(c);\n" +
                "\n" +
                "\t\tint contatore=0;\n" +
                "\n" +
                "\t\tfor(int i=0;i<4;i++) {\n" +
                "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\treturn clienti.get(contatore);\n" +
                "\t}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(imports)
                .setLOC(42)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("blob.package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\blob\\package")
                .setAffectedSmell()
                .build();

        hash = new HashMap<String, ClassBean>();
        hash.put("b", new ClassBean.Builder("String", "").build());
        metodo = new MethodBean.Builder("blob.package.Prodotto.withdraw", "public double withdraw(String b) {\n" +
                "            BankAccount new= BankAccount(b);\n" +
                "            b.getBalance() - 1000;\n" +
                "            return new;\n" +
                "        }")
                .setReturnType(new ClassBean.Builder("BankAccount", "").build())
                .setInstanceVariableList(new InstanceVariableList())
                .setMethodsCalls(called4)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Prodotto", "public String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("blob.package.Prodotto.listaClienti", "Scanner in= new Scanner(System.in);\n" +
                "\t\tString ristorante=in.nextLine();\n" +
                "\t\tRistorante r= new Ristorante(ristorante);\n" +
                "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                "\t")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(new InstanceVariableList())
                .setMethodsCalls(called2)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Prodotto", "public String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);

        instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("mobilePhone", "Phone", "", "private"));
        metodo = new MethodBean.Builder("blob.package.Prodotto.getMobilePhoneNumber", "return \"(\" +\n" +
                "         mobilePhone.getAreaCode() + \") \" +\n" +
                "         mobilePhone.getPrefix() + \"-\" +\n" +
                "         mobilePhone.getNumber();\n" +
                "   }")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(called3)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Prodotto ", "public String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);

        metodo = new MethodBean.Builder("blob.package.Prodotto.scorriListaClienti", "ArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Ugo\",51);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Maria\",16);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Lucia\",20);\n" +
                "\t\tclienti.add(c);\n" +
                "\n" +
                "\t\tint contatore=0;\n" +
                "\n" +
                "\t\tfor(int i=0;i<4;i++) {\n" +
                "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\treturn clienti.get(contatore);")
                .setReturnType(new ClassBean.Builder("Cliente", "").build())
                .setInstanceVariableList(new InstanceVariableList())
                .setMethodsCalls(called1)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("blob.package.Prodotto", "public String uno;\n" +
                        "\tpublic String due;\n" +
                        "\tpublic double tre;\n" +
                        "\n" +
                        "    public double withdraw(String b) {\n" +
                        "            BankAccount new= BankAccount(b);\n" +
                        "            b.getBalance() - 1000;\n" +
                        "            return new;\n" +
                        "        }" +
                        "\n" +
                        "    public String getMobilePhoneNumber(Phone mobilePhone) {\n" +
                        "          return \"(\" +\n" +
                        "             mobilePhone.getAreaCode() + \") \" +\n" +
                        "             mobilePhone.getPrefix() + \"-\" +\n" +
                        "             mobilePhone.getNumber();\n" +
                        "       }\n" +
                        "\n" +
                        "\tpublic String nuovoNomeRistorante() {\n" +
                        "\t\tScanner in= new Scanner(System.in);\n" +
                        "\t\tString ristorante=in.nextLine();\n" +
                        "\t\tRistorante r= new Ristorante(ristorante);\n" +
                        "\t\treturn ristorante=r.getNome_Ristorante();\n" +
                        "\t}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "\t\tfor(int i=0;i<4;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore)<clienti.get(i).getEtà()){contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore);\n" +
                        "\t}").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);
        pack.addClassList(smelly);

    }

    @Test
    public void testMinSimilarity(){
        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();
        double [][] utilityMatrix = new double[][]{
                {1.00, 0.70, 0.21, 0.02, 0.10, 0.00},
                {0.70, 1.00, 0.30, 0.06, 0.01, 0.03},
                {0.21, 0.30, 1.00, 0.50, 0.40, 0.22},
                {0.02, 0.06, 0.50, 1.00, 0.60, 0.30},
                {0.10, 0.01, 0.40, 0.60, 1.00, 0.80},
                {0.00, 0.03, 0.22, 0.30, 0.80, 1.00}
        };

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);

        int chosenI = chosenIndexes[0] ;
        int chosenJ = chosenIndexes[1];

        assertEquals(0, chosenI);
        assertEquals(5, chosenJ);
    }


    @Test
    public void testFiltraggio(){
        double [][] utilityMatrix = new double[][]{
                {1.00, 0.70, 0.21, 0.02, 0.10, 0.00},
                {0.70, 1.00, 0.30, 0.06, 0.01, 0.03},
                {0.21, 0.30, 1.00, 0.50, 0.40, 0.22},
                {0.02, 0.06, 0.50, 1.00, 0.60, 0.30},
                {0.10, 0.01, 0.40, 0.60, 1.00, 0.80},
                {0.00, 0.03, 0.22, 0.30, 0.80, 1.00}
        };
        double[][] filtered = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);
        double [][] expected = new double[][]{
                {1.00, 0.70, 0.21, 0, 0, 0},
                {0.70, 1.00, 0.30, 0, 0, 0},
                {0.21, 0.30, 1.00, 0.50, 0.40, 0.22},
                {0, 0, 0.50, 1.00, 0.60, 0.30},
                {0, 0, 0.40, 0.60, 1.00, 0.80},
                {0, 0, 0.22, 0.30, 0.80, 1.00}
        };
        assertTrue(Arrays.deepEquals(filtered, expected));
    }


    @Test
    public void testPrimaIterazionePayoffMatrix(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2
        MethodBean metodoSally = methods.getList().get(chosenIndexes[0]);
        MethodBean metodoTom = methods.getList().get(chosenIndexes[1]);

        System.out.println(metodoSally);
        System.out.println(metodoTom);

        assertTrue(metodoSally.equals(methods.getList().get(0)));
        assertTrue(metodoTom.equals(methods.getList().get(2)));

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        PayoffPair sameIndex = new PayoffPair(-1.0,-1.0);
        PayoffPair unoTre = new PayoffPair(-0.23, 0.0);
        PayoffPair treUno = new PayoffPair(0.23, 0.0);
        PayoffPair unoQuattro = new PayoffPair(0.0,0.09999999999999998);
        PayoffPair quattroUno = new PayoffPair(0.5,0.4);
        PayoffPair treQuattro = new PayoffPair(0.23, 0.09999999999999998);
        PayoffPair quattroTre = new PayoffPair(0.27, 0.4);

        PayoffPair[][] expected = {
                {sameIndex, sameIndex, sameIndex, sameIndex, sameIndex},
                {sameIndex, sameIndex, sameIndex, unoTre, unoQuattro},
                {sameIndex, sameIndex, sameIndex, sameIndex, sameIndex},
                {sameIndex, treUno, sameIndex, sameIndex, treQuattro},
                {sameIndex, quattroUno, sameIndex, quattroTre, sameIndex}
        };

        assertEquals(payoffIniziale.length, expected.length);

        for(int i = 0; i<payoffIniziale.length; i++){
            for(int j = 0; j<payoffIniziale.length; j++){
                System.out.println(payoffIniziale[i][j]);
            }
        }
        System.out.println("----------------------");

        assertTrue(payoffIniziale[0][0].toString().equals(expected[0][0].toString()));
        assertTrue(payoffIniziale[3][1].toString().equals(expected[3][1].toString()));

        for(int i = 0; i<payoffIniziale.length; i++){
            for(int j = 0; j<payoffIniziale.length; j++){
                assertTrue(payoffIniziale[i][j].toString().equals(expected[i][j].toString()));
            }
        }
    }


    @Test
    public void testLetThemPlay(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        gameTheory.letThemPlay(payoffIniziale);

        assertTrue(payoffIniziale[1][4].isMaximumTom());
        assertFalse(payoffIniziale[1][4].isMaximumSally());

        //ciao, sono un equilibrio di nash
        assertTrue(payoffIniziale[3][4].isMaximumSally());
        assertTrue(payoffIniziale[3][4].isMaximumTom());

        //ciao, sono un altro equilibrio di nash, che emozione
        assertTrue(payoffIniziale[4][1].isMaximumTom());
        assertTrue(payoffIniziale[4][1].isMaximumSally());

        assertTrue(payoffIniziale[4][3].isMaximumSally());
        assertFalse(payoffIniziale[4][3].isMaximumTom());

    }


    @Test
    public void testFindNashEquilibrium(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        Vector<MethodBean> vectorOfMethods = new Vector<>();
        //riempiamo quindi vectorOfMethods
        for (MethodBean method : methods.getList()) {
            vectorOfMethods.add(method);
        }
        //System.out.println("METHODS"+methods.getList());
        //System.out.println("VECTOR OF METHODS"+vectorOfMethods);

        Vector<MethodBean> methodBeans = new Vector<>();
        for(MethodBean m: methods.getList()){
            methodBeans.add(m);
        }


        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2
        MethodBean metodoSally = methods.getList().get(chosenIndexes[0]);
        MethodBean metodoTom = methods.getList().get(chosenIndexes[1]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceSally = new GameTheorySplitClasses.MethodAndIndex(metodoSally,chosenIndexes[0]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceTom = new GameTheorySplitClasses.MethodAndIndex(metodoTom, chosenIndexes[1]);

        Vector<MethodBean> sallyMethods = new Vector<>();
        Vector<MethodBean> tomMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> sallyIndexMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> tomIndexMethods = new Vector<>();

        sallyMethods.add(metodoSally);
        tomMethods.add(metodoTom);
        sallyIndexMethods.add(metodoIndiceSally);
        tomIndexMethods.add(metodoIndiceTom);

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        gameTheory.letThemPlay(payoffIniziale);

        //ciao sono il vincitore, qui Sally non ha preso nulla, Tom ha preso il metodo in posizione 1
        assertTrue(payoffIniziale[4][1].isMaximumTom());
        assertTrue(payoffIniziale[4][1].isMaximumSally());


        gameTheory.findNashEquilibrium(payoffIniziale,sallyMethods,tomMethods,sallyIndexMethods,tomIndexMethods,vectorOfMethods,methodBeans);
        System.out.println("cccc");

        //System.out.println(payoffIniziale[4][1].isMaximumTom());
        //System.out.println(payoffIniziale[4][1].isMaximumSally());

        PayoffPair sameIndex = new PayoffPair(-1.0, -1.0);

        System.out.println(tomMethods.elementAt(1));
        assertTrue(tomMethods.elementAt(1).equals(vectorOfMethods.elementAt(1)));
        for (int j = 0; j <payoffIniziale.length; j++){
            //System.out.println(payoffIniziale[1][j]);
            assertTrue(payoffIniziale[1][j].toString().equals(sameIndex.toString()));
        }
        for(int i = 0; i < payoffIniziale.length; i++){
            //for (int j = 0; j < payoffIniziale.length; j++){
            assertTrue(payoffIniziale[i][1].toString().equals(sameIndex.toString()));
            //System.out.println(payoffIniziale[i][1]);
        }
    }

    @Test
    public void testComputeFccMatrix(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        Vector<MethodBean> vectorOfMethods = new Vector<>();
        //riempiamo quindi vectorOfMethods
        for (MethodBean method : methods.getList()) {
            vectorOfMethods.add(method);
        }
        //System.out.println("METHODS"+methods.getList());
        //System.out.println("VECTOR OF METHODS"+vectorOfMethods);

        Vector<MethodBean> methodBeans = new Vector<>();
        for(MethodBean m: methods.getList()){
            methodBeans.add(m);
        }


        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2

        MethodBean metodoSally = methods.getList().get(chosenIndexes[0]);
        MethodBean metodoTom = methods.getList().get(chosenIndexes[1]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceSally = new GameTheorySplitClasses.MethodAndIndex(metodoSally,chosenIndexes[0]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceTom = new GameTheorySplitClasses.MethodAndIndex(metodoTom, chosenIndexes[1]);

        Vector<MethodBean> sallyMethods = new Vector<>();
        Vector<MethodBean> tomMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> sallyIndexMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> tomIndexMethods = new Vector<>();

        sallyMethods.add(metodoSally);
        tomMethods.add(metodoTom);
        sallyIndexMethods.add(metodoIndiceSally);
        tomIndexMethods.add(metodoIndiceTom);

        methodBeans.remove(metodoSally);
        methodBeans.remove(metodoTom);

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        gameTheory.letThemPlay(payoffIniziale);

        //ciao sono il vincitore, qui Sally non ha preso nulla, Tom ha preso il metodo in posizione 1
        assertTrue(payoffIniziale[4][1].isMaximumTom());
        assertTrue(payoffIniziale[4][1].isMaximumSally());

        gameTheory.findNashEquilibrium(payoffIniziale,sallyMethods,tomMethods,sallyIndexMethods,tomIndexMethods,vectorOfMethods,methodBeans);

        double [][] fccMatrix = gameTheory.computeFccMatrix(filteredFcc,sallyIndexMethods,tomIndexMethods);

        System.out.println(Arrays.deepToString(fccMatrix));

        assertTrue(fccMatrix[0][3]==0.23);
        assertTrue(fccMatrix[1][3]==0.45);

    }


    @Test
    public void testComputePayoffMatrix(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        Vector<MethodBean> vectorOfMethods = new Vector<>();
        //riempiamo quindi vectorOfMethods
        for (MethodBean method : methods.getList()) {
            vectorOfMethods.add(method);
        }
        //System.out.println("METHODS"+methods.getList());
        //System.out.println("VECTOR OF METHODS"+vectorOfMethods);

        Vector<MethodBean> methodBeans = new Vector<>();
        for(MethodBean m: methods.getList()){
            methodBeans.add(m);
        }


        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2

        MethodBean metodoSally = methods.getList().get(chosenIndexes[0]);
        MethodBean metodoTom = methods.getList().get(chosenIndexes[1]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceSally = new GameTheorySplitClasses.MethodAndIndex(metodoSally,chosenIndexes[0]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceTom = new GameTheorySplitClasses.MethodAndIndex(metodoTom, chosenIndexes[1]);

        Vector<MethodBean> sallyMethods = new Vector<>();
        Vector<MethodBean> tomMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> sallyIndexMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> tomIndexMethods = new Vector<>();

        sallyMethods.add(metodoSally);
        tomMethods.add(metodoTom);
        sallyIndexMethods.add(metodoIndiceSally);
        tomIndexMethods.add(metodoIndiceTom);

        methodBeans.remove(metodoSally);
        methodBeans.remove(metodoTom);

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        gameTheory.letThemPlay(payoffIniziale);

        gameTheory.findNashEquilibrium(payoffIniziale,sallyMethods,tomMethods,sallyIndexMethods,tomIndexMethods,vectorOfMethods,methodBeans);

        double [][] fccMatrix = gameTheory.computeFccMatrix(filteredFcc,sallyIndexMethods,tomIndexMethods);

        PayoffPair[][] payoffMatrix = gameTheory.computePayoffMatrix(fccMatrix, payoffIniziale);

        for(int i = 0; i <payoffMatrix.length; i++){
            for (int j = 0; j < payoffMatrix.length; j++){
                System.out.println(payoffMatrix[i][j]);
            }
        }

        assertTrue(payoffMatrix[3][4].getPayoffI()==0.23);
        assertTrue(payoffMatrix[3][4].getPayoffJ()==0.04999999999999999);

        assertTrue(payoffMatrix[4][3].getPayoffI()==0.27);
        assertTrue(payoffMatrix[4][3].getPayoffJ()==0.45);

    }

    //sei un test falso, perchè ho deciso io la fcc matrix come doveva essere
    //ma mi servi lo stesso
    @Test
    public void testSplitGame(){
        double [][] utilityMatrix = {
                {1.00, 0.10, 0.00, 0.23},
                {0.70, 1.00, 0.03, 0.50},
                {0.21, 0.40, 1.00, 0.40},
                {0.31, 0.66, 0.13, 1.00}
        };

        Vector<MethodBean> vectorOfMethods = new Vector<>();
        //riempiamo quindi vectorOfMethods
        for (MethodBean method : methods.getList()) {
            vectorOfMethods.add(method);
        }
        //System.out.println("METHODS"+methods.getList());
        //System.out.println("VECTOR OF METHODS"+vectorOfMethods);

        Vector<MethodBean> methodBeans = new Vector<>();
        for(MethodBean m: methods.getList()){
            methodBeans.add(m);
        }


        GameTheorySplitClasses gameTheory = new GameTheorySplitClasses();

        int [] chosenIndexes = gameTheory.minSimilarity(utilityMatrix);
        //chosenI = 0, chosenJ =2

        MethodBean metodoSally = methods.getList().get(chosenIndexes[0]);
        MethodBean metodoTom = methods.getList().get(chosenIndexes[1]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceSally = new GameTheorySplitClasses.MethodAndIndex(metodoSally,chosenIndexes[0]);
        GameTheorySplitClasses.MethodAndIndex metodoIndiceTom = new GameTheorySplitClasses.MethodAndIndex(metodoTom, chosenIndexes[1]);

        Vector<MethodBean> sallyMethods = new Vector<>();
        Vector<MethodBean> tomMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> sallyIndexMethods = new Vector<>();
        Vector<GameTheorySplitClasses.MethodAndIndex> tomIndexMethods = new Vector<>();

        sallyMethods.add(metodoSally);
        tomMethods.add(metodoTom);
        sallyIndexMethods.add(metodoIndiceSally);
        tomIndexMethods.add(metodoIndiceTom);

        methodBeans.remove(metodoSally);
        methodBeans.remove(metodoTom);

        double [][] filteredFcc = MethodByMethodMatrixConstruction.filterMatrix(utilityMatrix, 0.20);

        PayoffPair[][] payoffIniziale = gameTheory.computeStartingPayoffMatrix(filteredFcc, chosenIndexes[0], chosenIndexes[1]);

        gameTheory.letThemPlay(payoffIniziale);

        gameTheory.findNashEquilibrium(payoffIniziale,sallyMethods,tomMethods,sallyIndexMethods,tomIndexMethods,vectorOfMethods,methodBeans);


        while (!methodBeans.isEmpty()) {

            double[][] fccMatrix = gameTheory.computeFccMatrix(filteredFcc, sallyIndexMethods, tomIndexMethods);

            PayoffPair[][] payoffMatrix = gameTheory.computePayoffMatrix(fccMatrix, payoffIniziale);
            payoffIniziale = payoffMatrix;

            gameTheory.letThemPlay(payoffMatrix);

            gameTheory.findNashEquilibrium(payoffMatrix,sallyMethods,tomMethods,sallyIndexMethods,tomIndexMethods,vectorOfMethods,methodBeans);

        }

        assertTrue(sallyIndexMethods.size()==1);
        assertTrue(tomIndexMethods.size()==3);

    }

    //tu sei il vero test del metodo split
    @Test
    public void splitTrue() {
        Collection<ClassBean> splittedClasses = new ArrayList<ClassBean>();
        boolean errorOccured = false;
        try {
            splittedClasses = new GameTheorySplitClasses().split(smelly, 0.20);
        } catch (Exception e) {
            errorOccured = true;
            e.getMessage();
        }
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + (splittedClasses.size() == 2));
        assertTrue(splittedClasses.size() == 2);
        log.info("\nError occurred:" + errorOccured);
        assertTrue(!errorOccured);
    }

    @Test
    public void splitFalse() {
        Collection<ClassBean> splittedClasses = new ArrayList<ClassBean>();
        boolean errorOccured = false;
        try {
            splittedClasses = new GameTheorySplitClasses().split(noSmelly, 0);
        } catch (Exception e) {
            errorOccured = true;
            e.getMessage();
        }
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + (splittedClasses.size() == 1));
        assertTrue(splittedClasses.size() == 1);
        log.info("\nError occurred:" + errorOccured);
        assertTrue(!errorOccured);
    }
}
