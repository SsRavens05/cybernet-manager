package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateKhachHangController {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDu;
    @FXML private TextField txtSoDiemTichLuy;
    @FXML private ComboBox<String> cbTrangThai;

    private KhachHang khachHangDangSua;

    public void setKhachHangData(KhachHang kh) {
        this.khachHangDangSua = kh;

        txtMaKH.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtSoDu.setText(DisplayFormat.money(DisplayFormat.parseMoney(kh.getSoDu())));
        txtSoDu.setEditable(false);
        txtSoDiemTichLuy.setText(kh.getSoDiemTichLuy());
        txtSoDiemTichLuy.setEditable(false);
        txtSoDiemTichLuy.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280;");

        cbTrangThai.getItems().setAll("ACTIVE", "INACTIVE", "BANNED");
        cbTrangThai.setValue(kh.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        String diemStr = txtSoDiemTichLuy.getText().trim();
        try {
            long diem = Long.parseLong(diemStr);
            if (diem < 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R2");
                alert.setContentText("Điểm tích lũy của khách hàng không được âm!");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo ràng buộc");
            alert.setHeaderText("Dữ liệu không hợp lệ");
            alert.setContentText("Số điểm tích lũy phải là một số nguyên hợp lệ!");
            alert.showAndWait();
            return;
        }

        khachHangDangSua.setHoTen(txtHoTen.getText());
        khachHangDangSua.setSoDiemTichLuy(diemStr);
        khachHangDangSua.setTrangThai(cbTrangThai.getValue());
        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaKH.getScene().getWindow();
        stage.close();
    }
}
