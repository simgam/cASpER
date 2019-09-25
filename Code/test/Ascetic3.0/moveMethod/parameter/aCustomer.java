package moveMethod.parameter;

public class Customer {
    private int bar;

    public String getMobilePhoneNumber(String s, Foo foo, Phone mobilePhone) {
        return mobilePhone.getMobilePhoneNumber(s, foo, bar);
    }
}