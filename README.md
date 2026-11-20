# LibMan — Hệ thống tra cứu và báo cáo thư viện

LibMan là ứng dụng web phục vụ bạn đọc và nhân viên quản lý thư viện, được xây dựng bằng Java JSP, JavaBean và JDBC. Dự án tập trung vào hai nghiệp vụ: **tra cứu tài liệu** và **thống kê độc giả theo số lần mượn**, từ kết quả tổng hợp đến chi tiết từng phiếu.

## Chức năng

| Vai trò | Chức năng hiện có |
| --- | --- |
| Bạn đọc | Đăng nhập, tìm tài liệu theo tiêu đề, xem thông tin chi tiết và quay lại kết quả tìm kiếm. |
| Nhân viên quản lý | Chọn kỳ thống kê, xem số lần mượn của từng độc giả, xem lịch sử mượn và các tài liệu trên mỗi phiếu. |

Tài liệu gồm ba loại: sách tham khảo, giáo trình và tạp chí. Trang chi tiết hiển thị thông tin chung cùng thuộc tính riêng của từng loại, như ISBN, môn học hoặc kỳ phát hành.

Trang báo cáo có ba lựa chọn: thống kê độc giả, thống kê tài liệu và thống kê nhà cung cấp. **Hiện chỉ báo cáo độc giả theo số lần mượn được triển khai**; hai nút còn lại chưa có hành động. Dự án chưa có giao diện lập phiếu mượn, trả hoặc nhập tài liệu.

## Công nghệ

| Thành phần | Công nghệ |
| --- | --- |
| Ngôn ngữ | Java 17 trở lên |
| Giao diện | JSP, HTML, CSS |
| Xử lý HTTP và phiên đăng nhập | Jakarta Servlet 6.0, Filter, HttpSession |
| Truy cập dữ liệu | JDBC, MySQL Connector/J |
| Cơ sở dữ liệu khi chạy ứng dụng | MySQL |
| Máy chủ ứng dụng | Apache Tomcat 10.1 |
| Build | Maven 3.9 |
| Kiểm thử | JUnit 5, H2 trong bộ nhớ, embedded Tomcat/Jasper |

Ứng dụng được đóng gói thành `target/library.war`. Cần Tomcat 10.1 để hỗ trợ Jakarta Servlet 6.0; Tomcat 9 không tương thích với cấu hình này.

## Cấu trúc dự án

```text
LibMan/
├── pom.xml                         # Dependency và cấu hình build Maven
├── database/
│   ├── 00-create-database.sql       # Tạo và chọn database library
│   ├── schema.sql                  # Các bảng, khóa ngoại và chỉ mục
│   ├── seed.sql                    # Tài khoản và dữ liệu mẫu cơ bản
│   └── demo-large.sql              # Dữ liệu demo mở rộng, tùy chọn
├── scripts/
│   ├── start-local.sh              # Triển khai WAR vào Tomcat local đã cấu hình
│   └── stop-local.sh               # Dừng Tomcat local
└── src/
    ├── main/
    │   ├── java/vn/ptit/library/
    │   │   ├── config/             # Đọc cấu hình và mở kết nối JDBC
    │   │   ├── dao/                # DAO cha và các lớp truy vấn nghiệp vụ
    │   │   ├── model/              # JavaBean biểu diễn dữ liệu
    │   │   ├── security/           # Đăng nhập, phân quyền, xử lý mật khẩu
    │   │   └── web/                # Kiểm tra tham số, định dạng và escape HTML
    │   ├── resources/
    │   │   └── database.properties # Cấu hình kết nối MySQL
    │   └── webapp/
    │       ├── *.jsp               # Các trang giao diện và xử lý yêu cầu
    │       ├── assets/style.css    # Định dạng giao diện
    │       └── WEB-INF/
    │           ├── web.xml         # Filter, session và cấu hình UTF-8
    │           ├── jspf/           # Header/footer dùng chung
    │           ├── Error.jsp       # Trang thông báo lỗi
    │           └── Denied.jsp      # Trang từ chối truy cập
    └── test/java/vn/ptit/library/
        └── LibraryTest.java        # Kiểm thử DAO, HTTP/JSP và dữ liệu demo
```

### Cách các thành phần phối hợp

```text
Trình duyệt → AccessFilter → JSP → DAO → JDBC → MySQL
                              ↑      ↓
                              └─ JavaBean
```

