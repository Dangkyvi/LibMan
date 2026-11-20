# Thư viện PTIT — MySQL local và Tomcat

Ứng dụng Java JSP/JavaBean/JDBC, đóng gói WAR. Chạy với **MySQL cài trên máy** và **Tomcat 10.1**; Java 17 trở lên, Maven 3.9. Không còn cấu hình Docker hay máy chủ demo. Ứng dụng không tự tạo bảng hoặc nạp dữ liệu. Bạn tự thực hiện các lệnh SQL dưới đây.

## 1. Tự tạo database, bảng và dữ liệu

Mở terminal tại thư mục repository, đăng nhập MySQL local bằng tài khoản của bạn:

```sh
mysql --default-character-set=utf8mb4 -u root -p
```

Trong cửa sổ MySQL, tự chạy theo thứ tự:

```sql
SOURCE database/00-create-database.sql;
SOURCE database/schema.sql;
SOURCE database/seed.sql;
```

```sql
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS `library` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `library`;
```

Tên database được bao bằng dấu backtick vì `LIBRARY` là từ khoá dành riêng ở một số phiên bản MySQL mới. JDBC URL vẫn dùng `/library`, không thêm backtick.

Lệnh đầu tạo database `library` với `utf8mb4` và chọn database này bằng câu lệnh SQL bên dưới. Lệnh thứ hai tạo các bảng/khóa ngoại. Lệnh thứ ba thêm dữ liệu mẫu; có thể bỏ qua nếu bạn tự nhập dữ liệu và tài khoản có mật khẩu hash phù hợp. Nếu mở MySQL ở thư mục khác, dùng đường dẫn tuyệt đối đến từng file SQL. Nếu dùng MySQL Workbench, mở rồi thực thi ba file trên theo thứ tự, với database `library` được chọn cho schema/seed.

Chỉ nạp `schema.sql` và `seed.sql` vào database mới chưa có các bảng/dữ liệu này. Các script không xoá hay ghi đè bảng cũ. Toàn bộ câu lệnh tạo bảng nằm tại [database/schema.sql](database/schema.sql); dữ liệu mẫu nằm tại [database/seed.sql](database/seed.sql).

Nếu muốn dùng tài khoản riêng cho ứng dụng, tự chạy các lệnh sau bằng tài khoản quản trị MySQL, thay mật khẩu ví dụ:

```sql
CREATE USER 'library'@'localhost' IDENTIFIED BY 'ThayMatKhauCuaBan';
GRANT SELECT ON `library`.* TO 'library'@'localhost';
```

Hai module hiện tại chỉ đọc dữ liệu nên quyền `SELECT` là đủ. Nếu tài khoản `library` đã tồn tại, dùng mật khẩu của tài khoản đó và bỏ qua `CREATE USER`. Bạn cũng có thể cấu hình một tài khoản MySQL local sẵn có có quyền đọc database.

## 2. Cấu hình kết nối MySQL local

Sửa [src/main/resources/database.properties](src/main/resources/database.properties) trước khi build:

```properties
DB_URL=jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci&connectionTimeZone=Asia/Ho_Chi_Minh
DB_USER=library
DB_PASSWORD=ThayMatKhauCuaBan
```

Thay `DB_USER`/`DB_PASSWORD` bằng tài khoản MySQL local của bạn; đổi cổng `3306` nếu MySQL dùng cổng khác. Đây là thông tin kết nối database, khác với tài khoản đăng nhập website. Không đưa mật khẩu thật vào repository chia sẻ.

Nếu không muốn ghi mật khẩu trong file, đặt `DB_URL`, `DB_USER`, `DB_PASSWORD` dưới dạng biến môi trường **của tiến trình Tomcat** hoặc Java system properties. Thứ tự ưu tiên: Java system property → biến môi trường → `database.properties`. File cấu hình được đóng gói trong WAR nên cần build và triển khai lại khi sửa file.

## 3. Build và chạy trên Tomcat local

Tomcat 10.1.61 đã được cài riêng cho dự án tại `.runtime/apache-tomcat-10.1.61` (không đưa vào Git). Cổng HTTP chỉ lắng nghe trên `127.0.0.1:8080`.

```sh
# Chạy lại sau khi build WAR hoặc cấu hình lại database:
./scripts/start-local.sh
# Dừng Tomcat:
./scripts/stop-local.sh
```

Mở **http://localhost:8080/library/**. Script khởi động chép WAR đã build vào Tomcat; không tự build, không tạo bảng hoặc nạp SQL. Sau khi thay code hay `database.properties`, chạy `mvn clean package`, dừng rồi khởi động lại bằng hai script trên. Log nằm ở `.runtime/apache-tomcat-10.1.61/logs/catalina.out`.

