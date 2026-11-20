package vn.ptit.library.dao;
import java.sql.*;
import vn.ptit.library.config.Database;
import vn.ptit.library.model.Member;
import vn.ptit.library.security.Passwords;
public class MemberDAO {
 public Member authenticate(String username,String password) throws SQLException {
  String sql="SELECT m.id,m.fullname,m.username,m.password,m.role FROM tblMember m WHERE m.username=? AND m.status='ACTIVE' AND ((m.role='READER' AND EXISTS (SELECT 1 FROM tblReader r WHERE r.tblMemberid=m.id)) OR (m.role='MANAGEMENT' AND EXISTS (SELECT 1 FROM tblManageStaff s WHERE s.tblEmployeetblMemberid2=m.id)))";
  try(Connection c=Database.open();PreparedStatement p=c.prepareStatement(sql)) {
   p.setString(1,username);try(ResultSet r=p.executeQuery()) { if(!r.next() || !Passwords.verify(password,r.getString("password")))return null;
    Member m=new Member();m.setId(r.getInt("id"));m.setFullname(r.getString("fullname"));m.setUsername(r.getString("username"));m.setRole(r.getString("role"));return m;
   }
  }
 }
}
