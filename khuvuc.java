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

public class khuvuc {

    // ===================== MODEL =====================
    public static class KhuVucModel {
        private final StringProperty maKV;
        private final StringProperty tenKV;
        private final StringProperty loaiKV;
        private final StringProperty soTang;
        private final StringProperty soLuongMay;
        private final StringProperty trangThai;

        public KhuVucModel(String maKV, String tenKV, String loaiKV, String soTang, String soLuongMay, String trangThai) {
            this.maKV = new SimpleStringProperty(maKV != null ? maKV : "");
            this.tenKV = new SimpleStringProperty(tenKV != null ? tenKV : "");
            this.loaiKV = new SimpleStringProperty(loaiKV != null ? loaiKV : "");
            this.soTang = new SimpleStringProperty(soTang != null ? soTang : "0");
            this.soLuongMay = new SimpleStringProperty(soLuongMay != null ? soLuongMay : "0");
            this.trangThai = new SimpleStringProperty(trangThai != null ? trangThai : "");
        }

        public String getMaKV() { return maKV.get(); }
        public String getTenKV() { return tenKV.get(); }
        public String getLoaiKV() { return loaiKV.get(); }
        public String getSoTang() { return soTang.get(); }
        public String getSoLuongMay() { return soLuongMay.get(); }
        public String getTrangThai() { return trangThai.get(); }

        public StringProperty maKVProperty() { return maKV; }
        public StringProperty tenKVProperty() { return tenKV; }
        public StringProperty loaiKVProperty() { return loaiKV; }
        public StringProperty soTangProperty() { return soTang; }
        public StringProperty soLuongMayProperty() { return soLuongMay; }
        public StringProperty trangThaiProperty() { return trangThai; }
    }

    // ===================== DATA =====================
    private final ObservableList<KhuVucModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<KhuVucModel> filteredList;
    private TableView<KhuVucModel> tblAreaList;

