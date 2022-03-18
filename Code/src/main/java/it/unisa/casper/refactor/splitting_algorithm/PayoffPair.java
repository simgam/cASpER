package it.unisa.casper.refactor.splitting_algorithm;

public class PayoffPair {
    private final double payoffI;
    private final double payoffJ;
    private boolean maxSally;
    private boolean maxTom;

    public PayoffPair(double payoffI, double payoffJ){
        this.payoffI = payoffI;
        this.payoffJ = payoffJ;
        this.maxSally = false;
        this.maxTom = false;
    }

    public boolean isMaximumSally() {
        return maxSally;
    }

    public void setMaximumSally(boolean maxSally) {
        this.maxSally = maxSally;
    }

    public boolean isMaximumTom() {
        return maxTom;
    }

    public void setMaximumTom(boolean maxTom) {
        this.maxTom = maxTom;
    }

    public double getPayoffI() {
        return this.payoffI;
    }

    public double getPayoffJ(){
        return this.payoffJ;
    }

    @Override
    public String toString() {
        return "PayoffPair{" +
                "payoffI=" + payoffI +
                ", payoffJ=" + payoffJ +
                ", maxSally=" + maxSally +
                ", maxTom=" + maxTom +
                '}';
    }
}
