package com.example.cybergame_management;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateNhapHangController {

    @FXML private TextField txtMaPN;
    @FXML private TextField txtLoaiHang;
    @FXML private TextField txtNhaCC;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtDonGia;
    @FXML private TextField txtNgayNhap;
    @FXML private TextField txtNguoiNhap;
    @FXML private Label lblTongTienNhap;
    @FXML private ComboBox<String> cbTrangThai;

    private NhapHang nhapHangDangSua;

    public void setNhapHangData(NhapHang nh) {
        this.nhapHangDangSua = nh;

        txtMaPN.setText(nh.getMaPN());
        txtLoaiHang.setText(nh.getLoaiHang());
        txtNhaCC.setText(nh.getNhaCC());
        txtSoLuong.setText(nh.getSoLuong());
        txtDonGia.setText(boDonViTien(nh.getDonGia()));
        txtNgayNhap.setText(nh.getNgayNhap());
        txtNguoiNhap.setText(nh.getNguoiNhap());
        lblTongTienNhap.setText("Tổng tiền nhập: " + nh.getTongTienNhap());

        cbTrangThai.getItems().setAll("CHO_DUYET", "DA_NHAP", "HUY");
        cbTrangThai.setValue(nh.getTrangThai());

        ChangeListener<String> calcTotal = (obs, oldVal, newVal) -> capNhatTongTien();
        txtSoLuong.textProperty().addListener(calcTotal);
        txtDonGia.textProperty().addListener(calcTotal);
        capNhatTongTien();
    }

    @FXML
    public void onLuuClick() {
        nhapHangDangSua.setLoaiHang(txtLoaiHang.getText());
        nhapHangDangSua.setNhaCC(txtNhaCC.getText());
        nhapHangDangSua.setSoLuong(txtSoLuong.getText());
        nhapHangDangSua.setDonGia(chuanHoaTien(txtDonGia.getText()));
        nhapHangDangSua.setTongTienNhap(lblTongTienNhap.getText().replace("Tổng tiền nhập: ", ""));
        nhapHangDangSua.setNgayNhap(txtNgayNhap.getText());
        nhapHangDangSua.setNguoiNhap(txtNguoiNhap.getText());
        nhapHangDangSua.setTrangThai(cbTrangThai.getValue());

        // TODO: Gọi lệnh Update xuống CSDL Oracle ở đây.
        System.out.println("Đã lưu cập nhật cho Mã PN: " + nhapHangDangSua.getMaPN());

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
            lblTongTienNhap.setText(String.format("Tổng tiền nhập: %,dđ", soLuong * donGia).replace(",", "."));
        } catch (NumberFormatException e) {
            lblTongTienNhap.setText("Tổng tiền nhập: 0đ");
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
        Stage stage = (Stage) txtMaPN.getScene().getWindow();
        stage.close();
    }
}
