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

import javafx.scene.image.ImageView;
import java.io.IOException;

public class MainController {

    // Đây chính là cái "lỗ hổng" ở giữa màn hình mà mình đã đục sẵn ở main-view.fxml
    @FXML
    private StackPane contentArea;

    @FXML private AnchorPane btnThietBi;
    @FXML private AnchorPane btnPC;
    @FXML private AnchorPane btnKhachHang;
    @FXML private AnchorPane btnSubKhachHang;
    @FXML private AnchorPane btnNapTien;
    @FXML private AnchorPane btnLichSuChoi;
    @FXML private AnchorPane btnDoiQua;
    @FXML private AnchorPane btnQuaTang;
    @FXML private AnchorPane btnNhanVien;
    @FXML private AnchorPane btnSanPham;
    @FXML private AnchorPane btnTaiChinh;
    @FXML private AnchorPane btnKhuVuc;
    @FXML private AnchorPane btnSubKhuVuc;
    @FXML private AnchorPane btnLoaiKhuVuc;
    @FXML private AnchorPane btnKhuyenMai;
    @FXML private AnchorPane btnNhapHang;
    @FXML private VBox customerSubMenu;
    @FXML private VBox khuVucSubMenu;
    @FXML private VBox nhanVienSubMenu;
    @FXML private VBox sanPhamSubMenu;
    @FXML private Label lblKhachHangArrow;
    @FXML private Label lblKhuVucArrow;
    @FXML private Label lblNhanVienArrow;
    @FXML private Label lblSanPhamArrow;
    @FXML private AnchorPane btnSubNhanVien;
    @FXML private AnchorPane btnLoaiNhanVien;
    @FXML private AnchorPane btnCaLam;
    @FXML private AnchorPane btnSubSanPham;
    @FXML private AnchorPane btnSubDichVu;
    @FXML private Label lblProfileName;
    @FXML private Label lblProfileBadge;
    @FXML private AnchorPane btnTaiKhoan;
    @FXML private Label lblRolePill;
    @FXML private ImageView imgNhanVienIcon;
    @FXML private Label lblNhanVienTitle;


    @FXML
    public void initialize() {
        // Cập nhật thông tin profile
        lblProfileName.setText(UserSession.getUsername());
        lblProfileBadge.setText(UserSession.getRole());
        lblProfileBadge.getStyleClass().clear();
        lblProfileBadge.getStyleClass().add("badge");
        if (UserSession.isAdmin()) {
            lblProfileBadge.getStyleClass().add("badge-admin");
        } else {
            lblProfileBadge.getStyleClass().add("badge-staff");
        }

        // Áp dụng bộ lọc sidebar
        if (!UserSession.isAdmin()) {
            if (lblRolePill != null) {
                lblRolePill.setText("🛡  Nhân viên");
            }

            btnThietBi.setVisible(false);
            btnThietBi.setManaged(false);

            btnTaiChinh.setVisible(false);
            btnTaiChinh.setManaged(false);

            btnNhapHang.setVisible(false);
            btnNhapHang.setManaged(false);

            btnTaiKhoan.setVisible(false);
            btnTaiKhoan.setManaged(false);

            // Ẩn lịch sử chơi hoàn toàn đối với nhân viên
            if (btnLichSuChoi != null) {
                btnLichSuChoi.setVisible(false);
                btnLichSuChoi.setManaged(false);
            }

            // Submenus cho nhân viên
            btnSubNhanVien.setVisible(false);
            btnSubNhanVien.setManaged(false);
            btnLoaiNhanVien.setVisible(false);
            btnLoaiNhanVien.setManaged(false);
            if (lblNhanVienArrow != null) {
                lblNhanVienArrow.setVisible(false);
                lblNhanVienArrow.setManaged(false);
            }

            // Tái cấu trúc menu Nhân viên thành "Ca Làm" (nhưng giữ nguyên icon đồ họa và lề chữ)
            if (imgNhanVienIcon != null) {
                imgNhanVienIcon.setVisible(true);
                imgNhanVienIcon.setManaged(true);
            }
            if (lblNhanVienTitle != null) {
                lblNhanVienTitle.setText("Ca Làm");
                lblNhanVienTitle.setLayoutX(45.0);
            }

            // Submenus cho khu vực: ẩn Loại Khu Vực
            btnLoaiKhuVuc.setVisible(false);
            btnLoaiKhuVuc.setManaged(false);

            // Mở rộng mặc định tất cả các menu con cho nhân viên
            if (customerSubMenu != null) {
                customerSubMenu.setVisible(true);
                customerSubMenu.setManaged(true);
            }
            if (lblKhachHangArrow != null) {
                lblKhachHangArrow.setText("⌄");
            }

            if (sanPhamSubMenu != null) {
                sanPhamSubMenu.setVisible(true);
                sanPhamSubMenu.setManaged(true);
            }
            if (lblSanPhamArrow != null) {
                lblSanPhamArrow.setText("⌄");
            }

            if (khuVucSubMenu != null) {
                khuVucSubMenu.setVisible(true);
                khuVucSubMenu.setManaged(true);
            }
            if (lblKhuVucArrow != null) {
                lblKhuVucArrow.setText("⌄");
            }

            // Load màn hình khách hàng mặc định cho nhân viên
            loadContent("quan-ly-khach-hang.fxml", btnKhachHang);
        } else {
            if (lblRolePill != null) {
                lblRolePill.setText("🛡  Admin");
            }
            if (imgNhanVienIcon != null) {
                imgNhanVienIcon.setVisible(true);
                imgNhanVienIcon.setManaged(true);
            }
            if (lblNhanVienTitle != null) {
                lblNhanVienTitle.setText("Quản Lý Nhân Viên");
                lblNhanVienTitle.setLayoutX(45.0);
            }
            if (lblNhanVienArrow != null) {
                lblNhanVienArrow.setVisible(true);
                lblNhanVienArrow.setManaged(true);
            }
            if (lblKhuVucArrow != null) {
                lblKhuVucArrow.setVisible(true);
                lblKhuVucArrow.setManaged(true);
            }

            // Load màn hình thiết bị mặc định cho admin
            loadContent("quan-ly-thiet-bi.fxml", btnThietBi);
        }
    }

