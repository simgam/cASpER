package moveClass.moveClass2;

public class Vampiro{
    protected int forza;

    public Vampiro Costruttore() {
        forza = 15;
    }

    public void azzanna() {
        forza = forza - 2;
    }

    public String getForza() {
        return "Forza rimanente come vampiro:”
                + forza;
    }
}