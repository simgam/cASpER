package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.comparator.ComparableBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * bean che istanzia una classe
 */
public class ClassBean implements ComparableBean, Comparable {

    private String fullQualifiedName; // obbligatorio, full qualified name della classe
    private String textContent; // obbligatorio contenuto testuale della classe
    public InstanceVariableBeanList instanceVariables; // lista di variabili di istanza appartenenti alla classe
    public MethodBeanList methods; // lista di metodi appartenenti alla classe
    private List<String> imports; // lista degli import della classe
    private int LOC; //line of code
    private String superclass; //superclasse, se esistente
    private PackageBean belongingPackage; // package al quale appartiene alla classe
    private PackageBean enviedPackage; // package invidiato dalla classe,se esistente
    private int entityClassUsage; // numero di getter e setter presenti nella classe
    private String pathToFile; // path del file della classe
    private List<CodeSmell> affectedSmell; // lista di code smell presenti nella classe
    private double similarity; //somiglianza generata durante l'analisi con altre classi

    /**
     * Costruttore
     *
     * @param builder builder che si occupa dell'effettivo istanziamento del bean
     */
    protected ClassBean(Builder builder) {
        fullQualifiedName = builder._fullQualifiedName;
        textContent = builder._textContent;
        instanceVariables = builder._instanceVariables;
        methods = builder._methods;
        imports = builder._imports;
        LOC = builder._LOC;
        superclass = builder._superclass;
        belongingPackage = builder._belongingPackage;
        enviedPackage = builder._enviedPackage;
        entityClassUsage = builder._entityClassUsage;
        pathToFile = builder._pathToFile;
        if (builder._affectedSmell != null) {
            affectedSmell = builder._affectedSmell;
        } else {
            affectedSmell = new ArrayList<>();
        }
        similarity = builder._similarity;
    }

    /**
     * Metodo che stabilisce se il bean è affetto da smell
     *
     * @param smell smell del quale verificare la presenza
     * @return true se la classe è affetta dallo smell, false altrimenti
     */
    public boolean isAffected(CodeSmell smell) {
        return smell.affects(this);
    }

    ;

    /***
     * getter
     * @return full qualified name della classe
     */
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    /**
     * getter
     *
     * @return contenuto testuale della classe
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * setter
     *
     * @param textContent contenuto testuale della classe
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    /**
     * getter
     *
     * @return lista delle variabili di istanza appartenenti alla classe
     */
    public List<InstanceVariableBean> getInstanceVariablesList() {
        if (instanceVariables == null) return null;
        return instanceVariables.getList();
    }

    /**
     * metodo che aggiunge una variabile di istanza alla lista delle variabili di istanza
     * appartenenti alla classe
     *
     * @param bean variabile di istanza da aggiungere alla classe
     */
    public void addInstanceVariableList(InstanceVariableBean bean) {
        if (instanceVariables != null && instanceVariables.getList() != null) instanceVariables.getList().add(bean);
    }

    /**
     * metodo che rimuove una variabile di istanza dalla lista delle variabili di istanza
     * appartenenti alla classe
     *
     * @param bean variabile di istanza da rimuovere dalla classe
     */
    public void removeInstanceVariableList(InstanceVariableBean bean) {
        if (instanceVariables != null && instanceVariables.getList() != null) instanceVariables.getList().remove(bean);
    }

    /**
     * getter
     *
     * @return lista dei metodi appartenenti alla classe
     */
    public List<MethodBean> getMethodList() {
        if (methods == null) return null;
        return methods.getList();
    }

    /**
     * aggiunge un metodo alla lista dei metodi appartenenti alla classe
     *
     * @param bean metodo da aggiungere
     */
    public void addMethodBeanList(MethodBean bean) {
        if (methods != null && methods.getList() != null) methods.getList().add(bean);
    }

