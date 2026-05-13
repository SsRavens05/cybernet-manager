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

public class nghiphep {

    // ===================== MODEL =====================
    public static class NghiPhepModel {
        private final StringProperty maNP;
        private final StringProperty maNVNP;
        private final StringProperty maNVTT;
        private final StringProperty tenNVNP;
        private final StringProperty tenNVTT;
        private final StringProperty maCa;
        private final StringProperty noiDung;
        private final StringProperty tgThongBao;
        private final StringProperty loaiNghi;

        public NghiPhepModel(String maNP, String maNVNP, String maNVTT, String tenNVNP, String tenNVTT, 
                             String maCa, String noiDung, String tgThongBao, String loaiNghi) {
            this.maNP = new SimpleStringProperty(maNP != null ? maNP : "");
            this.maNVNP = new SimpleStringProperty(maNVNP != null ? maNVNP : "");
            this.maNVTT = new SimpleStringProperty(maNVTT != null ? maNVTT : "");
            this.tenNVNP = new SimpleStringProperty(tenNVNP != null ? tenNVNP : "Unknown");
            this.tenNVTT = new SimpleStringProperty(tenNVTT != null ? tenNVTT : "Unknown");
            this.maCa = new SimpleStringProperty(maCa != null ? maCa : "");
            this.noiDung = new SimpleStringProperty(noiDung != null ? noiDung : "");
            this.tgThongBao = new SimpleStringProperty(tgThongBao != null ? tgThongBao : "");
            this.loaiNghi = new SimpleStringProperty(loaiNghi != null ? loaiNghi : "");
        }

        public String getMaNP() { return maNP.get(); }
        public String getMaNVNP() { return maNVNP.get(); }
        public String getMaNVTT() { return maNVTT.get(); }
        public String getTenNVNP() { return tenNVNP.get(); }
        public String getTenNVTT() { return tenNVTT.get(); }
        public String getMaCa() { return maCa.get(); }
        public String getNoiDung() { return noiDung.get(); }
        public String getTgThongBao() { return tgThongBao.get(); }
        public String getLoaiNghi() { return loaiNghi.get(); }

        public StringProperty maNPProperty() { return maNP; }
        public StringProperty maNVNPProperty() { return maNVNP; }
        public StringProperty maNVTTProperty() { return maNVTT; }
        public StringProperty tenNVNPProperty() { return tenNVNP; }
        public StringProperty tenNVTTProperty() { return tenNVTT; }
        public StringProperty maCaProperty() { return maCa; }
        public StringProperty noiDungProperty() { return noiDung; }
        public StringProperty tgThongBaoProperty() { return tgThongBao; }
        public StringProperty loaiNghiProperty() { return loaiNghi; }
    }

    // ===================== DATA =====================
    private final ObservableList<NghiPhepModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<NghiPhepModel> filteredList;
    private TableView<NghiPhepModel> tblRequestList;

