package vn.ptit.library.model;

public class Employee extends Member {
    private Integer tblMemberid;

    public Integer getTblMemberid() {
        return tblMemberid;
    }

    public void setTblMemberid(Integer value) {
        this.tblMemberid = value;
    }

    private String jobPosition;

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String value) {
        this.jobPosition = value;
    }
}
