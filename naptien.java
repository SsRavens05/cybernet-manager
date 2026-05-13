package com.mycompany.test5;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.collections.transformation.FilteredList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class naptien {

    // ===================== MODEL =====================
    public static class GiaoDich {
        private final StringProperty maGD;
        private final StringProperty maKH;
        private final StringProperty tenKH;
        private final StringProperty soTien;
        private final StringProperty trangThai;
        private final StringProperty thoiGian;

        public GiaoDich(String maGD, String maKH, String tenKH, String soTien, String trangThai, String thoiGian) {
            this.maGD = new SimpleStringProperty(maGD);
            this.maKH = new SimpleStringProperty(maKH);
            this.tenKH = new SimpleStringProperty(tenKH);
            this.soTien = new SimpleStringProperty(soTien);
            this.trangThai = new SimpleStringProperty(trangThai);
            this.thoiGian = new SimpleStringProperty(thoiGian);
        }

        public String getMaGD() { return maGD.get(); }
        public String getMaKH() { return maKH.get(); }
        public String getTenKH() { return tenKH.get(); }
        public String getSoTien() { return soTien.get(); }
        public String getTrangThai() { return trangThai.get(); }
        public String getThoiGian() { return thoiGian.get(); }

        public StringProperty maGDProperty() { return maGD; }
        public StringProperty maKHProperty() { return maKH; }
        public StringProperty tenKHProperty() { return tenKH; }
        public StringProperty soTienProperty() { return soTien; }
        public StringProperty trangThaiProperty() { return trangThai; }
        public StringProperty thoiGianProperty() { return thoiGian; }
    }

    // ===================== DATA =====================
    private final ObservableList<GiaoDich> danhSachGD = FXCollections.observableArrayList();

    private FilteredList<GiaoDich> filteredList;
    private TableView<GiaoDich> tblDepositList;

    // Các nhãn thống kê
    private Label lblSoGD;
    private Label lblTongTien;
    private Label lblSoKH;

    // ===================== COLORS =====================
    private static final String WHITE = "#FFFFFF";
    private static final String LIGHT_GRAY = "#F5F5F5";
    private static final String BORDER_COLOR = "#E0E0E0";

    // ===================== MAIN VIEW =====================
    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");
        VBox content = buildContent();
        root.setCenter(content);
        
        loadDataFromDatabase();
        
        return root;
    }

    private VBox buildContent() {
        VBox vbox = new VBox(16);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        HBox cards = buildStatCards();
        VBox tablePanel = buildTablePanel();
        VBox.setVgrow(tablePanel, Priority.ALWAYS);

        vbox.getChildren().addAll(cards, tablePanel);
        return vbox;
    }

    // ===================== DATABASE OPERATIONS =====================
    private void loadDataFromDatabase() {
        danhSachGD.clear();
        String sql = "SELECT MaGD, MaKH, TenKhachHang, SoTien, TrangThai, ThoiGian FROM NapTien ORDER BY MaGD ASC";
        
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String maGD = rs.getString("MaGD");
                String maKH = rs.getString("MaKH");
                String ten = rs.getString("TenKhachHang");
                String tien = String.valueOf(rs.getDouble("SoTien"));
                String tt = rs.getString("TrangThai");
                String thoigian = rs.getString("ThoiGian");
                
                if (thoigian != null && thoigian.contains(".")) {
                    thoigian = thoigian.substring(0, thoigian.indexOf("."));
                }
                
                danhSachGD.add(new GiaoDich(maGD, maKH, ten, tien, tt, thoigian));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu nạp tiền: " + ex.getMessage());
        }
        refreshCards();
    }

    // Hàm sinh mã giao dịch tự động tăng
    private String generateNewMaGD(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaGD FROM NapTien WHERE MaGD LIKE 'GD%'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaGD");
                if (ma != null && ma.length() > 2) {
                    try {
                        // Cắt lấy phần số sau chữ "GD"
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua các mã sai định dạng
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Tăng thêm 1 và format về chuỗi 3 chữ số (VD: GD003, GD015)
        return String.format("GD%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoGD = new Label("0");
        lblTongTien = new Label("0");
        lblSoKH = new Label("0");

        StackPane cardSoGD = buildCard("Số GD", lblSoGD, "#7986CB", "#3F51B5", "🧾");
        StackPane cardTongTien = buildCard("Tổng tiền", lblTongTien, "#BA68C8", "#8E24AA", "💰");
        StackPane cardSoKH = buildCard("Số KH", lblSoKH, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoGD, Priority.ALWAYS);
        HBox.setHgrow(cardTongTien, Priority.ALWAYS);
        HBox.setHgrow(cardSoKH, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoGD, cardTongTien, cardSoKH);
        return hbox;
    }

    private StackPane buildCard(String title, Label valueLbl, String colorStart, String colorEnd, String icon) {
        StackPane stack = new StackPane();
        stack.setMinHeight(130);
        stack.setStyle("-fx-background-color: linear-gradient(to right, " + colorStart + ", " + colorEnd + "); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

        Circle bigCircle = new Circle(55); bigCircle.setFill(Color.web("#FFFFFF", 0.15)); bigCircle.setTranslateX(60);
        Circle smallCircle = new Circle(35); smallCircle.setFill(Color.web("#FFFFFF", 0.10)); smallCircle.setTranslateX(90); smallCircle.setTranslateY(-20);

        VBox textBox = new VBox(6); textBox.setAlignment(Pos.CENTER_LEFT); textBox.setPadding(new Insets(20, 20, 20, 24));
        HBox titleRow = new HBox(8); titleRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon); iconLbl.setStyle("-fx-font-size: 18px;");
        Label titleLbl = new Label(title); titleLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold;");
        titleRow.getChildren().addAll(iconLbl, titleLbl);

        valueLbl.setStyle("-fx-font-size: 30px; -fx-text-fill: white; -fx-font-weight: bold;");

        textBox.getChildren().addAll(titleRow, valueLbl);
        stack.getChildren().addAll(bigCircle, smallCircle, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);
        return stack;
    }

    // ===================== TABLE PANEL =====================
    private VBox buildTablePanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        HBox toolbar = buildToolbar();
        toolbar.setPadding(new Insets(14, 16, 14, 16));
        toolbar.setStyle("-fx-border-color: transparent transparent " + BORDER_COLOR + " transparent; -fx-border-width: 0 0 1 0;");

        tblDepositList = buildTable();
        VBox.setVgrow(tblDepositList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblDepositList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Nạp Tiền");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm giao dịch...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSachGD, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(gd -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return gd.getMaGD().toLowerCase().contains(filter) ||
                       gd.getMaKH().toLowerCase().contains(filter) ||
                       gd.getTenKH().toLowerCase().contains(filter);
            });
        });

        Button btnDeposit = new Button("💰 Nạp Tiền Mới");
        btnDeposit.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        btnDeposit.setOnAction(e -> showDepositDialog());

        hbox.getChildren().addAll(title, spacer, txtSearch, btnDeposit);
        return hbox;
    }

    @SuppressWarnings("unchecked")
    private TableView<GiaoDich> buildTable() {
        TableView<GiaoDich> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<GiaoDich, String> colMaGD = new TableColumn<>("Mã GD");
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGD"));

        TableColumn<GiaoDich, String> colMaKH = new TableColumn<>("Mã KH");
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKH"));

        TableColumn<GiaoDich, String> colTen = new TableColumn<>("Tên Khách Hàng");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKH"));

        TableColumn<GiaoDich, String> colTien = new TableColumn<>("Số Tiền");
        colTien.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        colTien.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        TableColumn<GiaoDich, String> colTT = new TableColumn<>("Trạng Thái");
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setPadding(new Insets(3, 10, 3, 10));
                String bg = item.equalsIgnoreCase("SUCCESS") ? "#2E7D32" : "#C62828";
                badge.setStyle("-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + bg + ";");
                setGraphic(badge);
                setText(null);
            }
        });

        TableColumn<GiaoDich, String> colTG = new TableColumn<>("Thời Gian");
        colTG.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));

        table.getColumns().addAll(colMaGD, colMaKH, colTen, colTien, colTT, colTG);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<GiaoDich> row = new TableRow<>();
            row.setOnMouseEntered(e -> { if (!row.isSelected()) row.setStyle("-fx-background-color: #F3F4F6;"); });
            row.setOnMouseExited(e -> { if (!row.isSelected()) row.setStyle(""); });
            return row;
        });

        return table;
    }

    // ===================== DEPOSIT DIALOG =====================
    private void showDepositDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nạp Tiền Cho Khách Hàng");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(380);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtMaKH = new TextField(); 
        txtMaKH.setPromptText("Nhập Mã KH (vd: KH001)");
        txtMaKH.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-padding: 5 8;");
        
        TextField txtSoTien = new TextField(); 
        txtSoTien.setPromptText("Nhập số tiền");
        txtSoTien.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-padding: 5 8;");

        grid.add(new Label("Mã Khách Hàng:"), 0, 0); grid.add(txtMaKH, 1, 0);
        grid.add(new Label("Số Tiền Nạp:"), 0, 1); grid.add(txtSoTien, 1, 1);

        GridPane.setHgrow(txtMaKH, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Xác Nhận Nạp", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        
        Node okBtn = dp.lookupButton(btnSave);
        okBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
        Node cancelBtn = dp.lookupButton(btnCancel);
        cancelBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 18;");

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String inputMaKH = txtMaKH.getText().trim();
                String inputTien = txtSoTien.getText().trim();

                if (inputMaKH.isEmpty() || inputTien.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đủ thông tin!");
                    return null;
                }

                double tienNap = 0;
                try {
                    tienNap = Double.parseDouble(inputTien);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ!");
                    return null;
                }

                String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Kết nối CSDL để kiểm tra và cập nhật
                try (Connection conn = ketnoicsdl.getConnection()) {
                    // Dùng hàm sinh mã tự động vừa tạo
                    String maGD = generateNewMaGD(conn);

                    String checkSql = "SELECT HoTen, SoDuTK FROM KhachHang WHERE MaKH = ?";
                    try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                        psCheck.setString(1, inputMaKH);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            
                            if (rsCheck.next()) {
                                String tenKH = rsCheck.getString("HoTen");
                                double soDuHienTai = rsCheck.getDouble("SoDuTK");

                                String insertSql = "INSERT INTO NapTien (MaGD, MaKH, TenKhachHang, SoTien, TrangThai, ThoiGian) VALUES (?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                                    psInsert.setString(1, maGD);
                                    psInsert.setString(2, inputMaKH);
                                    psInsert.setString(3, tenKH);
                                    psInsert.setDouble(4, tienNap);
                                    psInsert.setString(5, "SUCCESS");
                                    psInsert.setString(6, thoiGian);
                                    psInsert.executeUpdate();
                                }

                                String updateSql = "UPDATE KhachHang SET SoDuTK = ? WHERE MaKH = ?";
                                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                                    psUpdate.setDouble(1, soDuHienTai + tienNap);
                                    psUpdate.setString(2, inputMaKH);
                                    psUpdate.executeUpdate();
                                }

                                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã nạp " + String.format("%,.0f", tienNap) + " cho " + tenKH);
                            } else {
                                String insertSql = "INSERT INTO NapTien (MaGD, MaKH, TenKhachHang, SoTien, TrangThai, ThoiGian) VALUES (?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                                    psInsert.setString(1, maGD);
                                    psInsert.setString(2, inputMaKH);
                                    psInsert.setString(3, "Unknown");
                                    psInsert.setDouble(4, tienNap);
                                    psInsert.setString(5, "FAILED");
                                    psInsert.setString(6, thoiGian);
                                    psInsert.executeUpdate();
                                }
                                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không tìm thấy khách hàng có mã: " + inputMaKH);
                            }
                        }
                    }
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblSoGD != null) {
            updateStatValues();
        }
    }

    private void updateStatValues() {
        lblSoGD.setText(String.valueOf(danhSachGD.size()));

        double tongTien = danhSachGD.stream()
            .filter(gd -> gd.getTrangThai().equalsIgnoreCase("SUCCESS"))
            .mapToDouble(gd -> {
                try { return Double.parseDouble(gd.getSoTien()); } 
                catch (Exception e) { return 0; }
            }).sum();
        lblTongTien.setText(String.format("%,.0f", tongTien));

        long soKH = danhSachGD.stream()
            .filter(gd -> gd.getTrangThai().equalsIgnoreCase("SUCCESS"))
            .map(GiaoDich::getMaKH)
            .distinct()
            .count();
        lblSoKH.setText(String.valueOf(soKH));
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}