package vn.ptit.library.model;
public class Book extends Document {
 private String isbn;
 public String getIsbn() { return isbn; }
 public void setIsbn(String value) { this.isbn = value; }
 private int pageCount;
 public int getPageCount() { return pageCount; }
 public void setPageCount(int value) { this.pageCount = value; }
}
