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
import java.time.LocalDate;
import java.util.Optional;

public class calam {

    // ===================== MODEL =====================
    public static class ShiftModel {
        private final StringProperty maNV;
        private final StringProperty tenNV;
        private final StringProperty maCa;
        private final StringProperty ngayCC;
        private final StringProperty thoiGianBD;
        private final StringProperty thoiGianKT;
        private final StringProperty gioLamViec;
        private final StringProperty gioLamThem;
        private final StringProperty trangThai;

        public ShiftModel(String maNV, String tenNV, String maCa, String ngayCC, String thoiGianBD, 
                          String thoiGianKT, String gioLamViec, String gioLamThem, String trangThai) {
            this.maNV = new SimpleStringProperty(maNV != null ? maNV : "");
            this.tenNV = new SimpleStringProperty(tenNV != null ? tenNV : "Unknown");
            this.maCa = new SimpleStringProperty(maCa != null ? maCa : "");
            this.ngayCC = new SimpleStringProperty(ngayCC != null ? ngayCC : "");
            this.thoiGianBD = new SimpleStringProperty(thoiGianBD != null ? thoiGianBD : "");
            this.thoiGianKT = new SimpleStringProperty(thoiGianKT != null ? thoiGianKT : "");
            this.gioLamViec = new SimpleStringProperty(gioLamViec != null ? gioLamViec : "0");
            this.gioLamThem = new SimpleStringProperty(gioLamThem != null ? gioLamThem : "0");
            this.trangThai = new SimpleStringProperty(trangThai != null ? trangThai : "");
        }

        public String getMaNV() { return maNV.get(); }
        public String getTenNV() { return tenNV.get(); }
        public String getMaCa() { return maCa.get(); }
        public String getNgayCC() { return ngayCC.get(); }
        public String getThoiGianBD() { return thoiGianBD.get(); }
        public String getThoiGianKT() { return thoiGianKT.get(); }
        public String getGioLamViec() { return gioLamViec.get(); }
        public String getGioLamThem() { return gioLamThem.get(); }
        public String getTrangThai() { return trangThai.get(); }

        public StringProperty maNVProperty() { return maNV; }
        public StringProperty tenNVProperty() { return tenNV; }
        public StringProperty maCaProperty() { return maCa; }
        public StringProperty ngayCCProperty() { return ngayCC; }
        public StringProperty thoiGianBDProperty() { return thoiGianBD; }
        public StringProperty thoiGianKTProperty() { return thoiGianKT; }
        public StringProperty gioLamViecProperty() { return gioLamViec; }
        public StringProperty gioLamThemProperty() { return gioLamThem; }
        public StringProperty trangThaiProperty() { return trangThai; }
    }

    // ===================== DATA =====================
    private final ObservableList<ShiftModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<ShiftModel> filteredList;
    private TableView<ShiftModel> tblShift;

    // Các nhãn thống kê
    private Label lblSoCa;
    private Label lblTongGio;
    private Label lblTangCa;

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
        String sql = "SELECT MaCa, MaNV, TenNV, NgayCC, ThoiGianBD, ThoiGianKT, GioLamViec, GioLamThem, TrangThai FROM CaLam ORDER BY MaCa ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maca = rs.getString("MaCa");
                String manv = rs.getString("MaNV");
                String tennv = rs.getString("TenNV");
                String ngaycc = rs.getString("NgayCC");
                String tgbd = rs.getString("ThoiGianBD");
                String tgkt = rs.getString("ThoiGianKT");
                String gio = String.valueOf(rs.getDouble("GioLamViec"));
                String tangca = String.valueOf(rs.getDouble("GioLamThem"));
                String tt = rs.getString("TrangThai");

                if (tgbd != null && tgbd.contains(".")) tgbd = tgbd.substring(0, tgbd.indexOf("."));
                if (tgkt != null && tgkt.contains(".")) tgkt = tgkt.substring(0, tgkt.indexOf("."));
                
                // Chuẩn hóa format số
                if (gio.endsWith(".0")) gio = gio.replace(".0", "");
                if (tangca.endsWith(".0")) tangca = tangca.replace(".0", "");

