package com.example.cybergame_management;

import javafx.fxml.FXML;
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

        cbTrangThai.getItems().setAll("ACTIVE", "INACTIVE", "BANNED");
        cbTrangThai.setValue(kh.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        khachHangDangSua.setHoTen(txtHoTen.getText());
        khachHangDangSua.setSoDiemTichLuy(txtSoDiemTichLuy.getText());
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
