package vn.ptit.library.model;
public class ReturnReceiptDetail {
 private int id;
 public int getId() { return id; }
 public void setId(int value) { this.id = value; }
 private int receiptId;
 public int getReceiptId() { return receiptId; }
 public void setReceiptId(int value) { this.receiptId = value; }
 private String documentCondition;
 public String getDocumentCondition() { return documentCondition; }
 public void setDocumentCondition(String value) { this.documentCondition = value; }
 private Document document;
 public Document getDocument() { return document; }
 public void setDocument(Document value) { this.document = value; }
}
