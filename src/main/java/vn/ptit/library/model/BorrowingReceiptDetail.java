package vn.ptit.library.model;
public class BorrowingReceiptDetail {
 private int id;
 public int getId() { return id; }
 public void setId(int value) { this.id = value; }
 private int receiptId;
 public int getReceiptId() { return receiptId; }
 public void setReceiptId(int value) { this.receiptId = value; }
 private int quantity;
 public int getQuantity() { return quantity; }
 public void setQuantity(int value) { this.quantity = value; }
 private String conditionAtBorrowing;
 public String getConditionAtBorrowing() { return conditionAtBorrowing; }
 public void setConditionAtBorrowing(String value) { this.conditionAtBorrowing = value; }
 private Document document;
 public Document getDocument() { return document; }
 public void setDocument(Document value) { this.document = value; }
}
