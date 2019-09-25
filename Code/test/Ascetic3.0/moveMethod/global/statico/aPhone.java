package moveMethod.global.statico;

public class Phone {
    private final String unformattedNumber;

    public Phone(String unformattedNumber) {
        this.unformattedNumber = unformattedNumber;
    }

    public String getAreaCode() {
        return unformattedNumber.substring(0, 3);
    }

    public String getPrefix() {
        return unformattedNumber.substring(3, 6);
    }

    public String getNumber() {
        return unformattedNumber.substring(6, 10);
    }

    public static String getMobilePhoneNumber(String s, Foo foo, int bar) {
        bar = 3;
        return "(" +
                getAreaCode() + ") " +
                getPrefix() + "-" +
                getNumber();
    }
}
