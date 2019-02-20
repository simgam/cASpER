/*
package it.unisa.ascetic.topic;

import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statico org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;


class TopicExtracterTest {

    private TopicExtracter topicExtracterTest;

    @BeforeEach
    void setUp() {
        topicExtracterTest = new TopicExtracter();
    }

    @AfterEach
    void tearDown() {
        topicExtracterTest = null;
    }

    @Test
    void extractTopicFromNullPackageBeanTest() throws Exception {
        try {
            PackageBean packageBean=null; // PackageBean nullo

            */
/* Topic vuoti creati *//*

            TreeMap<String, Integer> packageBeanToCompare = new TreeMap<String, Integer>();

            TreeMap<String, Integer> packageBeanTopicTested = topicExtracterTest.extractTopicFromPackageBean(packageBean);
            assertEquals(packageBeanToCompare,packageBeanTopicTested, "I topic del packagebean non corrispondono a quelli dell'oracolo");
        }  catch(Exception e) {
            System.out.println("PackageBean is null");
        }
    }

    @Test
    void extractTopicFromEmptyPackageBeanTest() throws FileNotFoundException {
        */
/* Carica le tre classi di test del package vuoto *//*

        File file1 = new File("testData\\TopicEmptyPackageTest\\TopicClassTestBiometria.java");
        File file2 = new File("testData\\TopicEmptyPackageTest\\TopicClassTestIride.java");
        File file3 = new File("testData\\TopicEmptyPackageTest\\TopicClassTestViso.java");


        */
/* Inserisce in textContent il contenuto delle tre classi (niente) *//*

        Scanner in1 = new Scanner(file1);
        String textContent="";
        while(in1.hasNext()){
            textContent+=in1.nextLine()+"\n";
        }
        Scanner in2 = new Scanner(file2);
        while(in2.hasNext()){
            textContent+=in2.nextLine()+"\n";
        }
        Scanner in3 = new Scanner(file3);
        while(in3.hasNext()){
            textContent+=in3.nextLine()+"\n";
        }

        PackageBean.Builder builder = new PackageBean.Builder("testData.TopicEmptyPackageTest", textContent);
        PackageBean packageBean = builder.build(); // PackageBean packageBean da cui si estraggono i topic

        */
/* TreeMap di topic vuoto *//*

        TreeMap<String, Integer> packageBeanToCompare = new TreeMap<String, Integer>();

        */
/* Confronta package vuoto oracolo con package vuoto caricato *//*

        TreeMap<String, Integer> packageBeanTopicTested = topicExtracterTest.extractTopicFromPackageBean(packageBean);
        assertEquals(packageBeanToCompare,packageBeanTopicTested, "I topic del packagebean non corrispondono a quelli dell'oracolo");

    }

    @Test
    void extractTopicFromPackageBeanTest() throws FileNotFoundException {
        */
/* Carica le tre classi di test *//*

        File file1 = new File("testData\\TopicPackageTest\\TopicClassTestBiometria.java");
        File file2 = new File("testData\\TopicPackageTest\\TopicClassTestIride.java");
        File file3 = new File("testData\\TopicPackageTest\\TopicClassTestViso.java");

        */
/* Inserisce in textContent il contenuto delle tre classi  *//*

        Scanner in1 = new Scanner(file1);
        String textContent = "";
        while (in1.hasNext()) {
            textContent += in1.nextLine() + "\n";
        }
        Scanner in2 = new Scanner(file2);
        while (in2.hasNext()) {
            textContent += in2.nextLine() + "\n";
        }
        Scanner in3 = new Scanner(file3);
        while (in3.hasNext()) {
            textContent += in3.nextLine() + "\n";
        }

        PackageBean.Builder builder = new PackageBean.Builder("testData.TopicPackageTest", textContent);
        PackageBean packageBean = builder.build(); // PackageBean packageBean da cui si estraggono i topic
        TreeMap<String, Integer> packageBeanToCompare = new TreeMap<String, Integer>();

        */