`AccessFilter` thiết lập mã hóa và kiểm tra quyền truy cập. JSP nhận tham số, gọi DAO và hiển thị kết quả qua các JavaBean. Các lời gọi nghiệp vụ nằm trực tiếp trong JSP; dự án chưa tách một lớp controller riêng.

Tất cả DAO nghiệp vụ kế thừa [DAO.java](src/main/java/vn/ptit/library/dao/DAO.java). Lớp cha cung cấp `openConnection()`, còn từng phương thức truy vấn dùng `PreparedStatement` và try-with-resources để đóng kết nối, câu lệnh và kết quả.

| DAO | Trách nhiệm |
| --- | --- |
| `MemberDAO` | Xác thực tài khoản đăng nhập. |
| `DocumentDAO` | Tìm kiếm và lấy chi tiết từng loại tài liệu. |
| `ReaderStatisticDAO` | Đếm số phiếu mượn của độc giả trong kỳ. |
| `BorrowingReceiptDAO` | Lấy thông tin độc giả và danh sách phiếu mượn. |
| `BorrowingReceiptDetailDAO` | Lấy các dòng tài liệu của một phiếu mượn. |

### Luồng sử dụng chính

- **Bạn đọc:** Đăng nhập → Trang bạn đọc → Tìm tài liệu → Xem chi tiết → Quay lại kết quả.
- **Quản lý:** Đăng nhập → Báo cáo → Thống kê độc giả → Chọn kỳ → Xem độc giả → Xem phiếu → Chi tiết tài liệu đã mượn.

Ngày bắt đầu và kết thúc được giữ khi chuyển từ thống kê sang lịch sử. Các bảng có nút **Xem** riêng cho từng dòng.

## Cơ sở dữ liệu

Schema gồm các nhóm thành viên, tài liệu, mượn/trả và nhập tài liệu từ nhà cung cấp. Xem đầy đủ tại [database/schema.sql](database/schema.sql).

Các quan hệ chính:

- `tblMember` chứa thông tin tài khoản; `tblReader` và các bảng nhân viên biểu diễn vai trò tương ứng.
- `tblDocument` chứa thông tin chung; `tblBook`, `tblTextbook`, `tblMagazine` chứa thuộc tính riêng, dùng chung khóa tài liệu.
- Một độc giả có nhiều phiếu mượn; mỗi phiếu do một thủ thư lập.
- **Một phiếu mượn có nhiều chi tiết mượn; một tài liệu có thể xuất hiện trong nhiều chi tiết mượn.** Hai khóa ngoại `tblBorrowingReceiptid` và `tblDocumentid` nằm trên `tblBorrowingReceiptDetail`.
- Phiếu nhập và phiếu trả cũng có bảng chi tiết riêng. Schema hiện tại chưa có khóa ngoại trực tiếp từ phiếu trả đến phiếu mượn.

Số lần mượn được tính theo **số phiếu**, không theo số dòng chi tiết hoặc số cuốn sách. Thống kê lấy ngày mượn trong khoảng đã chọn, gồm cả hai ngày biên, hiển thị độc giả có ít nhất một phiếu và sắp xếp giảm dần số phiếu.

## Cài đặt và chạy

### 1. Chuẩn bị môi trường

Cài JDK 17 trở lên, Maven 3.9, MySQL và Tomcat 10.1. Sau khi lấy mã nguồn, mở terminal tại thư mục chứa `pom.xml`.

Các file SQL trong `database/` cần có sẵn để khởi tạo dữ liệu và chạy bộ kiểm thử. Tomcat được cài riêng trên máy; thư mục `.runtime/` không đi kèm mã nguồn.

### 2. Khởi tạo dữ liệu

Với database mới, mở MySQL từ thư mục dự án:

```sh
mysql --default-character-set=utf8mb4 -u root -p
```

Chạy lần lượt trong MySQL:

```sql
SOURCE database/00-create-database.sql;
SOURCE database/schema.sql;
SOURCE database/seed.sql;
```

`00-create-database.sql` tạo và chọn database `library`. Chỉ chạy schema và seed khi chưa có các bảng, dữ liệu này. Ứng dụng không tự tạo bảng hoặc tự nạp SQL.

Có thể nạp thêm dữ liệu demo **một lần**, sau seed:

```sql
USE `library`;
SOURCE database/demo-large.sql;
EXIT;
```

