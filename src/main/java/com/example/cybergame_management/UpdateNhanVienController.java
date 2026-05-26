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
        ObservableList<String> shiftsList = FXCollections.observableArrayList();
        try {
            ObservableList<CaLam> activeShifts;
            if (DatabaseConnection.isConfigured()) {
                activeShifts = CaLamRepository.findAll();
            } else {
                activeShifts = DatabaseSeedData.caLamShifts();
            }
            for (CaLam cl : activeShifts) {
                shiftsList.add(cl.getMaCa() + " (" + cl.getThoiGianBD() + " - " + cl.getThoiGianKT() + ")");
            }
        } catch (Exception ex) {
            ObservableList<CaLam> activeShifts = DatabaseSeedData.caLamShifts();
            for (CaLam cl : activeShifts) {
                shiftsList.add(cl.getMaCa() + " (" + cl.getThoiGianBD() + " - " + cl.getThoiGianKT() + ")");
            }
        }
        if (shiftsList.isEmpty()) {
            shiftsList.setAll("Ca Sáng", "Ca Trưa", "Ca Chiều", "Ca Đêm");
        }
        cbCaLam.getItems().setAll(shiftsList);

        String currentShift = nv.getCaLam();
        boolean found = false;
        for (String item : cbCaLam.getItems()) {
            String maCa = item.split(" ")[0]; // e.g. "CA001"
            if (currentShift != null && (currentShift.equalsIgnoreCase(maCa) || item.contains(currentShift) || currentShift.contains(maCa))) {
                cbCaLam.setValue(item);
                found = true;
                break;
            }
        }
        if (!found && currentShift != null) {
            // Fallback matching
            if (currentShift.contains("Sáng") || currentShift.equalsIgnoreCase("ca sang") || currentShift.equalsIgnoreCase("sang")) {
                for (String item : cbCaLam.getItems()) {
                    if (item.contains("06:00") || item.contains("08:00")) {
                        cbCaLam.setValue(item);
                        found = true;
                        break;
                    }
                }
            } else if (currentShift.contains("Trưa") || currentShift.equalsIgnoreCase("ca trua") || currentShift.equalsIgnoreCase("trua")) {
                for (String item : cbCaLam.getItems()) {
                    if (item.contains("12:00")) {
                        cbCaLam.setValue(item);
                        found = true;
                        break;
                    }
                }
            } else if (currentShift.contains("Chiều") || currentShift.equalsIgnoreCase("ca chieu") || currentShift.equalsIgnoreCase("chieu")) {
                for (String item : cbCaLam.getItems()) {
                    if (item.contains("13:00") || item.contains("18:00")) {
                        cbCaLam.setValue(item);
                        found = true;
                        break;
                    }
                }
            } else if (currentShift.contains("Đêm") || currentShift.equalsIgnoreCase("ca dem") || currentShift.equalsIgnoreCase("dem")) {
                for (String item : cbCaLam.getItems()) {
                    if (item.contains("00:00") || item.contains("24:00")) {
                        cbCaLam.setValue(item);
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found && !cbCaLam.getItems().isEmpty()) {
            cbCaLam.setValue(cbCaLam.getItems().get(0));
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