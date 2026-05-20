package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class QuanLyTaiChinhController {

    @FXML private Label lblTongThu;
    @FXML private Label lblTongChi;
    @FXML private Label lblLoiNhuan;
    @FXML private Button btnTongQuan;
    @FXML private Button btnGiaoDich;
    @FXML private HBox overviewPane;
    @FXML private VBox transactionPane;
    @FXML private BarChart<String, Number> barChartDoanhThu;
    @FXML private LineChart<String, Number> lineChartLoiNhuan;
    @FXML private TableView<GiaoDich> tbGiaoDich;
    @FXML private TableColumn<GiaoDich, String> colMaGD;
    @FXML private TableColumn<GiaoDich, String> colNgay;
    @FXML private TableColumn<GiaoDich, String> colKhachHang;
    @FXML private TableColumn<GiaoDich, String> colLoai;
    @FXML private TableColumn<GiaoDich, String> colSoTien;
    @FXML private TableColumn<GiaoDich, String> colGhiChu;

    private final ObservableList<GiaoDich> giaoDichList = DatabaseSeedData.giaoDich();

    @FXML
    public void initialize() {
        setupTable();
        tbGiaoDich.setItems(giaoDichList);
        updateSummaryCards();
        loadChartsFromTransactions();
        showTongQuan();
    }

    private void setupTable() {
        colMaGD.setCellValueFactory(cellData -> cellData.getValue().maGDProperty());
        colNgay.setCellValueFactory(cellData -> cellData.getValue().ngayProperty());
        colKhachHang.setCellValueFactory(cellData -> cellData.getValue().khachHangProperty());
        colLoai.setCellValueFactory(cellData -> cellData.getValue().loaiProperty());
        colSoTien.setCellValueFactory(cellData -> new SimpleStringProperty(formatSignedMoney(cellData.getValue().getSoTien())));
        colGhiChu.setCellValueFactory(cellData -> cellData.getValue().ghiChuProperty());

        colLoai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                if (item.equals("NAP_TIEN")) {
                    badge.getStyleClass().add("badge-active");
                } else if (item.equals("THANH_TOAN")) {
                    badge.getStyleClass().add("badge-info");
                } else {
                    badge.getStyleClass().add("badge-banned");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        colSoTien.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    getStyleClass().removeAll("money-income", "money-expense");
                    return;
                }

                setText(item);
                getStyleClass().removeAll("money-income", "money-expense");
                getStyleClass().add(item.startsWith("+") ? "money-income" : "money-expense");
            }
        });
    }

    private void updateSummaryCards() {
        long tongThu = giaoDichList.stream()
                .filter(gd -> gd.getSoTien() > 0)
                .mapToLong(GiaoDich::getSoTien)
                .sum();
        long tongChi = giaoDichList.stream()
                .filter(gd -> gd.getSoTien() < 0)
                .mapToLong(gd -> Math.abs(gd.getSoTien()))
                .sum();

        lblTongThu.setText(formatMoney(tongThu));
        lblTongChi.setText(formatMoney(tongChi));
        lblLoiNhuan.setText(formatMoney(tongThu - tongChi));
    }

    private void loadChartsFromTransactions() {
        XYChart.Series<String, Number> seriesThu = new XYChart.Series<>();
        seriesThu.setName("Tong Thu");
        XYChart.Series<String, Number> seriesChi = new XYChart.Series<>();
        seriesChi.setName("Tong Chi");
        XYChart.Series<String, Number> seriesLoiNhuan = new XYChart.Series<>();
        seriesLoiNhuan.setName("Loi Nhuan Luy Ke");

        long loiNhuanLuyKe = 0;
        for (GiaoDich giaoDich : giaoDichList) {
            String label = giaoDich.maGDProperty().get();
            long soTien = giaoDich.getSoTien();
            long thu = Math.max(soTien, 0);
            long chi = soTien < 0 ? Math.abs(soTien) : 0;
            loiNhuanLuyKe += soTien;

            seriesThu.getData().add(new XYChart.Data<>(label, thu));
            seriesChi.getData().add(new XYChart.Data<>(label, chi));
            seriesLoiNhuan.getData().add(new XYChart.Data<>(label, loiNhuanLuyKe));
        }

        barChartDoanhThu.getData().setAll(seriesThu, seriesChi);
        lineChartLoiNhuan.getData().setAll(seriesLoiNhuan);
    }

    @FXML
    public void showTongQuan() {
        overviewPane.setVisible(true);
        overviewPane.setManaged(true);
        transactionPane.setVisible(false);
        transactionPane.setManaged(false);
        setActiveTab(btnTongQuan, btnGiaoDich);
    }

    @FXML
    public void showGiaoDich() {
        overviewPane.setVisible(false);
        overviewPane.setManaged(false);
        transactionPane.setVisible(true);
        transactionPane.setManaged(true);
        setActiveTab(btnGiaoDich, btnTongQuan);
    }

    private void setActiveTab(Button active, Button inactive) {
        active.getStyleClass().removeAll("tab-secondary", "tab-primary");
        inactive.getStyleClass().removeAll("tab-secondary", "tab-primary");
        active.getStyleClass().add("tab-primary");
        inactive.getStyleClass().add("tab-secondary");
    }

    private String formatMoney(long amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "d";
    }

    private String formatSignedMoney(long amount) {
        return (amount >= 0 ? "+" : "-") + formatMoney(Math.abs(amount));
    }

    public static class GiaoDich {
        private final SimpleStringProperty maGD;
        private final SimpleStringProperty ngay;
        private final SimpleStringProperty khachHang;
        private final SimpleStringProperty loai;
        private final long soTien;
        private final SimpleStringProperty ghiChu;

        public GiaoDich(String maGD, String ngay, String khachHang, String loai, long soTien, String ghiChu) {
            this.maGD = new SimpleStringProperty(maGD);
            this.ngay = new SimpleStringProperty(ngay);
            this.khachHang = new SimpleStringProperty(khachHang);
            this.loai = new SimpleStringProperty(loai);
            this.soTien = soTien;
            this.ghiChu = new SimpleStringProperty(ghiChu);
        }

        public SimpleStringProperty maGDProperty() { return maGD; }
        public SimpleStringProperty ngayProperty() { return ngay; }
        public SimpleStringProperty khachHangProperty() { return khachHang; }
        public SimpleStringProperty loaiProperty() { return loai; }
        public long getSoTien() { return soTien; }
        public SimpleStringProperty ghiChuProperty() { return ghiChu; }
    }
}
