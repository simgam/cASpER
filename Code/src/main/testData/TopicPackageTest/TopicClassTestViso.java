package it.unisa.ascetic.topic.TopicPackageTest;

public class TopicClassTestViso extends TopicClassTestBiometria {
    public TopicClassTestViso(int data, String strumento, String foto) {
        super(data, strumento);
        this.foto=foto;
    }
    public String getFoto() {
        return foto;
    }
    public boolean verifica(TopicClassTestBiometria b) {
        if(b instanceof TopicClassTestViso == false) return false;
        TopicClassTestViso biometria = (TopicClassTestViso) b;
        return getFoto().equals(biometria.getFoto());
    }
    public double similiarità(TopicClassTestBiometria b) throws Exception {
        if(b instanceof TopicClassTestViso == false)
            throw new Exception();
        double sim=0;
        TopicClassTestViso biometria = (TopicClassTestViso) b;
        String foto1= getFoto();
        String foto2= biometria.getFoto();
        for(int i=0;i<foto1.length();i++) {
            if(foto1.charAt(i)==foto2.charAt(i)) {
                sim=sim+1+1/i;
            }
        }
        return sim;
    }
    private String foto;
}
