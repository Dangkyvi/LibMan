package vn.ptit.library;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.*;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import vn.ptit.library.config.Database;
import vn.ptit.library.dao.*;
import vn.ptit.library.model.*;
import vn.ptit.library.security.Passwords;
import vn.ptit.library.web.Ui;

import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibraryTest {
    Tomcat tomcat;
    String base;
    LocalDate start = LocalDate.parse("2026-11-01"), end = LocalDate.parse("2026-11-30");

    @BeforeAll
    void setup() throws Exception {
        System.setProperty(
                "DB_URL",
                "jdbc:h2:mem:library;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        System.setProperty("DB_USER", "sa");
        System.setProperty("DB_PASSWORD", "");
        try (Connection c = Database.open()) {
            RunScript.execute(c, Files.newBufferedReader(Path.of("database/schema.sql")));
            RunScript.execute(c, Files.newBufferedReader(Path.of("database/seed.sql")));
        }
        tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("library-tomcat").toString());
        tomcat.setPort(0);
        tomcat.getConnector();
        var context =
                tomcat.addWebapp(
                        "/library", Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(getClass().getClassLoader());
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(
                new DirResourceSet(
                        resources,
                        "/WEB-INF/classes",
                        Path.of("target/classes").toAbsolutePath().toString(),
                        "/"));
        context.setResources(resources);
        tomcat.start();
        base = "http://localhost:" + tomcat.getConnector().getLocalPort() + "/library/";
    }

    @AfterAll
    void stop() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @Test
    void documentsAndSubtypeMapping() throws Exception {
        var dao = new DocumentDAO();
        assertEquals(2, dao.searchByKeyword("Giải tích").size());
        assertTrue(dao.searchByKeyword("no-such-document").isEmpty());
        assertTrue(dao.searchByKeyword("' OR 1=1 --").isEmpty());
        assertTrue(dao.searchByKeyword("%").isEmpty());
        Book b = assertInstanceOf(Book.class, dao.getDetails(1));
        assertEquals("9786040000001", b.getIsbn());
        assertEquals(420, b.getPageCount());
        Textbook t = assertInstanceOf(Textbook.class, dao.getDetails(2));
        assertEquals("Giải tích", t.getSubject());
        assertEquals("Công nghệ thông tin", t.getMajor());
        Magazine m = assertInstanceOf(Magazine.class, dao.getDetails(3));
        assertEquals("11/2026", m.getIssueNumber());
        assertEquals("Hàng tháng", m.getIssuePeriod());
        assertNull(dao.getDetails(99999));
    }

    @Test
    void countsBoundariesAndReceiptRelationships() throws Exception {
        var stats = new ReaderStatisticDAO().getLoanStatistics(start, end);
        assertEquals(2, stats.size());
        assertEquals(1, stats.get(0).getReader().getId());
        assertEquals(2, stats.get(0).getLoanCount());
        assertEquals(1, stats.get(1).getLoanCount());
        for (var s : stats)
            assertEquals(
                    s.getLoanCount(),
                    new BorrowingReceiptDAO()
                            .getBorrowingReceipts(s.getReader().getId(), start, end)
                            .size());
        var receipts = new BorrowingReceiptDAO().getBorrowingReceipts(1, start, end);
        assertEquals(
                Set.of(101, 102),
                new HashSet<>(receipts.stream().map(BorrowingReceipt::getId).toList()));
        assertEquals(
                1, new ReaderStatisticDAO().getLoanStatistics(start, start).get(0).getLoanCount());
        assertEquals(1, new ReaderStatisticDAO().getLoanStatistics(end, end).get(0).getLoanCount());
        var details = new BorrowingReceiptDetailDAO().getDetails(101);
        assertEquals(2, details.size());
        assertEquals(1, details.get(0).getDocument().getId());
        assertEquals(2, details.get(1).getDocument().getId());
        assertEquals(2, details.get(1).getQuantity());
        assertFalse(new BorrowingReceiptDetailDAO().receiptExists(99999));
        assertTrue(
                new ReaderStatisticDAO()
                        .getLoanStatistics(start.minusYears(1), end.minusYears(1))
                        .isEmpty());
    }

    @Test
    void validationAndPasswords() {
        assertThrows(IllegalArgumentException.class, () -> Ui.period("2026-11-30", "2026-11-01"));
        assertThrows(IllegalArgumentException.class, () -> Ui.period("2026-02-30", "2026-11-01"));
        assertThrows(IllegalArgumentException.class, () -> Ui.period(null, ""));
        assertThrows(IllegalArgumentException.class, () -> Ui.id("0"));
        assertThrows(IllegalArgumentException.class, () -> Ui.id("abc"));
        assertEquals("&lt;script&gt;&quot;&#39;&amp;", Ui.h("<script>\"'&"));
        String hash = Passwords.hash("secret");
        assertTrue(Passwords.verify("secret", hash));
        assertFalse(Passwords.verify("wrong", hash));
    }

    HttpClient client() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
    }

    HttpResponse<String> get(HttpClient c, String path) throws Exception {
        return c.send(
                HttpRequest.newBuilder(URI.create(base + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(HttpClient c, String path, String body) throws Exception {
        return c.send(
                HttpRequest.newBuilder(URI.create(base + path))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    String csrf(String html) {
        var m = Pattern.compile("name=\"csrf\" value=\"([^\"]+)\"").matcher(html);
        assertTrue(m.find());
        return m.group(1);
    }

    HttpClient login(String username) throws Exception {
        var c = client();
        String token = csrf(get(c, "LoginUI.jsp").body());
        var r =
                post(
                        c,
                        "LoginUI.jsp",
                        "username=" + username + "&password=ThuVien%40123&csrf=" + token);
        assertEquals(302, r.statusCode(), r.body());
        return c;
    }

    void ok(HttpResponse<String> r, String... strings) {
        assertEquals(200, r.statusCode(), r.body());
        for (String s : strings) assertTrue(r.body().contains(s), s + " missing from " + r.body());
    }

    @Test
    void sharedLayoutUsesUtf8() throws Exception {
        var page = get(client(), "LoginUI.jsp");
        assertTrue(
                page.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .toLowerCase(Locale.ROOT)
                        .contains("charset=utf-8"));
        ok(
                page,
                "KHÔNG GIAN TRI THỨC",
                "Tra cứu thuận tiện · Quản lý hiệu quả",
                "Chào mừng bạn trở lại",
                "· LibMan</title>",
                "<b>LibMan</b>",
                "<footer>");
        assertTrue(Pattern.compile("<footer>\\s*LibMan").matcher(page.body()).find());
    }

    @Test
    void readerJspFlowAndAccess() throws Exception {
        assertEquals(302, get(client(), "DocumentDetailsUI.jsp?documentId=1").statusCode());
        var c = login("reader");
        ok(get(c, "ReaderMainUI.jsp"), "DocumentSearchUI.jsp", ">Tìm tài liệu</a>");
        ok(get(c, "DocumentSearchUI.jsp"), "Nhập từ khóa để bắt đầu");
        ok(
                get(
                        c,
                        "DocumentSearchUI.jsp?keyword="
                                + URLEncoder.encode(
                                        "Giải tích", java.nio.charset.StandardCharsets.UTF_8)),
                "Giải tích 1",
                "documentId=1",
                "documentId=2",
                ">Tìm kiếm</button>",
                "accept-charset=\"UTF-8\"");
        ok(get(c, "DocumentSearchUI.jsp?keyword=not-found"), "Không tìm thấy tài liệu phù hợp");
        ok(
                get(c, "DocumentDetailsUI.jsp?documentId=1"),
                "ISBN",
                "9786040000001",
                "name=\"btnBack\"");
        var details = get(c, "DocumentDetailsUI.jsp?documentId=1&keyword=Java%20%26%20SQL");
        ok(details, "name=\"keyword\" value=\"Java &amp; SQL\"", "action=\"DocumentSearchUI.jsp\"");
        ok(get(c, "DocumentDetailsUI.jsp?documentId=2"), "Môn học", "Công nghệ thông tin");
        ok(get(c, "DocumentDetailsUI.jsp?documentId=3"), "Kỳ phát hành", "Hàng tháng");
        assertEquals(400, get(c, "DocumentDetailsUI.jsp?documentId=abc").statusCode());
        assertEquals(404, get(c, "DocumentDetailsUI.jsp?documentId=99999").statusCode());
        for (String path :
                List.of(
                        "ManagementMainUI.jsp",
                        "ReportUI.jsp",
                        "StatisticPeriodUI.jsp",
                        "ReaderStatisticsUI.jsp",
                        "ReaderBorrowingHistoryUI.jsp",
                        "BorrowingReceiptDetailsUI.jsp"))
            assertEquals(403, get(c, path).statusCode(), path);
        String token = csrf(get(c, "ReaderMainUI.jsp").body());
        assertEquals(403, post(c, "Logout.jsp", "csrf=wrong").statusCode());
        assertEquals(302, post(c, "Logout.jsp", "csrf=" + token).statusCode());
        assertEquals(302, get(c, "ReaderMainUI.jsp").statusCode());
    }

    @Test
    void managementJspFlowAndErrors() throws Exception {
        var c = login("manager");
        ok(get(c, "ManagementMainUI.jsp"), "ReportUI.jsp");
        var reports = get(c, "ReportUI.jsp");
        ok(reports, "StatisticPeriodUI.jsp", "Thống kê tài liệu", "Thống kê nhà cung cấp");
        assertEquals(
                2, Pattern.compile("type=\"button\"").matcher(reports.body()).results().count());
        ok(get(c, "StatisticPeriodUI.jsp"), "ReaderStatisticsUI.jsp", "startDate", "endDate");
        String period = "startDate=2026-11-01&endDate=2026-11-30";
        var stats = get(c, "ReaderStatisticsUI.jsp?" + period);
        assertTrue(
                stats.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .toLowerCase(Locale.ROOT)
                        .contains("charset=utf-8"));
        ok(
                stats,
                "Nguyễn Minh Anh",
                "Phạm Hoàng Nam",
                "name=\"readerId\" value=\"1\"",
                "name=\"btnSee\"");
        assertTrue(
                stats.body().indexOf("Nguyễn Minh Anh") < stats.body().indexOf("Phạm Hoàng Nam"));
        var history = get(c, "ReaderBorrowingHistoryUI.jsp?readerId=1&" + period);
        ok(history, "PM-101", "PM-102", "01/11/2026", "30/11/2026", "Đang mượn");
        assertFalse(history.body().contains("BORROWED"));
        ok(
                history,
                "action=\"BorrowingReceiptDetailsUI.jsp\"",
                "name=\"receiptId\" value=\"101\"",
                "name=\"btnSee\"");
        assertFalse(
                Pattern.compile("<a[^>]*>\\s*(?:PM-|Nguyễn Minh Anh)")
                        .matcher(history.body())
                        .find());
        assertFalse(history.body().contains("PM-103"));
        ok(
                get(c, "BorrowingReceiptDetailsUI.jsp?receiptId=101"),
                "Giải tích nâng cao",
                "Giải tích 1",
                "Tình trạng lúc mượn");
        ok(
                get(c, "ReaderStatisticsUI.jsp?startDate=2025-11-01&endDate=2025-11-30"),
                "Không có lượt mượn");
        ok(get(c, "ReaderBorrowingHistoryUI.jsp?readerId=5&" + period), "không có phiếu mượn");
        for (String path :
                List.of(
                        "ReaderStatisticsUI.jsp",
                        "ReaderStatisticsUI.jsp?startDate=2026-11-30&endDate=2026-11-01",
                        "ReaderStatisticsUI.jsp?startDate=2026-02-30&endDate=2026-11-30",
                        "ReaderBorrowingHistoryUI.jsp?readerId=no&" + period,
                        "BorrowingReceiptDetailsUI.jsp?receiptId=-1"))
            assertEquals(400, get(c, path).statusCode(), path);
        assertEquals(
                404, get(c, "ReaderBorrowingHistoryUI.jsp?readerId=99999&" + period).statusCode());
        assertEquals(404, get(c, "BorrowingReceiptDetailsUI.jsp?receiptId=99999").statusCode());
        assertEquals(403, get(c, "DocumentSearchUI.jsp").statusCode());
        var guest = client();
        var wrong =
                post(
                        guest,
                        "LoginUI.jsp",
                        "username=reader&password=bad&csrf="
                                + csrf(get(guest, "LoginUI.jsp").body()));
        ok(wrong, "Tên đăng nhập hoặc mật khẩu không đúng");
    }

    @Test
    void daoInheritanceAndLargeDemoRelationships() throws Exception {
        for (Class<?> type :
                List.of(
                        DocumentDAO.class,
                        MemberDAO.class,
                        BorrowingReceiptDAO.class,
                        BorrowingReceiptDetailDAO.class,
                        ReaderStatisticDAO.class)) {
            assertEquals(DAO.class, type.getSuperclass());
        }
        // Separate database: the expanded demo must not alter the boundary fixtures above.
        try (Connection c =
                DriverManager.getConnection(
                        "jdbc:h2:mem:large_demo;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "")) {
            for (String script : List.of("schema.sql", "seed.sql", "demo-large.sql")) {
                // H2 uses BEGIN for MySQL's START TRANSACTION; all data SQL stays unchanged.
                String sql =
                        Files.readString(Path.of("database", script))
                                .replace("START TRANSACTION;", "BEGIN;");
                try (var reader = new java.io.StringReader(sql)) {
                    RunScript.execute(c, reader);
                }
            }
            assertCount(c, "SELECT COUNT(*) FROM tblReader", 53);
            assertCount(c, "SELECT COUNT(*) FROM tblDocument", 93);
            assertCount(c, "SELECT COUNT(*) FROM tblBorrowingReceipt WHERE id >= 20000", 592);
            assertCount(
                    c, "SELECT COUNT(*) FROM tblBorrowingReceiptDetail WHERE id >= 40000", 1776);
            assertCount(
                    c,
                    "SELECT COUNT(*) FROM (SELECT tblBorrowingReceiptid FROM"
                        + " tblBorrowingReceiptDetail WHERE id >= 40000 GROUP BY"
                        + " tblBorrowingReceiptid HAVING COUNT(*) >= 2) t",
                    592);
            assertCount(
                    c,
                    "SELECT COUNT(*) FROM (SELECT tblDocumentid FROM tblBorrowingReceiptDetail"
                        + " WHERE id >= 40000 GROUP BY tblDocumentid HAVING COUNT(*) > 1) t",
                    90);
            assertCount(
                    c, "SELECT COUNT(*) FROM tblBorrowingReceipt WHERE borrowDate > dueDate", 0);
            assertCount(
                    c,
                    "SELECT COUNT(*) FROM tblDocument d WHERE d.id >= 11000 AND d.quantity <>"
                        + " (SELECT SUM(i.importQuantity) FROM tblImportReceiptDetail i WHERE"
                        + " i.tblDocumentid=d.id)",
                    0);
            assertCount(
                    c,
                    "SELECT COUNT(*) FROM tblDocument d WHERE d.quantity < (SELECT"
                        + " COALESCE(SUM(bd.quantity),0) FROM tblBorrowingReceiptDetail bd JOIN"
                        + " tblBorrowingReceipt b ON b.id=bd.tblBorrowingReceiptid WHERE"
                        + " bd.tblDocumentid=d.id AND b.status='BORROWED')",
                    0);
            assertCount(c, "SELECT COUNT(*) FROM tblReturnReceiptDetail WHERE id >= 50000", 1260);
            assertCount(
                    c,
                    "SELECT COUNT(*) FROM tblDocument d WHERE d.id >= 11000 AND ((SELECT COUNT(*)"
                        + " FROM tblBook b WHERE b.tblDocumentid=d.id) + (SELECT COUNT(*) FROM"
                        + " tblTextbook t WHERE t.tblDocumentid=d.id) + (SELECT COUNT(*) FROM"
                        + " tblMagazine m WHERE m.tblDocumentid=d.id)) <> 1",
                    0);
        }
    }

    private void assertCount(Connection c, String sql, long expected) throws SQLException {
        try (var statement = c.createStatement();
                var result = statement.executeQuery(sql)) {
            assertTrue(result.next());
            assertEquals(expected, result.getLong(1), sql);
        }
    }
}
