package it.unisa.casper.storage.beans;

import it.unisa.casper.analysis.code_smell.CodeSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * oggetto che istanzia un metodo
 */
public class MethodBean implements Comparable {

    private String fullQualifiedName; // obbligatorio full qualified name del metodo
    private String textContent; // obbligatorio contenuto testuale del metodo
    private ClassBean returnType;// tipo di ritorno del metodo
    private HashMap<String, ClassBean> parameters; //lista di parametri passati al metodo
    private InstanceVariableBeanList instanceVariables; // lista di variabili di istanza usate dal metodo
    private MethodBeanList methodsCalls;// lista di metodi chiamati da un metodo
    private ClassBean belongingClass; // classe alla quale il metodo appartiene
    private ClassBean enviedClass; // classe invidiata dal metodo, se esistente
    private boolean staticMethod; // boolean che stabilisce se il metodo è statico
    private boolean isDefaultCostructor;// boolean che stabilisce se il costruttore utilizzato è quello di default
    private List<CodeSmell> affectedSmell; // lista di smell dai quali è affetto il metodo
    private String visibility; //visibilità del metodo

    /**
     * costruttore
     *
     * @param builder builder che istanzia effettivamente l'oggetto
     */
    private MethodBean(Builder builder) {
        fullQualifiedName = builder._fullQualifiedName;
        textContent = builder._textContent;
        returnType = builder._returnType;
        parameters = builder._parameters;
        instanceVariables = builder._instanceVariables;
        methodsCalls = builder._methodsCalls;
        belongingClass = builder._belongingClass;
        enviedClass = builder._enviedClass;
        staticMethod = builder._staticMethod;
        isDefaultCostructor = builder._isDefaultCostructor;
        if (builder._affectedSmell != null) {
            affectedSmell = builder._affectedSmell;
        } else {
            affectedSmell = new ArrayList<>();
        }
        visibility = builder._visibility;
    }

    /**
     * metodo che stabilisce se il metodo è affetto da un code smell
     *
     * @param smell smell del quale verificare la presenza
     * @return true se il metodo è affetto,false altrimenti
     */
    public boolean isAffected(CodeSmell smell) {
        return smell.affects(this);
    }

    /**
     * getter
     *
     * @return fqn del metodo
     */
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    /**
     * getter
     *
     * @return contenuto testuale del metodo
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * getter
     *
     * @return tipo di ritorno del metodo
     */
    public ClassBean getReturnType() {
        return returnType;
    }

    /**
     * getter
     *
     * @return parametri usati dal metodo
     */
    public HashMap<String, ClassBean> getParameters() {
        if (parameters == null) return null;
        return parameters;
    }

    /**
     * getter
     *
     * @return lista di variabili utilizzate dal metodo
     */
    public List<InstanceVariableBean> getInstanceVariableList() {
        if (instanceVariables == null) return null;
        return instanceVariables.getList();
    }

    /**
     * aggiunge una lista di variabili al metodo
     *
     * @param bean variabile da aggiungere
     */
    public void addInstanceVariableList(InstanceVariableBean bean) {
        if (instanceVariables != null && instanceVariables.getList() != null)
            instanceVariables.getList().add(bean);
    }

    /**
     * rimuove una variabile dalla lista
     *
     * @param bean variabile da rimuovere
     */
    public void removeInstanceVariableList(InstanceVariableBean bean) {
        if (instanceVariables != null && instanceVariables.getList() != null)
            instanceVariables.getList().remove(bean);
    }

    /**
     * getter
     *
     * @return lista di metodi chiamati
     */
    public List<MethodBean> getMethodsCalls() {
        if (methodsCalls == null) return null;
        return methodsCalls.getList();
    }

    /**
     * getter
     *
     * @return la classe alla quale appartiene il metodo
     */
    public ClassBean getBelongingClass() {
        return belongingClass;
    }

    /**
     * setter
     */
    public void setBelongingClass(ClassBean belongingClass) {
        this.belongingClass = belongingClass;
    }

    /**
     * getter
     *
     * @return classe invidiata dal metodo, se esistente
     */
    public ClassBean getEnviedClass() {
        return enviedClass;
    }

    /**
     * setter
     */
    public void setEnviedClass(ClassBean enviedClass) {
        this.enviedClass = enviedClass;
    }

    /**
     * getter
     *
     * @return true se il metodo è statico,false altrimenti
     */
    public boolean getStaticMethod() {
        return staticMethod;
    }

    /**
     * getter
     *
     * @return true se il costruttore è quello default, false altrimenti
     */
    public boolean getDefaultCostructor() {
        return isDefaultCostructor;
    }

    /**
     * getter
     *
     * @return lista di smell dai quali è affetto il metodo
     */
    public List<CodeSmell> getAffectedSmell() {
        return affectedSmell;
    }

    /**
     * aggiunge uno smell alla lista degli smell dai quali è affetto il metodo
     *
     * @param smell smell da aggiungere
     */
    public void addSmell(CodeSmell smell) {
        if (affectedSmell != null) {
            this.affectedSmell.add(smell);
        }
        ;
    }

