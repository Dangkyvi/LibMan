<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>
<% String pageTitle = "Chọn kỳ thống kê"; %>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<nav>
    <a href="ReportUI.jsp">Báo cáo</a>
    / Khoảng thời gian
</nav>
<section class="panel">
    <h2>Thống kê độc giả theo số lần mượn</h2>
    <p>Tính theo ngày mượn, bao gồm cả ngày bắt đầu và ngày kết thúc.</p>
    <form accept-charset="UTF-8" method="get" action="ReaderStatisticsUI.jsp" class="inline">
        <label>
            Ngày bắt đầu
            <input
                type="date"
                name="startDate"
                value="<%=Ui.h(request.getParameter("startDate"))%>"
                min="1000-01-01"
                max="9999-12-31"
                required
            >
        </label>
        <label>
            Ngày kết thúc
            <input
                type="date"
                name="endDate"
                value="<%=Ui.h(request.getParameter("endDate"))%>"
                min="1000-01-01"
                max="9999-12-31"
                required
            >
        </label>
        <button>Xem thống kê</button>
    </form>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
