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

public class sanpham {

    // ===================== MODEL =====================
    public static class SanPhamModel {
        private final StringProperty maSP;
        private final StringProperty tenSP;
        private final StringProperty loaiSP;
        private final StringProperty dvt;
        private final StringProperty soDiem;
        private final StringProperty donGia;
        private final StringProperty soLuongTK;
        private final StringProperty url;

        public SanPhamModel(String maSP, String tenSP, String loaiSP, String dvt, 
                            String soDiem, String donGia, String soLuongTK, String url) {
            this.maSP = new SimpleStringProperty(maSP != null ? maSP : "");
            this.tenSP = new SimpleStringProperty(tenSP != null ? tenSP : "");
            this.loaiSP = new SimpleStringProperty(loaiSP != null ? loaiSP : "");
            this.dvt = new SimpleStringProperty(dvt != null ? dvt : "");
            this.soDiem = new SimpleStringProperty(soDiem != null ? soDiem : "0");
            this.donGia = new SimpleStringProperty(donGia != null ? donGia : "0");
            this.soLuongTK = new SimpleStringProperty(soLuongTK != null ? soLuongTK : "0");
            this.url = new SimpleStringProperty(url != null ? url : "");
        }

        public String getMaSP() { return maSP.get(); }
        public String getTenSP() { return tenSP.get(); }
        public String getLoaiSP() { return loaiSP.get(); }
        public String getDvt() { return dvt.get(); }
        public String getSoDiem() { return soDiem.get(); }
        public String getDonGia() { return donGia.get(); }
        public String getSoLuongTK() { return soLuongTK.get(); }
        public String getUrl() { return url.get(); }

        public StringProperty maSPProperty() { return maSP; }
        public StringProperty tenSPProperty() { return tenSP; }
        public StringProperty loaiSPProperty() { return loaiSP; }
        public StringProperty dvtProperty() { return dvt; }
        public StringProperty soDiemProperty() { return soDiem; }
        public StringProperty donGiaProperty() { return donGia; }
        public StringProperty soLuongTKProperty() { return soLuongTK; }
        public StringProperty urlProperty() { return url; }
    }

    // ===================== DATA =====================
    private final ObservableList<SanPhamModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<SanPhamModel> filteredList;
    private TableView<SanPhamModel> tblProductList;

    private Label lblSoSP;
    private Label lblTonKho;
    private Label lblGiaTB;

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
        String sql = "SELECT MaSP, TenSP, LoaiSP, DVT, SoDiemTichLuy, DonGiaBQ, SoLuongTK, Url FROM SanPham ORDER BY MaSP ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("MaSP");
                String ten = rs.getString("TenSP");
                String loai = rs.getString("LoaiSP");
                String dvt = rs.getString("DVT");
                String diem = String.valueOf(rs.getInt("SoDiemTichLuy"));
                String gia = String.valueOf(rs.getDouble("DonGiaBQ"));
                String sl = String.valueOf(rs.getInt("SoLuongTK"));
                String url = rs.getString("Url");

                if (gia.endsWith(".0")) gia = gia.replace(".0", "");

