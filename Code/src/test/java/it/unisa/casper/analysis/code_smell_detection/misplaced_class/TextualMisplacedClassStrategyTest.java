package it.unisa.casper.analysis.code_smell_detection.misplaced_class;

import it.unisa.casper.analysis.code_smell.MisplacedClassCodeSmell;
import it.unisa.casper.analysis.code_smell_detection.Helper.CosineSimilarityStub;
import it.unisa.casper.storage.beans.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextualMisplacedClassStrategyTest {

    private List<PackageBean> systemPackage = new ArrayList<PackageBean>();
    private MethodBeanList methods;
    private ClassBean classe, smelly, noSmelly;
    private ClassBeanList classes;
    private PackageBean pack, packE;

    @Before
    public void setUp() {
        String filename = System.getProperty("user.home") + File.separator + ".casper" + File.separator + "stopwordlist.txt";
        File stopwordlist = new File(filename);
        stopwordlist.delete();
        InstanceVariableBeanList instances = new InstanceVariableList();

        classes = new ClassList();
        packE = new PackageBean.Builder("misplaced_class.package", "public class Cliente {\n" +
                "\n" +
                "\tprivate String name;\n" +
                "\tprivate int età;" +
                "\tprivate String luogoDiNascita;\n" +
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
                "\tpublic String setName(String name) {\n" +
                "\t\tthis.name = name;\n" +
                "\t}\n" +
                "\tpublic int setEtà(int newEta) {\n" +
                "\t\tthis.età = età;\n" +
                "\t}\n" +
                "\tpublic String getLuogo() {\n" +
                "\t\treturn luogoDiNascita;\n" +
                "\t}\n" +
                "\tpublic String setLuogo(String luogoDiNascita) {\n" +
                "\t\tthis.luogoDiNascita = luogoDiNascita;\n" +
                "\t}\n" +
                "\t}" +
                "\n" +
                "public class Gestione{" +
                "\tpublic Cliente scorriListaClienti() {\n" +
                "\t\t\n" +
                "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Ugo\",51,\"SA\");\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Maria\",16,\"MI\");\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Lucia\",20,\"MI\");\n" +
                "\t\tclienti.add(c);\n" +
                "\n" +
                "\t\tint contatore=0;\n" +
                "\n" +
                "String concatenationLuohi=\"\";" +
                "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                "\t\t\tif(clienti.get(contatore).getEtà()<clienti.get(i).getEtà()){clienti.get(contatore).setEtà(0);" +
                "contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                "\t\t\tif(clienti.get(contatore).getLuogo.equals(clienti.get(i).getLuogo())){concatenationLuohi+ =\" \"+ clienti.get(contatore).getLuogo();" +
                "contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\treturn clienti.get(contatore).setLuogo(\"NA\");\n" +
                "\t}}").setClassList(classes).build();

        instances.getList().add(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("eta", "int", "", "private "));
        methods = new MethodList();
        List<String> imports = new ArrayList<String>();

        methods = new MethodList();
        classe = new ClassBean.Builder("misplaced_class.package.Cliente", "\tprivate String name;\n" +
                "\tprivate int età;" +
                "\tprivate String luogoDiNascita;\n" +
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
                "\tpublic String setName(String name) {\n" +
                "\t\tthis.name = name;\n" +
                "\t}\n" +
                "\tpublic int setEtà(int newEta) {\n" +
                "\t\tthis.età = età;\n" +
                "\t}\n" +
                "\tpublic String getLuogo() {\n" +
                "\t\treturn luogoDiNascita;\n" +
                "\t}\n" +
                "\tpublic String setLuogo(String luogoDiNascita) {\n" +
                "\t\tthis.luogoDiNascita = luogoDiNascita;\n" +
                "\t}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(imports)
                .setLOC(22)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package", "public class Cliente {\n" +
                        "\n" +
                        "\tprivate String name;\n" +
                        "\tprivate int età;" +
                        "\tprivate String luogoDiNascita;\n" +
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
                        "\tpublic String setName(String name) {\n" +
                        "\t\tthis.name = name;\n" +
                        "\t}\n" +
                        "\tpublic int setEtà(int newEta) {\n" +
                        "\t\tthis.età = età;\n" +
                        "\t}\n" +
                        "\tpublic String getLuogo() {\n" +
                        "\t\treturn luogoDiNascita;\n" +
                        "\t}\n" +
                        "\tpublic String setLuogo(String luogoDiNascita) {\n" +
                        "\t\tthis.luogoDiNascita = luogoDiNascita;\n" +
                        "\t}\n" +
                        "\t}" +
                        "\n" +
                        "public class Gestione{" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51,\"SA\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16,\"MI\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20,\"MI\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "String concatenationLuohi=\"\";" +
                        "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore).getEtà()<clienti.get(i).getEtà()){clienti.get(contatore).setEtà(0);" +
                        "contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore).getLuogo.equals(clienti.get(i).getLuogo())){concatenationLuohi+ =\" \"+ clienti.get(contatore).getLuogo();" +
                        "contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore).setLuogo(\"NA\");\n" +
                        "\t}}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(3)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package")
                .setAffectedSmell()
                .build();
        packE.addClassList(classe);

        noSmelly = new ClassBean.Builder("misplaced_class.package.Gestione", "ArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Ugo\",51,\"SA\");\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Maria\",16,\"MI\");\n" +
                "\t\tclienti.add(c);\n" +
                "\t\tc= new Cliente(\"Lucia\",20,\"MI\");\n" +
                "\t\tclienti.add(c);\n" +
                "\n" +
                "\t\tint contatore=0;\n" +
                "\n" +
                "String concatenationLuohi=\"\";" +
                "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                "\t\t\tif(clienti.get(contatore).getEtà()<clienti.get(i).getEtà()){clienti.get(contatore).setEtà(0);" +
                "contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                "\t\t\tif(clienti.get(contatore).getLuogo.equals(clienti.get(i).getLuogo())){concatenationLuohi+ =\" \"+ clienti.get(contatore).getLuogo();" +
                "contatore=i;}\n" +
                "\t\t}\t\n" +
                "\t\treturn clienti.get(contatore).setLuogo(\"NA\");\n" +
                "\t}}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<>())
                .setLOC(18)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package2", "public class Cliente {\n" +
                        "\n" +
                        "\tprivate String name;\n" +
                        "\tprivate int età;" +
                        "\tprivate String luogoDiNascita;\n" +
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
                        "\tpublic String setName(String name) {\n" +
                        "\t\tthis.name = name;\n" +
                        "\t}\n" +
                        "\tpublic int setEtà(int newEta) {\n" +
                        "\t\tthis.età = età;\n" +
                        "\t}\n" +
                        "\tpublic String getLuogo() {\n" +
                        "\t\treturn luogoDiNascita;\n" +
                        "\t}\n" +
                        "\tpublic String setLuogo(String luogoDiNascita) {\n" +
                        "\t\tthis.luogoDiNascita = luogoDiNascita;\n" +
                        "\t}\n" +
                        "\t}" +
                        "\n" +
                        "public class Gestione{" +
                        "\tpublic Cliente scorriListaClienti() {\n" +
                        "\t\t\n" +
                        "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                        "\t\tCliente c= new Cliente(\"Lucia\",30);\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Ugo\",51,\"SA\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Maria\",16,\"MI\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\t\tc= new Cliente(\"Lucia\",20,\"MI\");\n" +
                        "\t\tclienti.add(c);\n" +
                        "\n" +
                        "\t\tint contatore=0;\n" +
                        "\n" +
                        "String concatenationLuohi=\"\";" +
                        "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore).getEtà()<clienti.get(i).getEtà()){clienti.get(contatore).setEtà(0);" +
                        "contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\tfor(int i=0;i<clienti.length;i++) {\n" +
                        "\t\t\tif(clienti.get(contatore).getLuogo.equals(clienti.get(i).getLuogo())){concatenationLuohi+ =\" \"+ clienti.get(contatore).getLuogo();" +
                        "contatore=i;}\n" +
                        "\t\t}\t\n" +
                        "\t\treturn clienti.get(contatore).setLuogo(\"NA\");\n" +
                        "\t}}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package")
                .setAffectedSmell()
                .build();

        packE.addClassList(noSmelly);
        systemPackage.add(packE);

        methods = new MethodList();
        classes = new ClassList();
        pack = new PackageBean.Builder("misplaced_class.package2", "private Gestione gestore" +
                "\n\tstatic void main(){" +
                "\t\tCliente c = new Cliente(\"\",20);" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t}" +
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
                "   }" +
                "public String nome_Ristorante;\n" +
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
                "\t}").setClassList(classes).build();

        smelly = new ClassBean.Builder("misplaced_class.package2.Main", "private Gestione gestore" +
                "\n\tstatic void main(){" +
                "\t\tCliente c = new Cliente(\"\",20);" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t\tc = gestore.scorriListaClienti();" +
                "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                "\t}")
                .setInstanceVariables(new InstanceVariableList())
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(8)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package2", "private Gestione gestore" +
                        "\n\tstatic void main(){" +
                        "\t\tCliente c = new Cliente(\"\",20);" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t}" +
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
                        "   }" +
                        "public String nome_Ristorante;\n" +
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
                .setEnviedPackage(packE)
                .setEntityClassUsage(1)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package2")
                .setAffectedSmell()
                .build();
        pack.addClassList(smelly);

        instances = new InstanceVariableList();
        methods = new MethodList();

        classe = new ClassBean.Builder("misplaced_class.package2.Phone", "private final String unformattedNumber;\n" +
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
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package2", "private Gestione gestore" +
                        "\n\tstatic void main(){" +
                        "\t\tCliente c = new Cliente(\"\",20);" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t}" +
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
                        "   }" +
                        "public String nome_Ristorante;\n" +
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
                .setEnviedPackage(null)
                .setEntityClassUsage(3)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package2")
                .setAffectedSmell()
                .build();
        pack.addClassList(classe);

        classe = new ClassBean.Builder("misplaced_class.package2.Ristorante", "public String nome_Ristorante;\n" +
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
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package2", "private Gestione gestore" +
                        "\n\tstatic void main(){" +
                        "\t\tCliente c = new Cliente(\"\",20);" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t\tc = gestore.scorriListaClienti();" +
                        "\t\tSystem.out.println(c.getName+\" \"+c.getEtà());)" +
                        "\t}" +
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
                        "   }" +
                        "public String nome_Ristorante;\n" +
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
                .setEnviedPackage(null)
                .setEntityClassUsage(2)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package2")
                .setAffectedSmell()
                .build();
        pack.addClassList(classe);

        systemPackage.add(pack);

    }

    @Test
    public void isSmellyTrue() {
        TextualMisplacedClassStrategy analisi = new TextualMisplacedClassStrategy(systemPackage, 0); //soglia default
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyNearThreshold() {
        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = smelly.getTextContent();
        String[] document2 = new String[2];
        document2[0] = "package";
        document2[1] = smelly.getEnviedPackage().getTextContent();

        TextualMisplacedClassStrategy analisi = new TextualMisplacedClassStrategy(systemPackage, CosineSimilarityStub.computeSimilarity(document1, document2) - 0.01);
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyMinThreshold() {
        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = smelly.getTextContent();
        String[] document2 = new String[2];
        document2[0] = "package";
        document2[1] = smelly.getEnviedPackage().getTextContent();

        TextualMisplacedClassStrategy analisi = new TextualMisplacedClassStrategy(systemPackage, CosineSimilarityStub.computeSimilarity(document1, document2));
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Textual");
        boolean risultato = smelly.isAffected(smell);
        assertFalse(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

    @Test
    public void isSmellyFalse() {
        TextualMisplacedClassStrategy analisi = new TextualMisplacedClassStrategy(systemPackage, 0); //soglia default
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Textual");
        boolean risultato = noSmelly.isAffected(smell);
        assertFalse(noSmelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

}