/* Dati oracolo per il test *//*

        packageBeanToCompare.put("biometria", 19);
        packageBeanToCompare.put("topic", 32);
        packageBeanToCompare.put("test", 29);
        packageBeanToCompare.put("foto", 15);
        packageBeanToCompare.put("code", 15);

        TreeMap<String, Integer> packageBeanTopicTested = topicExtracterTest.extractTopicFromPackageBean(packageBean);
        assertEquals(packageBeanToCompare, packageBeanTopicTested, "I topic del packagebean non corrispondono a quelli dell'oracolo");
    }

    @Test
    void extractTopicFromNullClassBeanTest() throws Exception {
        try {
            ClassBean classBean=null; // ClassBean nullo

            */
/* Topic vuoti creati *//*

            TreeMap<String, Integer> classBeanToCompare = new TreeMap<String, Integer>();

            TreeMap<String, Integer> classBeanTopicTested = topicExtracterTest.extractTopicFromClassBean(classBean);
            assertEquals(classBeanToCompare,classBeanTopicTested, "I topic del classbean non corrispondono a quelli dell'oracolo");
        }  catch(Exception e) {
            System.out.println("ClassBean is null");
        }
    }

    @Test
    void extractTopicFromEmptyClassBeanTest() throws FileNotFoundException {
        */
/* Carica le tre classi di test. Utilizza per questo test la classe TopicClassTestViso *//*

        //  File file = new File("testData\\TopicPackageTest\\TopicClassTestIride.java");
        File file = new File("testData\\TopicEmptyPackageTest\\TopicClassTestViso.java");
        //  File file = new File("testData\\TopicPackageTest\\TopicClassTestBiometria.java");Scanner in = new Scanner(file);
        Scanner in = new Scanner(file);

        */
/* Inserisce in textContent il contenuto delle classi. Utilizza per questo test la classe TopicClassTestVideo  *//*

        String textContent="";
        while(in.hasNext()){
            textContent+=in.nextLine()+"\n";
        }

        //  ClassBean.Builder builder = new ClassBean.Builder("testData.TopicEmptyPackageTest.TopicClassTestIride", textContent);
        ClassBean.Builder builder = new ClassBean.Builder("testData.TopicEmptyPackageTest.TopicClassTestViso", textContent);
        //  ClassBean.Builder builder = new ClassBean.Builder("testData.TopicEmptyPackageTest.TopicClassTestBiometria", textContent);
        ClassBean classBean = builder.build(); // ClassBean classBean da cui si estraggono i topic

        */
/* TreeMap di topic vuoto *//*

        TreeMap<String, Integer> classBeanToCompare = new TreeMap<String, Integer>();

        */
/* Confronta classe vuota oracolo con classe vuota caricata *//*

        TreeMap<String, Integer> classBeanTopicTested = topicExtracterTest.extractTopicFromClassBean(classBean); // ClassBean aBean da cui si estraggono i topic
        assertEquals(classBeanToCompare, classBeanTopicTested,"I topic del classbean non corrispondono a quelli dell'oracolo");

    }

    @Test
    void extractTopicFromClassBeanTest() throws FileNotFoundException {

        */
/* Carica le tre classi di test. Utilizza per questo test la classe TopicClassTestViso *//*

        //  File file = new File("testData\\TopicPackageTest\\TopicClassTestIride.java");
        File file = new File("testData\\TopicPackageTest\\TopicClassTestViso.java");
        //  File file = new File("testData\\TopicPackageTest\\TopicClassTestBiometria.java");Scanner in = new Scanner(file);
        Scanner in = new Scanner(file);

        */
/* Inserisce in textContent il contenuto delle classi. Utilizza per questo test la classe TopicClassTestVideo  *//*

        String textContent = "";
        while (in.hasNext()) {
            textContent += in.nextLine() + "\n";
        }

        //   ClassBean.Builder builder = new ClassBean.Builder("testData.TopicPackageTest.TopicClassTestIride", textContent);
        ClassBean.Builder builder = new ClassBean.Builder("testData.TopicPackageTest.TopicClassTestViso", textContent);
        //   ClassBean.Builder builder = new ClassBean.Builder("testData.TopicPackageTest.TopicClassTestBiometria", textContent);
        ClassBean classBean = builder.build(); // ClassBean classBean da cui si estraggono i topic
        TreeMap<String, Integer> classBeanToCompare = new TreeMap<String, Integer>();

        */
/* Dati oracolo per il test *//*

      */
