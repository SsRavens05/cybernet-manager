package com.example.cybergame_management;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateThietBiController {

    @FXML private TextField txtMaTB;
    @FXML private TextField txtTenTB;
    @FXML private ComboBox<String> cbLoaiTB;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private TextField txtNgayMua;

    @FXML private Button btnHuy;

    private ThietBi thietBiĐangSua;
    private ObservableList<ThietBi> allDevices;

    public void setThietBiData(ThietBi tb, ObservableList<ThietBi> allDevices) {
        this.thietBiĐangSua = tb;
        this.allDevices = allDevices;

        txtMaTB.setText(tb.getMaTB());
        txtTenTB.setText(tb.getTenTB());
        txtNgayMua.setText(tb.getNgayMua());

        cbLoaiTB.getItems().setAll("Loa", "Màn Hình", "Bàn Phím", "Chuột", "Tai Nghe");
        cbLoaiTB.setValue(tb.getLoaiTB());

        cbTrangThai.getItems().setAll("DALAP", "CHUALAP", "BAOTRI", "HONG", "HOATDONG");
        cbTrangThai.setValue(tb.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        String maPC = txtNgayMua.getText().trim();
        String trangThai = cbTrangThai.getValue();

        if ("HOATDONG".equals(trangThai) || "DALAP".equals(trangThai)) {
            if (allDevices != null) {
                long count = allDevices.stream()
                        .filter(tb -> tb != thietBiĐangSua && maPC.equalsIgnoreCase(tb.getNgayMua()) && ("HOATDONG".equals(tb.getTrangThai()) || "DALAP".equals(tb.getTrangThai())))
                        .count();
                if (count >= 10) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo ràng buộc");
                    alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R18");
                    alert.setContentText("Một máy tính đang hoạt động chỉ được gắn tối đa 10 thiết bị!");
                    alert.showAndWait();
                    return;
                }
            }
        }

        // Cập nhật lại vào Model trong RAM
        thietBiĐangSua.setTenTB(txtTenTB.getText());
        thietBiĐangSua.setLoaiTB(cbLoaiTB.getValue());
        thietBiĐangSua.setTrangThai(trangThai);
        thietBiĐangSua.setNgayMua(maPC);

        // Gọi lệnh Update xuống CSDL Oracle ở đây
        try {
            if (DatabaseConnection.isConfigured()) {
                ThietBiRepository.update(thietBiĐangSua);
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật thiết bị vào Database: " + ex.getMessage());
            alert.showAndWait();
            return;
        }

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) btnHuy.getScene().getWindow();
        stage.close();
    }
}