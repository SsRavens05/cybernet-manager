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

public class hangkhachhang {

    // ===================== MODEL =====================
    public static class HangKH {
        private final StringProperty maHang;
        private final StringProperty tenHang;
        private final StringProperty tyLeNap;

        public HangKH(String maHang, String tenHang, String tyLeNap) {
            this.maHang = new SimpleStringProperty(maHang);
            this.tenHang = new SimpleStringProperty(tenHang);
            this.tyLeNap = new SimpleStringProperty(tyLeNap);
        }

        public String getMaHang() { return maHang.get(); }
        public String getTenHang() { return tenHang.get(); }
        public String getTyLeNap() { return tyLeNap.get(); }

        public void setTenHang(String v) { tenHang.set(v); }
        public void setTyLeNap(String v) { tyLeNap.set(v); }

        public StringProperty maHangProperty() { return maHang; }
        public StringProperty tenHangProperty() { return tenHang; }
        public StringProperty tyLeNapProperty() { return tyLeNap; }
    }

    // ===================== DATA =====================
    // Dữ liệu sẽ được load từ CSDL thay vì code cứng
    public static final ObservableList<HangKH> danhSach = FXCollections.observableArrayList();
    
    private FilteredList<HangKH> filteredList;
    private TableView<HangKH> tblTierList;

    // Các nhãn thống kê
    private Label lblSoLoai;
    private Label lblTyLeTB;
    private Label lblTongTyLe;

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
        
        // Gọi dữ liệu từ Database khi khởi tạo view
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
        danhSach.clear();
        String sql = "SELECT MaHangKhachHang, HangKhachHang, TiLeNap FROM HangKhachHang ORDER BY MaHangKhachHang ASC";
        
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String ma = rs.getString("MaHangKhachHang");
                String ten = rs.getString("HangKhachHang");
                // Chuyển kiểu Float trong SQL thành String để đưa vào Model
                String tyle = String.valueOf(rs.getDouble("TiLeNap")); 
                
