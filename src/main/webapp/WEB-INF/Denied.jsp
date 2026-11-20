<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>
<% String pageTitle = "Không có quyền truy cập"; %>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<p class="alert" role="alert">Bạn không có quyền truy cập chức năng này.</p>
<a href="<%=request.getContextPath()%>/index.jsp">Về trang chính</a>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
