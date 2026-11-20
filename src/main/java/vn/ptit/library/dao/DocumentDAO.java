package vn.ptit.library.dao;

import vn.ptit.library.model.*;

import java.sql.*;
import java.util.*;

public class DocumentDAO extends DAO {
    private static final String BASE =
            "SELECT d.*,t.name AS typeName,t.description AS typeDescription FROM tblDocument d JOIN"
                    + " tblDocumentType t ON t.id=d.tblDocumentTypeid ";

    static <T extends Document> T map(ResultSet r, T d) throws SQLException {
        d.setId(r.getInt("id"));
        d.setTitle(r.getString("title"));
        d.setAuthor(r.getString("author"));
        DateHelper.publication(r, d);
        d.setPublisher(r.getString("publisher"));
        d.setQuantity(r.getInt("quantity"));
        d.setStorageLocation(r.getString("storageLocation"));
        d.setDescription(r.getString("description"));
        DocumentType t = new DocumentType();
        t.setId(r.getInt("tblDocumentTypeid"));
        t.setName(r.getString("typeName"));
        t.setDescription(r.getString("typeDescription"));
        d.setDocumentType(t);
        return d;
    }

    public List<Document> searchByKeyword(String keyword) throws SQLException {
        List<Document> list = new ArrayList<>();
        try (Connection c = openConnection();
                PreparedStatement p =
                        c.prepareStatement(
                                BASE
                                        + "WHERE LOWER(d.title) LIKE LOWER(?) ESCAPE '!' ORDER BY"
                                        + " d.title,d.id")) {
            p.setString(
                    1,
                    "%"
                            + keyword.strip()
                                    .replace("!", "!!")
                                    .replace("%", "!%")
                                    .replace("_", "!_")
                            + "%");
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r, new Document()));
            }
        }
        return list;
    }

    public Document getDetails(int documentId) throws SQLException {
        try (Connection c = openConnection();
                PreparedStatement p = c.prepareStatement(BASE + "WHERE d.id=?")) {
            p.setInt(1, documentId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return null;
                // Shared primary key identifies the actual subtype; no dependency on translated
                // type names.
                for (String table : List.of("tblBook", "tblTextbook", "tblMagazine")) {
                    try (PreparedStatement q =
                            c.prepareStatement(
                                    "SELECT * FROM " + table + " WHERE tblDocumentid=?")) {
                        q.setInt(1, documentId);
                        try (ResultSet s = q.executeQuery()) {
                            if (s.next()) {
                                if (table.equals("tblBook")) {
                                    Book b = map(r, new Book());
                                    b.setIsbn(s.getString("isbn"));
                                    b.setPageCount(s.getInt("pageCount"));
                                    return b;
                                }
                                if (table.equals("tblTextbook")) {
                                    Textbook b = map(r, new Textbook());
                                    b.setSubject(s.getString("subject"));
                                    b.setMajor(s.getString("major"));
                                    return b;
                                }
                                Magazine b = map(r, new Magazine());
                                b.setIssueNumber(s.getString("issueNumber"));
                                b.setIssuePeriod(s.getString("issuePeriod"));
                                return b;
                            }
                        }
                    }
                }
                return map(r, new Document());
            }
        }
    }

    private static class DateHelper {
        static void publication(ResultSet r, Document d) throws SQLException {
            java.sql.Date date = r.getDate("publicationYear");
            d.setPublicationYear(date == null ? null : date.toLocalDate());
        }
    }
}