File mở rộng dùng các ID từ 10000 trở lên, bổ sung 50 độc giả, 3 thủ thư, 90 tài liệu, 6 nhà cung cấp, 592 phiếu mượn và 1.776 chi tiết mượn, cùng dữ liệu nhập/trả tương ứng. File dùng INSERT và không ghi đè dữ liệu cũ; cần tránh các ID đã được sử dụng. Nếu có lỗi khi nạp, chạy `ROLLBACK;` trong cùng phiên và kiểm tra nguyên nhân trước khi thử lại.

### 3. Cấu hình kết nối

Sửa [database.properties](src/main/resources/database.properties) theo MySQL trên máy:

```properties
DB_URL=jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci&connectionTimeZone=Asia/Ho_Chi_Minh
DB_USER=your_mysql_user
DB_PASSWORD=your_mysql_password
```

Tài khoản này phải có quyền đọc database `library`; đây là tài khoản MySQL, khác với tài khoản đăng nhập website. Không commit mật khẩu thật. Có thể dùng Java system properties hoặc biến môi trường của tiến trình Tomcat với cùng tên biến; thứ tự ưu tiên là **system property → biến môi trường → file cấu hình**.

### 4. Build và triển khai

Build và chạy kiểm thử:

```sh
mvn clean verify
```

Đặt `CATALINA_HOME` trỏ tới bản Tomcat 10.1 đã cài, ví dụ:

```sh
export CATALINA_HOME="/path/to/apache-tomcat-10.1"
export CATALINA_BASE="$CATALINA_HOME"
cp target/library.war "$CATALINA_BASE/webapps/library.war"
"$CATALINA_HOME/bin/startup.sh"
```

Mở **http://localhost:8080/library/** nếu Tomcat dùng cổng mặc định 8080. Dừng máy chủ bằng:

```sh
"$CATALINA_HOME/bin/shutdown.sh"
```

Nếu máy đã có Tomcat tại `.runtime/apache-tomcat-10.1.61`, có thể dùng các script tiện ích thay cho bước triển khai thủ công:

```sh
mvn clean package
./scripts/start-local.sh
```

Sau khi sửa code hoặc file cấu hình, build lại, dừng rồi khởi động lại:

```sh
mvn clean package
./scripts/stop-local.sh
./scripts/start-local.sh
```

Script chỉ chép WAR và chạy Tomcat đã cài, không tự cài Tomcat hoặc nạp database.

## Tài khoản và kịch bản demo

Sau khi nạp `seed.sql`:

| Vai trò | Tên đăng nhập | Mật khẩu demo |
| --- | --- | --- |
| Bạn đọc | `reader` | `ThuVien@123` |
| Quản lý | `manager` | `ThuVien@123` |

- Đăng nhập bạn đọc, tìm **Giải tích**, mở chi tiết một tài liệu rồi dùng nút **Quay lại**.
- Đăng nhập quản lý, chọn thống kê độc giả trong kỳ **01/11/2026–30/11/2026**. Với seed cơ bản, Nguyễn Minh Anh có 2 phiếu và Phạm Hoàng Nam có 1 phiếu trong kỳ.
- Nếu đã nạp demo mở rộng, chọn **01/10/2026–30/11/2026** để xem nhiều kết quả. Bấm **Xem** tại dòng độc giả, rồi **Xem** tại dòng phiếu.

Dữ liệu demo mở rộng có thêm tài khoản `demo.reader01` đến `demo.reader50`, cùng mật khẩu demo ở trên. Dữ liệu sử dụng ngày cố định để có thể lặp lại kịch bản trình bày.

## Kiểm thử và quy ước

```sh
mvn clean verify
```

Bộ kiểm thử dùng H2 trong bộ nhớ ở MySQL mode và embedded Tomcat/Jasper. Các kiểm thử bao gồm truy vấn DAO, kế thừa DAO, tìm kiếm tiếng Việt, thống kê theo kỳ, lịch sử/chi tiết phiếu, đăng nhập, phân quyền, CSRF, tham số không hợp lệ và quan hệ dữ liệu demo. Kết quả nằm tại `target/surefire-reports/`.

Kiểm thử không truy cập MySQL local; khi triển khai vẫn cần kiểm tra cấu hình và kết nối MySQL thực tế.

Ứng dụng dùng UTF-8 cho JSP, request/response và biểu mẫu; MySQL dùng `utf8mb4`. Ngày hiển thị theo `dd/MM/yyyy`. Mật khẩu được kiểm tra bằng PBKDF2; dữ liệu hiển thị được escape HTML, các truy vấn nhận tham số qua `PreparedStatement`.
