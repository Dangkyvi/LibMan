<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui" %>
<% String pageTitle="Không thể xử lý yêu cầu"; response.setStatus(500); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<p class="alert">Đã xảy ra lỗi. Vui lòng quay về trang chính và thử lại.</p><a href="<%=request.getContextPath()%>/index.jsp">Về trang chính</a>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>