                danhSach.add(new SanPhamModel(ma, ten, loai, dvt, diem, gia, sl, url));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    private String generateNewMaSP(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaSP FROM SanPham WHERE MaSP LIKE 'SP%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaSP");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("SP%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblSoSP = new Label("0");
        lblTonKho = new Label("0");
        lblGiaTB = new Label("0");

        StackPane cardSoSP = buildCard("Số SP", lblSoSP, "#7986CB", "#3F51B5", "🧾");
        StackPane cardTonKho = buildCard("Tồn kho", lblTonKho, "#BA68C8", "#8E24AA", "📦");
        StackPane cardGiaTB = buildCard("Giá TB", lblGiaTB, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardSoSP, Priority.ALWAYS);
        HBox.setHgrow(cardTonKho, Priority.ALWAYS);
        HBox.setHgrow(cardGiaTB, Priority.ALWAYS);

        hbox.getChildren().addAll(cardSoSP, cardTonKho, cardGiaTB);
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

        tblProductList = buildTable();
        VBox.setVgrow(tblProductList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblProductList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Sản Phẩm");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(sp -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return sp.getMaSP().toLowerCase().contains(filter) ||
                       sp.getTenSP().toLowerCase().contains(filter) ||
                       sp.getLoaiSP().toLowerCase().contains(filter);
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
    private TableView<SanPhamModel> buildTable() {
        TableView<SanPhamModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<SanPhamModel, String> colMa = new TableColumn<>("Mã SP"); colMa.setCellValueFactory(new PropertyValueFactory<>("maSP"));
        colMa.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");
        TableColumn<SanPhamModel, String> colTen = new TableColumn<>("Tên SP"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenSP"));
        TableColumn<SanPhamModel, String> colLoai = new TableColumn<>("Loại SP"); colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiSP"));
        TableColumn<SanPhamModel, String> colDVT = new TableColumn<>("ĐVT"); colDVT.setCellValueFactory(new PropertyValueFactory<>("dvt"));
        TableColumn<SanPhamModel, String> colDiem = new TableColumn<>("Số Điểm"); colDiem.setCellValueFactory(new PropertyValueFactory<>("soDiem"));
        TableColumn<SanPhamModel, String> colGia = new TableColumn<>("Đơn Giá"); colGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        TableColumn<SanPhamModel, String> colTonKho = new TableColumn<>("SL Tồn Kho"); colTonKho.setCellValueFactory(new PropertyValueFactory<>("soLuongTK"));
        TableColumn<SanPhamModel, String> colUrl = new TableColumn<>("URL"); colUrl.setCellValueFactory(new PropertyValueFactory<>("url"));

        table.getColumns().addAll(colMa, colTen, colLoai, colDVT, colDiem, colGia, colTonKho, colUrl);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<SanPhamModel> row = new TableRow<>();
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
        TextField txtDvt = new TextField(); styleField(txtDvt);
        TextField txtLoai = new TextField(); styleField(txtLoai);
        TextField txtSl = new TextField("0"); styleField(txtSl);
        TextField txtDiem = new TextField("0"); styleField(txtDiem);
        TextField txtGia = new TextField("0.0"); styleField(txtGia);
        TextField txtUrl = new TextField("/resources/"); styleField(txtUrl);

        grid.add(new Label("TenSP:"), 0, 0); grid.add(txtTen, 1, 0);
        grid.add(new Label("Dvt:"), 0, 1); grid.add(txtDvt, 1, 1);
        grid.add(new Label("LoaiSP:"), 0, 2); grid.add(txtLoai, 1, 2);
        grid.add(new Label("SoLuongTK:"), 0, 3); grid.add(txtSl, 1, 3);
        grid.add(new Label("SoDiemTichLuy:"), 0, 4); grid.add(txtDiem, 1, 4);
        grid.add(new Label("DonGiaBQ:"), 0, 5); grid.add(txtGia, 1, 5);
        grid.add(new Label("Url:"), 0, 6); grid.add(txtUrl, 1, 6);

        GridPane.setHgrow(txtTen, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                if (txtTen.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên sản phẩm!");
                    return null;
                }

                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaSP(conn);
                    String sql = "INSERT INTO SanPham (MaSP, TenSP, LoaiSP, DVT, SoDiemTichLuy, DonGiaBQ, SoLuongTK, Url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtTen.getText().trim());
                        ps.setString(3, txtLoai.getText().trim());
                        ps.setString(4, txtDvt.getText().trim());
                        ps.setInt(5, Integer.parseInt(txtDiem.getText().trim().isEmpty() ? "0" : txtDiem.getText().trim()));
                        ps.setDouble(6, Double.parseDouble(txtGia.getText().trim().isEmpty() ? "0" : txtGia.getText().trim()));
                        ps.setInt(7, Integer.parseInt(txtSl.getText().trim().isEmpty() ? "0" : txtSl.getText().trim()));
                        ps.setString(8, txtUrl.getText().trim());
                        
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
        SanPhamModel selected = tblProductList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một sản phẩm để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM SanPham WHERE MaSP = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaSP());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Bản ghi này đang được sử dụng ở bảng khác, không thể xóa!\n" + ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        SanPhamModel selected = tblProductList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một sản phẩm để cập nhật!");
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

        TextField txtMa = new TextField(selected.getMaSP());
        txtMa.setEditable(false);
        txtMa.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4;");

        TextField txtTen = new TextField(selected.getTenSP()); styleField(txtTen);
        TextField txtDvt = new TextField(selected.getDvt()); styleField(txtDvt);
        TextField txtLoai = new TextField(selected.getLoaiSP()); styleField(txtLoai);
        TextField txtSl = new TextField(selected.getSoLuongTK()); styleField(txtSl);
        TextField txtDiem = new TextField(selected.getSoDiem()); styleField(txtDiem);
        TextField txtGia = new TextField(selected.getDonGia()); styleField(txtGia);
        TextField txtUrl = new TextField(selected.getUrl()); styleField(txtUrl);

        grid.add(new Label("MaSP:"), 0, 0); grid.add(txtMa, 1, 0);
        grid.add(new Label("TenSP:"), 0, 1); grid.add(txtTen, 1, 1);
        grid.add(new Label("Dvt:"), 0, 2); grid.add(txtDvt, 1, 2);
        grid.add(new Label("LoaiSP:"), 0, 3); grid.add(txtLoai, 1, 3);
        grid.add(new Label("SoLuongTK:"), 0, 4); grid.add(txtSl, 1, 4);
        grid.add(new Label("SoDiemTichLuy:"), 0, 5); grid.add(txtDiem, 1, 5);
        grid.add(new Label("DonGiaBQ:"), 0, 6); grid.add(txtGia, 1, 6);
        grid.add(new Label("Url:"), 0, 7); grid.add(txtUrl, 1, 7);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String sql = "UPDATE SanPham SET TenSP=?, LoaiSP=?, DVT=?, SoDiemTichLuy=?, DonGiaBQ=?, SoLuongTK=?, Url=? WHERE MaSP=?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, txtTen.getText().trim());
                    ps.setString(2, txtLoai.getText().trim());
                    ps.setString(3, txtDvt.getText().trim());
                    ps.setInt(4, Integer.parseInt(txtDiem.getText().trim().isEmpty() ? "0" : txtDiem.getText().trim()));
                    ps.setDouble(5, Double.parseDouble(txtGia.getText().trim().isEmpty() ? "0" : txtGia.getText().trim()));
                    ps.setInt(6, Integer.parseInt(txtSl.getText().trim().isEmpty() ? "0" : txtSl.getText().trim()));
                    ps.setString(7, txtUrl.getText().trim());
                    ps.setString(8, selected.getMaSP());
                    
                    ps.executeUpdate();
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
        if (lblSoSP == null) return;
        lblSoSP.setText(String.valueOf(danhSach.size()));

        int tongTon = danhSach.stream().mapToInt(sp -> {
            try { return Integer.parseInt(sp.getSoLuongTK()); } catch (Exception e) { return 0; }
        }).sum();
        lblTonKho.setText(String.valueOf(tongTon));

        double tongGia = danhSach.stream().mapToDouble(sp -> {
            try { return Double.parseDouble(sp.getDonGia()); } catch (Exception e) { return 0; }
        }).sum();
        
        double giaTB = danhSach.isEmpty() ? 0 : tongGia / danhSach.size();
        lblGiaTB.setText(String.format("%.0f", giaTB));
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