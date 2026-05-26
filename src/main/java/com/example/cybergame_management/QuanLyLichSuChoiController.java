package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class QuanLyLichSuChoiController {
    @FXML private Label lblTongPhien;
    @FXML private Label lblDangChoi;
    @FXML private Label lblDaThanhToan;
    @FXML private TextField txtSearch;
    @FXML private TableView<LichSuChoi> tbLichSuChoi;
    @FXML private TableColumn<LichSuChoi, String> colMaLS;
    @FXML private TableColumn<LichSuChoi, String> colMaPC;
    @FXML private TableColumn<LichSuChoi, String> colMaKH;
    @FXML private TableColumn<LichSuChoi, String> colKhachHang;
    @FXML private TableColumn<LichSuChoi, String> colNgayBD;
    @FXML private TableColumn<LichSuChoi, String> colNgayKT;

    private final ObservableList<LichSuChoi> data = FXCollections.observableArrayList(
            new LichSuChoi("LSC001", "PC001", "KH001", "Nguyen Van An", "2026-05-06 08:00:00", "2026-05-06 10:00:00", "Da thanh toan"),
            new LichSuChoi("LSC002", "PC002", "KH002", "Tran Thi Binh", "2026-05-06 10:10:00", "", "Dang choi"),
            new LichSuChoi("LSC003", "PC003", "KH003", "Le Minh Cuong", "2026-05-07 13:00:00", "2026-05-07 15:30:00", "Da thanh toan")
    );

    @FXML
    public void initialize() {
        colMaLS.setCellValueFactory(cellData -> cellData.getValue().maLSProperty());
        colMaPC.setCellValueFactory(cellData -> cellData.getValue().maPCProperty());
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colKhachHang.setCellValueFactory(cellData -> cellData.getValue().khachHangProperty());
        colNgayBD.setCellValueFactory(cellData -> cellData.getValue().ngayBDProperty());
        colNgayKT.setCellValueFactory(cellData -> cellData.getValue().ngayKTProperty());

        FilteredList<LichSuChoi> filtered = new FilteredList<>(data, item -> true);
        tbLichSuChoi.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
        updateStats();
    }

    private boolean matchesFilter(LichSuChoi item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.maLSProperty().get(), item.maPCProperty().get(), item.maKHProperty().get(),
                item.khachHangProperty().get(), item.ngayBDProperty().get(), item.ngayKTProperty().get());
    }

    private void updateStats() {
        lblTongPhien.setText(String.valueOf(data.size()));
        lblDangChoi.setText(String.valueOf(data.stream()
                .filter(item -> "Dang choi".equalsIgnoreCase(item.trangThaiProperty().get()))
                .count()));
        lblDaThanhToan.setText(String.valueOf(data.stream()
                .filter(item -> "Da thanh toan".equalsIgnoreCase(item.trangThaiProperty().get()))
                .count()));
    }

    public static class LichSuChoi {
        private final SimpleStringProperty maLS;
        private final SimpleStringProperty maPC;
        private final SimpleStringProperty maKH;
        private final SimpleStringProperty khachHang;
        private final SimpleStringProperty ngayBD;
        private final SimpleStringProperty ngayKT;
        private final SimpleStringProperty trangThai;

        public LichSuChoi(String maLS, String maPC, String maKH, String khachHang, String ngayBD, String ngayKT, String trangThai) {
            this.maLS = new SimpleStringProperty(maLS);
            this.maPC = new SimpleStringProperty(maPC);
            this.maKH = new SimpleStringProperty(maKH);
            this.khachHang = new SimpleStringProperty(khachHang);
            this.ngayBD = new SimpleStringProperty(ngayBD);
            this.ngayKT = new SimpleStringProperty(ngayKT);
            this.trangThai = new SimpleStringProperty(trangThai);
        }

        public SimpleStringProperty maLSProperty() { return maLS; }
        public SimpleStringProperty maPCProperty() { return maPC; }
        public SimpleStringProperty maKHProperty() { return maKH; }
        public SimpleStringProperty khachHangProperty() { return khachHang; }
        public SimpleStringProperty ngayBDProperty() { return ngayBD; }
        public SimpleStringProperty ngayKTProperty() { return ngayKT; }
        public SimpleStringProperty trangThaiProperty() { return trangThai; }
    }
}
