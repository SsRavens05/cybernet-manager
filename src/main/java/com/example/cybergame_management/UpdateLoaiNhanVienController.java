package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateLoaiNhanVienController {

    @FXML private TextField txtMaLoaiNV;
    @FXML private TextField txtViTri;
    @FXML private TextField txtMucLuong;

    private LoaiNhanVien loaiNVDangSua;

    public void setLoaiNhanVienData(LoaiNhanVien lnv) {
        this.loaiNVDangSua = lnv;

        txtMaLoaiNV.setText(lnv.getMaLoaiNV());
        txtViTri.setText(lnv.getViTri());
        txtMucLuong.setText(lnv.getMucLuong());
    }

    @FXML
    public void onLuuClick() {
        if (txtViTri.getText().trim().isEmpty() || txtMucLuong.getText().trim().isEmpty()) {
            return;
        }

        loaiNVDangSua.setViTri(txtViTri.getText().trim());
        long luongVal = DisplayFormat.parseMoney(txtMucLuong.getText());
        loaiNVDangSua.setMucLuong(DisplayFormat.money(luongVal));

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaLoaiNV.getScene().getWindow();
        stage.close();
    }
}
