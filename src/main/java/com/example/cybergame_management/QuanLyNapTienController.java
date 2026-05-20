package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuanLyNapTienController {
    @FXML private Label lblTongGiaoDich;
    @FXML private ComboBox<String> cbKhachHang;
    @FXML private TextField txtSoTien;
    @FXML private ComboBox<String> cbKhuyenMai;
    @FXML private ComboBox<String> cbPhuongThuc;
    @FXML private TableView<NapTien> tbNapTien;
    @FXML private TableColumn<NapTien, String> colMaGD;
    @FXML private TableColumn<NapTien, String> colThoiGian;
    @FXML private TableColumn<NapTien, String> colMaKH;
    @FXML private TableColumn<NapTien, String> colKhachHang;
    @FXML private TableColumn<NapTien, String> colSoTien;
    @FXML private TableColumn<NapTien, String> colDiemCong;
    @FXML private TableColumn<NapTien, String> colPhuongThuc;
    @FXML private TableColumn<NapTien, String> colKhuyenMai;
    @FXML private TableColumn<NapTien, String> colNguoiThucHien;

    private final ObservableList<NapTien> data = FXCollections.observableArrayList(
            new NapTien("GD001", "2026-04-20\n14:30", "KH001", "Nguyen Van An", "100000", "100", "Tien mat", "Khong", "Admin"),
            new NapTien("GD002", "2026-04-21\n09:15", "KH002", "Tran Thi Binh", "50000", "55", "Chuyen khoan", "Tang 10%", "Admin"),
            new NapTien("GD003", "2026-04-22\n18:45", "KH004", "Pham Thu Dung", "200000", "240", "Momo", "Tang 20%", "Admin")
    );

    @FXML
    public void initialize() {
        cbKhachHang.setItems(FXCollections.observableArrayList(
                "KH001 - Nguyen Van An",
                "KH002 - Tran Thi Binh",
                "KH003 - Le Minh Cuong",
                "KH004 - Pham Thu Dung"
        ));
        cbKhuyenMai.setItems(FXCollections.observableArrayList("Khong", "Tang 10%", "Tang 20%"));
        cbKhuyenMai.setValue("Khong");
        cbPhuongThuc.setItems(FXCollections.observableArrayList("Tien mat", "Chuyen khoan", "Momo"));
        cbPhuongThuc.setValue("Tien mat");

        colMaGD.setCellValueFactory(cellData -> cellData.getValue().maGDProperty());
        colThoiGian.setCellValueFactory(cellData -> cellData.getValue().thoiGianProperty());
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colKhachHang.setCellValueFactory(cellData -> cellData.getValue().khachHangProperty());
        colSoTien.setCellValueFactory(cellData -> new SimpleStringProperty("+" + DisplayFormat.money(DisplayFormat.parseMoney(cellData.getValue().soTienProperty().get()))));
        colDiemCong.setCellValueFactory(cellData -> new SimpleStringProperty("+ " + cellData.getValue().diemCongProperty().get()));
        colPhuongThuc.setCellValueFactory(cellData -> cellData.getValue().phuongThucProperty());
        colKhuyenMai.setCellValueFactory(cellData -> cellData.getValue().khuyenMaiProperty());
        colNguoiThucHien.setCellValueFactory(cellData -> cellData.getValue().nguoiThucHienProperty());

        colSoTien.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("money-income");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    getStyleClass().add("money-income");
                }
            }
        });
        colDiemCong.setCellFactory(column -> badgeCell("point-badge"));
        colKhuyenMai.setCellFactory(column -> promoBadgeCell());

        tbNapTien.setItems(data);
        updateStats();
    }

    @FXML
    public void onNapTienClick() {
        if (cbKhachHang.getValue() == null || cbKhachHang.getValue().isBlank()) {
            showWarning("Vui lòng chọn khách hàng.");
            return;
        }

        long soTien = DisplayFormat.parseMoney(txtSoTien.getText());
        if (soTien <= 0) {
            showWarning("Vui lòng nhập số tiền nạp hợp lệ.");
            return;
        }

        String[] customerParts = cbKhachHang.getValue().split(" - ", 2);
        String maKH = customerParts[0];
        String tenKH = customerParts.length > 1 ? customerParts[1] : "";
        String khuyenMai = cbKhuyenMai.getValue();
        int diemCong = calculatePoints(soTien, khuyenMai);

        String maGD = "GD" + String.format("%03d", data.size() + 1);
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm"));
        data.add(0, new NapTien(maGD, thoiGian, maKH, tenKH, String.valueOf(soTien), String.valueOf(diemCong),
                cbPhuongThuc.getValue(), khuyenMai, "Admin"));

        txtSoTien.clear();
        cbKhachHang.getSelectionModel().clearSelection();
        cbKhuyenMai.setValue("Khong");
        cbPhuongThuc.setValue("Tien mat");
        updateStats();
    }

    private int calculatePoints(long soTien, String khuyenMai) {
        int basePoints = (int) (soTien / 1000);
        if ("Tang 10%".equals(khuyenMai)) {
            return (int) Math.round(basePoints * 1.1);
        }
        if ("Tang 20%".equals(khuyenMai)) {
            return (int) Math.round(basePoints * 1.2);
        }
        return basePoints;
    }

    private TableCell<NapTien, String> badgeCell(String styleClass) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add(styleClass);
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private TableCell<NapTien, String> promoBadgeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add("Khong".equals(item) ? "promo-badge-muted" : "promo-badge");
                setGraphic(badge);
                setText(null);
            }
        };
    }

    private void updateStats() {
        lblTongGiaoDich.setText("Tổng: " + data.size() + " giao dịch");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class NapTien {
        private final SimpleStringProperty maGD;
        private final SimpleStringProperty thoiGian;
        private final SimpleStringProperty maKH;
        private final SimpleStringProperty khachHang;
        private final SimpleStringProperty soTien;
        private final SimpleStringProperty diemCong;
        private final SimpleStringProperty phuongThuc;
        private final SimpleStringProperty khuyenMai;
        private final SimpleStringProperty nguoiThucHien;

        public NapTien(String maGD, String thoiGian, String maKH, String khachHang, String soTien,
                       String diemCong, String phuongThuc, String khuyenMai, String nguoiThucHien) {
            this.maGD = new SimpleStringProperty(maGD);
            this.thoiGian = new SimpleStringProperty(thoiGian);
            this.maKH = new SimpleStringProperty(maKH);
            this.khachHang = new SimpleStringProperty(khachHang);
            this.soTien = new SimpleStringProperty(soTien);
            this.diemCong = new SimpleStringProperty(diemCong);
            this.phuongThuc = new SimpleStringProperty(phuongThuc);
            this.khuyenMai = new SimpleStringProperty(khuyenMai);
            this.nguoiThucHien = new SimpleStringProperty(nguoiThucHien);
        }

        public SimpleStringProperty maGDProperty() { return maGD; }
        public SimpleStringProperty thoiGianProperty() { return thoiGian; }
        public SimpleStringProperty maKHProperty() { return maKH; }
        public SimpleStringProperty khachHangProperty() { return khachHang; }
        public SimpleStringProperty soTienProperty() { return soTien; }
        public SimpleStringProperty diemCongProperty() { return diemCong; }
        public SimpleStringProperty phuongThucProperty() { return phuongThuc; }
        public SimpleStringProperty khuyenMaiProperty() { return khuyenMai; }
        public SimpleStringProperty nguoiThucHienProperty() { return nguoiThucHien; }
    }
}
