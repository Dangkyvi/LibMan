package vn.ptit.library.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import vn.ptit.library.model.Member;

import java.io.IOException;
import java.util.Set;

public class AccessFilter implements Filter {
    private static final Set<String> READER =
            Set.of("/ReaderMainUI.jsp", "/DocumentSearchUI.jsp", "/DocumentDetailsUI.jsp");
    private static final Set<String> MANAGEMENT =
            Set.of(
                    "/ManagementMainUI.jsp",
                    "/ReportUI.jsp",
                    "/StatisticPeriodUI.jsp",
                    "/ReaderStatisticsUI.jsp",
                    "/ReaderBorrowingHistoryUI.jsp",
                    "/BorrowingReceiptDetailsUI.jsp");

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse s = (HttpServletResponse) res;
        r.setCharacterEncoding("UTF-8");
        s.setCharacterEncoding("UTF-8");
        s.setHeader("X-Content-Type-Options", "nosniff");
        s.setHeader("X-Frame-Options", "DENY");
        s.setHeader(
                "Content-Security-Policy",
                "default-src 'self'; style-src 'self'; form-action 'self'; frame-ancestors 'none';"
                    + " base-uri 'self'");
        String path = r.getServletPath();
        if (path.startsWith("/assets/")) {
            chain.doFilter(req, res);
            return;
        }
        s.setHeader("Cache-Control", "no-store");
        HttpSession session = r.getSession(false);
        Member m = session == null ? null : (Member) session.getAttribute("member");
        if (m == null
                && !path.equals("/LoginUI.jsp")
                && !path.equals("/index.jsp")
                && !path.equals("/")) {
            s.sendRedirect(r.getContextPath() + "/LoginUI.jsp");
            return;
        }
        if ((READER.contains(path) && (m == null || !"READER".equals(m.getRole())))
                || (MANAGEMENT.contains(path)
                        && (m == null || !"MANAGEMENT".equals(m.getRole())))) {
            s.setStatus(403);
            r.setAttribute("accessDenied", true);
            r.getRequestDispatcher("/WEB-INF/Denied.jsp").forward(r, s);
            return;
        }
        chain.doFilter(req, res);
    }
}