Nếu dùng bản Tomcat khác hoặc checkout mới chưa có `.runtime`, làm theo các bước thủ công bên dưới.


```sh
mvn clean verify
```

Chép `target/library.war` vào thư mục `webapps` của Tomcat 10.1, rồi khởi động Tomcat. Nếu Tomcat đang chạy, triển khai lại WAR theo cơ chế của Tomcat. Truy cập:

**http://localhost:8080/library/**

Ví dụ khi đã đặt biến `CATALINA_BASE` và `CATALINA_HOME` trỏ tới Tomcat local:

```sh
cp target/library.war "$CATALINA_BASE/webapps/library.war"
"$CATALINA_HOME/bin/startup.sh"
```

Không dùng Tomcat 9 vì ứng dụng sử dụng Jakarta Servlet 6.0 / JSP 3.1. Build dùng Java 17 làm mức đích; đã kiểm tra trên Java 21. Maven tải dependency khi chưa có trong cache, không cần MySQL để build hoặc chạy bộ kiểm thử tự động.

## 4. Tài khoản mẫu và cách kiểm tra

Sau khi bạn tự nạp `seed.sql`:

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Bạn đọc | `reader` | `ThuVien@123` |
| Nhân viên quản lý | `manager` | `ThuVien@123` |

Seed còn có `reader2` (1 lượt trong kỳ), `reader0` (0 lượt), cùng mật khẩu. `librarian` phục vụ liên kết nhân viên trên phiếu, không có quyền đăng nhập vào hai module.

