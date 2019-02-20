package moveMethod.parameter;

public class Customer {
    private int bar;

    public String getMobilePhoneNumber(String s,Foo foo,Phone mobilePhone) {
        bar = 3;
        return "(" +
                mobilePhone.getAreaCode() + ") " +
                mobilePhone.getPrefix() + "-" +
                mobilePhone.getNumber();
    }
}