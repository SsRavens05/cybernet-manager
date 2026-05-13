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

public class songaynghi {

    // ===================== MODEL =====================
    public static class NgayNghiModel {
        private final StringProperty maSoNgayNghi;
        private final StringProperty maNhanVien;
        private final IntegerProperty tongSoNgayNghi;
        private final IntegerProperty ngayNghiDaDung;
        private final IntegerProperty soNgayConLai;
        private final IntegerProperty nam;

        public NgayNghiModel(String maSoNgayNghi, String maNhanVien, int tongSoNgayNghi, 
                             int ngayNghiDaDung, int soNgayConLai, int nam) {
            this.maSoNgayNghi = new SimpleStringProperty(maSoNgayNghi != null ? maSoNgayNghi : "");
            this.maNhanVien = new SimpleStringProperty(maNhanVien != null ? maNhanVien : "");
            this.tongSoNgayNghi = new SimpleIntegerProperty(tongSoNgayNghi);
            this.ngayNghiDaDung = new SimpleIntegerProperty(ngayNghiDaDung);
            this.soNgayConLai = new SimpleIntegerProperty(soNgayConLai);
            this.nam = new SimpleIntegerProperty(nam);
        }

        public String getMaSoNgayNghi() { return maSoNgayNghi.get(); }
        public String getMaNhanVien() { return maNhanVien.get(); }
        public int getTongSoNgayNghi() { return tongSoNgayNghi.get(); }
        public int getNgayNghiDaDung() { return ngayNghiDaDung.get(); }
        public int getSoNgayConLai() { return soNgayConLai.get(); }
        public int getNam() { return nam.get(); }

        public StringProperty maSoNgayNghiProperty() { return maSoNgayNghi; }
        public StringProperty maNhanVienProperty() { return maNhanVien; }
        public IntegerProperty tongSoNgayNghiProperty() { return tongSoNgayNghi; }
        public IntegerProperty ngayNghiDaDungProperty() { return ngayNghiDaDung; }
        public IntegerProperty soNgayConLaiProperty() { return soNgayConLai; }
        public IntegerProperty namProperty() { return nam; }
    }

    // ===================== DATA =====================
    private final ObservableList<NgayNghiModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<NgayNghiModel> filteredList;
    private TableView<NgayNghiModel> tblLeavesList;

    // Các nhãn thống kê
    private Label lblTongPhep;
    private Label lblDaDung;
    private Label lblConLai;

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
        String sql = "SELECT MaSoNgayNghi, MaNhanVien, TongSoNgayNghi, NgayNghiDaDung, SoNgayConLai, Nam FROM TongSoNgayNghiPhep ORDER BY MaSoNgayNghi ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maSNN = rs.getString("MaSoNgayNghi");
                String maNV = rs.getString("MaNhanVien");
                int tong = rs.getInt("TongSoNgayNghi");
                int daDung = rs.getInt("NgayNghiDaDung");
                int conLai = rs.getInt("SoNgayConLai");
                int nam = rs.getInt("Nam");

