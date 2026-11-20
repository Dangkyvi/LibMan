package vn.ptit.library.dao;

import vn.ptit.library.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class BorrowingReceiptDAO extends DAO {
    public Reader findReader(int readerId) throws SQLException {
        try (Connection c = openConnection();
                PreparedStatement p =
                        c.prepareStatement(
                                "SELECT m.id,m.fullname FROM tblReader r JOIN tblMember m ON"
                                        + " m.id=r.tblMemberid WHERE r.tblMemberid=?")) {
            p.setInt(1, readerId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return null;
                Reader reader = new Reader();
                reader.setId(r.getInt("id"));
                reader.setFullname(r.getString("fullname"));
                return reader;
            }
        }
    }

    public List<BorrowingReceipt> getBorrowingReceipts(
            int readerId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<BorrowingReceipt> list = new ArrayList<>();
        try (Connection c = openConnection();
                PreparedStatement p =
                        c.prepareStatement(
                                "SELECT b.*,m.fullname FROM tblBorrowingReceipt b JOIN tblReader r"
                                    + " ON r.tblMemberid=b.tblReadertblMemberid JOIN tblMember m ON"
                                    + " m.id=r.tblMemberid WHERE b.tblReadertblMemberid=? AND"
                                    + " b.borrowDate BETWEEN ? AND ? ORDER BY b.borrowDate"
                                    + " DESC,b.id DESC")) {
            p.setInt(1, readerId);
            p.setDate(2, java.sql.Date.valueOf(startDate));
            p.setDate(3, java.sql.Date.valueOf(endDate));
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    BorrowingReceipt b = new BorrowingReceipt();
                    b.setId(r.getInt("id"));
                    b.setBorrowDate(r.getDate("borrowDate").toLocalDate());
                    b.setDueDate(r.getDate("dueDate").toLocalDate());
                    b.setStatus(r.getString("status"));
                    Reader reader = new Reader();
                    reader.setId(readerId);
                    reader.setFullname(r.getString("fullname"));
                    b.setReader(reader);
                    LibraryStaff staff = new LibraryStaff();
                    staff.setId(r.getInt("tblLibraryStafftblEmployeetblMemberid2"));
                    b.setLibraryStaff(staff);
                    list.add(b);
                }
            }
        }
        return list;
    }
}
