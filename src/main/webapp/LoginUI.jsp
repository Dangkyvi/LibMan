<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,java.time.*,java.sql.SQLException,vn.ptit.library.model.*,vn.ptit.library.dao.*,vn.ptit.library.web.Ui" %>
<% String pageTitle="Chào mừng bạn trở lại"; 
String error=null;
if(session.getAttribute("csrf")==null)session.setAttribute("csrf",UUID.randomUUID().toString());
if("POST".equals(request.getMethod())){
 if(!Objects.equals(session.getAttribute("csrf"),request.getParameter("csrf"))){response.setStatus(403);error="Phiên biểu mẫu không hợp lệ. Vui lòng thử lại.";}
 else {String username=request.getParameter("username"),password=request.getParameter("password");
  if(username==null||password==null||username.isBlank()||password.isEmpty()||username.length()>100||password.length()>1024){error="Vui lòng nhập tên đăng nhập và mật khẩu hợp lệ.";}
  else try{Member member=new MemberDAO().authenticate(username.strip(),password);if(member==null)error="Tên đăng nhập hoặc mật khẩu không đúng.";
   else{request.changeSessionId();session.setAttribute("member",member);session.setAttribute("csrf",UUID.randomUUID().toString());response.sendRedirect(request.getContextPath()+("READER".equals(member.getRole())?"/ReaderMainUI.jsp":"/ManagementMainUI.jsp"));return;}
  }catch(SQLException e){application.log("Login failed",e);response.setStatus(503);error="Không thể kết nối dữ liệu. Vui lòng thử lại sau.";}
 }
} %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<p class="intro">Đăng nhập để tra cứu tài liệu hoặc xem báo cáo thư viện.</p><section class="panel login"><h2>Đăng nhập</h2>
<% if(error!=null){ %><p class="alert" role="alert"><%=Ui.h(error)%></p><% } %>
<form accept-charset="UTF-8" method="post" action="LoginUI.jsp" class="stack"><input type="hidden" name="csrf" value="<%=Ui.h(session.getAttribute("csrf"))%>"><label>Tên đăng nhập<input name="username" required maxlength="100" autocomplete="username" autofocus></label><label>Mật khẩu<input type="password" name="password" required maxlength="1024" autocomplete="current-password"></label><button>Đăng nhập</button></form></section>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>