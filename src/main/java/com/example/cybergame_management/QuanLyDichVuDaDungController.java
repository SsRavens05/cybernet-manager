package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class QuanLyDichVuDaDungController {

    @FXML private Label lblTongDichVu;
    @FXML private Label lblDaPhucVu;
    @FXML private Label lblTongSoLuong;
    @FXML private TableView<DichVuDaDung> tbDichVu;
    @FXML private TableColumn<DichVuDaDung, String> colMaDVDD;
    @FXML private TableColumn<DichVuDaDung, String> colMaSP;
    @FXML private TableColumn<DichVuDaDung, String> colSoLuong;
    @FXML private TableColumn<DichVuDaDung, String> colTrangThai;
    @FXML private TableColumn<DichVuDaDung, String> colThoiGian;
    @FXML private TextField txtSearch;

    private ObservableList<DichVuDaDung> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = DichVuDaDungRepository.findAll();
            } else {
                data = DatabaseSeedData.dichVuDaDung();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.dichVuDaDung();
        }
        colMaDVDD.setCellValueFactory(cellData -> cellData.getValue().maDVDDProperty());
        colMaSP.setCellValueFactory(cellData -> cellData.getValue().maSPProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colThoiGian.setCellValueFactory(cellData -> cellData.getValue().thoiGianProperty());

        // Custom status badge rendering for premium looks
        colTrangThai.setCellFactory(column -> new TableCell<DichVuDaDung, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if (item.equalsIgnoreCase("Serviced") || item.equalsIgnoreCase("DA_PHUC_VU")) {
                        badge.getStyleClass().add("badge-active"); // Green
                    } else if (item.equalsIgnoreCase("Processing") || item.equalsIgnoreCase("CHO_XU_LY")) {
                        badge.getStyleClass().add("badge-warning"); // Orange
                    } else {
                        badge.getStyleClass().add("badge-default"); // Grey
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Search Filter
        FilteredList<DichVuDaDung> filtered = new FilteredList<>(data, item -> true);
        tbDichVu.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        updateStats();
    }

    private boolean matchesFilter(DichVuDaDung item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.maDVDDProperty().get(),
                item.maSPProperty().get(),
                item.trangThaiProperty().get(),
                item.thoiGianProperty().get());
    }

    private void updateStats() {
        lblTongDichVu.setText(String.valueOf(data.size()));
        
        long countDaPhucVu = data.stream()
                .filter(d -> d.trangThaiProperty().get().equalsIgnoreCase("Serviced"))
                .count();
        lblDaPhucVu.setText(String.valueOf(countDaPhucVu));

        int tongSoLuong = data.stream()
                .mapToInt(d -> DisplayFormat.parseInt(d.soLuongProperty().get()))
                .sum();
        lblTongSoLuong.setText(String.valueOf(tongSoLuong));
    }
}
