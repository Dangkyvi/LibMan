<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui" %>
<% String pageTitle="Báo cáo thư viện";  %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<nav><a href="ManagementMainUI.jsp">Quản lý</a> / Báo cáo</nav>
<div class="actions"><a class="button" href="StatisticPeriodUI.jsp">Thống kê độc giả theo số lần mượn</a></div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
