package moveMethod.global.statico;

public class Customer {
    private Phone mobilePhone;
    private int bar;

    public String getMobilePhoneNumber(String s, Foo foo) {
        return Phone.getMobilePhoneNumber(s, foo, bar);
    }
}