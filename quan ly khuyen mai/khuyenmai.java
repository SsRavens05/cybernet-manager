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

public class khuyenmai {

    // ===================== MODEL =====================
    public static class KhuyenMaiModel {
        private final StringProperty maCT;
        private final StringProperty tenCT;
        private final StringProperty chietKhau;
        private final StringProperty ngayBD;
        private final StringProperty ngayKT;
        private final StringProperty loaiCT;
        private final StringProperty ngayTao;

        public KhuyenMaiModel(String maCT, String tenCT, String chietKhau, String ngayBD, 
                              String ngayKT, String loaiCT, String ngayTao) {
            this.maCT = new SimpleStringProperty(maCT != null ? maCT : "");
            this.tenCT = new SimpleStringProperty(tenCT != null ? tenCT : "");
            this.chietKhau = new SimpleStringProperty(chietKhau != null ? chietKhau : "0");
            this.ngayBD = new SimpleStringProperty(ngayBD != null ? ngayBD : "");
            this.ngayKT = new SimpleStringProperty(ngayKT != null ? ngayKT : "");
            this.loaiCT = new SimpleStringProperty(loaiCT != null ? loaiCT : "");
            this.ngayTao = new SimpleStringProperty(ngayTao != null ? ngayTao : "");
        }

        public String getMaCT() { return maCT.get(); }
        public String getTenCT() { return tenCT.get(); }
        public String getChietKhau() { return chietKhau.get(); }
        public String getNgayBD() { return ngayBD.get(); }
        public String getNgayKT() { return ngayKT.get(); }
        public String getLoaiCT() { return loaiCT.get(); }
        public String getNgayTao() { return ngayTao.get(); }

        public StringProperty maCTProperty() { return maCT; }
        public StringProperty tenCTProperty() { return tenCT; }
        public StringProperty chietKhauProperty() { return chietKhau; }
        public StringProperty ngayBDProperty() { return ngayBD; }
        public StringProperty ngayKTProperty() { return ngayKT; }
        public StringProperty loaiCTProperty() { return loaiCT; }
        public StringProperty ngayTaoProperty() { return ngayTao; }
    }

    // ===================== DATA =====================
    private final ObservableList<KhuyenMaiModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<KhuyenMaiModel> filteredList;
    private TableView<KhuyenMaiModel> tblDiscountList;

    // Nhãn thống kê
    private Label lblTongKM;
    private Label lblDangDienRa;
    private Label lblCKTB;

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
        String sql = "SELECT MaCT, TenCT, ChietKhau, NgayBD, NgayKT, LoaiCT, NgayTao FROM KhuyenMai ORDER BY MaCT ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ck = String.valueOf(rs.getDouble("ChietKhau"));
                if (ck.endsWith(".0")) ck = ck.replace(".0", ""); // Xóa số 0 thừa

