package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

        String bd = txtThoiGianBD.getText().trim();
        String kt = txtThoiGianKT.getText().trim();

        if (!validateThoiGianCaLam(bd, kt)) {
            return;
        }

        String sg = txtSoGioLam.getText().trim().isEmpty() ? "6h" : txtSoGioLam.getText().trim();
        try {
            String clean = sg.replaceAll("[^0-9.-]", "").trim();
            if (clean.isEmpty()) {
                throw new NumberFormatException();
            }
            double gio = Double.parseDouble(clean);
            if (gio <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R4");
                alert.setContentText("Số giờ làm của ca làm phải lớn hơn 0!");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo ràng buộc");
            alert.setHeaderText("Dữ liệu không hợp lệ");
            alert.setContentText("Số giờ làm phải là một số hợp lệ!");
            alert.showAndWait();
            return;
        }

        caLamDangSua.setThoiGianBD(bd);
        caLamDangSua.setThoiGianKT(kt);
        caLamDangSua.setSoGioLam(sg);
        caLamDangSua.setSoGioTangCa(txtSoGioTangCa.getText().trim().isEmpty() ? "—" : txtSoGioTangCa.getText().trim());
        caLamDangSua.setTrangThai(cbTrangThai.getValue());

        dongForm();
    }

    private boolean validateThoiGianCaLam(String bd, String kt) {
        try {
            java.time.LocalTime tBD = java.time.LocalTime.parse(bd);
            java.time.LocalTime tKT = java.time.LocalTime.parse(kt);
            if (!tKT.isAfter(tBD)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R7");
                alert.setContentText("Thời gian kết thúc ca làm phải lớn hơn thời gian bắt đầu ca làm!");
                alert.showAndWait();
                return false;
            }
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dữ liệu không hợp lệ");
            alert.setHeaderText("Sai định dạng thời gian");
            alert.setContentText("Thời gian phải đúng định dạng HH:mm (Ví dụ: 08:00)!");
            alert.showAndWait();
            return false;
        }
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
