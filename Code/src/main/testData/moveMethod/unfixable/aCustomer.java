package moveMethod.unfixable;

public class Customer {
    private int bar;

    public String getMobilePhoneNumber(String s,Foo foo) {
        Phone mobilePhone = new Phone();
        bar = 3;
        return "(" +
                mobilePhone.getAreaCode() + ") " +
                mobilePhone.getPrefix() + "-" +
                mobilePhone.getNumber();
    }
}
