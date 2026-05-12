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
import java.util.Optional;

public class loaikhuvuc {

    // ===================== MODEL =====================
    public static class LoaiKhuVucModel {
        private final StringProperty maLKV;
        private final StringProperty tenLKV;
        private final StringProperty giaTien;
        private final StringProperty soMayToiDa;

        public LoaiKhuVucModel(String maLKV, String tenLKV, String giaTien, String soMayToiDa) {
            this.maLKV = new SimpleStringProperty(maLKV != null ? maLKV : "");
            this.tenLKV = new SimpleStringProperty(tenLKV != null ? tenLKV : "");
            this.giaTien = new SimpleStringProperty(giaTien != null ? giaTien : "0");
            this.soMayToiDa = new SimpleStringProperty(soMayToiDa != null ? soMayToiDa : "0");
        }

        public String getMaLKV() { return maLKV.get(); }
        public String getTenLKV() { return tenLKV.get(); }
        public String getGiaTien() { return giaTien.get(); }
        public String getSoMayToiDa() { return soMayToiDa.get(); }

        public StringProperty maLKVProperty() { return maLKV; }
        public StringProperty tenLKVProperty() { return tenLKV; }
        public StringProperty giaTienProperty() { return giaTien; }
        public StringProperty soMayToiDaProperty() { return soMayToiDa; }
    }

    // ===================== DATA =====================
    private final ObservableList<LoaiKhuVucModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<LoaiKhuVucModel> filteredList;
    private TableView<LoaiKhuVucModel> tblTypeAreaList;

    // Các nhãn thống kê
    private Label lblSoLoaiKV;
    private Label lblGiaTB;
    private Label lblTongMay;

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
        danhSach.clear();
        String sql = "SELECT MaLKV, TenLKV, GiaTien, SoMayToiDa FROM LoaiKhuVuc ORDER BY MaLKV ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("MaLKV");
                String ten = rs.getString("TenLKV");
                String gia = String.valueOf(rs.getDouble("GiaTien"));
                String somay = String.valueOf(rs.getInt("SoMayToiDa"));
                
                if (gia.endsWith(".0")) gia = gia.replace(".0", "");

