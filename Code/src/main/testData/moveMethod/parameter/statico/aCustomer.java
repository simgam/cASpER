package moveMethod.parameter.statico;

public class Customer {
    private int bar;

    public String getMobilePhoneNumber(String s, Foo foo, Phone mobilePhone) {
        return Phone.getMobilePhoneNumber(s, foo, bar);
    }
}