- Với bạn đọc: chọn **Tìm tài liệu**, tìm `Giải tích`, chọn **Xem chi tiết**.
- Với quản lý: chọn **Xem báo cáo** → **Thống kê độc giả theo số lần mượn**, chọn từ **01/11/2026** đến **30/11/2026**, bấm **Xem thống kê**. Dữ liệu mẫu cho Nguyễn Minh Anh 2 phiếu, Phạm Hoàng Nam 1 phiếu; bấm tên rồi mã phiếu để xem chi tiết.
- Các nút chỉ chứa tên thao tác; không chứa mô tả. Trạng thái `BORROWED`/`RETURNED` hiển thị là **Đang mượn**/**Đã trả**; dữ liệu trạng thái trong database vẫn giữ nguyên.

## Tiếng Việt và UTF-8

JSP, response HTTP, request và biểu mẫu được cấu hình UTF-8; MySQL dùng `utf8mb4`; JDBC dùng UTF-8 và collation `utf8mb4_unicode_ci`. Dùng tùy chọn `--default-character-set=utf8mb4` khi nhập SQL như hướng dẫn trên. Ngày trên giao diện hiển thị `dd/MM/yyyy`; giá trị truyền trong URL và ô chọn ngày giữ định dạng `yyyy-MM-dd`.

Tomcat 10.1 mặc định giải mã URL bằng UTF-8. Nếu Tomcat local đã bị cấu hình sang encoding khác, thêm `URIEncoding="UTF-8"` vào Connector HTTP trong `conf/server.xml` rồi khởi động lại Tomcat. Nếu dữ liệu đã bị lưu sai dấu trong database cũ, đổi encoding hiển thị không thể tự phục hồi dữ liệu đó; hãy nhập lại từ file SQL UTF-8 hoặc bản sao đúng dấu.

## Luồng và hợp đồng truy vấn

Các lời gọi dưới đây nằm trực tiếp trong JSP được chỉ định; không chuyển chúng sang Servlet/controller. DAO mở connection, dùng PreparedStatement, đóng connection/statement/result set bằng try-with-resources. JavaBean biểu diễn kết quả; JSP nhận kết quả và render, escape mọi dữ liệu/giá trị tham số có thể thay đổi bằng `Ui.h()`.

| JSP gọi | DAO.phương thức | Đầu vào | Đầu ra |
|---|---|---|---|
| `DocumentSearchUI.jsp` | `DocumentDAO.searchByKeyword(String)` | `keyword` | `List<Document>` |
| `DocumentDetailsUI.jsp` | `DocumentDAO.getDetails(int)` | `documentId` | `Document`, đối tượng thực là `Book`, `Textbook` hoặc `Magazine`; `null` nếu không tồn tại |
| `ReaderStatisticsUI.jsp` | `ReaderStatisticDAO.getLoanStatistics(LocalDate, LocalDate)` | `startDate`, `endDate` | `List<ReaderStatistic>`, mỗi phần tử chứa `Reader` và `loanCount` |
| `ReaderBorrowingHistoryUI.jsp` | `BorrowingReceiptDAO.getBorrowingReceipts(int, LocalDate, LocalDate)` | `readerId`, `startDate`, `endDate` | `List<BorrowingReceipt>` |
| `BorrowingReceiptDetailsUI.jsp` | `BorrowingReceiptDetailDAO.getDetails(int)` | `receiptId` | `List<BorrowingReceiptDetail>`, mỗi dòng chứa `Document` |

Hai phương thức hỗ trợ `BorrowingReceiptDAO.findReader()` và `BorrowingReceiptDetailDAO.receiptExists()` phân biệt ID không tồn tại với kết quả rỗng.

Luồng bạn đọc:

`ReaderMainUI.jsp → DocumentSearchUI.jsp → DocumentSearchUI.jsp?keyword=... → DocumentDetailsUI.jsp?documentId=...`

Luồng quản lý:

`ManagementMainUI.jsp → ReportUI.jsp → StatisticPeriodUI.jsp → ReaderStatisticsUI.jsp → ReaderBorrowingHistoryUI.jsp → BorrowingReceiptDetailsUI.jsp`

Ngày được truyền tiếp từ thống kê sang lịch sử. Liên kết phiếu truyền `receiptId`, kèm các tham số kỳ/độc giả để quay lại lịch sử; dữ liệu chi tiết luôn lấy theo `receiptId`, không theo tham số độc giả trên URL. Chỉ vai trò MANAGEMENT xem được phiếu.

## Quy tắc dữ liệu và chênh lệch ERD

- Schema mới giữ tên và kiểu cột đã chép, gồm `tblMember.address INT NULL`, `tblDocument.publicationYear DATE`, `tblEmployee.tblMemberid` là cột thường; khóa kế thừa nhân viên là **tblMemberid2**.
- Theo quan hệ nghiệp vụ 1–N đã chốt, **không tạo** `tblBorrowingReceiptDetailid`, `tblReturnReceiptDetailid`, `tblImportReceiptDetailid` trên bảng phiếu. Thay bằng FK ở bảng chi tiết: `tblBorrowingReceiptDetail.tblBorrowingReceiptid`, `tblReturnReceiptDetail.tblReturnReceiptid`, `tblImportReceiptDetail.tblImportReceiptid`. Các JavaBean phiếu đều chứa `List<...Detail>`, mỗi chi tiết có một Document. `tblReturnReceipt.tblDocumentid` vẫn là cột thường như bản chép; tài liệu từng dòng luôn lấy từ bảng chi tiết.
- Repository trống, không thực hiện migration hay tự gán lại dòng chi tiết cũ. SQL là **schema khởi tạo**, không phải migration cho database đã có dữ liệu.
- Tìm chuỗi con theo tiêu đề, không phân biệt hoa/thường; escape `%`, `_`, `!` trong LIKE để chúng là ký tự thật, không phải wildcard do người dùng chèn.
- Thống kê chỉ hiển thị độc giả có **ít nhất một phiếu trong kỳ**, giảm dần số phiếu, thêm họ tên và ID để thứ tự ổn định khi bằng nhau.
- Đếm `COUNT(b.id)` trực tiếp trên phiếu, không JOIN chi tiết; không loại trạng thái phiếu. Lịch sử dùng cùng `borrowDate BETWEEN startDate AND endDate`, bao gồm cả hai biên ngày.
- Ngày thiếu/sai/ngược khoảng và ID không phải số nguyên dương: HTTP 400 kèm thông báo. ID không tồn tại: HTTP 404. Sai vai trò: HTTP 403. Chưa đăng nhập: chuyển đến LoginUI. Lỗi SQL: thông báo dịch vụ HTTP 503, ghi nguyên nhân vào log; không lộ SQL/mật khẩu ra trang.

## Kiểm thử và phạm vi kiểm tra

Bộ kiểm thử `src/test/java/vn/ptit/library/LibraryTest.java` dùng **H2 trong bộ nhớ ở MySQL mode** và embedded Tomcat/Jasper để kiểm tra DAO và HTTP/JSP. Chỉ các dependency có scope `test` sử dụng H2/embedded Tomcat; WAR chạy thật chỉ kết nối MySQL cấu hình ở trên. Bộ test tự gán JDBC URL H2 trước khi tạo connection, không dùng MySQL local và không chạy script trên MySQL của bạn.

```sh
mvn clean verify
```

Các nhóm kiểm thử bao gồm tìm kiếm tiếng Việt và ba loại tài liệu; số phiếu/lịch sử/chi tiết, biên ngày và phiếu ngoài kỳ; đăng nhập, phân quyền, CSRF, ngày và mã không hợp lệ; HTTP UTF-8, nhãn nút và trạng thái tiếng Việt. Kết quả chạy được ghi trong `target/surefire-reports/`.

Theo yêu cầu, không truy cập hay kiểm tra MySQL trên máy. Bạn tự nhập SQL và kiểm tra kết nối MySQL theo hướng dẫn. Kiểm thử H2 không thay thế xác minh trực tiếp trên MySQL.
