package vn.ptit.library.model;

public class Document {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String value) {
        this.author = value;
    }

    private java.time.LocalDate publicationYear;

    public java.time.LocalDate getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(java.time.LocalDate value) {
        this.publicationYear = value;
    }

    private String publisher;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String value) {
        this.publisher = value;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int value) {
        this.quantity = value;
    }

    private String storageLocation;

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String value) {
        this.storageLocation = value;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    private DocumentType documentType;

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType value) {
        this.documentType = value;
    }
}
