package it.unisa.ascetic.parser;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.Query;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.repository.*;
import javafx.util.Pair;
import org.junit.Assert;

import java.io.File;
import java.util.*;

public class PsiParserTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return new File(getSourceRoot(), "testData").getPath();
    }

    private static File getSourceRoot() {
        String testOutput = PathManager.getJarPathForClass(PsiParser.class);
        return new File(testOutput, "../../..");
    }

    public void testInstanceVariableBeanParser() {
        //Ottengo il progetto dalla test suite
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("ParserDataTest/bCustomer.java");
        JavaPsiFacade javaPsiFacade = myFixture.getJavaFacade();
        PsiClass psiSource = ((JavaPsiFacadeEx) javaPsiFacade).findClass("ParserDataTest.Customer");
        PsiField variabilepsi = psiSource.getFields()[0];
        InstanceVariableBean oracleIstanceBean, testInstanceBean;
        oracleIstanceBean = new InstanceVariableBean("mobilePhone", "Phone", "", "private");
        testInstanceBean = new PsiParser(project).parse(variabilepsi);
        Assert.assertEquals(oracleIstanceBean, testInstanceBean);


        List<Pair<String, String>> allMethod = new ArrayList<Pair<String, String>>();
        String boh = "Metodo1,update";
        String []vector = boh.split(",");
        String method = vector[0];
        String operation = vector[1];
        Pair<String, String> pair1 = new Pair<>(method, operation);
        allMethod.add(pair1);
        System.out.println("Metodo: "+ pair1.getKey() + " Operazione : " + pair1.getValue());
        System.out.println(allMethod);


    }

    public void testMethodCalls(){
        PsiParser parser = new PsiParser(myFixture.getProject());
        parser.setRepository(new PackageRepository(),new ClassRepository(),new MethodRepository(),new InstanceVariableRepository());
        PsiJavaFile file = (PsiJavaFile)myFixture.configureByFiles("ParserDataTest/bCustomer.java","ParserDataTest/bPhone.java")[0];
        PsiPackage pkg= myFixture.getJavaFacade().findPackage(file.getPackageName());

        for(PsiClass psiClass: pkg.getClasses()) {
            for(PsiMethod psiMethod: psiClass.getMethods()) {
                //Collection<MethodBean> invocations = new HashSet<>();

                Collection<PsiMethodCallExpression> psiMethodCallExpressions = PsiTreeUtil.findChildrenOfType(psiMethod, PsiMethodCallExpression.class);

                for (PsiMethodCallExpression call : psiMethodCallExpressions) {
                        PsiElement elementFound = call.getMethodExpression().resolve();
                        if(elementFound instanceof PsiMethod){
                            PsiMethod method = (PsiMethod)elementFound;
                            System.out.println(method.getContainingClass().getQualifiedName()+"."+method.getName());
                        }

                    //MethodBean bean = new MethodBean.Builder(call.getMethodExpression().getReference().getElement().toString(), "").build();
                    //System.out.println(bean);
                }
            }
        }

    }

    public void testMethodBeanParser(){
        //Cose Del tester
        MethodBean oracleMethodBean, testMethodBean;
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("ParserDataTest/bCustomer.java");
        JavaPsiFacade javaPsiFacade = myFixture.getJavaFacade();
        PsiClass psiSource = ((JavaPsiFacadeEx) javaPsiFacade).findClass("ParserDataTest.Customer");
        PsiMethod psiMethod =psiSource.findMethodsByName("getMobilePhoneNumber",true)[0];

        //Creo l'oracolo MethodBean
        MethodBean.Builder builder = new MethodBean.Builder("getMobilePhoneNumber","{\n" +
                "        bar = 3;\n" +
                "        return \"(\" +\n" +
                "                mobilePhone.getAreaCode() + \") \" +\n" +
                "                mobilePhone.getPrefix() + \"-\" +\n" +
                "                mobilePhone.getNumber();\n" +
                "    }");
        builder.setReturnType(new ClassBean.Builder("String","").build());
        builder.setBelongingClass(new ClassBean.Builder("ParserDataTest.Customer","").build());

        builder.setStaticMethod(false);
        HashMap<String, ClassBean> map = new HashMap<>();
        map.put("s",new ClassBean.Builder("String","").build());
        map.put("foo",new ClassBean.Builder("Foo","").build());
        builder.setParameters(map);
        ArrayList<InstanceVariableBean> listaVariabiliUsate = new ArrayList<>();
        listaVariabiliUsate.add(new InstanceVariableBean("mobilePhone", "Phone", "", "private"));
        listaVariabiliUsate.add(new InstanceVariableBean("bar", "int", "", "private"));
        InstanceVariableBeanList listaConcreta = new InstanceVariableList();
        ((InstanceVariableList) listaConcreta).setList(listaVariabiliUsate);
        builder.setInstanceVariableList(listaConcreta);
        oracleMethodBean = builder.build();



        //Ottengo il MethodBean Dal Parser
        testMethodBean= new PsiParser(myFixture.getProject()).parse(psiMethod);
        Assert.assertEquals(oracleMethodBean.getFullQualifiedName(),testMethodBean.getFullQualifiedName());
        Assert.assertEquals(oracleMethodBean.getTextContent(),testMethodBean.getTextContent());
        Assert.assertEquals(oracleMethodBean.getBelongingClass(),testMethodBean.getBelongingClass());
    }

    public void testClassBean(){
        ClassBean oracleClassBean, testClassBean;
        Project project = myFixture.getProject();
        PsiFile psiFile = myFixture.configureByFile("ParserDataTest/bCustomer.java");
        JavaPsiFacade javaPsiFacade = myFixture.getJavaFacade();
        PsiClass psiSource = ((JavaPsiFacadeEx) javaPsiFacade).findClass("ParserDataTest.Customer");

        //CREO ORACOLO PER CLASSBEAN

        ClassBean.Builder builder = new ClassBean.Builder("ParserDataTest.Customer", "" +
                "package ParserDataTest;\n\n" +
                "public class Customer {\n" +
                "    private Phone mobilePhone;\n" +
                "    private int bar;\n" +
                "\n" +
                "    public String getMobilePhoneNumber(String s,Foo foo) {\n" +
                "        bar = 3;\n" +
                "        return \"(\" +\n" +
                "                mobilePhone.getAreaCode() + \") \" +\n" +
                "                mobilePhone.getPrefix() + \"-\" +\n" +
                "                mobilePhone.getNumber();\n" +
                "    }\n" +
                "}");
        //CREO ARRAYLIST DI INSTANCE VARIABLE BEAN PER LA CLASSE
        ArrayList<InstanceVariableBean> listaVariabili = new ArrayList<>();
        InstanceVariableBean instanceVariableBean = new InstanceVariableBean("mobilePhone", "Phone", "", "private");
        InstanceVariableBean instanceVariableBean1 = new InstanceVariableBean("bar", "int", "", "private");
        listaVariabili.add(instanceVariableBean);
        listaVariabili.add(instanceVariableBean1);
        InstanceVariableList lista = new InstanceVariableList();
        lista.setList(listaVariabili);
        builder.setInstanceVariables(lista);

        //CREO IL METHODBEAN
        MethodBean.Builder methodBuilder = new MethodBean.Builder("getMobilePhoneNumber","{\n" +
                "        bar = 3;\n" +
                "        return \"(\" +\n" +
                "                mobilePhone.getAreaCode() + \") \" +\n" +
                "                mobilePhone.getPrefix() + \"-\" +\n" +
                "                mobilePhone.getNumber();\n" +
                "    }");
        methodBuilder.setReturnType(new ClassBean.Builder("String","").build());
        methodBuilder.setBelongingClass(new ClassBean.Builder("ParserDataTest.Customer","").build());

        methodBuilder.setStaticMethod(false);
        HashMap<String, ClassBean> map = new HashMap<>();
        map.put("s",new ClassBean.Builder("String","").build());
        map.put("foo",new ClassBean.Builder("Foo","").build());
        methodBuilder.setParameters(map);
        ArrayList<InstanceVariableBean> listaVariabiliUsate = new ArrayList<>();
        listaVariabiliUsate.add(new InstanceVariableBean("mobilePhone", "Phone", "", "private"));
        listaVariabiliUsate.add(new InstanceVariableBean("bar", "int", "", "private"));
        InstanceVariableBeanList listaConcreta = new InstanceVariableList();
        ((InstanceVariableList) listaConcreta).setList(listaVariabiliUsate);
        methodBuilder.setInstanceVariableList(listaConcreta);

        ArrayList<MethodBean> listaMetodiUsati = new ArrayList<>();
        listaMetodiUsati.add(methodBuilder.build());
        MethodList listaMetodi = new MethodList();
        listaMetodi.setList(listaMetodiUsati);
        builder.setMethods(listaMetodi);

        //CREO PACKAGE BEAN PER IL BELONGING PACKAGE
        PackageBean.Builder packageBeanBuilder = new PackageBean.Builder("ParserDataTest", "package ParserDataTest;\n" +
                "\n" +
                "public class Customer {\n" +
                "    private Phone mobilePhone;\n" +
                "    private int bar;\n" +
                "\n" +
                "    public String getMobilePhoneNumber(String s,Foo foo) {\n" +
                "        bar = 3;\n" +
                "        return \"(\" +\n" +
                "                mobilePhone.getAreaCode() + \") \" +\n" +
                "                mobilePhone.getPrefix() + \"-\" +\n" +
                "                mobilePhone.getNumber();\n" +
                "    }\n" +
                "}");
        builder.setBelongingPackage(packageBeanBuilder.build());

        PsiParser parser = new PsiParser(project);
        testClassBean = parser.parse(psiSource);
        oracleClassBean = builder.build();
        Assert.assertEquals(oracleClassBean.getFullQualifiedName(), testClassBean.getFullQualifiedName());
        Assert.assertEquals(oracleClassBean.getTextContent(), testClassBean.getTextContent());
        //Assert.assertEquals(oracleClassBean, testClassBean);




        //oracleClassBean = builder.build();

    }

    public void testGlobal() {

        PsiParser parser = new PsiParser(myFixture.getProject());
        parser.setRepository(new PackageRepository(),new ClassRepository(),new MethodRepository(),new InstanceVariableRepository());
        myFixture.configureByFiles("ParserDataTest/bCustomer.java","ParserDataTest/bPhone.java");

        /*StringBuilder dbPath = new StringBuilder();
        dbPath.append(System.getProperty("user.home"));
        dbPath.append(File.separator);
        dbPath.append(".ascetic");
        dbPath.append(File.separator);
        dbPath.append(myFixture.getProject().getName());
        dbPath.append(".db");

        File dbFile = new File(dbPath.toString());
        if(dbFile.exists()){
            dbFile.delete();
        }*/

        try {
            parser.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
            fail();
        } catch (RepositoryException e) {
            e.printStackTrace();
            fail();
        }
    }
    /*
    public void testPackageBean(){
        PackageBean oraclePackageBean,testPackageBean;
        Assert.assertEquals(oraclePackageBean,testPackageBean);
    }*/
}


