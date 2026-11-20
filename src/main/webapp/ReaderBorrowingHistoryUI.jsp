<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>

<%
    String pageTitle = "Lịch sử mượn tài liệu";
    String error = null;
    LocalDate[] dates = null;
    try {
        dates = Ui.period(request.getParameter("startDate"), request.getParameter("endDate"));
    } catch (IllegalArgumentException e) {
        response.setStatus(400);
        error = e.getMessage();
    }
    List<BorrowingReceipt> receipts = null;
    Reader reader = null;
    if (error == null)
        try {
            int id = Ui.id(request.getParameter("readerId"));
            BorrowingReceiptDAO dao = new BorrowingReceiptDAO();
            reader = dao.findReader(id);
            if (reader == null) {
                response.setStatus(404);
                error = "Độc giả không tồn tại.";
            } else receipts = dao.getBorrowingReceipts(id, dates[0], dates[1]);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            error = e.getMessage();
        } catch (SQLException e) {
            application.log("Borrowing history", e);
            response.setStatus(503);
            error = "Không thể tải dữ liệu. Vui lòng thử lại sau.";
        }
%>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<nav>
    <a href="ReportUI.jsp">Báo cáo</a>
    /
    <% if (dates != null) { %>
        <a href="ReaderStatisticsUI.jsp?startDate=<%=dates[0]%>&amp;endDate=<%=dates[1]%>">Bảng thống kê</a>
        /
    <% } %>
    Lịch sử
</nav>
<% if (error != null) { %>
    <p class="alert"><%=Ui.h(error)%></p>
<% } else { %>
    <h2><%=Ui.h(reader.getFullname())%></h2>
    <p class="period">
        Khoảng thống kê:
        <strong><%=Ui.date(dates[0])%></strong>
        đến
        <strong><%=Ui.date(dates[1])%></strong>
        ·
        <%=receipts.size()%>
        phiếu
    </p>
    <% if (receipts.isEmpty()) { %>
        <p class="empty">Độc giả không có phiếu mượn trong khoảng thời gian này.</p>
    <% } else { %>
        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Mã phiếu</th>
                        <th>Ngày mượn</th>
                        <th>Hạn trả</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (BorrowingReceipt b : receipts) { %>
                        <tr>
                            <td>PM-<%=b.getId()%></td>
                            <td><%=Ui.h(Ui.date(b.getBorrowDate()))%></td>
                            <td><%=Ui.h(Ui.date(b.getDueDate()))%></td>
                            <td><%=Ui.h(Ui.receiptStatus(b.getStatus()))%></td>
                            <td>
                                <form method="get" action="BorrowingReceiptDetailsUI.jsp">
                                    <input type="hidden" name="receiptId" value="<%=b.getId()%>">
                                    <input type="hidden" name="readerId" value="<%=reader.getId()%>">
                                    <input type="hidden" name="startDate" value="<%=dates[0]%>">
                                    <input type="hidden" name="endDate" value="<%=dates[1]%>">
                                    <button type="submit" name="btnSee"
                                            aria-label="Xem chi tiết phiếu PM-<%=b.getId()%>">Xem</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
<% } %>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
