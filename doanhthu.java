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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class doanhthu {

    // ===================== MODEL =====================
    public static class DoanhThuModel {
        private final StringProperty maDoanhThu;
        private final StringProperty noiDung;
        private final StringProperty ngay;
        private final StringProperty tongDoanhThu;
        private final StringProperty tongDoanhThuDV;
        private final StringProperty tongSoDichVu;
        private final StringProperty tongSoGioChoi;
        private final StringProperty loai;

        public DoanhThuModel(String maDoanhThu, String noiDung, String ngay, String tongDoanhThu, 
                             String tongDoanhThuDV, String tongSoDichVu, String tongSoGioChoi, String loai) {
            this.maDoanhThu = new SimpleStringProperty(maDoanhThu != null ? maDoanhThu : "");
            this.noiDung = new SimpleStringProperty(noiDung != null ? noiDung : "");
            this.ngay = new SimpleStringProperty(ngay != null ? ngay : "");
            this.tongDoanhThu = new SimpleStringProperty(tongDoanhThu != null ? tongDoanhThu : "0");
            this.tongDoanhThuDV = new SimpleStringProperty(tongDoanhThuDV != null ? tongDoanhThuDV : "0");
            this.tongSoDichVu = new SimpleStringProperty(tongSoDichVu != null ? tongSoDichVu : "0");
            this.tongSoGioChoi = new SimpleStringProperty(tongSoGioChoi != null ? tongSoGioChoi : "0");
            this.loai = new SimpleStringProperty(loai != null ? loai : "DAILY");
        }

        public String getMaDoanhThu() { return maDoanhThu.get(); }
        public String getNoiDung() { return noiDung.get(); }
        public String getNgay() { return ngay.get(); }
        public String getTongDoanhThu() { return tongDoanhThu.get(); }
        public String getTongDoanhThuDV() { return tongDoanhThuDV.get(); }
        public String getTongSoDichVu() { return tongSoDichVu.get(); }
        public String getTongSoGioChoi() { return tongSoGioChoi.get(); }
        public String getLoai() { return loai.get(); }

        public StringProperty maDoanhThuProperty() { return maDoanhThu; }
        public StringProperty noiDungProperty() { return noiDung; }
        public StringProperty ngayProperty() { return ngay; }
        public StringProperty tongDoanhThuProperty() { return tongDoanhThu; }
        public StringProperty tongDoanhThuDVProperty() { return tongDoanhThuDV; }
        public StringProperty tongSoDichVuProperty() { return tongSoDichVu; }
        public StringProperty tongSoGioChoiProperty() { return tongSoGioChoi; }
    }

    // ===================== DATA =====================
    private final ObservableList<DoanhThuModel> danhSach = FXCollections.observableArrayList();
    private FilteredList<DoanhThuModel> filteredList;
    private TableView<DoanhThuModel> tblRevenueList;

    private Label lblBanGhi;
    private Label lblTongDT;
    private Label lblTBDT;

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
        String sql = "SELECT MaDoanhThu, NoiDung, Ngay, TongDoanhThu, TongDoanhThuDV, TongSoDichVu, TongSoGioChoi, Loai FROM DoanhThu ORDER BY Ngay ASC";

        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("MaDoanhThu");
                String nd = rs.getString("NoiDung");
                String ngay = rs.getString("Ngay");
                String tdt = String.valueOf(rs.getDouble("TongDoanhThu"));
                String tdtdv = String.valueOf(rs.getDouble("TongDoanhThuDV"));
                String tsdv = String.valueOf(rs.getDouble("TongSoDichVu"));
                String tgc = String.valueOf(rs.getDouble("TongSoGioChoi"));
                String loai = rs.getString("Loai");

                if (ngay != null && ngay.contains(".")) ngay = ngay.substring(0, ngay.indexOf("."));
                
                danhSach.add(new DoanhThuModel(ma, nd, ngay, tdt, tdtdv, tsdv, tgc, loai));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + e.getMessage());
        }
        refreshCards();
    }

    private String generateNewMaDT(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaDoanhThu FROM DoanhThu WHERE MaDoanhThu LIKE 'DT%'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaDoanhThu");
                if (ma != null && ma.length() > 2) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return String.format("DT%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblBanGhi = new Label("0");
        lblTongDT = new Label("0");
        lblTBDT = new Label("0");

        StackPane cardBanGhi = buildCard("Bản ghi", lblBanGhi, "#7986CB", "#3F51B5", "🧾");
        StackPane cardTongDT = buildCard("Tổng DT", lblTongDT, "#BA68C8", "#8E24AA", "💰");
        StackPane cardTBDT = buildCard("TB DT", lblTBDT, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardBanGhi, Priority.ALWAYS);
        HBox.setHgrow(cardTongDT, Priority.ALWAYS);
        HBox.setHgrow(cardTBDT, Priority.ALWAYS);

        hbox.getChildren().addAll(cardBanGhi, cardTongDT, cardTBDT);
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

        valueLbl.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");
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

        tblRevenueList = buildTable();
        VBox.setVgrow(tblRevenueList, Priority.ALWAYS);
        panel.getChildren().addAll(toolbar, tblRevenueList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Doanh Thu");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnExport = createButton("📤 Export", "#F57C00", WHITE);
        Button btnInsert = createButton("+ Insert", "#2E7D32", WHITE);
        Button btnDelete = createButton("🗑 Delete", "#C62828", WHITE);
        Button btnUpdate = createButton("✏ Update", "#1565C0", WHITE);

        btnExport.setOnAction(e -> handleExport());
        btnInsert.setOnAction(e -> showInsertDialog());
        btnDelete.setOnAction(e -> handleDelete());
        btnUpdate.setOnAction(e -> handleUpdate());

        hbox.getChildren().addAll(title, spacer, btnExport, btnInsert, btnDelete, btnUpdate);
        return hbox;
    }

    @SuppressWarnings("unchecked")
    private TableView<DoanhThuModel> buildTable() {
        TableView<DoanhThuModel> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<DoanhThuModel, String> colMa = new TableColumn<>("Mã Doanh Thu"); colMa.setCellValueFactory(new PropertyValueFactory<>("maDoanhThu"));
        TableColumn<DoanhThuModel, String> colND = new TableColumn<>("Nội Dung"); colND.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        TableColumn<DoanhThuModel, String> colNgay = new TableColumn<>("Ngày"); colNgay.setCellValueFactory(new PropertyValueFactory<>("ngay"));
        TableColumn<DoanhThuModel, String> colTDT = new TableColumn<>("Tổng Doanh Thu"); colTDT.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
        TableColumn<DoanhThuModel, String> colTDTDV = new TableColumn<>("Tổng Doanh Thu DV"); colTDTDV.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThuDV"));
        TableColumn<DoanhThuModel, String> colTSDV = new TableColumn<>("Tổng Số Dịch Vụ"); colTSDV.setCellValueFactory(new PropertyValueFactory<>("tongSoDichVu"));
        TableColumn<DoanhThuModel, String> colTGC = new TableColumn<>("Tổng Số Giờ Chơi"); colTGC.setCellValueFactory(new PropertyValueFactory<>("tongSoGioChoi"));

        table.getColumns().addAll(colMa, colND, colNgay, colTDT, colTDTDV, colTSDV, colTGC);
        
        filteredList = new FilteredList<>(danhSach, p -> true);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<DoanhThuModel> row = new TableRow<>();
            row.setOnMouseEntered(e -> { if (!row.isSelected()) row.setStyle("-fx-background-color: #F3F4F6;"); });
            row.setOnMouseExited(e -> { if (!row.isSelected()) row.setStyle(""); });
            return row;
        });
        return table;
    }

    // ===================== EXPORT TO EXCEL/CSV =====================
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh thu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                // Ghi UTF-8 BOM để Excel hiển thị đúng tiếng Việt
                writer.write('\ufeff');
                // Ghi Header
                writer.println("Mã Doanh Thu,Nội Dung,Ngày,Tổng Doanh Thu,Tổng DT Dịch Vụ,Tổng Số Dịch Vụ,Tổng Giờ Chơi");
                // Ghi Data
                for (DoanhThuModel dt : danhSach) {
                    writer.printf("%s,\"%s\",%s,%s,%s,%s,%s\n",
                        dt.getMaDoanhThu(), dt.getNoiDung(), dt.getNgay(), dt.getTongDoanhThu(),
                        dt.getTongDoanhThuDV(), dt.getTongSoDichVu(), dt.getTongSoGioChoi());
                }
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xuất file báo cáo thành công!\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Export", "Không thể lưu file: " + ex.getMessage());
            }
        }
    }

    // ===================== INSERT DIALOG =====================
    private void showInsertDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create New Revenue");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtNoiDung = new TextField(); styleField(txtNoiDung);
        ComboBox<String> cboLoai = new ComboBox<>(FXCollections.observableArrayList("DAILY", "MONTHLY", "YEARLY")); 
        cboLoai.setValue("DAILY"); cboLoai.setMaxWidth(Double.MAX_VALUE);
        
        TextField txtTDT = new TextField("0.0"); styleField(txtTDT);
        TextField txtTDTDV = new TextField("0.0"); styleField(txtTDTDV);
        TextField txtTSDV = new TextField("0.0"); styleField(txtTSDV);
        TextField txtTGC = new TextField("0.0"); styleField(txtTGC);

        grid.add(new Label("Nội dung:"), 0, 0); grid.add(txtNoiDung, 1, 0);
        grid.add(new Label("Loại:"), 0, 1); grid.add(cboLoai, 1, 1);
        grid.add(new Label("Tổng Doanh Thu:"), 0, 2); grid.add(txtTDT, 1, 2);
        grid.add(new Label("Tổng DT Dịch Vụ:"), 0, 3); grid.add(txtTDTDV, 1, 3);
        grid.add(new Label("Tổng Số Dịch Vụ:"), 0, 4); grid.add(txtTSDV, 1, 4);
        grid.add(new Label("Tổng Giờ Chơi:"), 0, 5); grid.add(txtTGC, 1, 5);

        GridPane.setHgrow(txtNoiDung, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSubmit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(btnSubmit, ButtonType.CANCEL);
        styleDialogButtons(dp, btnSubmit, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == btnSubmit) {
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaDT(conn);
                    String tgNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    String sql = "INSERT INTO DoanhThu (MaDoanhThu, NoiDung, Ngay, TongDoanhThu, TongDoanhThuDV, TongSoDichVu, TongSoGioChoi, Loai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtNoiDung.getText().trim());
                        ps.setString(3, tgNow);
                        ps.setDouble(4, Double.parseDouble(txtTDT.getText().trim().isEmpty() ? "0" : txtTDT.getText().trim()));
                        ps.setDouble(5, Double.parseDouble(txtTDTDV.getText().trim().isEmpty() ? "0" : txtTDTDV.getText().trim()));
                        ps.setDouble(6, Double.parseDouble(txtTSDV.getText().trim().isEmpty() ? "0" : txtTSDV.getText().trim()));
                        ps.setDouble(7, Double.parseDouble(txtTGC.getText().trim().isEmpty() ? "0" : txtTGC.getText().trim()));
                        ps.setString(8, cboLoai.getValue());
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
        DoanhThuModel selected = tblRevenueList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một bản ghi để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM DoanhThu WHERE MaDoanhThu = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getMaDoanhThu());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        DoanhThuModel selected = tblRevenueList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một bản ghi để cập nhật!");
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

        TextField txtTDT = new TextField(selected.getTongDoanhThu()); styleField(txtTDT);
        TextField txtTDTDV = new TextField(selected.getTongDoanhThuDV()); styleField(txtTDTDV);
        TextField txtTSDV = new TextField(selected.getTongSoDichVu()); styleField(txtTSDV);
        TextField txtTGC = new TextField(selected.getTongSoGioChoi()); styleField(txtTGC);
        TextField txtNoiDung = new TextField(selected.getNoiDung()); styleField(txtNoiDung);

        grid.add(new Label("TongDoanhThu:"), 0, 0); grid.add(txtTDT, 1, 0);
        grid.add(new Label("Tongdoanhthudv:"), 0, 1); grid.add(txtTDTDV, 1, 1);
        grid.add(new Label("Tongsdv:"), 0, 2); grid.add(txtTSDV, 1, 2);
        grid.add(new Label("Tongthoigian:"), 0, 3); grid.add(txtTGC, 1, 3);
        grid.add(new Label("Noidung:"), 0, 4); grid.add(txtNoiDung, 1, 4);

        dp.setContent(grid);
        ButtonType btnSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSave, btnCancel);
        styleDialogButtons(dp, btnSave, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSave) {
                String sql = "UPDATE DoanhThu SET NoiDung=?, TongDoanhThu=?, TongDoanhThuDV=?, TongSoDichVu=?, TongSoGioChoi=? WHERE MaDoanhThu=?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, txtNoiDung.getText().trim());
                    ps.setDouble(2, Double.parseDouble(txtTDT.getText().trim()));
                    ps.setDouble(3, Double.parseDouble(txtTDTDV.getText().trim()));
                    ps.setDouble(4, Double.parseDouble(txtTSDV.getText().trim()));
                    ps.setDouble(5, Double.parseDouble(txtTGC.getText().trim()));
                    ps.setString(6, selected.getMaDoanhThu());
                    
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
        if (lblBanGhi == null) return;
        
        int soBanGhi = danhSach.size();
        lblBanGhi.setText(String.valueOf(soBanGhi));

        double tongDT = danhSach.stream().mapToDouble(dt -> {
            try { return Double.parseDouble(dt.getTongDoanhThu()); } catch (Exception e) { return 0; }
        }).sum();
        
        lblTongDT.setText(String.format("%.0f", tongDT));

        double tbdt = soBanGhi > 0 ? tongDT / soBanGhi : 0;
        lblTBDT.setText(String.format("%.2f", tbdt).replace(",", "."));
    }

    private Button createButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85)); btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void styleField(TextField tf) { tf.setStyle("-fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 5 8;"); }

    private void styleDialogButtons(DialogPane dp, ButtonType ok, ButtonType cancel) {
        if(dp.lookupButton(ok) != null) dp.lookupButton(ok).setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 18;");
        if(dp.lookupButton(cancel) != null) dp.lookupButton(cancel).setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 18;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg); alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}
