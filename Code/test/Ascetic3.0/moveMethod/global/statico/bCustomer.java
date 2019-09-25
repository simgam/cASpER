package moveMethod.global.statico;

public class Customer {
    private Phone mobilePhone;
    private int bar;

    public static String getMobilePhoneNumber(String s,Foo foo) {
        bar = 3;
        return "(" +
                mobilePhone.getAreaCode() + ") " +
                mobilePhone.getPrefix() + "-" +
                mobilePhone.getNumber();
    }
}