    /**
     * rimuove un metodo dalla lista dei metodi appartenenti alla classe
     *
     * @param bean metodo da rimuovere
     */
    public void removeMethodBeanList(MethodBean bean) {
        if (methods.getList() != null) methods.getList().remove(bean);
    }

    /**
     * getter
     *
     * @return lista degli import
     */
    public List<String> getImports() {
        return imports;
    }

    /**
     * aggiunge un import alla classe
     *
     * @param i import da aggiungere
     */
    public void addImports(String i) {
        imports.add(i);
    }

    /**
     * rimuove un import dalla classe
     *
     * @param i import da rimuovere
     */
    public void removeImports(String i) {
        imports.remove(i);
    }

    /**
     * getter
     *
     * @return linee di codice
     */
    public int getLOC() {
        return LOC;
    }

    /**
     * getter
     *
     * @return superclasse della classe
     */
    public String getSuperclass() {
        return superclass;
    }

    /**
     * getter
     *
     * @return package al quale appartiene la classe
     */
    public PackageBean getBelongingPackage() {
        return belongingPackage;
    }

    /**
     * setter
     *
     * @param belongingPackage classe di appartenenza da settare
     */
    public void setBelongingPackage(PackageBean belongingPackage) {
        this.belongingPackage = belongingPackage;
    }

    /**
     * getter
     *
     * @return package invidiato dalla classe, se esistente
     */
    public PackageBean getEnviedPackage() {
        return enviedPackage;
    }

    /**
     * setter
     *
     * @param enviedPackage package da settare come invidiato, se esistente
     */
    public void setEnviedPackage(PackageBean enviedPackage) {
        this.enviedPackage = enviedPackage;
    }

    /**
     * getter
     *
     * @return numero di getter e setter della classe
     */
    public int getEntityClassUsage() {
        return entityClassUsage;
    }

    /**
     * getter
     *
     * @return path del file della classe
     */
    public String getPathToFile() {
        return pathToFile;
    }

    /**
     * getter
     *
     * @return lista degli smell dai quali è affetta la classe
     */
    public List<CodeSmell> getAffectedSmell() {
        return affectedSmell;
    }

    /**
     * aggiunge uno smell alla lista degli smell appartenenti alla classe
     *
     * @param smell smell da aggiungere
     */
    public void addSmell(CodeSmell smell) {
        if (affectedSmell != null) {
            affectedSmell.add(smell);
        }
    }

    /**
     * getter
     *
     * @return similarity somiglianza della classe
     */
    public double getSimilarity() {
        return similarity;
    }


    /**
     * setter
     *
     * @param similarity della classe
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * override
     *
     * @return stringa contenente il contenuto di tutte le variabili d'istanza dell'oggetto della classe ClassBean
     */
    @Override
    public String toString() {

        String listImport = "'null'", belonging = "'null'", envied = "'null'";

        if (imports != null) {
            listImport = "'" + getImports().toString() + "'";
        }
        ;
        if (belongingPackage != null) {
            belonging = "'" + belongingPackage.getFullQualifiedName() + "'";
        }
        ;
        if (enviedPackage != null) {
            envied = "'" + enviedPackage.getFullQualifiedName() + "'";
        }
        ;

        return "ClassBean{" +
                "fullQualifiedName='" + fullQualifiedName + '\'' +
                ", textContent='" + textContent + '\'' +
                ", instanceVariables=" + instanceVariables +
                ", methods=" + methods +
                ", imports=" + listImport +
                ", LOC=" + LOC +
                ", superclass='" + superclass + '\'' +
                ", belongingPackage=" + belonging +
                ", enviedPackage=" + envied +
                ", entityClassUsage=" + entityClassUsage +
                ", pathToFile='" + pathToFile + '\'' +
                ", affectedSmell=" + affectedSmell +
                '}';
    }

