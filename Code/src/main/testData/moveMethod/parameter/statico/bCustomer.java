package moveMethod.parameter.statico;

public class Customer {
    private int bar;

    public static String getMobilePhoneNumber(String s,Foo foo,Phone mobilePhone) {
        bar = 3;
        return "(" +
                mobilePhone.getAreaCode() + ") " +
                mobilePhone.getPrefix() + "-" +
                mobilePhone.getNumber();
    }
}