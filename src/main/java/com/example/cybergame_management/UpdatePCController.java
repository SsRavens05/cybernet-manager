package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdatePCController {

    @FXML private TextField txtMaPC;
    @FXML private ComboBox<String> cbMaKV;
    @FXML private TextField txtCpu;
    @FXML private TextField txtRam;
    @FXML private TextField txtVga;
    @FXML private TextField txtRom;
    @FXML private TextField txtSoMay;
    @FXML private TextField txtLoaiPC;
    @FXML private ComboBox<String> cbTrangThai;

    private PC pcDangSua;

    public void setPCData(PC pc) {
        this.pcDangSua = pc;

        txtMaPC.setText(pc.getMaPC());
        txtCpu.setText(pc.getCpu());
        txtRam.setText(pc.getRam());
        txtVga.setText(pc.getVga());
        txtRom.setText(pc.getRom());
        txtSoMay.setText(pc.getSoMay());
        txtLoaiPC.setText(pc.getLoaiPC());

        // Tải danh sách Khu Vực khả dụng
        ObservableList<String> kvList = FXCollections.observableArrayList();
        if (PCRepository.isDatabaseEnabled()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT MAKV FROM KHUVUC WHERE NVL(IS_DELETE, 0) = 0 ORDER BY MAKV");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kvList.add(rs.getString("MAKV"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (kvList.isEmpty()) {
            for (KhuVuc kv : DatabaseSeedData.khuVuc()) {
                kvList.add(kv.getMaKV());
            }
        }
        cbMaKV.setItems(kvList);
        cbMaKV.setValue(pc.getMaKV());

        cbTrangThai.getItems().setAll("HOATDONG", "BAOTRI");
        cbTrangThai.setValue(pc.getTrangThai());
    }

    @FXML
    public void onLuuClick() {
        pcDangSua.setMaKV(cbMaKV.getValue());
        pcDangSua.setCpu(txtCpu.getText().trim());
        pcDangSua.setRam(txtRam.getText().trim());
        pcDangSua.setVga(txtVga.getText().trim());
        pcDangSua.setRom(txtRom.getText().trim());
        pcDangSua.setSoMay(txtSoMay.getText().trim());
        pcDangSua.setLoaiPC(txtLoaiPC.getText().trim());
        pcDangSua.setTrangThai(cbTrangThai.getValue());

        dongForm();
    }

    @FXML
    public void onHuyClick() {
        dongForm();
    }

    private void dongForm() {
        Stage stage = (Stage) txtMaPC.getScene().getWindow();
        stage.close();
    }
}
