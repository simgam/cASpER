package it.unisa.ascetic.topic.TopicPackageTest;

public abstract class TopicClassTestBiometria {
    public TopicClassTestBiometria(int data, String strumento) {
        this.data=data;
        this.strumento=strumento;
    }
    public abstract boolean verifica(TopicClassTestBiometria b);
    public abstract double similiarità(TopicClassTestBiometria b) throws Exception;
    protected int data;
    private String strumento;
}
