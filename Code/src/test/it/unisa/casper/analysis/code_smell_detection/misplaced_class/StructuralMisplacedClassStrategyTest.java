package it.unisa.casper.analysis.code_smell_detection.misplaced_class;

import it.unisa.casper.analysis.code_smell.MisplacedClassCodeSmell;
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

public class StructuralMisplacedClassStrategyTest {

    private List<PackageBean> systemPackage = new ArrayList<PackageBean>();
    private MethodBeanList methods, list;
    private MethodBean metodo;
    private ClassBean noSmelly, smelly;
    private ClassBeanList classes;
    private PackageBean pack, packE;

    @Before
    public void setUp() {
        classes = new ClassList();
        MethodBeanList vuoto = new MethodList();
        pack = new PackageBean.Builder("misplaced_class.package", "public class Cliente {\n" +
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
                "\t}}").setClassList(classes).build();

        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("eta", "int", "", "private "));
        methods = new MethodList();
        List<String> imports = new ArrayList<String>();
        MethodBeanList called = new MethodList();
        smelly = new ClassBean.Builder("misplaced_class.package.Cliente", "private String name;\n" +
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
                "\t}")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(imports)
                .setLOC(12)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package", "public class Cliente {\n" +
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
                        "\t}}").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(8)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package")
                .setAffectedSmell()
                .build();
        pack.getClassList().add(smelly);

        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());
        hash.put("età", new ClassBean.Builder("int", "").build());

        metodo = new MethodBean.Builder("misplaced_class.package.Cliente.Cliente", "this.name = name;\n" +
                "\t\tthis.età = età;")
                .setReturnType(new ClassBean.Builder("void", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuoto)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("misplaced_class.package.Cliente", "").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);
        called.getList().add(metodo);
        HashMap<String, ClassBean> nulla = new HashMap<String, ClassBean>();
        instances.getList().remove(new InstanceVariableBean("eta", "int", "", "private "));
        metodo = new MethodBean.Builder("misplaced_class.package.Cliente.getName", "return name;")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuoto)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("misplaced_class.package.Cliente", "").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);

        instances.getList().remove(new InstanceVariableBean("name", "String", "", "private "));
        instances.getList().add(new InstanceVariableBean("eta", "int", "", "private "));
        metodo = new MethodBean.Builder("misplaced_class.package.Cliente.getEtà", "return età;")
                .setReturnType(new ClassBean.Builder("int", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(vuoto)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("misplaced_class.package.Cliente", "").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        smelly.addMethodBeanList(metodo);
        called.getList().add(metodo);

        pack.addClassList(smelly);
        systemPackage.add(pack);
///////////////////////////////////
        instances = new InstanceVariableList();
        methods = new MethodList();
        classes = new ClassList();
        packE = new PackageBean.Builder("misplaced_class.package2", "public Cliente scorriListaClienti() {\n" +
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
                "\t}").setClassList(classes).build();

        instances = new InstanceVariableList();
        methods = new MethodList();
        noSmelly = new ClassBean.Builder("misplaced_class.package2.Gestione", "public Cliente scorriListaClienti() {\n" +
                "\t\t\n" +
                "\t\tArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
                "\t\tCliente c= new Cliente(\"Lucia\",\"Abagnale\",30);\n" +
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
                .setLOC(18)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("misplaced_class.package2", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(0)
                .setPathToFile("C:\\Users\\Simone\\Desktop\\IdeaProjects\\Code\\testData\\misplaced_class\\package2")
                .setAffectedSmell()
                .build();

        metodo = new MethodBean.Builder("misplaced_class.package2.Gestione.getMobilePhoneNumber", "ArrayList<Cliente> clienti= new ArrayList<Cliente>();\n" +
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
                .setInstanceVariableList(instances)
                .setMethodsCalls(called)
                .setParameters(nulla)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setBelongingClass(new ClassBean.Builder("misplaced_class.package2.Gestione ", "").build())
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        packE.getClassList().add(noSmelly);
        noSmelly.addMethodBeanList(metodo);
        packE.addClassList(noSmelly);
        systemPackage.add(packE);

    }

    @Test
    public void isSmellyTrue() {
        StructuralMisplacedClassStrategy analisi = new StructuralMisplacedClassStrategy(systemPackage, 0); //soglia default
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyNearThreshold() {
        StructuralMisplacedClassStrategy analisi = new StructuralMisplacedClassStrategy(systemPackage, (int) CKMetricsStub.getNumberOfDependencies(smelly, smelly.getEnviedPackage()) - 1);
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertTrue(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertTrue(risultato);
    }

    @Test
    public void isSmellyMinThreshold() {
        StructuralMisplacedClassStrategy analisi = new StructuralMisplacedClassStrategy(systemPackage, (int) CKMetricsStub.getNumberOfDependencies(smelly, smelly.getEnviedPackage()));
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Structural");
        boolean risultato = smelly.isAffected(smell);
        assertFalse(smelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

    @Test
    public void isSmellyFalse() {
        StructuralMisplacedClassStrategy analisi = new StructuralMisplacedClassStrategy(systemPackage, 0); //soglia default
        MisplacedClassCodeSmell smell = new MisplacedClassCodeSmell(analisi, "Structural");
        boolean risultato = noSmelly.isAffected(smell);
        assertFalse(noSmelly.getAffectedSmell().contains(smell));
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + risultato);
        assertFalse(risultato);
    }

}