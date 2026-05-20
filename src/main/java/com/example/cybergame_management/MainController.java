package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    // Đây chính là cái "lỗ hổng" ở giữa màn hình mà mình đã đục sẵn ở main-view.fxml
    @FXML
    private StackPane contentArea;

    @FXML private AnchorPane btnThietBi;
    @FXML private AnchorPane btnKhachHang;
    @FXML private AnchorPane btnSubKhachHang;
    @FXML private AnchorPane btnNapTien;
    @FXML private AnchorPane btnLichSuChoi;
    @FXML private AnchorPane btnNhanVien;
    @FXML private AnchorPane btnSanPham;
    @FXML private AnchorPane btnTaiChinh;
    @FXML private AnchorPane btnKhuVuc;
    @FXML private AnchorPane btnKhuyenMai;
    @FXML private AnchorPane btnSuKien;
    @FXML private AnchorPane btnNhapHang;
    @FXML private VBox customerSubMenu;
    @FXML private Label lblKhachHangArrow;

    @FXML
    public void initialize() {
        loadContent("quan-ly-thiet-bi.fxml", btnThietBi);
    }

    private void setActiveMenu(AnchorPane activeBtn) {
        // Gom tất cả các nút vào 1 mảng để xử lý cho lẹ
        AnchorPane[] allBtns = {btnThietBi, btnKhachHang, btnNhanVien, btnSanPham,
                btnTaiChinh, btnKhuVuc, btnKhuyenMai, btnSuKien, btnNhapHang};

        for (AnchorPane btn : allBtns) {
            if (btn != null) {
                // Tháo mác "Active" khỏi tất cả các nút
                btn.getStyleClass().remove("sidebar-btn-active");

                // Đảm bảo nút nào cũng có mác bình thường
                if (!btn.getStyleClass().contains("sidebar-btn")) {
                    btn.getStyleClass().add("sidebar-btn");
                }
            }
        }
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("sidebar-btn-active");
        }
        if (activeBtn != btnKhachHang) {
            setActiveCustomerSub(null);
        }
    }

    private void setActiveCustomerSub(AnchorPane activeSubBtn) {
        AnchorPane[] customerBtns = {btnSubKhachHang, btnNapTien, btnLichSuChoi};
        for (AnchorPane btn : customerBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-sub-btn-active");
                if (!btn.getStyleClass().contains("sidebar-sub-btn")) {
                    btn.getStyleClass().add("sidebar-sub-btn");
                }
            }
        }
        if (activeSubBtn != null) {
            activeSubBtn.getStyleClass().add("sidebar-sub-btn-active");
        }
    }

    // Hàm này sẽ chạy khi ông click chuột vào nút Quản lý thiết bị trên Sidebar
    private void loadContent(String fxmlFile, AnchorPane activeBtn) {
        setActiveMenu(activeBtn);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Khong tim thay file " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    public void onThietBiMenuClick() {
        setActiveMenu(btnThietBi);
        try {
            // 1. Tải cái ruột (file quan-ly-thiet-bi.fxml) lên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-thiet-bi.fxml"));

            // 2. Xóa sạch những gì đang có ở giữa màn hình
            contentArea.getChildren().clear();

            // 3. Nhét cái ruột mới tải vào giữa
            contentArea.getChildren().add(fxml);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onKhachHangMenuClick() {
        boolean visible = !customerSubMenu.isVisible();
        customerSubMenu.setVisible(visible);
        customerSubMenu.setManaged(visible);
        lblKhachHangArrow.setText(visible ? "⌄" : "›");
        if (visible) {
            onSubKhachHangClick();
        }
    }

    @FXML
    public void onSubKhachHangClick() {
        loadContent("quan-ly-khach-hang.fxml", btnKhachHang);
        setActiveCustomerSub(btnSubKhachHang);
    }

    @FXML
    public void onNapTienMenuClick() {
        loadContent("quan-ly-nap-tien.fxml", btnKhachHang);
        setActiveCustomerSub(btnNapTien);
    }

    @FXML
    public void onLichSuChoiMenuClick() {
        loadContent("quan-ly-lich-su-choi.fxml", btnKhachHang);
        setActiveCustomerSub(btnLichSuChoi);
    }

    @FXML
    public void onNhanVienMenuClick() {
        setActiveMenu(btnNhanVien);
        try {
            // Nạp file giao diện nhân viên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-nhan-vien.fxml"));

            // Dọn dẹp chỗ trống ở giữa và nhét trang nhân viên vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-nhan-vien.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onSanPhamMenuClick() {
        setActiveMenu(btnSanPham);
        try {
            // Nạp file giao diện nhân viên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-san-pham.fxml"));

            // Dọn dẹp chỗ trống ở giữa và nhét trang nhân viên vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-san-pham.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onTaiChinhMenuClick() {
        setActiveMenu(btnTaiChinh);
        try {
            // Nạp file giao diện nhân viên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-tai-chinh.fxml"));

            // Dọn dẹp chỗ trống ở giữa và nhét trang nhân viên vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-tai-chinh.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onKhuVucMenuClick() {
        setActiveMenu(btnKhuVuc);
        try {
            // Nạp file giao diện nhân viên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-khu-vuc.fxml"));

            // Dọn dẹp chỗ trống ở giữa và nhét trang nhân viên vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-khu-vuc.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onKhuyenMaiMenuClick() {
        setActiveMenu(btnKhuyenMai);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-khuyen-mai.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Khong tim thay file quan-ly-khuyen-mai.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onSuKienMenuClick() {
        setActiveMenu(btnSuKien);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-su-kien.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Khong tim thay file quan-ly-su-kien.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onNhapHangMenuClick() {
        setActiveMenu(btnNhapHang);
        try {
            // Nạp file giao diện nhân viên
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-nhap-hang.fxml"));

            // Dọn dẹp chỗ trống ở giữa và nhét trang nhân viên vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-nhap-hang.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private AnchorPane btnUserProfile; // Khai báo id của vùng User

    @FXML
    public void onUserClick(MouseEvent event) {
        // 1. Tạo ContextMenu
        ContextMenu logoutMenu = new ContextMenu();

        // 2. Tạo Item "Đăng xuất"
        MenuItem logoutItem = new MenuItem("🚪 Đăng xuất");

        // 3. Xử lý sự kiện khi bấm vào chữ Đăng xuất
        logoutItem.setOnAction(e -> {
            performLogout();
        });

        logoutMenu.getItems().add(logoutItem);

        // 4. Hiển thị menu ngay dưới cái AnchorPane khi click
        logoutMenu.show(btnUserProfile, Side.BOTTOM, 0, 0);
    }

    private void performLogout() {
        try {
            // Tải lại giao diện Đăng nhập
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = fxmlLoader.load();

            // Lấy Stage hiện tại và chuyển Scene
            Stage stage = (Stage) btnUserProfile.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            System.out.println("Đã đăng xuất hệ thống!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label lblGreeting;

    // Hàm này sẽ được gọi từ màn hình Login để truyền tên qua
    public void setGreeting(String username) {
        if (username != null && !username.trim().isEmpty()) {
            lblGreeting.setText("Hello, " + username + "!");
        } else {
            lblGreeting.setText("Hello, Admin!"); // Phòng hờ nếu bị rỗng
        }
    }

}
