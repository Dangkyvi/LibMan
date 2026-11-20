<%@ page
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui"
%>

<%
    if (!"POST".equals(request.getMethod())
            || !Objects.equals(session.getAttribute("csrf"), request.getParameter("csrf"))) {
        response.sendError(403);
        return;
    }
    session.invalidate();
    response.sendRedirect(request.getContextPath() + "/LoginUI.jsp");
%>
