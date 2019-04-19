package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.comparator.ComparableBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * oggetto che istanzia un package
 */
public class PackageBean implements ComparableBean, Comparable {

    private String fullQualifiedName; // obbligatorio full qualified name del package
    private String textContent; // obbligatorio contenuto testuale del package
    public ClassBeanList classes; // lista delle classi appartenenti al metodo
    private List<CodeSmell> affectedSmell; // lista di smell di cui è affetto il package
    private double similarity;

    /**
     * costruttore
     *
     * @param builder builder che effettivamente crea l'oggetto
     */
    protected PackageBean(Builder builder) {
        fullQualifiedName = builder._fullQualifiedName;
        textContent = builder._textContent;
        classes = builder._classes;
        if (builder._affectedSmell != null) {
            affectedSmell = builder._affectedSmell;
        } else {
            affectedSmell = new ArrayList<CodeSmell>();
        }
        similarity = builder._similarity;
    }

    /**
     * metodo che sancisce la presenza di uno smell nel package
     *
     * @param smell smell del quale verificare la presenza
     * @return true se il package è affetto dallo smell, false altrimenti
     */
    public boolean isAffected(CodeSmell smell) {
        return smell.affects(this);
    }

    /**
     * getter
     *
     * @return fqn del package
     */
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    /**
     * getter
     *
     * @return contenuto testuale del package
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * getter
     *
     * @return lista delle classi appartenenti al package
     */
    public List<ClassBean> getClassList() {
        if (classes != null) return classes.getList();
        return null;
    }

    /**
     * aggiunge una classe al package
     *
     * @param bean classe da aggiungere
     */
    public void addClassList(ClassBean bean) {
        if (classes != null && classes.getList() != null) classes.getList().add(bean);
    }

    /**
     * rimuove una classe dalla lista
     *
     * @param bean classe da rimuovere
     */
    public void removeClassList(ClassBean bean) {
        if (classes != null && classes.getList() != null) classes.getList().remove(bean);
    }

    /**
     * getter
     *
     * @return smell di cui è affetto il package
     */
    public List<CodeSmell> getAffectedSmell() {
        return affectedSmell;
    }

    /**
     * aggiunge uno smell alla lista degli smell dai quali è affetto il package
     *
     * @param smell smell da aggiungere
     */
    public void addSmell(CodeSmell smell) {
        if (affectedSmell != null) {
            smell.setIndex(this.similarity);
            this.affectedSmell.add(smell);
        }
    }

    /**
     * rimuove uno smell dalla lista degli smell dai quali è affetto il package
     *
     * @param smell smell da rimuovere
     */
    public void removeSmell(CodeSmell smell) {
        if (affectedSmell != null) affectedSmell.remove(smell);
    }

    /**
     * getter
     *
     * @return similarity somiglianza del package
     */
    public double getSimilarity() {
        return similarity;
    }

    /**
     * setter
     *
     * @return similarity somiglianza del package
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * override
     *
     * @return stringa contenente il contenuto di tutte le variabili d'istanza dell'oggetto della classe PackageBean
     */
    @Override
    public String toString() {
        return "PackageBean{full qualified name='" + fullQualifiedName +
                "',text content='" + textContent +
                "',classes='" + classes + "'}";
    }

    /**
     * override
     *
     * @param o package da confrontare
     * @return true se i package sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageBean)) return false;
        PackageBean that = (PackageBean) o;
        return Objects.equals(fullQualifiedName, that.fullQualifiedName) &&
                Objects.equals(textContent, that.textContent);
    }

    public int compareTo(Object pPackageBean) {
        return this.getFullQualifiedName().compareTo(((PackageBean) pPackageBean).getFullQualifiedName());
    }

    /**
     * builder del package
     */
    public static class Builder {

        private String _fullQualifiedName; // obbligatorio
        private String _textContent; // obbligatorio
        private ClassBeanList _classes;
        private List<CodeSmell> _affectedSmell;
        private double _similarity;

        /**
         * setter
         *
         * @param classes lista di classi da settare
         * @return builder
         */
        public Builder setClassList(ClassBeanList classes) {
            _classes = classes;
            return this;
        }

        /**
         * setta la lista degli smell dai quali è affetto il package
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
         * @param fullQualifiedName fqn del package
         * @param textContent       contenuto testuale del package
         */
        public Builder(String fullQualifiedName, String textContent) {
            _fullQualifiedName = fullQualifiedName;
            _textContent = textContent;
        }

        /**
         * metodo che costruisce il bean
         *
         * @return il bean costruito
         */
        public PackageBean build() {
            return new PackageBean(this);
        }

    }

}
