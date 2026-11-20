package vn.ptit.library.model;

public class Member {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String value) {
        this.fullname = value;
    }

    private Integer address;

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer value) {
        this.address = value;
    }

    private java.time.LocalDate dateOfBirth;

    public java.time.LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.time.LocalDate value) {
        this.dateOfBirth = value;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String value) {
        this.role = value;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }
}
