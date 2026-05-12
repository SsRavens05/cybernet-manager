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

public class nhacungcap {

    // ===================== MODEL =====================
    public static class NhaCungCapModel {
        private final StringProperty maNCC;
        private final StringProperty tenNCC;
        private final StringProperty sdt;
        private final StringProperty email;
        private final StringProperty website;
        private final StringProperty diaChi;
        private final StringProperty sanPhamCungCap;

        public NhaCungCapModel(String maNCC, String tenNCC, String sdt, String email, 
                               String website, String diaChi, String sanPhamCungCap) {
            this.maNCC = new SimpleStringProperty(maNCC != null ? maNCC : "");
            this.tenNCC = new SimpleStringProperty(tenNCC != null ? tenNCC : "");
            this.sdt = new SimpleStringProperty(sdt != null ? sdt : "");
            this.email = new SimpleStringProperty(email != null ? email : "");
            this.website = new SimpleStringProperty(website != null ? website : "");
            this.diaChi = new SimpleStringProperty(diaChi != null ? diaChi : "");
            this.sanPhamCungCap = new SimpleStringProperty(sanPhamCungCap != null ? sanPhamCungCap : "");
        }

        public String getMaNCC() { return maNCC.get(); }
        public String getTenNCC() { return tenNCC.get(); }
        public String getSdt() { return sdt.get(); }
        public String getEmail() { return email.get(); }
        public String getWebsite() { return website.get(); }
        public String getDiaChi() { return diaChi.get(); }
        public String getSanPhamCungCap() { return sanPhamCungCap.get(); }

        public StringProperty maNCCProperty() { return maNCC; }
        public StringProperty tenNCCProperty() { return tenNCC; }
        public StringProperty sdtProperty() { return sdt; }
        public StringProperty emailProperty() { return email; }
        public StringProperty websiteProperty() { return website; }
        public StringProperty diaChiProperty() { return diaChi; }
        public StringProperty sanPhamCungCapProperty() { return sanPhamCungCap; }
    }

    // ===================== DATA =====================
    private final ObservableList<NhaCungCapModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<NhaCungCapModel> filteredList;
    private TableView<NhaCungCapModel> tblSupplierList;