/*  classBeanToCompare.put("iris",10);
        classBeanToCompare.put("code",15);
        classBeanToCompare.put("biometria",8);
        classBeanToCompare.put("topic",13);
        classBeanToCompare.put("test",12);
        *//*

        classBeanToCompare.put("biometria", 7);
        classBeanToCompare.put("foto", 15);
        classBeanToCompare.put("viso", 8);
        classBeanToCompare.put("topic", 13);
        classBeanToCompare.put("test", 12);

      */
/*  classBeanToCompare.put("biometria",4);
        classBeanToCompare.put("data",4);
        classBeanToCompare.put("strumento",4);
        classBeanToCompare.put("topic",6);
        classBeanToCompare.put("test",5);
        *//*


        TreeMap<String, Integer> classBeanTopicTested = topicExtracterTest.extractTopicFromClassBean(classBean); // ClassBean aBean da cui si estraggono i topic
        assertEquals(classBeanToCompare, classBeanTopicTested, "I topic del classbean non corrispondono a quelli dell'oracolo");
    }

    @Test
    void extractTopicFromNullMethodBeanTest() throws Exception {
        try {
            MethodBean methodBean=null; // ClassBean nullo

            */
/* Topic vuoti creati *//*

            TreeMap<String, Integer> methodBeanToCompare = new TreeMap<String, Integer>();

            TreeMap<String, Integer> methodBeanTopicTested = topicExtracterTest.extractTopicFromMethodBean(methodBean);
            assertEquals(methodBeanToCompare,methodBeanTopicTested, "I topic del methodbean non corrispondono a quelli dell'oracolo");
        }  catch(Exception e) {
            System.out.println("MethodBean is null");
        }
    }

    @Test
    void extractTopicFromEmptyMethodBeanTest() {
        */
/* Inserisce in textContent il contenuto del metodo . Il metodo è preso dalla classe TopicClassTestViso *//*

        MethodBean.Builder builder = new MethodBean.Builder("testData.TopicEmptyPackageTest.TopicClassTestViso.similiarità", "");
        MethodBean methodBean = builder.build(); // MethodBean methodBean da cui si estraggono i topic

        */
/* TreeMap di topic vuoto *//*

        TreeMap<String, Integer> methodBeanToCompare = new TreeMap<String, Integer>();

        */
/* Confronta metodo vuoto oracolo con metodo vuoto caricato *//*

        TreeMap<String, Integer> methodBeanTopicTested = topicExtracterTest.extractTopicFromMethodBean(methodBean);
        assertEquals(methodBeanToCompare, methodBeanTopicTested, "I topic del methodbean non corrispondono a quelli dell'oracolo");
    }

    @Test
    void extractTopicFromMethodBeanTest() {
        */
/* Inserisce in textContent il contenuto del metodo . Il metodo è preso dalla classe TopicClassTestViso *//*

        MethodBean.Builder builder = new MethodBean.Builder("testData.TopicPackageTest.TopicClassTestViso.similiarità", "public double similiarità(TopicClassTestBiometria b) throws WrongTypeException {\n" +
                "        if(b instanceof TopicClassTestViso == false)\n" +
                "            throw new WrongTypeException();\n" +
                "        double sim=0;\n" +
                "        TopicClassTestViso biometria = (TopicClassTestViso) b;\n" +
                "        String foto1= getFoto();\n" +
                "        String foto2= biometria.getFoto();\n" +
                "        for(int i=0;i<foto1.length();i++) {\n" +
                "            if(foto1.charAt(i)==foto2.charAt(i)) {\n" +
                "                sim=sim+1+1/i;\n" +
                "            }\n" +
                "        }\n" +
                "        return sim;");

        MethodBean methodBean = builder.build(); // MethodBean methodBean da cui si estraggono i topic
        TreeMap<String, Integer> methodBeanToCompare = new TreeMap<String, Integer>();

        */
/* Dati oracolo per il test *//*

        methodBeanToCompare.put("topic", 4);
        methodBeanToCompare.put("test", 4);
        methodBeanToCompare.put("biometria", 3);
        methodBeanToCompare.put("foto", 7);
        methodBeanToCompare.put("sim", 4);

        TreeMap<String, Integer> methodBeanTopicTested = topicExtracterTest.extractTopicFromMethodBean(methodBean);
        assertEquals(methodBeanToCompare, methodBeanTopicTested, "I topic del methodbean non corrispondono a quelli dell'oracolo");
    }

}*/
