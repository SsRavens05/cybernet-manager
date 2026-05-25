package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML private ComboBox<String> cbChucVu;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private ComboBox<String> cbCaLam;

    private NhanVien nhanVienDangSua;
    private ObservableList<LoaiNhanVien> loaiNVList = FXCollections.observableArrayList();

    public void setNhanVienData(NhanVien nv) {
        this.nhanVienDangSua = nv;

        // Đổ data text
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtMaSoThue.setText(nv.getMaSoThue());
        txtSoBHYT.setText(nv.getSoBHYT());
        txtNgayVao.setText(nv.getNgayVao());
        txtNgayThoiViec.setText(nv.getNgayThoiViec());

        // Setup và đổ data cho ComboBox Chức Vụ (Loại Nhân Viên)
        try {
            if (DatabaseConnection.isConfigured()) {
                loaiNVList = LoaiNhanVienRepository.findAll();
            } else {
                loaiNVList = DatabaseSeedData.loaiNhanVien();
            }
        } catch (Exception ex) {
            loaiNVList = DatabaseSeedData.loaiNhanVien();
        }

        cbChucVu.getItems().clear();
        for (LoaiNhanVien lnv : loaiNVList) {
            cbChucVu.getItems().add(lnv.getViTri());
        }
        cbChucVu.setValue(nv.getChucVu());

        // Setup và đổ data cho ComboBox Trạng Thái
        cbTrangThai.getItems().setAll("Đang làm", "Nghỉ phép", "Nghỉ việc");
        cbTrangThai.setValue(nv.getTrangThai());

        // Setup và đổ data cho ComboBox Ca Làm
        cbCaLam.getItems().setAll("Ca Sáng", "Ca Trưa", "Ca Chiều", "Ca Đêm");
        String currentShift = nv.getCaLam();
        if (currentShift != null) {
            if (currentShift.contains("Sáng") || currentShift.equalsIgnoreCase("ca sang") || currentShift.equalsIgnoreCase("sang")) {
                cbCaLam.setValue("Ca Sáng");
            } else if (currentShift.contains("Trưa") || currentShift.equalsIgnoreCase("ca trua") || currentShift.equalsIgnoreCase("trua")) {
                cbCaLam.setValue("Ca Trưa");
            } else if (currentShift.contains("Chiều") || currentShift.equalsIgnoreCase("ca chieu") || currentShift.equalsIgnoreCase("chieu")) {
                cbCaLam.setValue("Ca Chiều");
            } else if (currentShift.contains("Đêm") || currentShift.equalsIgnoreCase("ca dem") || currentShift.equalsIgnoreCase("dem")) {
                cbCaLam.setValue("Ca Đêm");
            } else {
                cbCaLam.setValue("Ca Sáng");
            }
        } else {
            cbCaLam.setValue("Ca Sáng");
        }
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
        nhanVienDangSua.setCaLam(cbCaLam.getValue());

        String selectedRole = cbChucVu.getValue();
        nhanVienDangSua.setChucVu(selectedRole);

        String luongText = "7.000.000đ";
        for (LoaiNhanVien lnv : loaiNVList) {
            if (lnv.getViTri().equalsIgnoreCase(selectedRole)) {
                luongText = lnv.getMucLuong();
                break;
            }
        }
        nhanVienDangSua.setLuong(luongText);

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