                danhSach.add(new HangKH(ma, ten, tyle));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + ex.getMessage());
        }
        refreshCards();
    }

    // Hàm sinh mã hạng tự động tăng (HKH01, HKH02...)
    private String generateNewMaHKH(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaHangKhachHang FROM HangKhachHang WHERE MaHangKhachHang LIKE 'HKH%'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaHangKhachHang");
                if (ma != null && ma.length() > 3) {
                    try {
                        // Cắt lấy phần số sau chữ "HKH"
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua các mã không đúng định dạng số ở đuôi
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Tăng 1 và format thành 2 chữ số (VD: HKH04, HKH05)
        return String.format("HKH%02d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoLoai = new Label("0");
        lblTyLeTB = new Label("0");
        lblTongTyLe = new Label("0");

        StackPane cardSoLoai = buildCard("Số loại KH", lblSoLoai, "#7986CB", "#3F51B5", "📈");
        StackPane cardTyLeTB = buildCard("Tỷ lệ TB", lblTyLeTB, "#BA68C8", "#8E24AA", "⚖");
        StackPane cardTongTyLe = buildCard("Tổng tỷ lệ", lblTongTyLe, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoLoai, Priority.ALWAYS);
        HBox.setHgrow(cardTyLeTB, Priority.ALWAYS);
        HBox.setHgrow(cardTongTyLe, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoLoai, cardTyLeTB, cardTongTyLe);
        return hbox;
    }

    private StackPane buildCard(String title, Label valueLbl, String colorStart, String colorEnd, String icon) {
        StackPane stack = new StackPane();
        stack.setMinHeight(130);
        stack.setStyle(
            "-fx-background-color: linear-gradient(to right, " + colorStart + ", " + colorEnd + ");" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );

        Circle bigCircle = new Circle(55);
        bigCircle.setFill(Color.web("#FFFFFF", 0.15));
        bigCircle.setTranslateX(60);

        Circle smallCircle = new Circle(35);
        smallCircle.setFill(Color.web("#FFFFFF", 0.10));
        smallCircle.setTranslateX(90);
        smallCircle.setTranslateY(-20);

        VBox textBox = new VBox(6);
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setPadding(new Insets(20, 20, 20, 24));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18px;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold;");
        titleRow.getChildren().addAll(iconLbl, titleLbl);

        valueLbl.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;");

        textBox.getChildren().addAll(titleRow, valueLbl);
        stack.getChildren().addAll(bigCircle, smallCircle, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);
        return stack;
    }

    // ===================== TABLE PANEL =====================
    private VBox buildTablePanel() {
        VBox panel = new VBox(0);
        panel.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        HBox toolbar = buildToolbar();
        toolbar.setPadding(new Insets(14, 16, 14, 16));
        toolbar.setStyle("-fx-border-color: transparent transparent " + BORDER_COLOR + " transparent; -fx-border-width: 0 0 1 0;");

        tblTierList = buildTable();
        VBox.setVgrow(tblTierList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblTierList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Hạng Khách Hàng");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle(
            "-fx-background-radius: 6; -fx-border-radius: 6;" +
            "-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1;" +
            "-fx-font-size: 13px; -fx-padding: 6 10;"
        );

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(hk -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return hk.getMaHang().toLowerCase().contains(filter) ||
                       hk.getTenHang().toLowerCase().contains(filter);
            });
        });

        Button btnInsert = createButton("+ Insert", "#2E7D32", WHITE);
        Button btnDelete = createButton("🗑 Delete", "#C62828", WHITE);
        Button btnUpdate = createButton("✏ Update", "#1565C0", WHITE);

        btnInsert.setOnAction(e -> showInsertDialog());
        btnDelete.setOnAction(e -> handleDelete());
        btnUpdate.setOnAction(e -> handleUpdate());

        hbox.getChildren().addAll(title, spacer, txtSearch, btnInsert, btnDelete, btnUpdate);
        return hbox;
    }

    @SuppressWarnings("unchecked")
    private TableView<HangKH> buildTable() {
        TableView<HangKH> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<HangKH, String> colMa = new TableColumn<>("Mã Hạng Khách Hàng");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maHang"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");

        TableColumn<HangKH, String> colTen = new TableColumn<>("Hạng Khách Hàng");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenHang"));

        TableColumn<HangKH, String> colTyLe = new TableColumn<>("Tỉ Lệ Nạp");
        colTyLe.setCellValueFactory(new PropertyValueFactory<>("tyLeNap"));

        table.getColumns().addAll(colMa, colTen, colTyLe);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<HangKH> row = new TableRow<>();
            row.setOnMouseEntered(e -> { if (!row.isSelected()) row.setStyle("-fx-background-color: #F3F4F6;"); });
            row.setOnMouseExited(e -> { if (!row.isSelected()) row.setStyle(""); });
            return row;
        });

        return table;
    }

    // ===================== INSERT DIALOG =====================
    private void showInsertDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Insert Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(350);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTenHang = new TextField(); styleField(txtTenHang);
        TextField txtTyLeNap = new TextField(); styleField(txtTyLeNap);

        grid.add(new Label("TenHang:"), 0, 0); grid.add(txtTenHang, 1, 0);
        grid.add(new Label("TyLeNap:"), 0, 1); grid.add(txtTyLeNap, 1, 1);

        GridPane.setHgrow(txtTenHang, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTenHang.getText().isEmpty() || txtTyLeNap.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
                    return null;
                }
                
                double tiLe = 0;
                try {
                    tiLe = Double.parseDouble(txtTyLeNap.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tỷ lệ nạp phải là số!");
                    return null;
                }
                
                // MỞ KẾT NỐI VÀ TỰ ĐỘNG TẠO MÃ
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maHangMoi = generateNewMaHKH(conn);
                    String sql = "INSERT INTO HangKhachHang (MaHangKhachHang, HangKhachHang, TiLeNap) VALUES (?, ?, ?)";
                    
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maHangMoi);
                        ps.setString(2, txtTenHang.getText().trim());
                        ps.setDouble(3, tiLe);
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase(); // Cập nhật bảng
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        HangKH selected = tblTierList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một hạng khách hàng để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            // DELETE TRONG DATABASE
            String sql = "DELETE FROM HangKhachHang WHERE MaHangKhachHang = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                 
                ps.setString(1, selected.getMaHang());
                ps.executeUpdate();
                loadDataFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Bản ghi này đang được sử dụng ở bảng khác, không thể xóa!\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        HangKH selected = tblTierList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một hạng khách hàng để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(350);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtMaHKH = new TextField(selected.getMaHang());
        txtMaHKH.setEditable(false);
        txtMaHKH.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTenHang = new TextField(selected.getTenHang()); styleField(txtTenHang);
        TextField txtTyLeNap = new TextField(selected.getTyLeNap()); styleField(txtTyLeNap);

        grid.add(new Label("MaHKH:"), 0, 0); grid.add(txtMaHKH, 1, 0);
        grid.add(new Label("TenHang:"), 0, 1); grid.add(txtTenHang, 1, 1);
        grid.add(new Label("TyLeNap:"), 0, 2); grid.add(txtTyLeNap, 1, 2);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                double tiLe = 0;
                try {
                    tiLe = Double.parseDouble(txtTyLeNap.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tỷ lệ nạp phải là số!");
                    return null;
                }
                
                // UPDATE XUỐNG DATABASE
                String sql = "UPDATE HangKhachHang SET HangKhachHang = ?, TiLeNap = ? WHERE MaHangKhachHang = ?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                     
                    ps.setString(1, txtTenHang.getText().trim());
                    ps.setDouble(2, tiLe);
                    ps.setString(3, selected.getMaHang());
                    
                    ps.executeUpdate();
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
        if (lblSoLoai != null && lblTyLeTB != null && lblTongTyLe != null) {
            updateStatValues();
        }
    }

    private void updateStatValues() {
        int soLoai = danhSach.size();
        lblSoLoai.setText(String.valueOf(soLoai));

        double tongTyLe = 0;
        for (HangKH hk : danhSach) {
            try {
                tongTyLe += Double.parseDouble(hk.getTyLeNap());
            } catch (NumberFormatException ignored) {}
        }
        
        lblTongTyLe.setText(String.format("%.1f", tongTyLe).replace(",", "."));
        
        double tyLeTB = soLoai > 0 ? tongTyLe / soLoai : 0;
        lblTyLeTB.setText(String.format("%.2f", tyLeTB).replace(",", "."));
    }

    private Button createButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 5 8;");
    }

    private void styleDialogButtons(DialogPane dp, ButtonType ok, ButtonType cancel) {
        Node okBtn = dp.lookupButton(ok);
        okBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
        Node cancelBtn = dp.lookupButton(cancel);
        cancelBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 18;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}