                danhSach.add(new ShiftModel(manv, tennv, maca, ngaycc, tgbd, tgkt, gio, tangca, tt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }
    
    // Hàm truy vấn Tên Nhân Viên
    private String getTenNhanVien(Connection conn, String maNV) {
        String sql = "SELECT TenNV FROM NhanVien WHERE MaNV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("TenNV");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Unknown";
    }

    // Hàm sinh mã Ca Làm tự động (CL001, CL002...)
    private String generateNewMaCa(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaCa FROM CaLam WHERE MaCa LIKE 'CL%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaCa");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("CL%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoCa = new Label("0");
        lblTongGio = new Label("0");
        lblTangCa = new Label("0");

        StackPane cardSoCa = buildCard("Số ca", lblSoCa, "#7986CB", "#3F51B5", "🧾");
        StackPane cardTongGio = buildCard("Tổng giờ", lblTongGio, "#BA68C8", "#8E24AA", "💰");
        StackPane cardTangCa = buildCard("Tăng ca", lblTangCa, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoCa, Priority.ALWAYS);
        HBox.setHgrow(cardTongGio, Priority.ALWAYS);
        HBox.setHgrow(cardTangCa, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoCa, cardTongGio, cardTangCa);
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

        tblShift = buildTable();
        VBox.setVgrow(tblShift, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblShift);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Ca Làm");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(cl -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return cl.getMaNV().toLowerCase().contains(filter) ||
                       cl.getTenNV().toLowerCase().contains(filter) ||
                       cl.getMaCa().toLowerCase().contains(filter);
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
    private TableView<ShiftModel> buildTable() {
        TableView<ShiftModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<ShiftModel, String> colMaNV = new TableColumn<>("Mã NV"); colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        TableColumn<ShiftModel, String> colTen = new TableColumn<>("Tên NV"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        TableColumn<ShiftModel, String> colMaCa = new TableColumn<>("Mã Ca"); colMaCa.setCellValueFactory(new PropertyValueFactory<>("maCa"));
        colMaCa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;"); // Làm nổi bật mã ca
        TableColumn<ShiftModel, String> colNgayCC = new TableColumn<>("Ngày CC"); colNgayCC.setCellValueFactory(new PropertyValueFactory<>("ngayCC"));
        TableColumn<ShiftModel, String> colTGBD = new TableColumn<>("Thời Gian BĐ"); colTGBD.setCellValueFactory(new PropertyValueFactory<>("thoiGianBD"));
        TableColumn<ShiftModel, String> colTGKT = new TableColumn<>("Thời Gian KT"); colTGKT.setCellValueFactory(new PropertyValueFactory<>("thoiGianKT"));
        TableColumn<ShiftModel, String> colGio = new TableColumn<>("Giờ Làm Việc"); colGio.setCellValueFactory(new PropertyValueFactory<>("gioLamViec"));
        TableColumn<ShiftModel, String> colTangCa = new TableColumn<>("Giờ Làm Thêm"); colTangCa.setCellValueFactory(new PropertyValueFactory<>("gioLamThem"));

        TableColumn<ShiftModel, String> colTT = new TableColumn<>("Trạng Thái");
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setPadding(new Insets(3, 10, 3, 10));
                String bg = item.equalsIgnoreCase("RUNNING") ? "#E65100" : "#2E7D32";
                badge.setStyle("-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + bg + ";");
                setGraphic(badge);
                setText(null);
            }
        });

        table.getColumns().addAll(colMaNV, colTen, colMaCa, colNgayCC, colTGBD, colTGKT, colGio, colTangCa, colTT);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<ShiftModel> row = new TableRow<>();
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
        dp.setPrefWidth(420);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtManv = new TextField(); styleField(txtManv);
        TextField txtTgbd = new TextField(); styleField(txtTgbd); txtTgbd.setPromptText("VD: 2026-05-09 07:00:00");
        TextField txtTgkt = new TextField(); styleField(txtTgkt); txtTgkt.setPromptText("VD: 2026-05-10 07:00:00");
        DatePicker dtpNgaycc = new DatePicker(LocalDate.now()); dtpNgaycc.setMaxWidth(Double.MAX_VALUE);
        TextField txtWorkHours = new TextField("0"); styleField(txtWorkHours);
        TextField txtOvertimeHours = new TextField("0"); styleField(txtOvertimeHours);
        
        ComboBox<String> cboTrangThai = new ComboBox<>();
        cboTrangThai.getItems().addAll("RUNNING", "Finish");
        cboTrangThai.setValue("RUNNING");
        cboTrangThai.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Manv:"), 0, 0); grid.add(txtManv, 1, 0);
        grid.add(new Label("Tgbd:"), 0, 1); grid.add(txtTgbd, 1, 1);
        grid.add(new Label("Tgkt:"), 0, 2); grid.add(txtTgkt, 1, 2);
        grid.add(new Label("Ngaycc:"), 0, 3); grid.add(dtpNgaycc, 1, 3);
        grid.add(new Label("WorkHours:"), 0, 4); grid.add(txtWorkHours, 1, 4);
        grid.add(new Label("OvertimeHours:"), 0, 5); grid.add(txtOvertimeHours, 1, 5);
        grid.add(new Label("Trangthai:"), 0, 6); grid.add(cboTrangThai, 1, 6);

        GridPane.setHgrow(txtManv, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtManv.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Mã NV!");
                    return null;
                }
                
                String tgbd = txtTgbd.getText().trim();
                String tgkt = txtTgkt.getText().trim();
                
                if (tgbd.isEmpty() || tgkt.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Thời gian BĐ và Thời gian KT!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String tenNhanVien = getTenNhanVien(conn, txtManv.getText().trim());
                    if (tenNhanVien.equals("Unknown")) {
                        showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không tìm thấy Mã Nhân Viên này trong CSDL!");
                        return null;
                    }
                    
                    // Lấy mã ca tự động tăng
                    String maCaMoi = generateNewMaCa(conn);
                    String ngaycc = dtpNgaycc.getValue() != null ? dtpNgaycc.getValue().toString() : "";
                    
                    String sql = "INSERT INTO CaLam (MaCa, MaNV, TenNV, NgayCC, ThoiGianBD, ThoiGianKT, GioLamViec, GioLamThem, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maCaMoi);
                        ps.setString(2, txtManv.getText().trim());
                        ps.setString(3, tenNhanVien);
                        
                        if(ngaycc.isEmpty()) ps.setNull(4, java.sql.Types.DATE);
                        else ps.setString(4, ngaycc);
                        
                        ps.setString(5, tgbd);
                        ps.setString(6, tgkt);
                        ps.setDouble(7, Double.parseDouble(txtWorkHours.getText().trim().isEmpty() ? "0" : txtWorkHours.getText().trim()));
                        ps.setDouble(8, Double.parseDouble(txtOvertimeHours.getText().trim().isEmpty() ? "0" : txtOvertimeHours.getText().trim()));
                        ps.setString(9, cboTrangThai.getValue());
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Lỗi Database/Nhập liệu", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        ShiftModel selected = tblShift.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một ca làm để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM CaLam WHERE MaCa = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaCa());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Bản ghi này đang được sử dụng ở bảng khác, không thể xóa!\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        ShiftModel selected = tblShift.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một ca làm để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(420);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtManv = new TextField(selected.getMaNV()); styleField(txtManv);
        TextField txtTgbd = new TextField(selected.getThoiGianBD()); styleField(txtTgbd);
        TextField txtTgkt = new TextField(selected.getThoiGianKT()); styleField(txtTgkt);
        
        DatePicker dtpNgaycc = new DatePicker(); dtpNgaycc.setMaxWidth(Double.MAX_VALUE);
        try { if (!selected.getNgayCC().isEmpty()) dtpNgaycc.setValue(LocalDate.parse(selected.getNgayCC())); } catch (Exception e) {}
        
        TextField txtWorkHours = new TextField(selected.getGioLamViec()); styleField(txtWorkHours);
        TextField txtOvertimeHours = new TextField(selected.getGioLamThem()); styleField(txtOvertimeHours);
        
        ComboBox<String> cboTrangThai = new ComboBox<>();
        cboTrangThai.getItems().addAll("RUNNING", "Finish");
        cboTrangThai.setValue(selected.getTrangThai());
        cboTrangThai.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Manv:"), 0, 0); grid.add(txtManv, 1, 0);
        grid.add(new Label("Tgbd:"), 0, 1); grid.add(txtTgbd, 1, 1);
        grid.add(new Label("Tgkt:"), 0, 2); grid.add(txtTgkt, 1, 2);
        grid.add(new Label("Ngaycc:"), 0, 3); grid.add(dtpNgaycc, 1, 3);
        grid.add(new Label("WorkHours:"), 0, 4); grid.add(txtWorkHours, 1, 4);
        grid.add(new Label("OvertimeHours:"), 0, 5); grid.add(txtOvertimeHours, 1, 5);
        grid.add(new Label("Trangthai:"), 0, 6); grid.add(cboTrangThai, 1, 6);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String tgbd = txtTgbd.getText().trim();
                String tgkt = txtTgkt.getText().trim();
                
                if (tgbd.isEmpty() || tgkt.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Thời gian BĐ và Thời gian KT!");
                    return null;
                }
                
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String tenNhanVien = getTenNhanVien(conn, txtManv.getText().trim());
                    String ngaycc = dtpNgaycc.getValue() != null ? dtpNgaycc.getValue().toString() : "";
                    
                    String sql = "UPDATE CaLam SET MaNV=?, TenNV=?, NgayCC=?, ThoiGianBD=?, ThoiGianKT=?, GioLamViec=?, GioLamThem=?, TrangThai=? WHERE MaCa=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtManv.getText().trim());
                        ps.setString(2, tenNhanVien);
                        if(ngaycc.isEmpty()) ps.setNull(3, java.sql.Types.DATE);
                        else ps.setString(3, ngaycc);
                        
                        ps.setString(4, tgbd);
                        ps.setString(5, tgkt);
                        ps.setDouble(6, Double.parseDouble(txtWorkHours.getText().trim().isEmpty() ? "0" : txtWorkHours.getText().trim()));
                        ps.setDouble(7, Double.parseDouble(txtOvertimeHours.getText().trim().isEmpty() ? "0" : txtOvertimeHours.getText().trim()));
                        ps.setString(8, cboTrangThai.getValue());
                        ps.setString(9, selected.getMaCa());
                        
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
        if (lblSoCa == null) return;
        lblSoCa.setText(String.valueOf(danhSach.size()));

        double tongGio = danhSach.stream().mapToDouble(cl -> {
            try { return Double.parseDouble(cl.getGioLamViec()); } catch (Exception e) { return 0; }
        }).sum();
        lblTongGio.setText(String.format("%.0f", tongGio));

        double tangCa = danhSach.stream().mapToDouble(cl -> {
            try { return Double.parseDouble(cl.getGioLamThem()); } catch (Exception e) { return 0; }
        }).sum();
        lblTangCa.setText(String.format("%.0f", tangCa));
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