    private Label lblSoKV;
    private Label lblTongMay;
    private Label lblKVHoatDong;

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
        String sql = "SELECT MaKV, TenKV, LoaiKV, SoTang, SoLuongMayKV, TrangThai FROM KhuVuc ORDER BY MaKV ASC";
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(new KhuVucModel(
                    rs.getString("MaKV"), rs.getString("TenKV"), rs.getString("LoaiKV"),
                    String.valueOf(rs.getInt("SoTang")), String.valueOf(rs.getInt("SoLuongMayKV")),
                    rs.getString("TrangThai")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        refreshCards();
    }

    private String generateNewMaKV(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaKV FROM KhuVuc WHERE MaKV LIKE 'KV%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaKV");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("KV%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);
        lblSoKV = new Label("0"); lblTongMay = new Label("0"); lblKVHoatDong = new Label("0");
        StackPane card1 = buildCard("Số KV", lblSoKV, "#7986CB", "#3F51B5", "🗺");
        StackPane card2 = buildCard("Tổng máy", lblTongMay, "#BA68C8", "#8E24AA", "🖥");
        StackPane card3 = buildCard("KV hoạt động", lblKVHoatDong, "#FFD54F", "#FF8F00", "✅");
        HBox.setHgrow(card1, Priority.ALWAYS); HBox.setHgrow(card2, Priority.ALWAYS); HBox.setHgrow(card3, Priority.ALWAYS);
        hbox.getChildren().addAll(card1, card2, card3);
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
        return stack;
    }

    // ===================== TABLE PANEL =====================
    private VBox buildTablePanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        HBox toolbar = buildToolbar();
        toolbar.setPadding(new Insets(14, 16, 14, 16));
        toolbar.setStyle("-fx-border-color: transparent transparent " + BORDER_COLOR + " transparent; -fx-border-width: 0 0 1 0;");
        tblAreaList = buildTable();
        VBox.setVgrow(tblAreaList, Priority.ALWAYS);
        panel.getChildren().addAll(toolbar, tblAreaList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Danh Sách Khu Vực");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");
        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(kv -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return kv.getMaKV().toLowerCase().contains(filter) || kv.getTenKV().toLowerCase().contains(filter);
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
    private TableView<KhuVucModel> buildTable() {
        TableView<KhuVucModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);
        TableColumn<KhuVucModel, String> colMa = new TableColumn<>("Mã KV"); colMa.setCellValueFactory(new PropertyValueFactory<>("maKV"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        TableColumn<KhuVucModel, String> colTen = new TableColumn<>("Tên Khu Vực"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenKV"));
        TableColumn<KhuVucModel, String> colLoai = new TableColumn<>("Loại KV"); colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiKV"));
        TableColumn<KhuVucModel, String> colTang = new TableColumn<>("Số Tầng"); colTang.setCellValueFactory(new PropertyValueFactory<>("soTang"));
        TableColumn<KhuVucModel, String> colMay = new TableColumn<>("Số Lượng Máy KV"); colMay.setCellValueFactory(new PropertyValueFactory<>("soLuongMay"));
        TableColumn<KhuVucModel, String> colTT = new TableColumn<>("Trạng Thái"); colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item); badge.setPadding(new Insets(3, 10, 3, 10));
                String bg = item.equalsIgnoreCase("HOATDONG") ? "#2E7D32" : "#E65100";
                badge.setStyle("-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + bg + ";");
                setGraphic(badge); setText(null);
            }
        });
        table.getColumns().addAll(colMa, colTen, colLoai, colTang, colMay, colTT);
        table.setItems(filteredList);
        return table;
    }

    // ===================== DIALOGS =====================
    private void showInsertDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Insert Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(380);
        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTen = new TextField(); styleField(txtTen);
        TextField txtLoai = new TextField(); styleField(txtLoai); // Trong thực tế nên dùng ComboBox lấy từ LoaiKhuVuc
        ComboBox<String> cboTrangThai = new ComboBox<>(FXCollections.observableArrayList("HOATDONG", "DANGBAOTRI")); cboTrangThai.setValue("HOATDONG"); cboTrangThai.setMaxWidth(Double.MAX_VALUE);
        TextField txtTang = new TextField("1"); styleField(txtTang);
        TextField txtMay = new TextField("0"); styleField(txtMay);

        grid.add(new Label("Tên KV:"), 0, 0); grid.add(txtTen, 1, 0);
        grid.add(new Label("Loại KV (Mã):"), 0, 1); grid.add(txtLoai, 1, 1);
        grid.add(new Label("Trạng thái:"), 0, 2); grid.add(cboTrangThai, 1, 2);
        grid.add(new Label("Số tầng:"), 0, 3); grid.add(txtTang, 1, 3);
        grid.add(new Label("Số lượng máy:"), 0, 4); grid.add(txtMay, 1, 4);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(btnSave, ButtonType.CANCEL);
        styleDialogButtons(dp, btnSave);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaKV(conn);
                    String sql = "INSERT INTO KhuVuc (MaKV, TenKV, LoaiKV, SoTang, SoLuongMayKV, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi); ps.setString(2, txtTen.getText().trim());
                        ps.setString(3, txtLoai.getText().trim()); ps.setInt(4, Integer.parseInt(txtTang.getText().trim()));
                        ps.setInt(5, Integer.parseInt(txtMay.getText().trim())); ps.setString(6, cboTrangThai.getValue());
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException | NumberFormatException ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage()); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void handleDelete() {
        KhuVucModel selected = tblAreaList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM KhuVuc WHERE MaKV = ?";
            try (Connection conn = ketnoicsdl.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaKV()); ps.executeUpdate(); loadDataFromDatabase();
            } catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa do ràng buộc!"); }
        }
    }

    private void handleUpdate() {
        KhuVucModel selected = tblAreaList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        // Logic tương tự Insert, nhưng dùng lệnh UPDATE...
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblSoKV == null) return;
        lblSoKV.setText(String.valueOf(danhSach.size()));
        lblTongMay.setText(String.valueOf(danhSach.stream().mapToInt(kv -> Integer.parseInt(kv.getSoLuongMay())).sum()));
        lblKVHoatDong.setText(String.valueOf(danhSach.stream().filter(kv -> kv.getTrangThai().equalsIgnoreCase("HOATDONG")).count()));
    }

    private Button createButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        return btn;
    }

    private void styleField(TextField tf) { tf.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 5 8;"); }

    private void styleDialogButtons(DialogPane dp, ButtonType ok) {
        dp.lookupButton(ok).setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg); alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}