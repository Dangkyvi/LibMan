<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>

<%
    Member m = (Member) session.getAttribute("member");
    response.sendRedirect(
            request.getContextPath()
                    + (m == null
                            ? "/LoginUI.jsp"
                            : "READER".equals(m.getRole())
                                    ? "/ReaderMainUI.jsp"
                                    : "/ManagementMainUI.jsp"));
%>