                danhSach.add(new LoaiKhuVucModel(ma, ten, gia, somay));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }
    
    // Sinh mã tự động (LKV01, LKV02...)
    private String generateNewMaLKV(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaLKV FROM LoaiKhuVuc WHERE MaLKV LIKE 'LKV%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaLKV");
                if (ma != null && ma.length() > 3) {
                    try {
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("LKV%02d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoLoaiKV = new Label("0");
        lblGiaTB = new Label("0");
        lblTongMay = new Label("0");

        StackPane cardSoLoai = buildCard("Số loại KV", lblSoLoaiKV, "#7986CB", "#3F51B5", "🧾");
        StackPane cardGiaTB = buildCard("Giá TB", lblGiaTB, "#BA68C8", "#8E24AA", "💰");
        StackPane cardTongMay = buildCard("Tổng máy", lblTongMay, "#FFD54F", "#FF8F00", "🖥");

        HBox.setHgrow(cardSoLoai, Priority.ALWAYS);
        HBox.setHgrow(cardGiaTB, Priority.ALWAYS);
        HBox.setHgrow(cardTongMay, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoLoai, cardGiaTB, cardTongMay);
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

        valueLbl.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;");
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

        tblTypeAreaList = buildTable();
        VBox.setVgrow(tblTypeAreaList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblTypeAreaList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Loại Khu Vực");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(lkv -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return lkv.getMaLKV().toLowerCase().contains(filter) ||
                       lkv.getTenLKV().toLowerCase().contains(filter);
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
    private TableView<LoaiKhuVucModel> buildTable() {
        TableView<LoaiKhuVucModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<LoaiKhuVucModel, String> colMa = new TableColumn<>("Mã LKV"); 
        colMa.setCellValueFactory(new PropertyValueFactory<>("maLKV"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");

        TableColumn<LoaiKhuVucModel, String> colTen = new TableColumn<>("Tên LKV"); 
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenLKV"));

        TableColumn<LoaiKhuVucModel, String> colGia = new TableColumn<>("Giá Tiền"); 
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaTien"));

        TableColumn<LoaiKhuVucModel, String> colSoMay = new TableColumn<>("Số Máy Tối Đa"); 
        colSoMay.setCellValueFactory(new PropertyValueFactory<>("soMayToiDa"));

        table.getColumns().addAll(colMa, colTen, colGia, colSoMay);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<LoaiKhuVucModel> row = new TableRow<>();
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
        dp.setPrefWidth(380);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTenLKV = new TextField(); styleField(txtTenLKV);
        TextField txtGiaTien = new TextField("0"); styleField(txtGiaTien);
        
        // Theo đúng thiết kế (Bảng 4.24), Số lượng máy là ComboBox (Editable)
        ComboBox<String> cboSoLuongMay = new ComboBox<>();
        cboSoLuongMay.getItems().addAll("1", "2", "5", "10", "20", "30", "50");
        cboSoLuongMay.setEditable(true); // Cho phép người dùng tự gõ số nếu không có trong list
        cboSoLuongMay.setMaxWidth(Double.MAX_VALUE);
        cboSoLuongMay.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px;");

        grid.add(new Label("TenLoai:"), 0, 0); grid.add(txtTenLKV, 1, 0);
        grid.add(new Label("GiaTien:"), 0, 1); grid.add(txtGiaTien, 1, 1);
        grid.add(new Label("SoLuongMay:"), 0, 2); grid.add(cboSoLuongMay, 1, 2);

        GridPane.setHgrow(txtTenLKV, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTenLKV.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên loại khu vực!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaLKV(conn);
                    String slMayStr = cboSoLuongMay.getValue() != null ? cboSoLuongMay.getValue() : "0";
                    
                    String sql = "INSERT INTO LoaiKhuVuc (MaLKV, TenLKV, GiaTien, SoMayToiDa) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTenLKV.getText().trim());
                        ps.setDouble(3, Double.parseDouble(txtGiaTien.getText().trim()));
                        ps.setInt(4, Integer.parseInt(slMayStr.trim()));
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Database/Nhập liệu", "Dữ liệu không hợp lệ: " + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        LoaiKhuVucModel selected = tblTypeAreaList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một loại khu vực để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM LoaiKhuVuc WHERE MaLKV = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaLKV());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Bản ghi này đang được sử dụng, không thể xóa!\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        LoaiKhuVucModel selected = tblTypeAreaList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một loại khu vực để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(380);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtMaLKV = new TextField(selected.getMaLKV());
        txtMaLKV.setEditable(false);
        txtMaLKV.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTenLKV = new TextField(selected.getTenLKV()); styleField(txtTenLKV);
        TextField txtGiaTien = new TextField(selected.getGiaTien()); styleField(txtGiaTien);
        
        // Theo thiết kế (Bảng 4.26), màn hình cập nhật sử dụng TextField cho Số Lượng Máy
        TextField txtSoLuongMay = new TextField(selected.getSoMayToiDa()); styleField(txtSoLuongMay);

        grid.add(new Label("MaLKV:"), 0, 0); grid.add(txtMaLKV, 1, 0);
        grid.add(new Label("TenLoai:"), 0, 1); grid.add(txtTenLKV, 1, 1);
        grid.add(new Label("GiaTien:"), 0, 2); grid.add(txtGiaTien, 1, 2);
        grid.add(new Label("SoLuongMay:"), 0, 3); grid.add(txtSoLuongMay, 1, 3);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String sql = "UPDATE LoaiKhuVuc SET TenLKV=?, GiaTien=?, SoMayToiDa=? WHERE MaLKV=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtTenLKV.getText().trim());
                        ps.setDouble(2, Double.parseDouble(txtGiaTien.getText().trim()));
                        ps.setInt(3, Integer.parseInt(txtSoLuongMay.getText().trim()));
                        ps.setString(4, selected.getMaLKV());
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Database/Nhập liệu", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblSoLoaiKV == null) return;
        lblSoLoaiKV.setText(String.valueOf(danhSach.size()));

        double tongGia = danhSach.stream().mapToDouble(l -> {
            try { return Double.parseDouble(l.getGiaTien()); } catch (Exception e) { return 0; }
        }).sum();
        double giaTB = danhSach.isEmpty() ? 0 : tongGia / danhSach.size();
        lblGiaTB.setText(String.format("%.0f", giaTB));

        int tongMay = danhSach.stream().mapToInt(l -> {
            try { return Integer.parseInt(l.getSoMayToiDa()); } catch (Exception e) { return 0; }
        }).sum();
        lblTongMay.setText(String.valueOf(tongMay));
    }

    private Button createButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        return btn;
    }

    private void styleField(TextField tf) { tf.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 5 8;"); }

    private void styleDialogButtons(DialogPane dp, ButtonType ok, ButtonType cancel) {
        dp.lookupButton(ok).setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
        dp.lookupButton(cancel).setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 18;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg); alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}