    /**
     * getter
     *
     * @return visibility del metodo
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * override
     *
     * @return stringa contenente il contenuto di tutte le variabili d'istanza dell'oggetto della classe MethodBean
     */
    @Override
    public String toString() {

        String envied = "'null'", belonging = "'null'";

        if (enviedClass != null) {
            envied = "'" + enviedClass.getFullQualifiedName() + "'";
        }
        ;
        if (belongingClass != null) {
            belonging = "'" + belongingClass.getFullQualifiedName() + "'";
        }
        ;

        return "MethodBean{" +
                "fullQualifiedName='" + fullQualifiedName + '\'' +
                ", textContent='" + textContent + '\'' +
                ", returnType=" + returnType +
                ", parameters=" + parameters +
                ", instanceVariables=" + instanceVariables +
                ", methodsCalls=" + methodsCalls +
                ", belongingClass=" + belonging +
                ", enviedClass=" + envied +
                ", staticMethod=" + staticMethod +
                ", isDefaultCostructor=" + isDefaultCostructor +
                ", affectedSmell=" + affectedSmell +
                ", visibility=" + visibility +
                '}';
    }

    /**
     * override
     *
     * @param o metodo da confrontare
     * @return true se  i metodi sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodBean)) return false;
        MethodBean that = (MethodBean) o;
        return staticMethod == that.staticMethod &&
                isDefaultCostructor == that.isDefaultCostructor &&
                Objects.equals(fullQualifiedName, that.fullQualifiedName) &&
                Objects.equals(textContent, that.textContent) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(belongingClass, that.belongingClass) &&
                Objects.equals(enviedClass, that.enviedClass) &&
                Objects.equals(visibility, that.visibility);
    }

    public int compareTo(Object o) {
        return this.getFullQualifiedName().compareTo(((MethodBean) o).getFullQualifiedName());
    }

    /**
     * builder del metodo
     */
    public static class Builder {

        private String _fullQualifiedName; // obbligatorio
        private String _textContent; // obbligatorio
        private ClassBean _returnType;
        private HashMap<String, ClassBean> _parameters;
        private InstanceVariableBeanList _instanceVariables;
        private MethodBeanList _methodsCalls;
        private ClassBean _belongingClass;
        private ClassBean _enviedClass;
        private boolean _staticMethod;
        private boolean _isDefaultCostructor;
        private List<CodeSmell> _affectedSmell;
        private String _visibility;

        /**
         * setter
         *
         * @param returnType tipo di ritorno da settare
         * @return builder
         */
        public Builder setReturnType(ClassBean returnType) {
            _returnType = returnType;
            return this;
        }

        /**
         * setter
         *
         * @param parameters lista di parametri da settare
         * @return builder
         */
        public Builder setParameters(HashMap<String, ClassBean> parameters) {
            _parameters = parameters;
            return this;
        }

        /**
         * setter
         *
         * @param instanceVariables lista di variabili di istanza utilizzate da settare
         * @return builder
         */
        public Builder setInstanceVariableList(InstanceVariableBeanList instanceVariables) {
            _instanceVariables = instanceVariables;
            return this;
        }

        /**
         * setter
         *
         * @param methodsCalls lista di metodi chiamati da settare
         * @return builder
         */
        public Builder setMethodsCalls(MethodBeanList methodsCalls) {
            _methodsCalls = methodsCalls;
            return this;
        }

        /**
         * setter
         *
         * @param belongingClass classe di appartenenza da settare
         * @return builder
         */
        public Builder setBelongingClass(ClassBean belongingClass) {
            _belongingClass = belongingClass;
            return this;
        }

        /**
         * setter
         *
         * @param enviedClass classe invidiata da settare, se esistente
         * @return builder
         */
        public Builder setEnviedClass(ClassBean enviedClass) {
            _enviedClass = enviedClass;
            return this;
        }

        /**
         * setter
         *
         * @param staticMethod boolean per definire se il metodo è statico
         * @return builder
         */
        public Builder setStaticMethod(boolean staticMethod) {
            _staticMethod = staticMethod;
            return this;
        }

        /**
         * setter
         *
         * @param isDefaultCostructor boolean per definire se il costruttore usato è quello di default
         * @return builder
         */
        public Builder setDefaultCostructor(boolean isDefaultCostructor) {
            _isDefaultCostructor = isDefaultCostructor;
            return this;
        }

        /**
         * setta la lista degli smell dai quali è affetto il metodo
         *
         * @return builder
         */
        public Builder setAffectedSmell() {
            _affectedSmell = new ArrayList<CodeSmell>();
            return this;
        }

        /**
         * setta la visibilità del metodo
         *
         * @return builder
         */
        public Builder setVisibility(String visibility) {
            _visibility = visibility;
            return this;
        }

        /**
         * costruttore
         *
         * @param fullQualifiedName fqn del metodo
         * @param textContent       contenuto testuale del metodo
         */
        public Builder(String fullQualifiedName, String textContent) {
            _fullQualifiedName = fullQualifiedName;
            _textContent = textContent;
        }

        /**
         * costruisce il bean
         *
         * @return un methodbean
         */
        public MethodBean build() {
            return new MethodBean(this);
        }

    }
}