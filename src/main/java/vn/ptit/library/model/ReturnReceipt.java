package vn.ptit.library.model;
public class ReturnReceipt {
 private int id;
 public int getId() { return id; }
 public void setId(int value) { this.id = value; }
 private java.time.LocalDate returnDate;
 public java.time.LocalDate getReturnDate() { return returnDate; }
 public void setReturnDate(java.time.LocalDate value) { this.returnDate = value; }
 private String status;
 public String getStatus() { return status; }
 public void setStatus(String value) { this.status = value; }
 private Reader reader;
 public Reader getReader() { return reader; }
 public void setReader(Reader value) { this.reader = value; }
 private LibraryStaff libraryStaff;
 public LibraryStaff getLibraryStaff() { return libraryStaff; }
 public void setLibraryStaff(LibraryStaff value) { this.libraryStaff = value; }
 private java.util.List<ReturnReceiptDetail> details = new java.util.ArrayList<>();
 public java.util.List<ReturnReceiptDetail> getDetails() { return details; }
 public void setDetails(java.util.List<ReturnReceiptDetail> value) { this.details = value; }
}
