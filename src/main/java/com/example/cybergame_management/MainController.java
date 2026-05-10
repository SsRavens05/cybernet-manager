package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    // Đây chính là cái "lỗ hổng" ở giữa màn hình mà mình đã đục sẵn ở main-view.fxml
    @FXML
    private StackPane contentArea;

    @FXML private AnchorPane btnThietBi;
    @FXML private AnchorPane btnKhachHang;
    @FXML private AnchorPane btnNhanVien;
    @FXML private AnchorPane btnSanPham;
    @FXML private AnchorPane btnTaiChinh;
    @FXML private AnchorPane btnKhuVuc;
    @FXML private AnchorPane btnKhuyenMai;
    @FXML private AnchorPane btnSuKien;
    @FXML private AnchorPane btnNhapHang;

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
    }

    // Hàm này sẽ chạy khi ông click chuột vào nút Quản lý thiết bị trên Sidebar
    @FXML
    public void onThietBiMenuClick() {
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
        setActiveMenu(btnKhachHang);
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("quan-ly-khach-hang.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
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


}