                danhSach.add(new NgayNghiModel(maSNN, maNV, tong, daDung, conLai, nam));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }
    
    // Hàm sinh mã tự động tăng (SNP001, SNP002...)
    private String generateNewMaSNP(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaSoNgayNghi FROM TongSoNgayNghiPhep WHERE MaSoNgayNghi LIKE 'SNP%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaSoNgayNghi");
                if (ma != null && ma.length() > 3) {
                    try {
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("SNP%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblTongPhep = new Label("0");
        lblDaDung = new Label("0");
        lblConLai = new Label("0");

        StackPane cardTong = buildCard("Tổng phép", lblTongPhep, "#7986CB", "#3F51B5", "🧾");
        StackPane cardDaDung = buildCard("Đã dùng", lblDaDung, "#BA68C8", "#8E24AA", "💰");
        StackPane cardConLai = buildCard("Còn lại", lblConLai, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardTong, Priority.ALWAYS);
        HBox.setHgrow(cardDaDung, Priority.ALWAYS);
        HBox.setHgrow(cardConLai, Priority.ALWAYS);

        hbox.getChildren().addAll(cardTong, cardDaDung, cardConLai);
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

        tblLeavesList = buildTable();
        VBox.setVgrow(tblLeavesList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblLeavesList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tổng Số Ngày Nghỉ Phép");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(sn -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return sn.getMaNhanVien().toLowerCase().contains(filter) ||
                       sn.getMaSoNgayNghi().toLowerCase().contains(filter) ||
                       String.valueOf(sn.getNam()).contains(filter);
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
    private TableView<NgayNghiModel> buildTable() {
        TableView<NgayNghiModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<NgayNghiModel, String> colMaSNN = new TableColumn<>("Mã Số Ngày Nghỉ"); 
        colMaSNN.setCellValueFactory(new PropertyValueFactory<>("maSoNgayNghi"));
        colMaSNN.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");

        TableColumn<NgayNghiModel, String> colMaNV = new TableColumn<>("Mã Nhân Viên"); 
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<NgayNghiModel, Integer> colTong = new TableColumn<>("Tổng Số Ngày Nghỉ"); 
        colTong.setCellValueFactory(new PropertyValueFactory<>("tongSoNgayNghi"));

        TableColumn<NgayNghiModel, Integer> colDaDung = new TableColumn<>("Ngày Nghỉ Đã Dùng"); 
        colDaDung.setCellValueFactory(new PropertyValueFactory<>("ngayNghiDaDung"));

        TableColumn<NgayNghiModel, Integer> colConLai = new TableColumn<>("Số Ngày Còn Lại"); 
        colConLai.setCellValueFactory(new PropertyValueFactory<>("soNgayConLai"));

        TableColumn<NgayNghiModel, Integer> colNam = new TableColumn<>("Năm"); 
        colNam.setCellValueFactory(new PropertyValueFactory<>("nam"));

        table.getColumns().addAll(colMaSNN, colMaNV, colTong, colDaDung, colConLai, colNam);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<NgayNghiModel> row = new TableRow<>();
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

        TextField txtManv = new TextField(); styleField(txtManv);
        TextField txtTotal = new TextField("0"); styleField(txtTotal);
        TextField txtUsed = new TextField("0"); styleField(txtUsed);
        TextField txtRemaining = new TextField("0"); styleField(txtRemaining);
        txtRemaining.setEditable(false); txtRemaining.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");
        TextField txtYear = new TextField("2026"); styleField(txtYear);

        // Tự động tính số ngày còn lại
        txtTotal.textProperty().addListener((obs, oldVal, newVal) -> autoCalculateRemaining(txtTotal, txtUsed, txtRemaining));
        txtUsed.textProperty().addListener((obs, oldVal, newVal) -> autoCalculateRemaining(txtTotal, txtUsed, txtRemaining));

        grid.add(new Label("Manv:"), 0, 0); grid.add(txtManv, 1, 0);
        grid.add(new Label("TotalLeaves:"), 0, 1); grid.add(txtTotal, 1, 1);
        grid.add(new Label("UsedLeaves:"), 0, 2); grid.add(txtUsed, 1, 2);
        grid.add(new Label("RemainingLeaves:"), 0, 3); grid.add(txtRemaining, 1, 3);
        grid.add(new Label("Nam:"), 0, 4); grid.add(txtYear, 1, 4);

        GridPane.setHgrow(txtManv, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtManv.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Mã Nhân Viên!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    // Kiểm tra nhân viên tồn tại
                    String checkSql = "SELECT MaNV FROM NhanVien WHERE MaNV = ?";
                    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                        checkPs.setString(1, txtManv.getText().trim());
                        ResultSet rs = checkPs.executeQuery();
                        if (!rs.next()) {
                            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mã nhân viên không tồn tại trong hệ thống!");
                            return null;
                        }
                    }

                    String maMoi = generateNewMaSNP(conn);
                    String sql = "INSERT INTO TongSoNgayNghiPhep (MaSoNgayNghi, MaNhanVien, TongSoNgayNghi, NgayNghiDaDung, SoNgayConLai, Nam) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtManv.getText().trim());
                        ps.setInt(3, Integer.parseInt(txtTotal.getText().trim()));
                        ps.setInt(4, Integer.parseInt(txtUsed.getText().trim()));
                        ps.setInt(5, Integer.parseInt(txtRemaining.getText().trim()));
                        ps.setInt(6, Integer.parseInt(txtYear.getText().trim()));
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

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        NgayNghiModel selected = tblLeavesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một mục để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM TongSoNgayNghiPhep WHERE MaSoNgayNghi = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaSoNgayNghi());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        NgayNghiModel selected = tblLeavesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một mục để cập nhật!");
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

        TextField txtManp = new TextField(selected.getMaSoNgayNghi());
        txtManp.setEditable(false);
        txtManp.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtManv = new TextField(selected.getMaNhanVien()); styleField(txtManv);
        TextField txtTotal = new TextField(String.valueOf(selected.getTongSoNgayNghi())); styleField(txtTotal);
        TextField txtUsed = new TextField(String.valueOf(selected.getNgayNghiDaDung())); styleField(txtUsed);
        TextField txtRemaining = new TextField(String.valueOf(selected.getSoNgayConLai())); styleField(txtRemaining);
        txtRemaining.setEditable(false); txtRemaining.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");
        TextField txtYear = new TextField(String.valueOf(selected.getNam())); styleField(txtYear);

        // Tự động tính số ngày còn lại
        txtTotal.textProperty().addListener((obs, oldVal, newVal) -> autoCalculateRemaining(txtTotal, txtUsed, txtRemaining));
        txtUsed.textProperty().addListener((obs, oldVal, newVal) -> autoCalculateRemaining(txtTotal, txtUsed, txtRemaining));

        grid.add(new Label("Manp:"), 0, 0); grid.add(txtManp, 1, 0);
        grid.add(new Label("Manv:"), 0, 1); grid.add(txtManv, 1, 1);
        grid.add(new Label("TotalLeaves:"), 0, 2); grid.add(txtTotal, 1, 2);
        grid.add(new Label("UsedLeaves:"), 0, 3); grid.add(txtUsed, 1, 3);
        grid.add(new Label("RemainingLeaves:"), 0, 4); grid.add(txtRemaining, 1, 4);
        grid.add(new Label("Nam:"), 0, 5); grid.add(txtYear, 1, 5);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String checkSql = "SELECT MaNV FROM NhanVien WHERE MaNV = ?";
                    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                        checkPs.setString(1, txtManv.getText().trim());
                        ResultSet rs = checkPs.executeQuery();
                        if (!rs.next()) {
                            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mã nhân viên không tồn tại trong hệ thống!");
                            return null;
                        }
                    }

                    String sql = "UPDATE TongSoNgayNghiPhep SET MaNhanVien=?, TongSoNgayNghi=?, NgayNghiDaDung=?, SoNgayConLai=?, Nam=? WHERE MaSoNgayNghi=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtManv.getText().trim());
                        ps.setInt(2, Integer.parseInt(txtTotal.getText().trim()));
                        ps.setInt(3, Integer.parseInt(txtUsed.getText().trim()));
                        ps.setInt(4, Integer.parseInt(txtRemaining.getText().trim()));
                        ps.setInt(5, Integer.parseInt(txtYear.getText().trim()));
                        ps.setString(6, selected.getMaSoNgayNghi());
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
    private void autoCalculateRemaining(TextField txtTotal, TextField txtUsed, TextField txtRemaining) {
        try {
            int total = Integer.parseInt(txtTotal.getText().trim());
            int used = Integer.parseInt(txtUsed.getText().trim());
            txtRemaining.setText(String.valueOf(total - used));
        } catch (NumberFormatException e) {
            txtRemaining.setText("0");
        }
    }

    private void refreshCards() {
        if (lblTongPhep == null) return;
        
        int tongPhep = danhSach.stream().mapToInt(NgayNghiModel::getTongSoNgayNghi).sum();
        lblTongPhep.setText(String.valueOf(tongPhep));

        int daDung = danhSach.stream().mapToInt(NgayNghiModel::getNgayNghiDaDung).sum();
        lblDaDung.setText(String.valueOf(daDung));

        int conLai = danhSach.stream().mapToInt(NgayNghiModel::getSoNgayConLai).sum();
        lblConLai.setText(String.valueOf(conLai));
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
