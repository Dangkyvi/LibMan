package vn.ptit.library.dao;

import vn.ptit.library.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReaderStatisticDAO extends DAO {
    public List<ReaderStatistic> getLoanStatistics(LocalDate startDate, LocalDate endDate)
            throws SQLException {
        List<ReaderStatistic> list = new ArrayList<>();
        String sql =
                "SELECT r.tblMemberid,m.fullname,COUNT(b.id) AS loanCount FROM tblReader r JOIN"
                        + " tblMember m ON m.id=r.tblMemberid JOIN tblBorrowingReceipt b ON"
                        + " b.tblReadertblMemberid=r.tblMemberid WHERE b.borrowDate BETWEEN ? AND ?"
                        + " GROUP BY r.tblMemberid,m.fullname ORDER BY loanCount"
                        + " DESC,m.fullname,r.tblMemberid";
        try (Connection c = openConnection();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setDate(1, java.sql.Date.valueOf(startDate));
            p.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Reader reader = new Reader();
                    reader.setId(r.getInt("tblMemberid"));
                    reader.setFullname(r.getString("fullname"));
                    ReaderStatistic s = new ReaderStatistic();
                    s.setReader(reader);
                    s.setLoanCount(r.getLong("loanCount"));
                    list.add(s);
                }
            }
        }
        return list;
    }
}
