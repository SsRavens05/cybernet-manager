# BÁO CÁO PHÂN TÍCH GIAO DIỆN & LAYOUT CHI TIẾT
## MÀN HÌNH QUẢN LÝ THIẾT BỊ (DEVICE MANAGEMENT) - CYBERNET MANAGER PRO

Tài liệu này cung cấp bảng phân tích chi tiết các thành phần giao diện (UI Layout Analysis) của **Màn hình Quản lý Thiết bị (Device Management)** tương ứng với 10 vị trí được đánh dấu trên hình ảnh thiết kế. 

Báo cáo này được cấu trúc theo định dạng chuẩn học thuật để người dùng có thể dễ dàng sao chép (copy) và dán (paste) trực tiếp vào Microsoft Word, Google Docs hoặc LaTeX làm tài liệu chứng minh cho đồ án môn học hoặc đồ án tốt nghiệp.

---

### HÌNH ẢNH GIAO DIỆN PHÂN TÍCH VỚI CÁC VỊ TRÍ ĐÁNH DẤU (1 - 10)

Để chèn hình ảnh vào báo cáo Word, bạn có thể chụp màn hình đã đánh dấu vị trí từ tài liệu gốc hoặc sử dụng hình ảnh bên dưới:

![Hình 4.3: Các vị trí thành phần giao diện được đánh dấu trên Màn hình Quản lý Thiết bị](C:\Users\ACER\.gemini\antigravity\brain\51eddc04-5ac6-40e4-8959-262036f65cb9\media__1779421620125.png)

---

### BẢNG 4.4: MÔ TẢ CHI TIẾT LAYOUT THEO CÁC VỊ TRÍ ĐÁNH DẤU (1 - 10)

Dưới đây là bảng đặc tả chi tiết cấu trúc, kiểu thành phần (Component Type), mã định danh (FX ID) và chức năng nghiệp vụ của từng vị trí từ **1** đến **10** tương ứng với mã nguồn giao diện JavaFX FXML (`quan-ly-thiet-bi.fxml` và `main-view.fxml`):

