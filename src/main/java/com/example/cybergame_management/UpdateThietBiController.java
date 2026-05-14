package com.example.cybergame_management;

import javafx.fxml.FXML;
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

    public void setThietBiData(ThietBi tb) {
        this.thietBiĐangSua = tb;

        txtMaTB.setText(tb.getMaTB());
        txtTenTB.setText(tb.getTenTB());
        txtNgayMua.setText(tb.getNgayMua());

        cbLoaiTB.getItems().setAll("Loa", "Màn Hình", "Bàn Phím", "Chuột", "Tai Nghe");
        cbLoaiTB.setValue(tb.getLoaiTB());

        cbTrangThai.getItems().setAll("DALAP", "CHUALAP", "BAOTRI", "HONG");
        cbTrangThai.setValue(tb.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        // Cập nhật lại vào Model trong RAM
        thietBiĐangSua.setTenTB(txtTenTB.getText());
        thietBiĐangSua.setLoaiTB(cbLoaiTB.getValue());
        thietBiĐangSua.setTrangThai(cbTrangThai.getValue());
        thietBiĐangSua.setNgayMua(txtNgayMua.getText());

        // TODO: Gọi lệnh Update xuống CSDL Oracle ở đây
        System.out.println("Đã lưu cập nhật cho Mã TB: " + thietBiĐangSua.getMaTB());

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