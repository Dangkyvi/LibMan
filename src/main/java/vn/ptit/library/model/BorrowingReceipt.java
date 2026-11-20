package vn.ptit.library.model;

public class BorrowingReceipt {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    private java.time.LocalDate borrowDate;

    public java.time.LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(java.time.LocalDate value) {
        this.borrowDate = value;
    }

    private java.time.LocalDate dueDate;

    public java.time.LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.time.LocalDate value) {
        this.dueDate = value;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    private Reader reader;

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader value) {
        this.reader = value;
    }

    private LibraryStaff libraryStaff;

    public LibraryStaff getLibraryStaff() {
        return libraryStaff;
    }

    public void setLibraryStaff(LibraryStaff value) {
        this.libraryStaff = value;
    }

    private java.util.List<BorrowingReceiptDetail> details = new java.util.ArrayList<>();

    public java.util.List<BorrowingReceiptDetail> getDetails() {
        return details;
    }

    public void setDetails(java.util.List<BorrowingReceiptDetail> value) {
        this.details = value;
    }
}
