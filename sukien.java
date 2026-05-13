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

public class sukien {

    // ===================== MODEL =====================
    public static class SuKienModel {
        private final StringProperty maSK;
        private final StringProperty tenSK;
        private final StringProperty maNV;
        private final StringProperty maKV;
        private final StringProperty noiDung;
        private final StringProperty ngayBD;
        private final StringProperty ngayKT;
        private final StringProperty ngayTao;

        public SuKienModel(String maSK, String tenSK, String maNV, String maKV, 
                           String noiDung, String ngayBD, String ngayKT, String ngayTao) {
            this.maSK = new SimpleStringProperty(maSK != null ? maSK : "");
            this.tenSK = new SimpleStringProperty(tenSK != null ? tenSK : "");
            this.maNV = new SimpleStringProperty(maNV != null ? maNV : "");
            this.maKV = new SimpleStringProperty(maKV != null ? maKV : "");
            this.noiDung = new SimpleStringProperty(noiDung != null ? noiDung : "");
            this.ngayBD = new SimpleStringProperty(ngayBD != null ? ngayBD : "");
            this.ngayKT = new SimpleStringProperty(ngayKT != null ? ngayKT : "");
            this.ngayTao = new SimpleStringProperty(ngayTao != null ? ngayTao : "");
        }

        public String getMaSK() { return maSK.get(); }
        public String getTenSK() { return tenSK.get(); }
        public String getMaNV() { return maNV.get(); }
        public String getMaKV() { return maKV.get(); }
        public String getNoiDung() { return noiDung.get(); }
        public String getNgayBD() { return ngayBD.get(); }
        public String getNgayKT() { return ngayKT.get(); }
        public String getNgayTao() { return ngayTao.get(); }

        public StringProperty maSKProperty() { return maSK; }
        public StringProperty tenSKProperty() { return tenSK; }
        public StringProperty maNVProperty() { return maNV; }
        public StringProperty maKVProperty() { return maKV; }
        public StringProperty noiDungProperty() { return noiDung; }
        public StringProperty ngayBDProperty() { return ngayBD; }
        public StringProperty ngayKTProperty() { return ngayKT; }
        public StringProperty ngayTaoProperty() { return ngayTao; }
    }

    // ===================== DATA =====================
    private final ObservableList<SuKienModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<SuKienModel> filteredList;
    private TableView<SuKienModel> tblEventList;

