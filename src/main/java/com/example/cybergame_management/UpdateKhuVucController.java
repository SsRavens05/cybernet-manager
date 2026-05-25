package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateKhuVucController {

    @FXML private TextField txtMaKV;
    @FXML private TextField txtTenKV;
    @FXML private TextField txtSoMay;
    @FXML private TextField txtGiaThue;
    @FXML private TextField txtMoTa;
    @FXML private ComboBox<String> cbTrangThai;

    private KhuVuc khuVucDangSua;

    public void setKhuVucData(KhuVuc kv) {
        this.khuVucDangSua = kv;

        txtMaKV.setText(kv.getMaKV());
        txtTenKV.setText(kv.getTenKV());
        txtSoMay.setText(kv.getSoMay());
        txtGiaThue.setText(kv.getGiaThue());
        txtMoTa.setText(kv.getMoTa());

        cbTrangThai.getItems().setAll("HOATDONG", "BAOTRI", "DONG_CUA");
        cbTrangThai.setValue(kv.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        khuVucDangSua.setTenKV(txtTenKV.getText());
        khuVucDangSua.setSoMay(txtSoMay.getText());
        khuVucDangSua.setGiaThue(chuanHoaTien(txtGiaThue.getText()));
        khuVucDangSua.setTrangThai(cbTrangThai.getValue());
        khuVucDangSua.setMoTa(txtMoTa.getText());

        try {
            if (KhuVucRepository.isDatabaseEnabled()) {
                KhuVucRepository.update(khuVucDangSua);
            }
            System.out.println("Đã lưu cập nhật cho Mã KV: " + khuVucDangSua.getMaKV());
            dongForm();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Lỗi khi cập nhật khu vực vào Database: " + e.getMessage());
            alert.showAndWait();
        }
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
        Stage stage = (Stage) txtMaKV.getScene().getWindow();
        stage.close();
    }
}
