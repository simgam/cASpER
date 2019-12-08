package it.unisa.casper.storage.beans;

import java.util.Objects;

/**
 * oggetto che istanzia una variabile di istanza
 */
public class InstanceVariableBean {

    private String visibility; //visibilità della variabile di istanza
    private String fullQualifiedName; // fqn della variabile
    private String type;// tipo della variabile
    private String initialization; // inizializzazione della variabile

    /**
     * costruttore
     *
     * @param fullQualifiedName
     * @param type
     * @param initialization
     */
    public InstanceVariableBean(String fullQualifiedName, String type, String initialization, String visibility) {
        this.fullQualifiedName = fullQualifiedName;
        this.type = type;
        this.initialization = initialization;
        this.visibility = visibility;
    }

    /**
     * getter
     *
     * @return fqn della variabile
     */
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    /**
     * getter
     *
     * @return tipo della variabile
     */
    public String getType() {
        return type;
    }

    /**
     * getter
     *
     * @return inizializzazione della variabile
     */
    public String getInitialization() {
        return initialization;
    }

    /**
     * getter
     *
     * @return visibilità della variabile
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * setter
     *
     * @return pVisibility visibilità della variabile
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * setter
     *
     * @param fullQualifiedName fqn da settare
     */
    public void setFullQualifiedName(String fullQualifiedName) {
        this.fullQualifiedName = fullQualifiedName;
    }

    /**
     * setter
     *
     * @param type tipo da settare
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * setter
     *
     * @param initialization inizializzazione da settare
     */
    public void setInitialization(String initialization) {
        this.initialization = initialization;
    }

    /**
     * override
     *
     * @return fqn, tipo e inizializzazione
     */
    @Override
    public String toString() {
        return "InstanceVariableBean{" +
                "fullQualifiedName='" + fullQualifiedName + '\'' +
                ", type='" + type + '\'' +
                ", initialization='" + initialization + '\'' +
                ", visibility='" + visibility + '\'' +
                '}';
    }

    /**
     * override
     *
     * @param o oggetto da confrontare
     * @return true se le due variabili confrontate sono uguali,false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceVariableBean)) return false;
        InstanceVariableBean that = (InstanceVariableBean) o;
        return Objects.equals(visibility, that.visibility) &&
                Objects.equals(fullQualifiedName, that.fullQualifiedName) &&
                Objects.equals(type, that.type) &&
                Objects.equals(initialization, that.initialization);
    }

}