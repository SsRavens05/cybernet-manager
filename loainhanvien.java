/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

public class loainhanvien {

    // ===================== MODEL =====================
    public static class LoaiNV {
        private final StringProperty maLoai;
        private final StringProperty tenLoai;
        private final StringProperty mucLuong;

        public LoaiNV(String maLoai, String tenLoai, String mucLuong) {
            this.maLoai = new SimpleStringProperty(maLoai != null ? maLoai : "");
            this.tenLoai = new SimpleStringProperty(tenLoai != null ? tenLoai : "");
            this.mucLuong = new SimpleStringProperty(mucLuong != null ? mucLuong : "0");
        }

        public String getMaLoai() { return maLoai.get(); }
        public String getTenLoai() { return tenLoai.get(); }
        public String getMucLuong() { return mucLuong.get(); }

        public void setTenLoai(String v) { tenLoai.set(v); }
        public void setMucLuong(String v) { mucLuong.set(v); }

        public StringProperty maLoaiProperty() { return maLoai; }
        public StringProperty tenLoaiProperty() { return tenLoai; }
        public StringProperty mucLuongProperty() { return mucLuong; }
    }

    // ===================== DATA =====================
    public static final ObservableList<LoaiNV> danhSach = FXCollections.observableArrayList();
    private FilteredList<LoaiNV> filteredList;
    private TableView<LoaiNV> tblEmpTypeList;

    // Các nhãn thống kê
    private Label lblSoLoai;
    private Label lblLuongTB;
    private Label lblTongLuong;

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
        
        loadDataFromDatabase(); // Load từ CSDL
        
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
        String sql = "SELECT MaLoaiNhanVien, TenLoaiNhanVien, MucLuong FROM LoaiNhanVien ORDER BY MaLoaiNhanVien ASC";
        
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String ma = rs.getString("MaLoaiNhanVien");
                String ten = rs.getString("TenLoaiNhanVien");
                // Loại bỏ phần thập phân .00 nếu không cần thiết
                double luong = rs.getDouble("MucLuong");
                String mucLuongStr = String.format("%.0f", luong);
                
