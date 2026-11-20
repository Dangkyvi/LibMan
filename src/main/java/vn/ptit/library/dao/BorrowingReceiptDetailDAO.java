package vn.ptit.library.dao;

import vn.ptit.library.model.*;

import java.sql.*;
import java.util.*;

public class BorrowingReceiptDetailDAO extends DAO {
    public boolean receiptExists(int receiptId) throws SQLException {
        try (Connection c = openConnection();
                PreparedStatement p =
                        c.prepareStatement("SELECT id FROM tblBorrowingReceipt WHERE id=?")) {
            p.setInt(1, receiptId);
            try (ResultSet r = p.executeQuery()) {
                return r.next();
            }
        }
    }

    public List<BorrowingReceiptDetail> getDetails(int receiptId) throws SQLException {
        List<BorrowingReceiptDetail> list = new ArrayList<>();
        String sql =
                "SELECT d.*,t.name AS typeName,t.description AS typeDescription,bd.id AS"
                    + " detailId,bd.quantity AS borrowedQuantity,bd.conditionAtBorrowing FROM"
                    + " tblBorrowingReceiptDetail bd JOIN tblDocument d ON d.id=bd.tblDocumentid"
                    + " JOIN tblDocumentType t ON t.id=d.tblDocumentTypeid WHERE"
                    + " bd.tblBorrowingReceiptid=? ORDER BY bd.id";
        try (Connection c = openConnection();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, receiptId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    BorrowingReceiptDetail d = new BorrowingReceiptDetail();
                    d.setId(r.getInt("detailId"));
                    d.setReceiptId(receiptId);
                    d.setQuantity(r.getInt("borrowedQuantity"));
                    d.setConditionAtBorrowing(r.getString("conditionAtBorrowing"));
                    d.setDocument(DocumentDAO.map(r, new Document()));
                    list.add(d);
                }
            }
        }
        return list;
    }
}
