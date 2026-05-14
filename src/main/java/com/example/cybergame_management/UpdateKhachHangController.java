package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateKhachHangController {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt; // Đã sửa lại chữ thường cho khớp FXML
    @FXML private TextField txtEmail;
    @FXML private TextField txtSoDu; // Đã thêm ô số dư

    @FXML private ComboBox<String> cbHang;
    @FXML private ComboBox<String> cbTrangThai;

    private KhachHang khachHangDangSua;

    public void setKhachHangData(KhachHang kh) {
        this.khachHangDangSua = kh;

        // Lấy data bằng các hàm get chuẩn
        txtMaKH.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtSdt.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail());
        txtSoDu.setText(kh.getSoDu());

        // Khóa ô Số Dư lại, chỉ cho xem chứ không cho sửa tay
        txtSoDu.setEditable(false);
        txtSoDu.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #64748b; -fx-padding: 0 15 0 15;");

        // Đổ data vào 2 cái ComboBox
        cbHang.getItems().setAll("Thường", "VIP", "SVIP", "Tuyển Thủ");
        cbHang.setValue(kh.getHang());

        cbTrangThai.getItems().setAll("Hoạt động", "Bị Khóa", "Chờ Xác Thực");
        cbTrangThai.setValue(kh.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        // Lưu data bằng các hàm set chuẩn
        khachHangDangSua.setHoTen(txtHoTen.getText());
        khachHangDangSua.setSdt(txtSdt.getText());
        khachHangDangSua.setEmail(txtEmail.getText());
        khachHangDangSua.setHang(cbHang.getValue());
        khachHangDangSua.setTrangThai(cbTrangThai.getValue());

        // TODO: Viết lệnh Update DB Oracle ở đây.
        // (Nhớ là chỉ update mấy trường cơ bản thôi nha, bỏ qua cái DOANHSO đi vì doanh thu là tính từ hóa đơn cộng lại chứ không cập nhật trực tiếp vô bảng Khách Hàng)

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        // Lấy đại thằng txtMaKH làm mỏ neo để tìm cái Window hiện tại và đóng nó lại
        Stage stage = (Stage) txtMaKH.getScene().getWindow();
        stage.close();
    }
}