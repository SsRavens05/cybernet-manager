package com.example.cybergame_management;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateNhanVienController {

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;

    @FXML private ComboBox<String> cbChucVu;
    @FXML private ComboBox<String> cbCaLam;
    @FXML private ComboBox<String> cbTrangThai;

    private NhanVien nhanVienDangSua;

    public void setNhanVienData(NhanVien nv) {
        this.nhanVienDangSua = nv;

        // Đổ data text
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtSdt.setText(nv.getSdt());

        // Setup và đổ data cho ComboBox Chức Vụ
        cbChucVu.getItems().setAll("Quản Lý", "Thu Ngân", "Pha Chế", "Bảo Vệ");
        cbChucVu.setValue(nv.getChucVu());

        // Setup và đổ data cho ComboBox Ca Làm
        cbCaLam.getItems().setAll("Ca Sáng (6h-14h)", "Ca Chiều (14h-22h)", "Ca Đêm (22h-6h)");
        cbCaLam.setValue(nv.getCaLam());

        // Setup và đổ data cho ComboBox Trạng Thái
        cbTrangThai.getItems().setAll("Đang Làm Việc", "Đang Nghỉ Phép", "Đã Nghỉ Việc");
        cbTrangThai.setValue(nv.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        // Cập nhật lại object NhanVien từ giao diện
        nhanVienDangSua.setHoTen(txtHoTen.getText());
        nhanVienDangSua.setSdt(txtSdt.getText());
        nhanVienDangSua.setChucVu(cbChucVu.getValue());
        nhanVienDangSua.setCaLam(cbCaLam.getValue());
        nhanVienDangSua.setTrangThai(cbTrangThai.getValue());

        // TODO: Gắn lệnh gọi trigger/procedure dưới Oracle để update bảng NHANVIEN ở đây

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