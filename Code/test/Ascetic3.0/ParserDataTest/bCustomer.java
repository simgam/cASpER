package ParserDataTest;

public class Customer {
    private Phone mobilePhone;
    private int bar;
    private int scemo;

    public String getMobilePhoneNumber(String s,Foo foo) {
        bar = 3;
        return "(" +
                mobilePhone.getAreaCode() + ") " +
                mobilePhone.getPrefix() + "-" +
                mobilePhone.getNumber();
    }

    public String cacca(){
        return "cacca";
    }

}