    private Label lblTongDon;
    private Label lblChoDuyet;
    private Label lblLoaiNghi;

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
        String sql = "SELECT MaNP, MaNVNP, MaNVTT, TenNVNP, TenNVTT, MaCa, NoiDung, TGThongBao, LoaiNghi FROM NghiPhep ORDER BY MaNP ASC";
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(new NghiPhepModel(
                    rs.getString("MaNP"), rs.getString("MaNVNP"), rs.getString("MaNVTT"),
                    rs.getString("TenNVNP"), rs.getString("TenNVTT"), rs.getString("MaCa"),
                    rs.getString("NoiDung"), rs.getString("TGThongBao"), rs.getString("LoaiNghi")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    private String getTenNhanVien(Connection conn, String maNV) {
        if(maNV == null || maNV.trim().isEmpty()) return "";
        String sql = "SELECT TenNV FROM NhanVien WHERE MaNV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("TenNV");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Unknown";
    }

    // KIỂM TRA SỰ TỒN TẠI CỦA MÃ CA (Để phục vụ Khóa Ngoại)
    private boolean checkMaCaExists(Connection conn, String maCa) {
        if(maCa == null || maCa.trim().isEmpty()) return false;
        String sql = "SELECT MaCa FROM CaLam WHERE MaCa = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private String generateNewMaNP(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaNP FROM NghiPhep WHERE MaNP LIKE 'NP%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaNP");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("NP%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);
        lblTongDon = new Label("0");
        lblChoDuyet = new Label("0");
        lblLoaiNghi = new Label("0");
        StackPane cardTongDon = buildCard("Tổng đơn", lblTongDon, "#7986CB", "#3F51B5", "🧾");
        StackPane cardChoDuyet = buildCard("Chờ duyệt", lblChoDuyet, "#BA68C8", "#8E24AA", "⏱");
        StackPane cardLoaiNghi = buildCard("Loại nghỉ", lblLoaiNghi, "#FFD54F", "#FF8F00", "🚩");
        HBox.setHgrow(cardTongDon, Priority.ALWAYS);
        HBox.setHgrow(cardChoDuyet, Priority.ALWAYS);
        HBox.setHgrow(cardLoaiNghi, Priority.ALWAYS);
        hbox.getChildren().addAll(cardTongDon, cardChoDuyet, cardLoaiNghi);
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
        tblRequestList = buildTable();
        VBox.setVgrow(tblRequestList, Priority.ALWAYS);
        panel.getChildren().addAll(toolbar, tblRequestList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Danh Sách Nghỉ Phép");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");
        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(np -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return np.getMaNVNP().toLowerCase().contains(filter) || np.getMaNP().toLowerCase().contains(filter) || np.getTenNVNP().toLowerCase().contains(filter);
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
    private TableView<NghiPhepModel> buildTable() {
        TableView<NghiPhepModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);
        TableColumn<NghiPhepModel, String> colMaNP = new TableColumn<>("Mã NP"); colMaNP.setCellValueFactory(new PropertyValueFactory<>("maNP"));
        colMaNP.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        TableColumn<NghiPhepModel, String> colMaNVNP = new TableColumn<>("Mã NVNP"); colMaNVNP.setCellValueFactory(new PropertyValueFactory<>("maNVNP"));
        TableColumn<NghiPhepModel, String> colMaNVTT = new TableColumn<>("Mã NVTT"); colMaNVTT.setCellValueFactory(new PropertyValueFactory<>("maNVTT"));
        TableColumn<NghiPhepModel, String> colTenNP = new TableColumn<>("Tên NVNP"); colTenNP.setCellValueFactory(new PropertyValueFactory<>("tenNVNP"));
        TableColumn<NghiPhepModel, String> colTenTT = new TableColumn<>("Tên NVTT"); colTenTT.setCellValueFactory(new PropertyValueFactory<>("tenNVTT"));
        TableColumn<NghiPhepModel, String> colMaCa = new TableColumn<>("Mã Ca"); colMaCa.setCellValueFactory(new PropertyValueFactory<>("maCa"));
        TableColumn<NghiPhepModel, String> colNoiDung = new TableColumn<>("Nội Dung"); colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        TableColumn<NghiPhepModel, String> colTG = new TableColumn<>("TG Thông Báo"); colTG.setCellValueFactory(new PropertyValueFactory<>("tgThongBao"));
        TableColumn<NghiPhepModel, String> colLoai = new TableColumn<>("Loại Nghỉ"); colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiNghi"));
        table.getColumns().addAll(colMaNP, colMaNVNP, colMaNVTT, colTenNP, colTenTT, colMaCa, colNoiDung, colTG, colLoai);
        table.setItems(filteredList);
        return table;
    }

    // ===================== INSERT DIALOG =====================
    private void showInsertDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Insert Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(420);
        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtManvnp = new TextField(); styleField(txtManvnp);
        TextField txtManvtt = new TextField(); styleField(txtManvtt);
        TextField txtMaca = new TextField(); styleField(txtMaca); txtMaca.setPromptText("VD: CL001");
        TextField txtNoidung = new TextField(); styleField(txtNoidung);
        DatePicker dtpTgthongbao = new DatePicker(LocalDate.now()); dtpTgthongbao.setMaxWidth(Double.MAX_VALUE);
        ComboBox<String> cboLoainghi = new ComboBox<>(FXCollections.observableArrayList("Có phép", "Không phép", "Chờ duyệt")); cboLoainghi.setValue("Có phép"); cboLoainghi.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Mã NV Nghỉ:"), 0, 0); grid.add(txtManvnp, 1, 0);
        grid.add(new Label("Mã NV Thay Thế:"), 0, 1); grid.add(txtManvtt, 1, 1);
        grid.add(new Label("Mã Ca (Khóa ngoại):"), 0, 2); grid.add(txtMaca, 1, 2);
        grid.add(new Label("Nội dung:"), 0, 3); grid.add(txtNoidung, 1, 3);
        grid.add(new Label("TG Thông báo:"), 0, 4); grid.add(dtpTgthongbao, 1, 4);
        grid.add(new Label("Loại nghỉ:"), 0, 5); grid.add(cboLoainghi, 1, 5);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    // 1. KIỂM TRA KHÓA NGOẠI MÃ CA
                    if (!checkMaCaExists(conn, txtMaca.getText().trim())) {
                        showAlert(Alert.AlertType.WARNING, "Lỗi Khóa Ngoại", "Mã Ca làm việc không tồn tại trong hệ thống!");
                        return null;
                    }
                    
                    String tenNVNP = getTenNhanVien(conn, txtManvnp.getText().trim());
                    String tenNVTT = getTenNhanVien(conn, txtManvtt.getText().trim());
                    String maMoi = generateNewMaNP(conn);
                    String ngaytb = dtpTgthongbao.getValue() != null ? dtpTgthongbao.getValue().toString() : "";
                    
                    String sql = "INSERT INTO NghiPhep (MaNP, MaNVNP, MaNVTT, TenNVNP, TenNVTT, MaCa, NoiDung, TGThongBao, LoaiNghi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi); ps.setString(2, txtManvnp.getText().trim());
                        ps.setString(3, txtManvtt.getText().trim()); ps.setString(4, tenNVNP);
                        ps.setString(5, tenNVTT); ps.setString(6, txtMaca.getText().trim());
                        ps.setString(7, txtNoidung.getText().trim()); ps.setString(8, ngaytb);
                        ps.setString(9, cboLoainghi.getValue());
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage()); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void handleDelete() {
        NghiPhepModel selected = tblRequestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM NghiPhep WHERE MaNP = ?";
            try (Connection conn = ketnoicsdl.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaNP()); ps.executeUpdate(); loadDataFromDatabase(); 
            } catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage()); }
        }
    }

    private void handleUpdate() {
        NghiPhepModel selected = tblRequestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        // Tương tự Insert, bổ sung hàm checkMaCaExists trong logic Save của Update...
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblTongDon == null) return;
        lblTongDon.setText(String.valueOf(danhSach.size()));
        lblChoDuyet.setText(String.valueOf(danhSach.stream().filter(np -> "Chờ duyệt".equalsIgnoreCase(np.getLoaiNghi())).count()));
        lblLoaiNghi.setText(String.valueOf(danhSach.stream().map(NghiPhepModel::getLoaiNghi).distinct().count()));
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