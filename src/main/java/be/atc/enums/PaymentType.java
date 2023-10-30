package be.atc.enums;

public enum PaymentType {
    Bancontact("Bancontact"),
    Cash("Cash"),
    BankTransfer("Bank Transfer");

    private String text;

    PaymentType(String text) { this.text = text; }
    public String display(){return text;}
}
