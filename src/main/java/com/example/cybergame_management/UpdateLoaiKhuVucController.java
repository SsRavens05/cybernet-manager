package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateLoaiKhuVucController {

    @FXML private TextField txtMaLoaiKV;
    @FXML private TextField txtTenLoaiKV;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtGia;

    private LoaiKhuVuc loaiKhuVucDangSua;

    public void setLoaiKhuVucData(LoaiKhuVuc lkv) {
        this.loaiKhuVucDangSua = lkv;

        txtMaLoaiKV.setText(lkv.getMaLoaiKV());
        txtTenLoaiKV.setText(lkv.getTenLoaiKV());
        txtSoLuong.setText(lkv.getSoLuong());
        txtGia.setText(lkv.getGia());
    }

    @FXML
    public void onLuuClick() {
        loaiKhuVucDangSua.setTenLoaiKV(txtTenLoaiKV.getText());
        loaiKhuVucDangSua.setSoLuong(txtSoLuong.getText());
        loaiKhuVucDangSua.setGia(chuanHoaTien(txtGia.getText()));

        System.out.println("Đã lưu cập nhật cho Mã Loại KV: " + loaiKhuVucDangSua.getMaLoaiKV());

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private String chuanHoaTien(String giaThue) {
        String gia = giaThue == null ? "" : giaThue.trim();
        if (gia.isEmpty() || gia.endsWith("đ")) {
            return gia;
        }
        return gia + "đ";
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaLoaiKV.getScene().getWindow();
        stage.close();
    }
}