                danhSach.add(new LoaiNV(ma, ten, mucLuongStr));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + ex.getMessage());
        }
        refreshCards();
    }

    // Sinh mã tự động (LNV01, LNV02...)
    private String generateNewMaLNV(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaLoaiNhanVien FROM LoaiNhanVien WHERE MaLoaiNhanVien LIKE 'LNV%'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaLoaiNhanVien");
                if (ma != null && ma.length() > 3) {
                    try {
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return String.format("LNV%02d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoLoai = new Label("0");
        lblLuongTB = new Label("0");
        lblTongLuong = new Label("0");

        StackPane cardSoLoai = buildCard("Số loại", lblSoLoai, "#7986CB", "#3F51B5", "🧾");
        StackPane cardLuongTB = buildCard("Lương TB", lblLuongTB, "#BA68C8", "#8E24AA", "💰");
        StackPane cardTongLuong = buildCard("Tổng lương", lblTongLuong, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoLoai, Priority.ALWAYS);
        HBox.setHgrow(cardLuongTB, Priority.ALWAYS);
        HBox.setHgrow(cardTongLuong, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoLoai, cardLuongTB, cardTongLuong);
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

        valueLbl.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;"); // Chữ nhỏ lại chút để vừa số lớn
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

        tblEmpTypeList = buildTable();
        VBox.setVgrow(tblEmpTypeList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblEmpTypeList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Loại Nhân Viên");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(lnv -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return lnv.getMaLoai().toLowerCase().contains(filter) ||
                       lnv.getTenLoai().toLowerCase().contains(filter);
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
    private TableView<LoaiNV> buildTable() {
        TableView<LoaiNV> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<LoaiNV, String> colMa = new TableColumn<>("Mã Loại Nhân Viên"); 
        colMa.setCellValueFactory(new PropertyValueFactory<>("maLoai"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");

        TableColumn<LoaiNV, String> colTen = new TableColumn<>("Tên Loại Nhân Viên"); 
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenLoai"));

        TableColumn<LoaiNV, String> colLuong = new TableColumn<>("Mức Lương"); 
        colLuong.setCellValueFactory(new PropertyValueFactory<>("mucLuong"));

        table.getColumns().addAll(colMa, colTen, colLuong);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<LoaiNV> row = new TableRow<>();
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
        dp.setPrefWidth(400);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTenLoai = new TextField(); styleField(txtTenLoai);
        TextField txtMucLuong = new TextField("0"); styleField(txtMucLuong);

        grid.add(new Label("TenLoaiNV:"), 0, 0); grid.add(txtTenLoai, 1, 0);
        grid.add(new Label("Mucluong:"), 0, 1); grid.add(txtMucLuong, 1, 1);

        GridPane.setHgrow(txtTenLoai, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTenLoai.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên loại nhân viên!");
                    return null;
                }
                
                double luong = 0;
                try {
                    luong = Double.parseDouble(txtMucLuong.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mức lương phải là một số hợp lệ!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaLNV(conn);
                    String sql = "INSERT INTO LoaiNhanVien (MaLoaiNhanVien, TenLoaiNhanVien, MucLuong) VALUES (?, ?, ?)";
                    
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTenLoai.getText().trim());
                        ps.setDouble(3, luong);
                        ps.executeUpdate();
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

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        LoaiNV selected = tblEmpTypeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một loại nhân viên để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM LoaiNhanVien WHERE MaLoaiNhanVien = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                 
                ps.setString(1, selected.getMaLoai());
                ps.executeUpdate();
                loadDataFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể xóa loại nhân viên này (Có thể đang bị ràng buộc khóa ngoại với bảng NhanVien).\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        LoaiNV selected = tblEmpTypeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một loại nhân viên để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(400);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtMa = new TextField(selected.getMaLoai());
        txtMa.setEditable(false);
        txtMa.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTen = new TextField(selected.getTenLoai()); styleField(txtTen);
        TextField txtLuong = new TextField(selected.getMucLuong()); styleField(txtLuong);

        grid.add(new Label("MaLoaiNV:"), 0, 0); grid.add(txtMa, 1, 0);
        grid.add(new Label("TenLoaiNV:"), 0, 1); grid.add(txtTen, 1, 1);
        grid.add(new Label("Mucluong:"), 0, 2); grid.add(txtLuong, 1, 2);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                double luong = 0;
                try {
                    luong = Double.parseDouble(txtLuong.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mức lương phải là một số hợp lệ!");
                    return null;
                }

                String sql = "UPDATE LoaiNhanVien SET TenLoaiNhanVien = ?, MucLuong = ? WHERE MaLoaiNhanVien = ?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, txtTen.getText().trim());
                    ps.setDouble(2, luong);
                    ps.setString(3, selected.getMaLoai());
                    
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
        if (lblSoLoai != null && lblLuongTB != null && lblTongLuong != null) {
            // Tính toán Số Loại
            int soLoai = danhSach.size();
            lblSoLoai.setText(String.valueOf(soLoai));

            // Tính Tổng lương và Lương TB
            double tongLuong = 0;
            for (LoaiNV nv : danhSach) {
                try {
                    tongLuong += Double.parseDouble(nv.getMucLuong());
                } catch (NumberFormatException ignored) {}
            }
            
            double luongTB = soLoai > 0 ? tongLuong / soLoai : 0;

            // Format giống ảnh (hiển thị số thực, vd: 8666666.67 và 26000000)
            lblLuongTB.setText(String.format("%.2f", luongTB).replace(",", "."));
            lblTongLuong.setText(String.format("%.0f", tongLuong));
        }
    }

    private Button createButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85)); btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void styleField(TextField tf) { tf.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 5 8;"); }

    private void styleDialogButtons(DialogPane dp, ButtonType ok, ButtonType cancel) {
        Node okBtn = dp.lookupButton(ok); okBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
        Node cancelBtn = dp.lookupButton(cancel); cancelBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 18;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg); alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}