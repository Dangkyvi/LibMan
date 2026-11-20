package vn.ptit.library.model;

public class ImportReceipt {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    private java.time.LocalDate importDate;

    public java.time.LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(java.time.LocalDate value) {
        this.importDate = value;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    private Supplier supplier;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier value) {
        this.supplier = value;
    }

    private LibraryStaff libraryStaff;

    public LibraryStaff getLibraryStaff() {
        return libraryStaff;
    }

    public void setLibraryStaff(LibraryStaff value) {
        this.libraryStaff = value;
    }

    private java.util.List<ImportReceiptDetail> details = new java.util.ArrayList<>();

    public java.util.List<ImportReceiptDetail> getDetails() {
        return details;
    }

    public void setDetails(java.util.List<ImportReceiptDetail> value) {
        this.details = value;
    }
}
