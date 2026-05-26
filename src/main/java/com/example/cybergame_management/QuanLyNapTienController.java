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

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.geometry.Pos;

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
        ObservableList<String> customerList = FXCollections.observableArrayList();
        if (KhachHangRepository.isDatabaseEnabled()) {
            try {
                for (KhachHang kh : KhachHangRepository.findAll()) {
                    customerList.add(kh.getMaKH() + " - " + kh.getHoTen());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to seed data in case of error
                for (KhachHang kh : DatabaseSeedData.khachHang()) {
                    customerList.add(kh.getMaKH() + " - " + kh.getHoTen());
                }
            }
        } else {
            for (KhachHang kh : DatabaseSeedData.khachHang()) {
                customerList.add(kh.getMaKH() + " - " + kh.getHoTen());
            }
        }
        cbKhachHang.setItems(customerList);

        cbKhuyenMai.setItems(getPromotionsFromDb());
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
        String phuongThuc = cbPhuongThuc.getValue();

        if ("Chuyen khoan".equals(phuongThuc) || "Momo".equals(phuongThuc)) {
            showMomoQRDialog(maKH, tenKH, soTien, diemCong, khuyenMai, phuongThuc);
        } else {
            if (KhachHangRepository.isDatabaseEnabled()) {
                try {
                    KhachHangRepository.deposit(maKH, soTien, diemCong);
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                    showError("Không thể nạp tiền vào database: " + ex.getMessage());
                    return;
                }
            }

            String maGD = "GD" + String.format("%03d", data.size() + 1);
            String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm"));
            data.add(0, new NapTien(maGD, thoiGian, maKH, tenKH, String.valueOf(soTien), String.valueOf(diemCong),
                    phuongThuc, khuyenMai, "Admin"));

            txtSoTien.clear();
            cbKhachHang.getSelectionModel().clearSelection();
            cbKhuyenMai.setValue("Khong");
            cbPhuongThuc.setValue("Tien mat");
            updateStats();
        }
    }

    private void showMomoQRDialog(String maKH, String tenKH, long soTien, int diemCong, String khuyenMai, String phuongThuc) {
        // === CẤU HÌNH TÀI KHOẢN MOMO NHẬN TIỀN CỦA BẠN ===
        String momoPhone = "0878008115"; // SĐT đăng ký MoMo của bạn (Thay bằng SĐT MoMo thật ở đây)
        String momoName = "PHAM CHI NGHIA"; // Tên chủ tài khoản MoMo viết hoa không dấu

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setTitle("Thanh Toán Chuyển Khoản MoMo");
        dialogStage.setResizable(false);

        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-alignment: center;");
        root.setPrefWidth(400);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #A50064; -fx-background-radius: 8; -fx-padding: 10;");
        header.setPrefWidth(360);
        Label lblTitle = new Label("NẠP TIỀN QUA VÍ MOMO");
        lblTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;");
        header.getChildren().add(lblTitle);

        long soTienCanThanhToan = soTien;
        if (khuyenMai != null && !"Khong".equalsIgnoreCase(khuyenMai)) {
            try {
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)%").matcher(khuyenMai);
                if (matcher.find()) {
                    double pct = Double.parseDouble(matcher.group(1));
                    if (khuyenMai.toLowerCase().contains("giảm") || khuyenMai.toLowerCase().contains("giam")) {
                        soTienCanThanhToan = (long) (soTien * (1.0 - pct / 100.0));
                    }
                }
            } catch (Exception ignored) {}
        }

        VBox detailsCard = new VBox(6);
        detailsCard.setStyle("-fx-background-color: #fff0f6; -fx-border-color: #ffd6e7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        detailsCard.setAlignment(Pos.CENTER_LEFT);
        detailsCard.setPrefWidth(360);

        Label lblKhachHangInfo = new Label("Khách hàng: " + maKH + " - " + tenKH);
        lblKhachHangInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #4b5563; -fx-font-weight: bold;");

        Label lblSoTienInfo = new Label("Số tiền nạp (Cộng tài khoản): " + DisplayFormat.money(soTien));
        lblSoTienInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280; -fx-font-weight: bold;");

        Label lblKhuyenMaiInfo = new Label("Khuyến mãi áp dụng: " + khuyenMai);
        lblKhuyenMaiInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #db2777; -fx-font-weight: bold;");

        Label lblThanhToanInfo = new Label("SỐ TIỀN CẦN THANH TOÁN: " + DisplayFormat.money(soTienCanThanhToan));
        lblThanhToanInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #A50064; -fx-font-weight: bold;");

        String maGDTemp = "GD" + String.format("%03d", data.size() + 1);
        String addInfo = "NAP_" + maKH + "_" + maGDTemp;
        Label lblNoiDung = new Label("Nội dung CK: " + addInfo);
        lblNoiDung.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563; -fx-font-family: 'Courier New', monospace; -fx-font-weight: bold;");

        if (khuyenMai != null && !"Khong".equalsIgnoreCase(khuyenMai)) {
            detailsCard.getChildren().addAll(lblKhachHangInfo, lblSoTienInfo, lblKhuyenMaiInfo, lblThanhToanInfo, lblNoiDung);
        } else {
            detailsCard.getChildren().addAll(lblKhachHangInfo, lblSoTienInfo, lblThanhToanInfo, lblNoiDung);
        }

        StackPane qrFrame = new StackPane();
        qrFrame.setStyle("-fx-background-color: #ffffff; -fx-border-color: #f472b6; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-background-radius: 8; -fx-padding: 8;");
        qrFrame.setPrefSize(220, 220);
        qrFrame.setMaxSize(220, 220);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: #A50064;");
        progress.setMaxSize(35, 35);

        ImageView qrImageView = new ImageView();
        qrImageView.setFitWidth(200);
        qrImageView.setFitHeight(200);
        qrImageView.setPreserveRatio(true);

        qrFrame.getChildren().addAll(progress, qrImageView);

        Image qrImage = null;
        try {
            // 1. Tự động sinh mã VietQR MoMo động chứa sẵn số tiền và nội dung chuyển khoản từ VietQR API
            String qrUrl = "https://img.vietqr.io/image/momo-" + momoPhone + "-compact.png?amount=" + soTienCanThanhToan + "&addInfo=" + addInfo + "&cardHolder=" + momoName.replace(" ", "%20");
            qrImage = new Image(qrUrl, true);
            
            qrImage.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0) {
                    progress.setVisible(false);
                }
            });
            qrImage.errorProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // 2. Tự động dự phòng: Nếu không có kết nối Internet hoặc lỗi API, nạp ảnh mã QR tĩnh của bạn!
                    try {
                        java.net.URL resourceUrl = getClass().getResource("images/momo_qr.png");
                        Image backupImage = null;
                        if (resourceUrl != null) {
                            backupImage = new Image(resourceUrl.toExternalForm());
                        } else {
                            java.io.File file = new java.io.File("src/main/resources/com/example/cybergame_management/images/momo_qr.png");
                            if (file.exists()) {
                                backupImage = new Image(file.toURI().toString());
                            } else {
                                java.io.File fileTemp = new java.io.File("C:\\Users\\ACER\\.gemini\\antigravity\\brain\\f6498dcd-259b-4eb2-bab8-72381e3468b0\\media__1779779547053.png");
                                if (fileTemp.exists()) {
                                    backupImage = new Image(fileTemp.toURI().toString());
                                }
                            }
                        }
                        if (backupImage != null) {
                            qrImageView.setImage(backupImage);
                        } else {
                            Label lblError = new Label("Lỗi mạng\nKhông thể tải mã QR!");
                            lblError.setStyle("-fx-text-fill: #ef4444; -fx-text-alignment: center; -fx-font-weight: bold;");
                            qrFrame.getChildren().add(lblError);
                        }
                    } catch (Exception ignored) {}
                    progress.setVisible(false);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (qrImage != null) {
            qrImageView.setImage(qrImage);
        } else {
            progress.setVisible(false);
            Label lblError = new Label("Lỗi tải mã QR\nVui lòng thử lại!");
            lblError.setStyle("-fx-text-fill: #ef4444; -fx-text-alignment: center; -fx-font-weight: bold;");
            qrFrame.getChildren().add(lblError);
        }

        HBox statusBox = new HBox(6);
        statusBox.setAlignment(Pos.CENTER);
        Label blinkingDot = new Label("●");
        blinkingDot.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px;");

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, e -> blinkingDot.setOpacity(1.0)),
                new KeyFrame(Duration.seconds(0.5), e -> blinkingDot.setOpacity(0.2)),
                new KeyFrame(Duration.seconds(1.0), e -> blinkingDot.setOpacity(1.0))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();

        Label lblStatus = new Label("Đang chờ quét mã thanh toán...");
        lblStatus.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(blinkingDot, lblStatus);

        Button btnConfirm = new Button("Xác nhận thành công");
        btnConfirm.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;");

        Button btnCancel = new Button("Hủy thanh toán");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;");

        HBox buttonsBox = new HBox(12);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(btnCancel, btnConfirm);

        btnConfirm.setOnAction(e -> {
            btnConfirm.setDisable(true);
            btnCancel.setDisable(true);
            executeDepositAndSuccess(dialogStage, maKH, tenKH, soTien, diemCong, khuyenMai, phuongThuc, pulse, blinkingDot, lblStatus);
        });

        btnCancel.setOnAction(e -> {
            pulse.stop();
            dialogStage.close();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Giao dịch nạp tiền đã bị hủy bởi nhân viên.");
            alert.show();
        });

        root.getChildren().addAll(header, detailsCard, qrFrame, statusBox, buttonsBox);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void executeDepositAndSuccess(Stage stage, String maKH, String tenKH, long soTien, int diemCong, String khuyenMai, String phuongThuc, Timeline pulse, Label blinkingDot, Label lblStatus) {
        pulse.stop();
        blinkingDot.setText("✔");
        blinkingDot.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 16px;");
        lblStatus.setText("Thanh toán thành công!");
        lblStatus.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px; -fx-font-weight: bold;");

        if (KhachHangRepository.isDatabaseEnabled()) {
            try {
                KhachHangRepository.deposit(maKH, soTien, diemCong);
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                showError("Không thể nạp tiền vào database: " + ex.getMessage());
                stage.close();
                return;
            }
        }

        String maGD = "GD" + String.format("%03d", data.size() + 1);
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm"));
        data.add(0, new NapTien(maGD, thoiGian, maKH, tenKH, String.valueOf(soTien), String.valueOf(diemCong),
                phuongThuc, khuyenMai, "Admin"));

        updateStats();

        Timeline closeTimeline = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            stage.close();
            txtSoTien.clear();
            cbKhachHang.getSelectionModel().clearSelection();
            cbKhuyenMai.setValue("Khong");
            cbPhuongThuc.setValue("Tien mat");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Nạp tiền thành công cho khách hàng: " + tenKH + "\nSố tiền: " + DisplayFormat.money(soTien) + " (+ " + diemCong + " điểm)");
            alert.show();
        }));
        closeTimeline.play();
    }

    private int calculatePoints(long soTien, String khuyenMai) {
        int basePoints = (int) (soTien / 1000);
        if (khuyenMai == null || "Khong".equalsIgnoreCase(khuyenMai)) {
            return basePoints;
        }

        try {
            // Tự động tìm số phần trăm chiết khấu (ví dụ: "20%") trong chuỗi khuyến mãi để nhân điểm cộng
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)%").matcher(khuyenMai);
            if (matcher.find()) {
                double pct = Double.parseDouble(matcher.group(1));
                return (int) Math.round(basePoints * (1.0 + pct / 100.0));
            }
        } catch (Exception ignored) {}

        return basePoints;
    }

    private ObservableList<String> getPromotionsFromDb() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Khong");

        if (!DatabaseConnection.isConfigured()) {
            loadSeedPromotions(list);
            return list;
        }

        // Truy vấn các chương trình khuyến mãi có Loại là GIAM_GIA (Giảm giá)
        String sql = "SELECT TENCTR, CHIETKHAU FROM CHUONG_TRINH_KHUYEN_MAI WHERE (UPPER(LOAICTR) = 'GIAM_GIA' OR LOAICTR = N'Giảm giá') AND NVL(IS_DELETE, 0) = 0";
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String ten = rs.getString("TENCTR");
                double chietKhau = rs.getDouble("CHIETKHAU");
                list.add(ten + " (Giảm " + (int) chietKhau + "%)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadSeedPromotions(list);
        }
        return list;
    }

    private void loadSeedPromotions(ObservableList<String> list) {
        for (KhuyenMai km : DatabaseSeedData.khuyenMai()) {
            if ("GIAM_GIA".equalsIgnoreCase(km.getLoaiCTR())) {
                list.add(km.getTenCTR() + " (Giảm " + km.getChietKhau() + ")");
            }
        }
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
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