    /**
     * override
     *
     * @param o oggetto da confrontare
     * @return true se gli oggetti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassBean)) return false;
        ClassBean classBean = (ClassBean) o;
        return LOC == classBean.LOC &&
                entityClassUsage == classBean.entityClassUsage &&
                Objects.equals(fullQualifiedName, classBean.fullQualifiedName) &&
                Objects.equals(textContent, classBean.textContent) &&
                Objects.equals(superclass, classBean.superclass) &&
                Objects.equals(belongingPackage, classBean.belongingPackage) &&
                Objects.equals(pathToFile, classBean.pathToFile);
    }

    public int compareTo(Object pClassBean) {
        return this.getFullQualifiedName().compareTo(((ClassBean) pClassBean).getFullQualifiedName());
    }

    /**
     * builder della classe
     */
    public static class Builder {

        private String _fullQualifiedName; // obbligatorio
        private String _textContent; // obbligatorio
        private InstanceVariableBeanList _instanceVariables;
        private MethodBeanList _methods;
        private List<String> _imports;
        private int _LOC;
        private String _superclass;
        private PackageBean _belongingPackage;
        private PackageBean _enviedPackage;
        private int _entityClassUsage;
        private String _pathToFile;
        private List<CodeSmell> _affectedSmell;
        private double _similarity;

        /**
         * setter
         *
         * @param aInstanceVariable variabili di istanza da settare
         * @return builder
         */
        public Builder setInstanceVariables(InstanceVariableBeanList aInstanceVariable) {
            _instanceVariables = aInstanceVariable;
            return this;
        }

        /**
         * setter
         *
         * @param methods metodi da settare
         * @return builder
         */
        public Builder setMethods(MethodBeanList methods) {
            _methods = methods;
            return this;
        }

        /**
         * setter
         *
         * @param imports import da settare
         * @return builder
         */
        public Builder setImports(List<String> imports) {
            _imports = imports;
            return this;
        }

        /**
         * setter
         *
         * @param LOC loc da settare
         * @return builder
         */
        public Builder setLOC(int LOC) {
            _LOC = LOC;
            return this;
        }

        /**
         * setter
         *
         * @param superclass superclasse da settare
         * @return builder
         */
        public Builder setSuperclass(String superclass) {
            if (superclass == null) _superclass = null;
            _superclass = superclass;
            return this;
        }

        /**
         * setter
         *
         * @param belongingPackage package da settare come appartenente della classe
         * @return builder
         */
        public Builder setBelongingPackage(PackageBean belongingPackage) {
            _belongingPackage = belongingPackage;
            return this;
        }

        /**
         * setter
         *
         * @param enviedPackage package da settare come invidiato, se esistente
         * @return builder
         */
        public Builder setEnviedPackage(PackageBean enviedPackage) {
            _enviedPackage = enviedPackage;
            return this;
        }

        /**
         * setter
         *
         * @param entityClassUsage entity class usage da settare
         * @return builder
         */
        public Builder setEntityClassUsage(int entityClassUsage) {
            _entityClassUsage = entityClassUsage;
            return this;
        }

        /**
         * setter
         *
         * @param pathToFile path da settare
         * @return builder
         */
        public Builder setPathToFile(String pathToFile) {
            _pathToFile = pathToFile;
            return this;
        }

        /**
         * setter
         *
         * @return builder
         */
        public Builder setAffectedSmell() {
            _affectedSmell = new ArrayList<CodeSmell>();
            return this;
        }

        /**
         * setter
         *
         * @param similarity somiglianza da settare
         * @return builder
         */
        public Builder setSimilarity(double similarity) {
            _similarity = similarity;
            return this;
        }

        /**
         * costruttore
         *
         * @param fullQualifiedName fqn della classe
         * @param textContent       contenuto testuale della classe
         */
        public Builder(String fullQualifiedName, String textContent) {
            _fullQualifiedName = fullQualifiedName;
            _textContent = textContent;
        }

        /**
         * costruisce il bean
         *
         * @return classbean istanziato
         */
        public ClassBean build() {
            return new ClassBean(this);
        }

    }
}