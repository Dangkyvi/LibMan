<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>
<% String pageTitle = "Báo cáo thư viện"; %>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<nav>
    <a href="ManagementMainUI.jsp">Quản lý</a>
    / Báo cáo
</nav>
<div class="actions report-actions">
    <form method="get" action="StatisticPeriodUI.jsp">
        <button type="submit" name="btnReaderStatistic">Thống kê độc giả theo số lần mượn</button>
    </form>
    <button type="button" name="btnDocumentStatistic">Thống kê tài liệu</button>
    <button type="button" name="btnSupplierStatistic">Thống kê nhà cung cấp</button>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
