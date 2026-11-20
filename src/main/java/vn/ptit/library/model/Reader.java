package vn.ptit.library.model;

public class Reader extends Member {
    private String CCCD;

    public String getCCCD() {
        return CCCD;
    }

    public void setCCCD(String value) {
        this.CCCD = value;
    }

    private String img;

    public String getImg() {
        return img;
    }

    public void setImg(String value) {
        this.img = value;
    }

    private java.time.LocalDate registrationDate;

    public java.time.LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(java.time.LocalDate value) {
        this.registrationDate = value;
    }

    private String cardStatus;

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String value) {
        this.cardStatus = value;
    }

    private java.time.LocalDate issueDate;

    public java.time.LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(java.time.LocalDate value) {
        this.issueDate = value;
    }

    private java.time.LocalDate expiryDate;

    public java.time.LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(java.time.LocalDate value) {
        this.expiryDate = value;
    }
}
