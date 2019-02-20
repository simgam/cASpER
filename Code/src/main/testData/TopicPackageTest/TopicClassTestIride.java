package it.unisa.ascetic.topic.TopicPackageTest;

public class TopicClassTestIride extends TopicClassTestBiometria {
    public TopicClassTestIride(int data, String strumento, String occhio, String irisCode) {
        super(data, strumento);
        this.occhio=occhio;
        this.irisCode=irisCode;
    }
    public String getOcchio() {
        return occhio;
    }
    public String getIrisCode() {
        return irisCode;
    }
    public boolean verifica(TopicClassTestBiometria b) {
        if(b instanceof TopicClassTestIride == false) return false;
        TopicClassTestIride biometria = (TopicClassTestIride) b;
        return getOcchio().equals(biometria.getOcchio()) && getIrisCode().equals(biometria.getIrisCode());
    }
    public double similiarità(TopicClassTestBiometria b) throws Exception {
        if(b instanceof TopicClassTestIride == false)
            throw new Exception();
        double sim = 0;
        TopicClassTestIride biometria = (TopicClassTestIride) b;
        String code1= getIrisCode();
        String code2= biometria.getIrisCode();
        for(int i=0;i<code1.length();i++) {
            if((code1.charAt(i))==code2.charAt(i)) {
                sim++;
            }
        }
        return sim;
    }
    private String occhio, irisCode;
}