    private Label lblSoSK;
    private Label lblSoKV;
    private Label lblSoNV;

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
        String sql = "SELECT MaSK, TenSK, MaNV, MaKV, NoiDung, NgayBD, NgayKT, NgayTao FROM SuKien ORDER BY MaSK ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(new SuKienModel(
                    rs.getString("MaSK"), rs.getString("TenSK"), rs.getString("MaNV"), 
                    rs.getString("MaKV"), rs.getString("NoiDung"), rs.getString("NgayBD"), 
                    rs.getString("NgayKT"), rs.getString("NgayTao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    private String generateNewMaSK(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaSK FROM SuKien WHERE MaSK LIKE 'SK%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaSK");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("SK%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoSK = new Label("0");
        lblSoKV = new Label("0");
        lblSoNV = new Label("0");

        StackPane cardSoSK = buildCard("Sự kiện", lblSoSK, "#7986CB", "#3F51B5", "🧾");
        StackPane cardSoKV = buildCard("Khu vực", lblSoKV, "#BA68C8", "#8E24AA", "💰");
        StackPane cardSoNV = buildCard("Nhân viên", lblSoNV, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoSK, Priority.ALWAYS);
        HBox.setHgrow(cardSoKV, Priority.ALWAYS);
        HBox.setHgrow(cardSoNV, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoSK, cardSoKV, cardSoNV);
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

        tblEventList = buildTable();
        VBox.setVgrow(tblEventList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblEventList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Sự Kiện");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(sk -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return sk.getMaSK().toLowerCase().contains(filter) ||
                       sk.getTenSK().toLowerCase().contains(filter) ||
                       sk.getMaKV().toLowerCase().contains(filter) ||
                       sk.getMaNV().toLowerCase().contains(filter);
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
    private TableView<SuKienModel> buildTable() {
        TableView<SuKienModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<SuKienModel, String> colMa = new TableColumn<>("Mã SK"); colMa.setCellValueFactory(new PropertyValueFactory<>("maSK"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        TableColumn<SuKienModel, String> colTen = new TableColumn<>("Tên SK"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenSK"));
        TableColumn<SuKienModel, String> colNV = new TableColumn<>("Mã NV"); colNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        TableColumn<SuKienModel, String> colKV = new TableColumn<>("Mã KV"); colKV.setCellValueFactory(new PropertyValueFactory<>("maKV"));
        TableColumn<SuKienModel, String> colND = new TableColumn<>("Nội Dung"); colND.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        TableColumn<SuKienModel, String> colNgayBD = new TableColumn<>("Ngày BĐ"); colNgayBD.setCellValueFactory(new PropertyValueFactory<>("ngayBD"));
        TableColumn<SuKienModel, String> colNgayKT = new TableColumn<>("Ngày KT"); colNgayKT.setCellValueFactory(new PropertyValueFactory<>("ngayKT"));
        TableColumn<SuKienModel, String> colNgayTao = new TableColumn<>("Ngày Tạo"); colNgayTao.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));

        table.getColumns().addAll(colMa, colTen, colNV, colKV, colND, colNgayBD, colNgayKT, colNgayTao);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<SuKienModel> row = new TableRow<>();
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

        TextField txtTen = new TextField(); styleField(txtTen);
        DatePicker dtpBD = new DatePicker(LocalDate.now()); dtpBD.setMaxWidth(Double.MAX_VALUE);
        DatePicker dtpKT = new DatePicker(LocalDate.now().plusDays(1)); dtpKT.setMaxWidth(Double.MAX_VALUE);
        TextField txtMaKV = new TextField(); styleField(txtMaKV); txtMaKV.setPromptText("VD: KV001");
        TextField txtNoiDung = new TextField(); styleField(txtNoiDung);
        TextField txtMaNV = new TextField(); styleField(txtMaNV); txtMaNV.setPromptText("VD: NV001");

        grid.add(new Label("Tên SK:"), 0, 0); grid.add(txtTen, 1, 0);
        grid.add(new Label("Tgbd:"), 0, 1); grid.add(dtpBD, 1, 1);
        grid.add(new Label("Tgkt:"), 0, 2); grid.add(dtpKT, 1, 2);
        grid.add(new Label("Makv:"), 0, 3); grid.add(txtMaKV, 1, 3);
        grid.add(new Label("Noidung:"), 0, 4); grid.add(txtNoiDung, 1, 4);
        grid.add(new Label("Manv:"), 0, 5); grid.add(txtMaNV, 1, 5);

        GridPane.setHgrow(txtTen, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTen.getText().isEmpty() || txtMaNV.getText().isEmpty() || txtMaKV.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên SK, Mã NV và Mã KV!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaSK(conn);
                    String ngayBD = dtpBD.getValue() != null ? dtpBD.getValue().toString() : "";
                    String ngayKT = dtpKT.getValue() != null ? dtpKT.getValue().toString() : "";
                    String ngayTao = LocalDate.now().toString(); // Ngày tạo tự động lấy ngày hiện tại
                    
                    String sql = "INSERT INTO SuKien (MaSK, TenSK, MaNV, MaKV, NoiDung, NgayBD, NgayKT, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTen.getText().trim());
                        ps.setString(3, txtMaNV.getText().trim());
                        ps.setString(4, txtMaKV.getText().trim());
                        ps.setString(5, txtNoiDung.getText().trim());
                        
                        if(ngayBD.isEmpty()) ps.setNull(6, java.sql.Types.DATE); else ps.setString(6, ngayBD);
                        if(ngayKT.isEmpty()) ps.setNull(7, java.sql.Types.DATE); else ps.setString(7, ngayKT);
                        ps.setString(8, ngayTao);
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Khóa Ngoại / Nhập liệu", "Vui lòng kiểm tra lại Mã NV hoặc Mã KV có tồn tại không.\n" + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        SuKienModel selected = tblEventList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một sự kiện để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM SuKien WHERE MaSK = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaSK());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        SuKienModel selected = tblEventList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một sự kiện để cập nhật!");
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

        TextField txtMaSK = new TextField(selected.getMaSK());
        txtMaSK.setEditable(false);
        txtMaSK.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTen = new TextField(selected.getTenSK()); styleField(txtTen);
        
        DatePicker dtpBD = new DatePicker(); dtpBD.setMaxWidth(Double.MAX_VALUE);
        try { if (!selected.getNgayBD().isEmpty()) dtpBD.setValue(LocalDate.parse(selected.getNgayBD())); } catch (Exception e) {}
        
        DatePicker dtpKT = new DatePicker(); dtpKT.setMaxWidth(Double.MAX_VALUE);
        try { if (!selected.getNgayKT().isEmpty()) dtpKT.setValue(LocalDate.parse(selected.getNgayKT())); } catch (Exception e) {}

        TextField txtMaKV = new TextField(selected.getMaKV()); styleField(txtMaKV);
        TextField txtNoiDung = new TextField(selected.getNoiDung()); styleField(txtNoiDung);
        TextField txtMaNV = new TextField(selected.getMaNV()); styleField(txtMaNV);
        
        TextField txtCreateAt = new TextField(selected.getNgayTao());
        txtCreateAt.setEditable(false); // Ngày tạo không nên sửa
        txtCreateAt.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        grid.add(new Label("Mask:"), 0, 0); grid.add(txtMaSK, 1, 0);
        grid.add(new Label("Tensk:"), 0, 1); grid.add(txtTen, 1, 1);
        grid.add(new Label("Tgbd:"), 0, 2); grid.add(dtpBD, 1, 2);
        grid.add(new Label("Tgkt:"), 0, 3); grid.add(dtpKT, 1, 3);
        grid.add(new Label("Makv:"), 0, 4); grid.add(txtMaKV, 1, 4);
        grid.add(new Label("Noidung:"), 0, 5); grid.add(txtNoiDung, 1, 5);
        grid.add(new Label("Manv:"), 0, 6); grid.add(txtMaNV, 1, 6);
        grid.add(new Label("Createat:"), 0, 7); grid.add(txtCreateAt, 1, 7);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String sql = "UPDATE SuKien SET TenSK=?, MaNV=?, MaKV=?, NoiDung=?, NgayBD=?, NgayKT=? WHERE MaSK=?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    String ngayBD = dtpBD.getValue() != null ? dtpBD.getValue().toString() : "";
                    String ngayKT = dtpKT.getValue() != null ? dtpKT.getValue().toString() : "";
                    
                    ps.setString(1, txtTen.getText().trim());
                    ps.setString(2, txtMaNV.getText().trim());
                    ps.setString(3, txtMaKV.getText().trim());
                    ps.setString(4, txtNoiDung.getText().trim());
                    
                    if(ngayBD.isEmpty()) ps.setNull(5, java.sql.Types.DATE); else ps.setString(5, ngayBD);
                    if(ngayKT.isEmpty()) ps.setNull(6, java.sql.Types.DATE); else ps.setString(6, ngayKT);
                    
                    ps.setString(7, selected.getMaSK());
                    
                    ps.executeUpdate();
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Khóa Ngoại / Cập nhật", "Vui lòng kiểm tra Mã NV/Mã KV.\n" + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblSoSK == null) return;
        lblSoSK.setText(String.valueOf(danhSach.size()));

        long tongKV = danhSach.stream()
            .map(SuKienModel::getMaKV)
            .filter(kv -> kv != null && !kv.isEmpty())
            .distinct()
            .count();
        lblSoKV.setText(String.valueOf(tongKV));

        long tongNV = danhSach.stream()
            .map(SuKienModel::getMaNV)
            .filter(nv -> nv != null && !nv.isEmpty())
            .distinct()
            .count();
        lblSoNV.setText(String.valueOf(tongNV));
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
