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
    @FXML private ComboBox<String> cbLoaiKV;
    @FXML private ComboBox<String> cbTrangThai;

    private KhuVuc khuVucDangSua;

    public void setKhuVucData(KhuVuc kv) {
        this.khuVucDangSua = kv;

        txtMaKV.setText(kv.getMaKV());
        txtTenKV.setText(kv.getTenKV());
        txtSoMay.setText(kv.getSoMay());
        txtGiaThue.setText(kv.getGiaThue());

        // Setup và đổ data cho ComboBox Loại Khu Vực
        cbLoaiKV.getItems().setAll("VIP", "Thường", "Esport", "Offline");
        String currentLkv = kv.getMoTa();
        if (currentLkv != null) {
            if (currentLkv.contains("VIP") || currentLkv.equalsIgnoreCase("LKV001")) {
                cbLoaiKV.setValue("VIP");
            } else if (currentLkv.contains("Thường") || currentLkv.contains("Thuong") || currentLkv.equalsIgnoreCase("LKV002")) {
                cbLoaiKV.setValue("Thường");
            } else if (currentLkv.contains("Esport") || currentLkv.equalsIgnoreCase("LKV003")) {
                cbLoaiKV.setValue("Esport");
            } else if (currentLkv.contains("Offline") || currentLkv.equalsIgnoreCase("LKV004")) {
                cbLoaiKV.setValue("Offline");
            } else {
                cbLoaiKV.setValue("Thường");
            }
        } else {
            cbLoaiKV.setValue("Thường");
        }

        // Đăng ký listener tự cập nhật Giá Thuê
        cbLoaiKV.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("VIP".equals(newVal)) txtGiaThue.setText("15.000đ");
            else if ("Thường".equals(newVal)) txtGiaThue.setText("10.000đ");
            else if ("Esport".equals(newVal)) txtGiaThue.setText("20.000đ");
            else if ("Offline".equals(newVal)) txtGiaThue.setText("8.000đ");
        });

        cbTrangThai.getItems().setAll("HOATDONG", "BAOTRI", "DONG_CUA");
        cbTrangThai.setValue(kv.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        khuVucDangSua.setTenKV(txtTenKV.getText());
        khuVucDangSua.setSoMay(txtSoMay.getText());
        khuVucDangSua.setGiaThue(chuanHoaTien(txtGiaThue.getText()));
        khuVucDangSua.setTrangThai(cbTrangThai.getValue());
        khuVucDangSua.setMoTa(cbLoaiKV.getValue());

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
