package moveClass.moveClass1;

public class Phone {
   private final String unformattedNumber;
   public Phone Costruttore(String unformattedNumber) {
      this.unformattedNumber = unformattedNumber;
   }
   private String getAreaCode() {
      return unformattedNumber.substring(0,3);
   }
   private String getPrefix() {
      return unformattedNumber.substring(3,6);
   }
   private String getNumber() {
      return unformattedNumber.substring(6,10);
   }
   public String toFormattedString() {

      String formattedString = "(" + getAreaCode() + ") " + getPrefix() + "-" + getNumber();
      return formattedString;
   }
}
