package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateCaLamController {

    @FXML private TextField txtMaCa;
    @FXML private TextField txtThoiGianBD;
    @FXML private TextField txtThoiGianKT;
    @FXML private TextField txtSoGioLam;
    @FXML private TextField txtSoGioTangCa;

    @FXML private ComboBox<String> cbTrangThai;

    private CaLam caLamDangSua;

    public void setCaLamData(CaLam cl) {
        this.caLamDangSua = cl;

        txtMaCa.setText(cl.getMaCa());
        txtThoiGianBD.setText(cl.getThoiGianBD());
        txtThoiGianKT.setText(cl.getThoiGianKT());
        txtSoGioLam.setText(cl.getSoGioLam());
        txtSoGioTangCa.setText(cl.getSoGioTangCa());

        cbTrangThai.getItems().setAll("Đang làm", "Sắp tới", "Đã kết thúc");
        cbTrangThai.setValue(cl.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        if (txtThoiGianBD.getText().trim().isEmpty() || txtThoiGianKT.getText().trim().isEmpty()) {
            return;
        }

        caLamDangSua.setThoiGianBD(txtThoiGianBD.getText().trim());
        caLamDangSua.setThoiGianKT(txtThoiGianKT.getText().trim());
        caLamDangSua.setSoGioLam(txtSoGioLam.getText().trim().isEmpty() ? "6h" : txtSoGioLam.getText().trim());
        caLamDangSua.setSoGioTangCa(txtSoGioTangCa.getText().trim().isEmpty() ? "—" : txtSoGioTangCa.getText().trim());
        caLamDangSua.setTrangThai(cbTrangThai.getValue());

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaCa.getScene().getWindow();
        stage.close();
    }
}