    private void setActiveMenu(AnchorPane activeBtn) {
        // Gom tất cả các nút vào 1 mảng để xử lý cho lẹ
        AnchorPane[] allBtns = {btnThietBi, btnPC, btnKhachHang, btnNhanVien, btnSanPham,
                btnTaiChinh, btnKhuVuc, btnKhuyenMai, btnNhapHang, btnTaiKhoan};

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
        if (activeBtn != btnKhuVuc) {
            setActiveKhuVucSub(null);
        }
        if (activeBtn != btnNhanVien) {
            setActiveNhanVienSub(null);
        }
        if (activeBtn != btnSanPham) {
            setActiveSanPhamSub(null);
        }
    }

    private void setActiveSanPhamSub(AnchorPane activeSubBtn) {
        AnchorPane[] sanPhamBtns = {btnSubSanPham, btnSubDichVu};
        for (AnchorPane btn : sanPhamBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-sub-btn-active");
                if (!btn.getStyleClass().contains("sidebar-sub-btn")) {
                    btn.getStyleClass().add("sidebar-sub-btn");
                }
                // Cập nhật màu chữ nhãn động
                for (javafx.scene.Node node : btn.getChildren()) {
                    if (node instanceof Label) {
                        Label lbl = (Label) node;
                        if (btn == activeSubBtn) {
                            lbl.setTextFill(javafx.scene.paint.Color.WHITE);
                        } else {
                            lbl.setTextFill(javafx.scene.paint.Color.web("#f0a3ad"));
                        }
                    }
                }
            }
        }
        if (activeSubBtn != null) {
            activeSubBtn.getStyleClass().add("sidebar-sub-btn-active");
        }
    }

    private void setActiveCustomerSub(AnchorPane activeSubBtn) {
        AnchorPane[] customerBtns = {btnSubKhachHang, btnNapTien, btnLichSuChoi, btnDoiQua, btnQuaTang};
        for (AnchorPane btn : customerBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-sub-btn-active");
                if (!btn.getStyleClass().contains("sidebar-sub-btn")) {
                    btn.getStyleClass().add("sidebar-sub-btn");
                }
                // Cập nhật màu chữ nhãn động
                for (javafx.scene.Node node : btn.getChildren()) {
                    if (node instanceof Label) {
                        Label lbl = (Label) node;
                        if (btn == activeSubBtn) {
                            lbl.setTextFill(javafx.scene.paint.Color.WHITE);
                        } else {
                            lbl.setTextFill(javafx.scene.paint.Color.web("#f0a3ad"));
                        }
                    }
                }
            }
        }
        if (activeSubBtn != null) {
            activeSubBtn.getStyleClass().add("sidebar-sub-btn-active");
        }
    }

    private void setActiveKhuVucSub(AnchorPane activeSubBtn) {
        AnchorPane[] khuVucBtns = {btnSubKhuVuc, btnLoaiKhuVuc};
        for (AnchorPane btn : khuVucBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-sub-btn-active");
                if (!btn.getStyleClass().contains("sidebar-sub-btn")) {
                    btn.getStyleClass().add("sidebar-sub-btn");
                }
                // Cập nhật màu chữ nhãn động
                for (javafx.scene.Node node : btn.getChildren()) {
                    if (node instanceof Label) {
                        Label lbl = (Label) node;
                        if (btn == activeSubBtn) {
                            lbl.setTextFill(javafx.scene.paint.Color.WHITE);
                        } else {
                            lbl.setTextFill(javafx.scene.paint.Color.web("#f0a3ad"));
                        }
                    }
                }
            }
        }
        if (activeSubBtn != null) {
            activeSubBtn.getStyleClass().add("sidebar-sub-btn-active");
        }
    }

    private void setActiveNhanVienSub(AnchorPane activeSubBtn) {
        AnchorPane[] nhanVienBtns = {btnSubNhanVien, btnLoaiNhanVien, btnCaLam};
        for (AnchorPane btn : nhanVienBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-sub-btn-active");
                if (!btn.getStyleClass().contains("sidebar-sub-btn")) {
                    btn.getStyleClass().add("sidebar-sub-btn");
                }
                // Cập nhật màu chữ nhãn động
                for (javafx.scene.Node node : btn.getChildren()) {
                    if (node instanceof Label) {
                        Label lbl = (Label) node;
                        if (btn == activeSubBtn) {
                            lbl.setTextFill(javafx.scene.paint.Color.WHITE);
                        } else {
                            lbl.setTextFill(javafx.scene.paint.Color.web("#f0a3ad"));
                        }
                    }
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
    public void onPCMenuClick() {
        setActiveMenu(btnPC);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-pc.fxml"));
            contentArea.getChildren().clear();
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
    public void onDoiQuaMenuClick() {
        loadContent("quan-ly-doi-qua.fxml", btnKhachHang);
        setActiveCustomerSub(btnDoiQua);
    }


    @FXML
    public void onQuaTangMenuClick() {
        loadContent("quan-ly-qua-tang.fxml", btnKhachHang);
        setActiveCustomerSub(btnQuaTang);
    }

    @FXML
    public void onNhanVienMenuClick() {
        if (!UserSession.isAdmin()) {
            onCaLamMenuClick();
            return;
        }
        boolean visible = !nhanVienSubMenu.isVisible();
        nhanVienSubMenu.setVisible(visible);
        nhanVienSubMenu.setManaged(visible);
        lblNhanVienArrow.setText(visible ? "⌄" : "›");
        if (visible) {
            onSubNhanVienClick();
        }
    }

    @FXML
    public void onSubNhanVienClick() {
        loadContent("quan-ly-nhan-vien.fxml", btnNhanVien);
        setActiveNhanVienSub(btnSubNhanVien);
    }

    @FXML
    public void onLoaiNhanVienMenuClick() {
        loadContent("quan-ly-loai-nhan-vien.fxml", btnNhanVien);
        setActiveNhanVienSub(btnLoaiNhanVien);
    }

    @FXML
    public void onCaLamMenuClick() {
        loadContent("quan-ly-ca-lam.fxml", btnNhanVien);
        setActiveNhanVienSub(btnCaLam);
    }

    @FXML
    public void onSanPhamMenuClick() {
        boolean visible = !sanPhamSubMenu.isVisible();
        sanPhamSubMenu.setVisible(visible);
        sanPhamSubMenu.setManaged(visible);
        lblSanPhamArrow.setText(visible ? "⌄" : "›");
        if (visible) {
            onSubSanPhamClick();
        }
    }

    @FXML
    public void onSubSanPhamClick() {
        loadContent("quan-ly-san-pham.fxml", btnSanPham);
        setActiveSanPhamSub(btnSubSanPham);
    }

    @FXML
    public void onSubDichVuClick() {
        loadContent("quan-ly-dich-vu-da-dung.fxml", btnSanPham);
        setActiveSanPhamSub(btnSubDichVu);
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
        boolean visible = !khuVucSubMenu.isVisible();
        khuVucSubMenu.setVisible(visible);
        khuVucSubMenu.setManaged(visible);
        lblKhuVucArrow.setText(visible ? "⌄" : "›");
        if (visible) {
            onSubKhuVucClick();
        }
    }

    @FXML
    public void onSubKhuVucClick() {
        loadContent("quan-ly-khu-vuc.fxml", btnKhuVuc);
        setActiveKhuVucSub(btnSubKhuVuc);
    }

    @FXML
    public void onLoaiKhuVucMenuClick() {
        loadContent("quan-ly-loai-khu-vuc.fxml", btnKhuVuc);
        setActiveKhuVucSub(btnLoaiKhuVuc);
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
    public void onTaiKhoanMenuClick() {
        setActiveMenu(btnTaiKhoan);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-tai-khoan.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            System.out.println("Lỗi rồi: Không tìm thấy file quan-ly-tai-khoan.fxml");
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
            
            // Hủy phóng to nếu có
            stage.setMaximized(false);
            
            // Chuyển sang Scene với kích thước chuẩn 1280x720 như lúc khởi chạy
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            
            // Reset kích thước Stage
            stage.setWidth(1280);
            stage.setHeight(720);
            
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
        if (lblGreeting != null) {
            if (username != null && !username.trim().isEmpty()) {
                lblGreeting.setText("Hello, " + username + "!");
            } else {
                lblGreeting.setText("Hello, Admin!"); // Phòng hờ nếu bị rỗng
            }
        }
    }

}
