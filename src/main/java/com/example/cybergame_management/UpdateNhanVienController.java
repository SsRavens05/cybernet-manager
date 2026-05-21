package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateNhanVienController {

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtMaSoThue;
    @FXML private TextField txtSoBHYT;
    @FXML private TextField txtNgayVao;
    @FXML private TextField txtNgayThoiViec;

    @FXML private ComboBox<String> cbTrangThai;

    private NhanVien nhanVienDangSua;

    public void setNhanVienData(NhanVien nv) {
        this.nhanVienDangSua = nv;

        // Đổ data text
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtMaSoThue.setText(nv.getMaSoThue());
        txtSoBHYT.setText(nv.getSoBHYT());
        txtNgayVao.setText(nv.getNgayVao());
        txtNgayThoiViec.setText(nv.getNgayThoiViec());

        // Setup và đổ data cho ComboBox Trạng Thái
        cbTrangThai.getItems().setAll("Đang làm", "Nghỉ phép", "Nghỉ việc");
        cbTrangThai.setValue(nv.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        // Cập nhật lại object NhanVien từ giao diện
        nhanVienDangSua.setHoTen(txtHoTen.getText());
        nhanVienDangSua.setMaSoThue(txtMaSoThue.getText());
        nhanVienDangSua.setSoBHYT(txtSoBHYT.getText());
        nhanVienDangSua.setNgayVao(txtNgayVao.getText());
        nhanVienDangSua.setNgayThoiViec(txtNgayThoiViec.getText());
        nhanVienDangSua.setTrangThai(cbTrangThai.getValue());

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaNV.getScene().getWindow();
        stage.close();
    }
}