    private Label lblTongNCC;
    private Label lblNhaCungCapSP;

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
        String sql = "SELECT MaNCC, TenNCC, SDT, Email, Website, DiaChi, SanPhamCungCap FROM NhaCungCap ORDER BY MaNCC ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(new NhaCungCapModel(
                    rs.getString("MaNCC"), rs.getString("TenNCC"), rs.getString("SDT"),
                    rs.getString("Email"), rs.getString("Website"), rs.getString("DiaChi"),
                    rs.getString("SanPhamCungCap")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    private String generateNewMaNCC(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaNCC FROM NhaCungCap WHERE MaNCC LIKE 'NCC%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaNCC");
                if (ma != null && ma.length() > 3) {
                    try {
                        int num = Integer.parseInt(ma.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("NCC%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblTongNCC = new Label("0");
        lblNhaCungCapSP = new Label("0");

        StackPane cardTong = buildCard("Tổng NCC", lblTongNCC, "#7986CB", "#3F51B5", "🚚");
        StackPane cardSP = buildCard("NCC Có SP", lblNhaCungCapSP, "#BA68C8", "#8E24AA", "📦");

        HBox.setHgrow(cardTong, Priority.ALWAYS);
        HBox.setHgrow(cardSP, Priority.ALWAYS);

        hbox.getChildren().addAll(cardTong, cardSP);
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

        tblSupplierList = buildTable();
        VBox.setVgrow(tblSupplierList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblSupplierList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Nhà Cung Cấp");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(ncc -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return ncc.getMaNCC().toLowerCase().contains(filter) ||
                       ncc.getTenNCC().toLowerCase().contains(filter) ||
                       ncc.getSanPhamCungCap().toLowerCase().contains(filter);
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
    private TableView<NhaCungCapModel> buildTable() {
        TableView<NhaCungCapModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<NhaCungCapModel, String> colMa = new TableColumn<>("Mã NCC"); colMa.setCellValueFactory(new PropertyValueFactory<>("maNCC"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        
        TableColumn<NhaCungCapModel, String> colTen = new TableColumn<>("Tên NCC"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenNCC"));
        TableColumn<NhaCungCapModel, String> colSDT = new TableColumn<>("SĐT"); colSDT.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        TableColumn<NhaCungCapModel, String> colEmail = new TableColumn<>("Email"); colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<NhaCungCapModel, String> colWeb = new TableColumn<>("Website"); colWeb.setCellValueFactory(new PropertyValueFactory<>("website"));
        TableColumn<NhaCungCapModel, String> colDiaChi = new TableColumn<>("Địa Chỉ"); colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        
        // Thêm cột Sản phẩm cung cấp vào bảng
        TableColumn<NhaCungCapModel, String> colSP = new TableColumn<>("Sản Phẩm Cung Cấp"); 
        colSP.setCellValueFactory(new PropertyValueFactory<>("sanPhamCungCap"));
        colSP.setPrefWidth(200);

        table.getColumns().addAll(colMa, colTen, colSDT, colEmail, colWeb, colDiaChi, colSP);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<NhaCungCapModel> row = new TableRow<>();
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
        dp.setPrefWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtTen = new TextField(); styleField(txtTen);
        TextField txtSdt = new TextField(); styleField(txtSdt);
        TextField txtEmail = new TextField(); styleField(txtEmail);
        TextField txtWeb = new TextField(); styleField(txtWeb);
        TextField txtDc = new TextField(); styleField(txtDc);
        TextField txtSp = new TextField(); styleField(txtSp); txtSp.setPromptText("VD: Snack, Mì tôm...");

        grid.add(new Label("Tên NCC:"), 0, 0); grid.add(txtTen, 1, 0);
        grid.add(new Label("SĐT:"), 0, 1); grid.add(txtSdt, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(txtEmail, 1, 2);
        grid.add(new Label("Website:"), 0, 3); grid.add(txtWeb, 1, 3);
        grid.add(new Label("Địa chỉ:"), 0, 4); grid.add(txtDc, 1, 4);
        grid.add(new Label("SP Cung Cấp:"), 0, 5); grid.add(txtSp, 1, 5);

        GridPane.setHgrow(txtTen, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTen.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên Nhà Cung Cấp!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaNCC(conn);
                    
                    String sql = "INSERT INTO NhaCungCap (MaNCC, TenNCC, SDT, Email, Website, DiaChi, SanPhamCungCap) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTen.getText().trim());
                        ps.setString(3, txtSdt.getText().trim());
                        ps.setString(4, txtEmail.getText().trim());
                        ps.setString(5, txtWeb.getText().trim());
                        ps.setString(6, txtDc.getText().trim());
                        ps.setString(7, txtSp.getText().trim()); // Lưu sản phẩm cung cấp
                        
                        ps.executeUpdate();
                    }
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu / CSDL", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== DELETE CONFIRMATION =====================
    private void handleDelete() {
        NhaCungCapModel selected = tblSupplierList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một nhà cung cấp để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM NhaCungCap WHERE MaNCC = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaNCC());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Bản ghi này đang được sử dụng, không thể xóa!\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        NhaCungCapModel selected = tblSupplierList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một nhà cung cấp để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Information");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtMaNCC = new TextField(selected.getMaNCC());
        txtMaNCC.setEditable(false);
        txtMaNCC.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTen = new TextField(selected.getTenNCC()); styleField(txtTen);
        TextField txtSdt = new TextField(selected.getSdt()); styleField(txtSdt);
        TextField txtEmail = new TextField(selected.getEmail()); styleField(txtEmail);
        TextField txtWeb = new TextField(selected.getWebsite()); styleField(txtWeb);
        TextField txtDc = new TextField(selected.getDiaChi()); styleField(txtDc);
        TextField txtSp = new TextField(selected.getSanPhamCungCap()); styleField(txtSp);

        grid.add(new Label("Mã NCC:"), 0, 0); grid.add(txtMaNCC, 1, 0);
        grid.add(new Label("Tên NCC:"), 0, 1); grid.add(txtTen, 1, 1);
        grid.add(new Label("SĐT:"), 0, 2); grid.add(txtSdt, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(txtEmail, 1, 3);
        grid.add(new Label("Website:"), 0, 4); grid.add(txtWeb, 1, 4);
        grid.add(new Label("Địa chỉ:"), 0, 5); grid.add(txtDc, 1, 5);
        grid.add(new Label("SP Cung Cấp:"), 0, 6); grid.add(txtSp, 1, 6);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String sql = "UPDATE NhaCungCap SET TenNCC=?, SDT=?, Email=?, Website=?, DiaChi=?, SanPhamCungCap=? WHERE MaNCC=?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, txtTen.getText().trim());
                    ps.setString(2, txtSdt.getText().trim());
                    ps.setString(3, txtEmail.getText().trim());
                    ps.setString(4, txtWeb.getText().trim());
                    ps.setString(5, txtDc.getText().trim());
                    ps.setString(6, txtSp.getText().trim());
                    ps.setString(7, selected.getMaNCC());
                    
                    ps.executeUpdate();
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ===================== HELPERS =====================
    private void refreshCards() {
        if (lblTongNCC == null) return;
        
        lblTongNCC.setText(String.valueOf(danhSach.size()));

        long nccCoSP = danhSach.stream()
            .map(NhaCungCapModel::getSanPhamCungCap)
            .filter(sp -> sp != null && !sp.trim().isEmpty())
            .count();
        lblNhaCungCapSP.setText(String.valueOf(nccCoSP));
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