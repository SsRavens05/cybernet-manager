package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class QuanLyLichSuDoiQuaController {

    @FXML private Label lblTongDoiQua;
    @FXML private Label lblDangCho;
    @FXML private Label lblSoLoaiQua;

    @FXML private TableView<DoiQua> tbDoiQua;
    @FXML private TableColumn<DoiQua, String> colMaDQ;
    @FXML private TableColumn<DoiQua, String> colMaKH;
    @FXML private TableColumn<DoiQua, String> colMaQT;
    @FXML private TableColumn<DoiQua, String> colNgayDoi;
    @FXML private TableColumn<DoiQua, String> colSoLuong;
    @FXML private TableColumn<DoiQua, String> colTrangThai;

    @FXML private TextField txtSearch;

    private ObservableList<DoiQua> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = DoiQuaRepository.findAll();
            } else {
                data = DatabaseSeedData.doiQua();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.doiQua();
        }
        colMaDQ.setCellValueFactory(cellData -> cellData.getValue().maDQProperty());
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colMaQT.setCellValueFactory(cellData -> cellData.getValue().maQTProperty());
        colNgayDoi.setCellValueFactory(cellData -> cellData.getValue().ngayDoiProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // Custom styling for TrangThai Column with badges
        colTrangThai.setCellFactory(column -> new TableCell<DoiQua, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");

                    if (item.equalsIgnoreCase("Completed")) {
                        badge.getStyleClass().add("badge-active");
                    } else if (item.equalsIgnoreCase("Pending")) {
                        badge.getStyleClass().add("badge-warning");
                    } else if (item.equalsIgnoreCase("Cancelled")) {
                        badge.getStyleClass().add("badge-banned");
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        FilteredList<DoiQua> filtered = new FilteredList<>(data, item -> true);
        tbDoiQua.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        updateStats();
    }

    private boolean matchesFilter(DoiQua item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaDQ(), item.getMaKH(), item.getMaQT(), item.getNgayDoi(), item.getSoLuong(), item.getTrangThai());
    }

    private void updateStats() {
        lblTongDoiQua.setText(String.valueOf(data.size()));
        lblDangCho.setText(String.valueOf(data.stream().filter(dq -> "Pending".equalsIgnoreCase(dq.getTrangThai())).count()));
        lblSoLoaiQua.setText(String.valueOf(data.stream().map(DoiQua::getMaQT).distinct().count()));
    }
}
