package com.example.cybergame_management;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateNhapHangController {

    @FXML private TextField txtMaNH;
    @FXML private TextField txtTenSP;
    @FXML private TextField txtNhaCC;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtDonGia;
    @FXML private TextField txtNgayNhap;
    @FXML private TextField txtNguoiNhap;
    @FXML private Label lblTongTien;
    @FXML private ComboBox<String> cbTrangThai;

    private NhapHang nhapHangDangSua;

    public void setNhapHangData(NhapHang nh) {
        this.nhapHangDangSua = nh;

        txtMaNH.setText(nh.getMaNH());
        txtTenSP.setText(nh.getTenSP());
        txtNhaCC.setText(nh.getNhaCC());
        txtSoLuong.setText(nh.getSoLuong());
        txtDonGia.setText(boDonViTien(nh.getDonGia()));
        txtNgayNhap.setText(nh.getNgayNhap());
        txtNguoiNhap.setText(nh.getNguoiNhap());
        lblTongTien.setText("Tổng tiền: " + nh.getTongTien());

        cbTrangThai.getItems().setAll("CHO_DUYET", "DA_NHAP", "HUY");
        cbTrangThai.setValue(nh.getTrangThai());

        ChangeListener<String> calcTotal = (obs, oldVal, newVal) -> capNhatTongTien();
        txtSoLuong.textProperty().addListener(calcTotal);
        txtDonGia.textProperty().addListener(calcTotal);
        capNhatTongTien();
    }

    @FXML
    public void onLuuClick() {
        nhapHangDangSua.setTenSP(txtTenSP.getText());
        nhapHangDangSua.setNhaCC(txtNhaCC.getText());
        nhapHangDangSua.setSoLuong(txtSoLuong.getText());
        nhapHangDangSua.setDonGia(chuanHoaTien(txtDonGia.getText()));
        nhapHangDangSua.setTongTien(lblTongTien.getText().replace("Tổng tiền: ", ""));
        nhapHangDangSua.setNgayNhap(txtNgayNhap.getText());
        nhapHangDangSua.setNguoiNhap(txtNguoiNhap.getText());
        nhapHangDangSua.setTrangThai(cbTrangThai.getValue());

        // TODO: Gọi lệnh Update xuống CSDL Oracle ở đây.
        System.out.println("Đã lưu cập nhật cho Mã NH: " + nhapHangDangSua.getMaNH());

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void capNhatTongTien() {
        try {
            long soLuong = Long.parseLong(txtSoLuong.getText().trim());
            long donGia = Long.parseLong(boDonViTien(txtDonGia.getText()).replace(".", "").trim());
            lblTongTien.setText(String.format("Tổng tiền: %,dđ", soLuong * donGia).replace(",", "."));
        } catch (NumberFormatException e) {
            lblTongTien.setText("Tổng tiền: 0đ");
        }
    }

    private String boDonViTien(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("đ", "").trim();
    }

    private String chuanHoaTien(String value) {
        String tien = boDonViTien(value);
        if (tien.isEmpty()) {
            return tien;
        }
        return tien + "đ";
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaNH.getScene().getWindow();
        stage.close();
    }
}