| Vị Trí Đánh Dấu | Mã Định Danh (FX ID) | Kiểu Component (JavaFX) | Thuộc Tính CSS / Style Class | Chức Năng & Mô Tả Chi Tiết Nghiệp Vụ |
| :---: | :--- | :--- | :--- | :--- |
| **1** | `btnThietBi` | **AnchorPane** (Sidebar Item) | `.sidebar-btn` | **Nút điều hướng danh mục "Quản lý Thiết bị"**: Nằm trong cột thanh điều hướng dọc bên trái (Sidebar VBox). Khi được click, nó sẽ kích hoạt sự kiện `onThietBiMenuClick` trong `MainController` để tải tệp `quan-ly-thiet-bi.fxml` lên vùng hiển thị trung tâm (`contentArea`). |
| **2** | `btnUserProfile` | **AnchorPane** (User Area Container) | `.user-profile-area` | **Khu vực hiển thị hồ sơ cá nhân**: Nằm ở góc trên cùng bên trái của thanh Header. Gồm ảnh đại diện dạng tròn, nhãn hiển thị tên tài khoản đăng nhập `lblProfileName` ("ad") và huy hiệu phân quyền `lblProfileBadge` ("Admin"). Khi click sẽ kích hoạt sự kiện `onUserClick` để mở menu chức năng nhanh (Đăng xuất / Đổi mật khẩu). |
| **3** | `lblTongTB` | **Label** (nằm trong VBox) | `.stat-card-blue` | **Thẻ thống kê "Tổng thiết bị"**: Được thiết kế dưới dạng Stat Card với nền Gradient xanh dương nổi bật. Hiển thị tổng số lượng tất cả thiết bị và linh kiện được cấu hình trong hệ thống phòng máy. Giá trị số hiển thị cỡ chữ lớn (48px) đậm. |
| **4** | `lblTBHong` | **Label** (nằm trong VBox) | `.stat-card-purple` | **Thẻ thống kê "Thiết bị hỏng"**: Stat Card với tông nền Gradient màu tím/hồng cánh sen. Dùng để thống kê số lượng thiết bị đang ghi nhận trạng thái gặp sự cố kỹ thuật cần bảo trì (HỎNG) trong cơ sở dữ liệu giúp kỹ thuật viên dễ dàng phát hiện nhanh. |
| **5** | `lblTBDangDung` | **Label** (nằm trong VBox) | `.stat-card-yellow` | **Thẻ thống kê "Thiết bị đang dùng"**: Stat Card với tông nền Gradient màu vàng cam. Cho thấy thời gian thực (Real-time) có bao nhiêu máy trạm hoặc linh kiện đang có phiên hoạt động của người chơi trực tuyến trong tiệm net. |
| **6** | `btnDelete` | **Button** | `.btn-delete` | **Nút hành động "Xóa thiết bị"**: Nút nhấn có màu viền xám nhạt kèm biểu tượng thùng rác màu đen từ đường dẫn `@icons/xoa.png`. Kích hoạt sự kiện `onDeleteClick` để thực hiện xóa thiết bị đang được chọn khỏi danh sách và cập nhật lại cơ sở dữ liệu sau khi xác nhận. |
| **7** | `btnUpdate` | **Button** | `.btn-action` | **Nút hành động "Cập nhật thiết bị"**: Nút nhấn kèm biểu tượng làm mới/bút chì từ đường dẫn `@icons/cap-nhat.png`. Kích hoạt sự kiện `onUpdateClick` để mở form biểu mẫu cho phép sửa đổi thông tin về trạng thái hoặc cấu hình của thiết bị đang chọn trên bảng. |
| **8** | *(Insert Button)* | **Button** | `.btn-action` | **Nút hành động "Thêm mới thiết bị"**: Nút nhấn nổi bật với tiền tố `+ Insert`. Khi người dùng click chuột, sự kiện `onInsertClick` trong `QuanLyThietBiController` sẽ được gọi để hiển thị form biểu mẫu thêm mới một thiết bị vào hệ thống. |
| **9** | `txtSearch` | **TextField** | `.search-box` | **Ô tìm kiếm nhanh**: Hộp văn bản hỗ trợ người dùng nhập ký tự tìm kiếm (ví dụ: tên linh kiện, loại thiết bị, mã máy). Sự kiện thay đổi ký tự sẽ lọc dữ liệu của bảng `TableView` bên dưới theo thời gian thực (Real-time) một cách mượt mà. |
| **10** | `tbThietBi` | **TableView** | `.table-panel` | **Bảng dữ liệu lưới danh sách thiết bị**: Thành phần trung tâm hiển thị trực quan thông tin dưới dạng lưới cột rộng rãi gồm 5 trường dữ liệu quan trọng: <br>1. *Mã TB* (`colMaTB`) <br>2. *Tên TB* (`colTenTB`) <br>3. *Loại TB* (`colLoaiTB`) <br>4. *Trạng Thái* (`colTrangThai`) <br>5. *Mã PC* (bản chất liên kết với trường ngày mua `colNgayMua`). |

---

### HƯỚNG DẪN SAO CHÉP & TRÌNH BÀY TRONG MICROSOFT WORD

Để báo cáo của bạn đạt điểm tối đa và có tính chuyên nghiệp nhất khi chèn vào Microsoft Word, hãy làm theo 3 bước đơn giản sau:

1. **Quét khối và Sao chép (Copy):**
   - Quét chọn toàn bộ văn bản và bảng Markdown ở mục trên.
   - Nhấn `Ctrl + C` để sao chép.

2. **Dán vào Word (Paste):**
   - Mở file báo cáo đồ án của bạn bằng Microsoft Word.
   - Di chuyển con trỏ chuột đến vị trí cần chèn và nhấn `Ctrl + V`.
   - Microsoft Word sẽ **tự động chuyển đổi** bảng Markdown thành một bảng Word thông thường cực kỳ sạch sẽ và ngay ngắn.

3. **Định dạng bảng trong Word (Tùy chọn nâng cao):**
   - Chọn bảng vừa dán trong Word, thẻ **Table Design** sẽ xuất hiện ở thanh công cụ phía trên.
   - Bạn có thể chọn các mẫu thiết kế bảng có sẵn (Table Styles) hoặc tô màu xanh đậm/đỏ đô cho hàng đầu tiên (Header Row) để đồng bộ với màu sắc thương hiệu của CyberNet Manager Pro.
   - Căn lề giữa cho các cột như: **Vị Trí Đánh Dấu**, **Mã Định Danh (FX ID)**, và **Kiểu Component** để bảng trông cân đối và dễ đọc hơn.
