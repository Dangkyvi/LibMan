package vn.ptit.library.model;

public class ImportReceiptDetail {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    private int receiptId;

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int value) {
        this.receiptId = value;
    }

    private int importQuantity;

    public int getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(int value) {
        this.importQuantity = value;
    }

    private int unitPrice;

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int value) {
        this.unitPrice = value;
    }

    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document value) {
        this.document = value;
    }
}
