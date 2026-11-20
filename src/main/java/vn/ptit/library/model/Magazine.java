package vn.ptit.library.model;

public class Magazine extends Document {
    private String issueNumber;

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String value) {
        this.issueNumber = value;
    }

    private String issuePeriod;

    public String getIssuePeriod() {
        return issuePeriod;
    }

    public void setIssuePeriod(String value) {
        this.issuePeriod = value;
    }
}
