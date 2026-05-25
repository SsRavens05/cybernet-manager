# HƯỚNG DẪN CÀI ĐẶT & CHẠY CHƯƠNG TRÌNH
## HỆ THỐNG QUẢN LÝ PHÒNG NET (CYBERGAME MANAGEMENT)

Tài liệu này hướng dẫn chi tiết cách cài đặt và chạy ứng dụng **CyberGame Management** trên bất kỳ máy tính nào, kể cả máy tính **chỉ mới cài đặt Java cơ bản (JDK)**.

### 📌 Mục lục
1. [Yêu cầu hệ thống tối thiểu](#1-yêu-cầu-hệ-thống-tối-thiểu)
2. [Cách 1: Chạy nhanh ở chế độ Ngoại tuyến (Offline - Không cần cài Database)](#cách-1-chạy-nhanh-ở-chế-độ-ngoại-tuyến-offline-khuyến-nghị-để-test-nhanh-giao-diện)
3. [Cách 2: Chạy đầy đủ ở chế độ Trực tuyến (Online - Sử dụng Oracle Database)](#cách-2-chạy-đầy-đủ-ở-chế-độ-trực-tuyến-online---kết-nối-oracle-database)
4. [Khắc phục sự cố thường gặp (Troubleshooting)](#-khắc-phục-sự-cố-thường-gặp)

---

### 1. Yêu cầu hệ thống tối thiểu

Để chạy được ứng dụng này, máy tính của bạn cần đáp ứng điều kiện tiên quyết sau:

1. **Java Development Kit (JDK) 21 hoặc mới hơn**:
   - Ứng dụng được biên dịch và tối ưu hóa cho **Java 21**. Các phiên bản Java cũ hơn (như Java 8, 11, 17) sẽ gây ra lỗi `UnsupportedClassVersionError`.
   - **Cách kiểm tra phiên bản Java**: Mở Command Prompt (cmd) hoặc PowerShell và gõ:
     ```cmd
     java -version
     ```
     Nếu kết quả trả về hiển thị phiên bản `21.x.x` hoặc cao hơn, bạn đã sẵn sàng.
   - **Nếu chưa cài đặt Java hoặc phiên bản quá cũ**:
     - Tải và cài đặt JDK 21 tại: [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) hoặc [Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21).
     - Thiết lập biến môi trường `JAVA_HOME` và thêm đường dẫn `bin` vào biến môi trường `Path` (đặc biệt quan trọng trên Windows).

2. **Mã nguồn ứng dụng**:
   - Tải hoặc clone toàn bộ thư mục dự án **CyberGame_Management** về máy tính.

> [!NOTE]
> **Không cần cài đặt Maven trước!**
> Dự án đã tích hợp sẵn công cụ **Maven Wrapper** (`mvnw` và `mvnw.cmd`). Hệ thống sẽ tự động tải phiên bản Maven phù hợp và toàn bộ các thư viện cần thiết khi bạn chạy lệnh lần đầu tiên.

---

### Cách 1: Chạy nhanh ở chế độ Ngoại tuyến (Offline) - *Khuyến nghị để test nhanh giao diện*

Ứng dụng hỗ trợ chế độ **Offline (Mock Data)**. Trong chế độ này, bạn **không cần cài đặt cơ sở dữ liệu Oracle**, ứng dụng tự động tải dữ liệu mẫu cực kỳ đầy đủ để hiển thị đầy đủ các màn hình JavaFX, chức năng và biểu đồ.

#### Bước 1: Kiểm tra cấu hình cấu hình
Theo mặc định, ứng dụng đã được cấu hình sẵn chạy ở chế độ **Offline**.
Tuy nhiên, bạn có thể kiểm tra lại trong file:
`src/main/resources/com/example/cybergame_management/database.properties`
Đảm bảo dòng sau được thiết lập:
```properties
cybergame.db.enabled=false
```

#### Bước 2: Chạy ứng dụng
Mở terminal (cmd, PowerShell hoặc Git Bash) và di chuyển vào thư mục gốc của dự án (`CyberGame_Management`).

* **Trên Windows (Command Prompt hoặc PowerShell)**:
  ```cmd
  mvnw.cmd clean javafx:run
  ```
* **Trên Linux / macOS / Git Bash**:
  ```bash
  chmod +x mvnw
  ./mvnw clean javafx:run
  ```

> [!TIP]
> Trong lần chạy đầu tiên, Maven sẽ tự động tải các thư viện JavaFX, điều này có thể mất từ 1 - 3 phút tùy tốc độ mạng của bạn. Các lần chạy sau sẽ khởi động ngay lập tức.

#### 🔑 Tài khoản đăng nhập mặc định (Offline Mode):
Khi màn hình đăng nhập hiện lên, bạn có thể dùng một trong các tài khoản mẫu sau để trải nghiệm:

| Quyền hạn | Tên đăng nhập (Username) | Mật khẩu (Password) |
| :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin` | `12345678` |
| **Quản trị viên (Admin)** | `ad` | `1` |
| **Nhân viên (Staff)** | `nv001` | `123456` |
| **Nhân viên (Staff)** | `nv` | `1` |

---

### Cách 2: Chạy đầy đủ ở chế độ Trực tuyến (Online) - *Kết nối Oracle Database*

Nếu bạn muốn ứng dụng lưu trữ và tương tác thực tế với cơ sở dữ liệu, hãy làm theo các bước dưới đây để thiết lập kết nối Oracle Database.

#### Bước 1: Chuẩn bị Cơ sở dữ liệu Oracle
1. Cài đặt **Oracle Database** trên máy tính của bạn (Khuyên dùng bản nhẹ: **Oracle Database XE** - Express Edition hoặc **Oracle Database 23c Free**).
2. Tạo một tài khoản schema (User) mới trên Oracle Database.

#### Bước 2: Khởi tạo dữ liệu (Import Schema & Tables)
1. Kết nối với cơ sở dữ liệu Oracle của bạn thông qua các công cụ quản lý như: **SQL Developer**, **DBeaver**, **Navicat** hoặc **Command Line (sqlplus)**.
2. Mở file script SQL có sẵn trong dự án tại:
   `src/main/resources/database/final.sql`
3. Thực thi (Run) toàn bộ file script này. Lệnh này sẽ tự động khởi tạo:
   - Tất cả các bảng (Tables) độc lập và phụ thuộc.
   - Các ràng buộc toàn vẹn (Constraints, Foreign Keys).
   - Các hàm kích hoạt tự động (Triggers) để tính tiền, cập nhật tồn kho, v.v.
   - Các thủ tục lưu trữ (Stored Procedures).

#### Bước 3: Cấu hình kết nối cơ sở dữ liệu trong mã nguồn
Bạn có thể cấu hình kết nối bằng **một trong ba cách** cực kỳ linh hoạt dưới đây:

##### 👉 Cách A: Sửa trực tiếp trong file `database.properties` (Khuyên dùng)
Mở file `src/main/resources/com/example/cybergame_management/database.properties` và cập nhật thông tin:
```properties
# Kích hoạt kết nối Database
cybergame.db.enabled=true

# Đường dẫn URL JDBC kết nối Oracle (thay localhost/port/service_name nếu khác)
cybergame.db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1

# Tài khoản Oracle vừa tạo
cybergame.db.user=TEN_DATABASE_USER_CUA_BAN
cybergame.db.password=MAT_KHAU_DATABASE_CUA_BAN
```

##### 👉 Cách B: Thiết lập qua Biến môi trường hệ thống (System Environment Variables)
Nếu bạn không muốn thay đổi mã nguồn, hãy thiết lập các biến môi trường sau trên hệ điều hành của bạn:
- `CYBERGAME_DB_ENABLED=true`
- `CYBERGAME_DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1`
- `CYBERGAME_DB_USER=tên_user`
- `CYBERGAME_DB_PASSWORD=mật_khẩu`

##### 👉 Cách C: Truyền tham số trực tiếp khi khởi động ứng dụng qua dòng lệnh
Bạn có thể truyền các biến trực tiếp lúc chạy lệnh:
```cmd
mvnw.cmd clean javafx:run -Dcybergame.db.enabled=true -Dcybergame.db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1 -Dcybergame.db.user=username -Dcybergame.db.password=password
```

#### Bước 4: Chạy chương trình
Sau khi đã cấu hình kết nối thành công, tiến hành khởi động chương trình:
* **Trên Windows**:
  ```cmd
  mvnw.cmd clean javafx:run
  ```
* **On Linux / macOS**:
  ```bash
  ./mvnw clean javafx:run
  ```

---

### 🛠 Khắc phục sự cố thường gặp

#### 1. Lỗi `UnsupportedClassVersionError` (class file version 65.0)
* **Nguyên nhân**: Phiên bản Java trên máy tính của bạn hiện tại thấp hơn Java 21 (ví dụ đang dùng Java 17 trở xuống).
* **Khắc phục**: Hãy chạy lệnh `java -version` để kiểm tra. Đảm bảo bạn tải đúng **JDK 21** và đã cấu hình lại biến môi trường `JAVA_HOME` để nhận diện Java 21.

#### 2. Lỗi `java.sql.SQLException: Database is disabled or missing credentials.`
* **Nguyên nhân**: Bạn đang bật chế độ online (`cybergame.db.enabled=true`) nhưng chưa điền đầy đủ tài khoản/mật khẩu trong file cấu hình.
* **Khắc phục**: Điền đầy đủ tài khoản/mật khẩu Oracle Database hoặc chuyển `cybergame.db.enabled` về `false` để sử dụng chế độ Offline.

#### 3. Lỗi không tìm thấy file `mvnw.cmd` hoặc không chạy được lệnh
* **Nguyên nhân**: Bạn đang gõ lệnh ở sai thư mục, hoặc terminal không có quyền thực thi file.
* **Khắc phục**: Đảm bảo terminal của bạn đang đứng đúng thư mục `CyberGame_Management` (chứa file `pom.xml` và `mvnw.cmd`). Trên macOS/Linux, hãy nhớ chạy lệnh cấp quyền: `chmod +x mvnw` trước khi chạy `./mvnw`.

---
Chúc các bạn cài đặt thành công và trải nghiệm ứng dụng thật tuyệt vời! 🚀