                danhSach.add(new KhuyenMaiModel(
                    rs.getString("MaCT"), rs.getString("TenCT"), ck,
                    rs.getString("NgayBD"), rs.getString("NgayKT"), 
                    rs.getString("LoaiCT"), rs.getString("NgayTao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    // Hàm sinh mã tự động tăng (CTR001, CTR002...)
    private String generateNewMaCT(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaCT FROM KhuyenMai WHERE MaCT LIKE 'CTR%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaCT");
                if (ma != null && ma.length() > 3) {
                    try {
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("CTR%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblTongKM = new Label("0");
        lblDangDienRa = new Label("0");
        lblCKTB = new Label("0");

        StackPane cardTongKM = buildCard("Tổng KM", lblTongKM, "#7986CB", "#3F51B5", "🧾");
        StackPane cardDangDienRa = buildCard("Đang diễn ra", lblDangDienRa, "#BA68C8", "#8E24AA", "⏱");
        StackPane cardCKTB = buildCard("CK TB", lblCKTB, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardTongKM, Priority.ALWAYS);
        HBox.setHgrow(cardDangDienRa, Priority.ALWAYS);
        HBox.setHgrow(cardCKTB, Priority.ALWAYS);

        hbox.getChildren().addAll(cardTongKM, cardDangDienRa, cardCKTB);
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

        tblDiscountList = buildTable();
        VBox.setVgrow(tblDiscountList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblDiscountList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Khuyến Mãi");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(km -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return km.getMaCT().toLowerCase().contains(filter) ||
                       km.getTenCT().toLowerCase().contains(filter) ||
                       km.getLoaiCT().toLowerCase().contains(filter);
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
    private TableView<KhuyenMaiModel> buildTable() {
        TableView<KhuyenMaiModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<KhuyenMaiModel, String> colMa = new TableColumn<>("Mã CT"); colMa.setCellValueFactory(new PropertyValueFactory<>("maCT"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        TableColumn<KhuyenMaiModel, String> colTen = new TableColumn<>("Tên CT"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenCT"));
        TableColumn<KhuyenMaiModel, String> colCK = new TableColumn<>("Chiết Khấu"); colCK.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));
        TableColumn<KhuyenMaiModel, String> colBD = new TableColumn<>("Ngày BĐ"); colBD.setCellValueFactory(new PropertyValueFactory<>("ngayBD"));
        TableColumn<KhuyenMaiModel, String> colKT = new TableColumn<>("Ngày KT"); colKT.setCellValueFactory(new PropertyValueFactory<>("ngayKT"));
        TableColumn<KhuyenMaiModel, String> colLoai = new TableColumn<>("Loại CT"); colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiCT"));
        TableColumn<KhuyenMaiModel, String> colTao = new TableColumn<>("Ngày Tạo"); colTao.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));

        table.getColumns().addAll(colMa, colTen, colCK, colBD, colKT, colLoai, colTao);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<KhuyenMaiModel> row = new TableRow<>();
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
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTenCT = new TextField(); styleField(txtTenCT);
        TextField txtLoaiCT = new TextField(); styleField(txtLoaiCT);
        DatePicker dtpBD = new DatePicker(LocalDate.now()); dtpBD.setMaxWidth(Double.MAX_VALUE);
        DatePicker dtpKT = new DatePicker(LocalDate.now().plusDays(7)); dtpKT.setMaxWidth(Double.MAX_VALUE);
        TextField txtChietKhau = new TextField("0"); styleField(txtChietKhau);

        grid.add(new Label("TenCTR:"), 0, 0); grid.add(txtTenCT, 1, 0);
        grid.add(new Label("LoaiCTR:"), 0, 1); grid.add(txtLoaiCT, 1, 1);
        grid.add(new Label("NgayBD:"), 0, 2); grid.add(dtpBD, 1, 2);
        grid.add(new Label("NgayKT:"), 0, 3); grid.add(dtpKT, 1, 3);
        grid.add(new Label("ChietKhau:"), 0, 4); grid.add(txtChietKhau, 1, 4);

        GridPane.setHgrow(txtTenCT, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaCT(conn);
                    String ngayBD = dtpBD.getValue() != null ? dtpBD.getValue().toString() : "";
                    String ngayKT = dtpKT.getValue() != null ? dtpKT.getValue().toString() : "";
                    String ngayTao = LocalDate.now().toString(); 
                    
                    String sql = "INSERT INTO KhuyenMai (MaCT, TenCT, ChietKhau, NgayBD, NgayKT, LoaiCT, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTenCT.getText().trim());
                        ps.setDouble(3, Double.parseDouble(txtChietKhau.getText().trim().isEmpty() ? "0" : txtChietKhau.getText().trim()));
                        
                        if(ngayBD.isEmpty()) ps.setNull(4, java.sql.Types.DATE); else ps.setString(4, ngayBD);
                        if(ngayKT.isEmpty()) ps.setNull(5, java.sql.Types.DATE); else ps.setString(5, ngayKT);
                        
                        ps.setString(6, txtLoaiCT.getText().trim());
                        ps.setString(7, ngayTao);
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu / CSDL", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        KhuyenMaiModel selected = tblDiscountList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một chương trình để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM KhuyenMai WHERE MaCT = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaCT());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        KhuyenMaiModel selected = tblDiscountList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một chương trình để cập nhật!");
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

        TextField txtMaCT = new TextField(selected.getMaCT());
        txtMaCT.setEditable(false);
        txtMaCT.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTen = new TextField(selected.getTenCT()); styleField(txtTen);
        TextField txtLoai = new TextField(selected.getLoaiCT()); styleField(txtLoai);
        
        // Dùng DatePicker thay cho chuỗi String rối rắm để tránh lỗi
        DatePicker dtpBD = new DatePicker(); dtpBD.setMaxWidth(Double.MAX_VALUE);
        try { if (!selected.getNgayBD().isEmpty()) dtpBD.setValue(LocalDate.parse(selected.getNgayBD())); } catch (Exception e) {}
        
        DatePicker dtpKT = new DatePicker(); dtpKT.setMaxWidth(Double.MAX_VALUE);
        try { if (!selected.getNgayKT().isEmpty()) dtpKT.setValue(LocalDate.parse(selected.getNgayKT())); } catch (Exception e) {}

        TextField txtChietKhau = new TextField(selected.getChietKhau()); styleField(txtChietKhau);
        
        TextField txtNgayTao = new TextField(selected.getNgayTao());
        txtNgayTao.setEditable(false);
        txtNgayTao.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        grid.add(new Label("MaCTR:"), 0, 0); grid.add(txtMaCT, 1, 0);
        grid.add(new Label("TenCTR:"), 0, 1); grid.add(txtTen, 1, 1);
        grid.add(new Label("LoaiCTR:"), 0, 2); grid.add(txtLoai, 1, 2);
        grid.add(new Label("NgayBD:"), 0, 3); grid.add(dtpBD, 1, 3);
        grid.add(new Label("NgayKT:"), 0, 4); grid.add(dtpKT, 1, 4);
        grid.add(new Label("ChietKhau:"), 0, 5); grid.add(txtChietKhau, 1, 5);
        grid.add(new Label("Createat:"), 0, 6); grid.add(txtNgayTao, 1, 6);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String sql = "UPDATE KhuyenMai SET TenCT=?, LoaiCT=?, NgayBD=?, NgayKT=?, ChietKhau=? WHERE MaCT=?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    String ngayBD = dtpBD.getValue() != null ? dtpBD.getValue().toString() : "";
                    String ngayKT = dtpKT.getValue() != null ? dtpKT.getValue().toString() : "";
                    
                    ps.setString(1, txtTen.getText().trim());
                    ps.setString(2, txtLoai.getText().trim());
                    if(ngayBD.isEmpty()) ps.setNull(3, java.sql.Types.DATE); else ps.setString(3, ngayBD);
                    if(ngayKT.isEmpty()) ps.setNull(4, java.sql.Types.DATE); else ps.setString(4, ngayKT);
                    ps.setDouble(5, Double.parseDouble(txtChietKhau.getText().trim().isEmpty() ? "0" : txtChietKhau.getText().trim()));
                    ps.setString(6, selected.getMaCT());
                    
                    ps.executeUpdate();
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblTongKM == null) return;
        lblTongKM.setText(String.valueOf(danhSach.size()));

        LocalDate today = LocalDate.now();
        long dangDienRa = danhSach.stream().filter(km -> {
            try {
                LocalDate bd = LocalDate.parse(km.getNgayBD());
                LocalDate kt = LocalDate.parse(km.getNgayKT());
                return !today.isBefore(bd) && !today.isAfter(kt);
            } catch (Exception e) { return false; }
        }).count();
        lblDangDienRa.setText(String.valueOf(dangDienRa));

        double tongCK = danhSach.stream().mapToDouble(km -> {
            try { return Double.parseDouble(km.getChietKhau()); } catch (Exception e) { return 0; }
        }).sum();
        double cktb = danhSach.isEmpty() ? 0 : tongCK / danhSach.size();
        lblCKTB.setText(String.format("%.1f", cktb).replace(",", "."));
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