package be.atc.enums;

public enum PaymentType {
    bancontact("bancontact"),
    cash("cash"),
    banktransfer("banktransfer");

    private String text;

    PaymentType(String text) { this.text = text; }
    public String display(){return text;}
}
