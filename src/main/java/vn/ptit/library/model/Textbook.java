package vn.ptit.library.model;

public class Textbook extends Document {
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String value) {
        this.subject = value;
    }

    private String major;

    public String getMajor() {
        return major;
    }

    public void setMajor(String value) {
        this.major = value;
    }
}
