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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class khachhang {

    // ===================== MODEL =====================
    public static class Customer {
        private final StringProperty maKH;
        private final StringProperty hoTen;
        private final StringProperty sdt;
        private final StringProperty diaChi;
        private final StringProperty email;
        private final StringProperty ngaySinh;
        private final StringProperty soDuTK;
        private final StringProperty maHangKH;

        public Customer(String maKH, String hoTen, String sdt, String diaChi, String email, String ngaySinh, String soDuTK, String maHangKH) {
            this.maKH = new SimpleStringProperty(maKH != null ? maKH : "");
            this.hoTen = new SimpleStringProperty(hoTen != null ? hoTen : "");
            this.sdt = new SimpleStringProperty(sdt != null ? sdt : "");
            this.diaChi = new SimpleStringProperty(diaChi != null ? diaChi : "");
            this.email = new SimpleStringProperty(email != null ? email : "");
            this.ngaySinh = new SimpleStringProperty(ngaySinh != null ? ngaySinh : "");
            this.soDuTK = new SimpleStringProperty(soDuTK != null ? soDuTK : "0");
            this.maHangKH = new SimpleStringProperty(maHangKH != null ? maHangKH : "");
        }

        public String getMaKH() { return maKH.get(); }
        public String getHoTen() { return hoTen.get(); }
        public String getSdt() { return sdt.get(); }
        public String getDiaChi() { return diaChi.get(); }
        public String getEmail() { return email.get(); }
        public String getNgaySinh() { return ngaySinh.get(); }
        public String getSoDuTK() { return soDuTK.get(); }
        public String getMaHangKH() { return maHangKH.get(); }

        public void setHoTen(String v) { hoTen.set(v); }
        public void setSdt(String v) { sdt.set(v); }
        public void setDiaChi(String v) { diaChi.set(v); }
        public void setEmail(String v) { email.set(v); }
        public void setNgaySinh(String v) { ngaySinh.set(v); }
        public void setMaHangKH(String v) { maHangKH.set(v); }
        public void setSoDuTK(String v) { soDuTK.set(v); }

        public StringProperty maKHProperty() { return maKH; }
        public StringProperty hoTenProperty() { return hoTen; }
        public StringProperty sdtProperty() { return sdt; }
        public StringProperty diaChiProperty() { return diaChi; }
        public StringProperty emailProperty() { return email; }
        public StringProperty ngaySinhProperty() { return ngaySinh; }
        public StringProperty soDuTKProperty() { return soDuTK; }
        public StringProperty maHangKHProperty() { return maHangKH; }
    }

    // ===================== DATA =====================
    public static final ObservableList<Customer> danhSach = FXCollections.observableArrayList();
    
    private FilteredList<Customer> filteredList;
    private TableView<Customer> tblCustomerList;

    // Các nhãn thống kê
    private Label lblTotal;
    private Label lblActive;
    private Label lblCategory;

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
        String sql = "SELECT MaKH, HoTen, SDT, DiaChi, Email, NgaySinh, SoDuTK, MaHangKH FROM KhachHang ORDER BY MaKH ASC";
        
        try (Connection conn = ketnoicsdl.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String ma = rs.getString("MaKH");
                String ten = rs.getString("HoTen");
                String sdt = rs.getString("SDT");
                String dc = rs.getString("DiaChi");
                String email = rs.getString("Email");
                String ns = rs.getString("NgaySinh");
                String sodu = String.valueOf(rs.getDouble("SoDuTK"));
                String maHang = rs.getString("MaHangKH");
                
                danhSach.add(new Customer(ma, ten, sdt, dc, email, ns, sodu, maHang));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể tải dữ liệu: " + ex.getMessage());
        }
        refreshCards();
    }

    // Hàm sinh mã khách hàng tự động tăng (KH001, KH002...)
    private String generateNewMaKH(Connection conn) {
        int maxNum = 0;
        String sql = "SELECT MaKH FROM KhachHang WHERE MaKH LIKE 'KH%'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaKH");
                if (ma != null && ma.length() > 2) {
                    try {
                        // Cắt phần số sau "KH"
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua các mã sai định dạng kiểu cũ như KH20250509...
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return String.format("KH%03d", maxNum + 1);
    }

    // ===================== STAT CARDS =====================
    private HBox buildStatCards() {
        HBox hbox = new HBox(16);
        hbox.setFillHeight(true);

        lblTotal = new Label("0");
        lblActive = new Label("0");
        lblCategory = new Label("0");

        StackPane cardTotal = buildCard("Tổng KH", lblTotal, "#7986CB", "#3F51B5", "👤");
        StackPane cardActive = buildCard("KH hoạt động", lblActive, "#BA68C8", "#8E24AA", "🌟");
        StackPane cardCategory = buildCard("Số loại hạng", lblCategory, "#FFD54F", "#FF8F00", "🚩");

        HBox.setHgrow(cardTotal, Priority.ALWAYS);
        HBox.setHgrow(cardActive, Priority.ALWAYS);
        HBox.setHgrow(cardCategory, Priority.ALWAYS);

        hbox.getChildren().addAll(cardTotal, cardActive, cardCategory);
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

        tblCustomerList = buildTable();
        VBox.setVgrow(tblCustomerList, Priority.ALWAYS);

        panel.getChildren().addAll(toolbar, tblCustomerList);
        return panel;
    }

    private HBox buildToolbar() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Danh Sách Khách Hàng");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1; -fx-font-size: 13px; -fx-padding: 6 10;");

        filteredList = new FilteredList<>(danhSach, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(kh -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return kh.getMaKH().toLowerCase().contains(filter) ||
                       kh.getHoTen().toLowerCase().contains(filter) ||
                       kh.getSdt().toLowerCase().contains(filter) ||
                       kh.getEmail().toLowerCase().contains(filter);
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
    private TableView<Customer> buildTable() {
        TableView<Customer> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(350);

        TableColumn<Customer, String> colMa = new TableColumn<>("Mã KH"); colMa.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        TableColumn<Customer, String> colTen = new TableColumn<>("Họ Tên"); colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen")); colTen.setStyle("-fx-font-weight: bold;");
        TableColumn<Customer, String> colSDT = new TableColumn<>("SĐT"); colSDT.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        TableColumn<Customer, String> colDiaChi = new TableColumn<>("Địa Chỉ"); colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        TableColumn<Customer, String> colEmail = new TableColumn<>("Email"); colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Customer, String> colNgaySinh = new TableColumn<>("Ngày Sinh"); colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        TableColumn<Customer, String> colSoDu = new TableColumn<>("Số Dư TK"); colSoDu.setCellValueFactory(new PropertyValueFactory<>("soDuTK"));
        
        TableColumn<Customer, String> colMaHang = new TableColumn<>("Mã Hạng"); 
        colMaHang.setCellValueFactory(new PropertyValueFactory<>("maHangKH"));

        table.getColumns().addAll(colMa, colTen, colSDT, colDiaChi, colEmail, colNgaySinh, colSoDu, colMaHang);
        table.setItems(filteredList);

        table.setRowFactory(tv -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseEntered(e -> { if (!row.isSelected()) row.setStyle("-fx-background-color: #F3F4F6;"); });
            row.setOnMouseExited(e -> { if (!row.isSelected()) row.setStyle(""); });
            return row;
        });
        return table;
    }

    // ===================== INSERT DIALOG =====================
    private void showInsertDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Insert Customer");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(420);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtHoTen = new TextField(); styleField(txtHoTen);
        TextField txtSDT = new TextField(); styleField(txtSDT);
        DatePicker dtpNgaySinh = new DatePicker(); dtpNgaySinh.setStyle("-fx-font-size: 13px;"); dtpNgaySinh.setMaxWidth(Double.MAX_VALUE);
        TextField txtEmail = new TextField(); styleField(txtEmail);
        TextField txtDiaChi = new TextField(); styleField(txtDiaChi);
        TextField txtMaHang = new TextField(); styleField(txtMaHang);

        grid.add(new Label("Họ tên:"), 0, 0); grid.add(txtHoTen, 1, 0);
        grid.add(new Label("Số điện thoại:"), 0, 1); grid.add(txtSDT, 1, 1);
        grid.add(new Label("Ngày sinh:"), 0, 2); grid.add(dtpNgaySinh, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(txtEmail, 1, 3);
        grid.add(new Label("Địa chỉ:"), 0, 4); grid.add(txtDiaChi, 1, 4);
        grid.add(new Label("Mã hạng khách hàng:"), 0, 5); grid.add(txtMaHang, 1, 5);

        GridPane.setHgrow(txtHoTen, Priority.ALWAYS);

        dp.setContent(grid);
        ButtonType btnSubmit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSubmit, btnCancel);
        styleDialogButtons(dp, btnSubmit, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSubmit) {
                if (txtHoTen.getText().isEmpty() || txtSDT.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Họ tên và SĐT!");
                    return null;
                }
                
                String ngaySinhStr = dtpNgaySinh.getValue() != null ? dtpNgaySinh.getValue().toString() : "";
                
                // MỞ KẾT NỐI VÀ TỰ ĐỘNG TẠO MÃ
                try (Connection conn = ketnoicsdl.getConnection()) {
                    String maMoi = generateNewMaKH(conn);
                    
                    String sql = "INSERT INTO KhachHang (MaKH, HoTen, SDT, DiaChi, Email, NgaySinh, SoDuTK, MaHangKH) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maMoi);
                        ps.setString(2, txtHoTen.getText().trim());
                        ps.setString(3, txtSDT.getText().trim());
                        ps.setString(4, txtDiaChi.getText().trim());
                        ps.setString(5, txtEmail.getText().trim());
                        
                        if (ngaySinhStr.isEmpty()) {
                            ps.setNull(6, java.sql.Types.DATE);
                        } else {
                            ps.setString(6, ngaySinhStr);
                        }
                        ps.setDouble(7, 0.0); // Mặc định số dư là 0 khi tạo mới
                        ps.setString(8, txtMaHang.getText().trim()); 
                        
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
        Customer selected = tblCustomerList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một khách hàng để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete?");
        confirm.setTitle("Confirm");
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
            try (Connection conn = ketnoicsdl.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                 
                ps.setString(1, selected.getMaKH());
                ps.executeUpdate();
                loadDataFromDatabase(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", ex.getMessage());
            }
        }
    }

    // ===================== UPDATE DIALOG =====================
    private void handleUpdate() {
        Customer selected = tblCustomerList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một khách hàng để cập nhật!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Customer");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dp.setPrefWidth(420);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField txtHoTen = new TextField(selected.getHoTen()); styleField(txtHoTen);
        TextField txtSDT = new TextField(selected.getSdt()); styleField(txtSDT);
        
        DatePicker dtpNgaySinh = new DatePicker();
        dtpNgaySinh.setMaxWidth(Double.MAX_VALUE);
        if (selected.getNgaySinh() != null && !selected.getNgaySinh().isEmpty()) {
            try { dtpNgaySinh.setValue(LocalDate.parse(selected.getNgaySinh())); } catch (Exception e) {}
        }

        TextField txtEmail = new TextField(selected.getEmail()); styleField(txtEmail);
        TextField txtDiaChi = new TextField(selected.getDiaChi()); styleField(txtDiaChi);
        TextField txtMaHang = new TextField(selected.getMaHangKH()); styleField(txtMaHang);

        grid.add(new Label("Họ tên:"), 0, 0); grid.add(txtHoTen, 1, 0);
        grid.add(new Label("Số điện thoại:"), 0, 1); grid.add(txtSDT, 1, 1);
        grid.add(new Label("Ngày sinh:"), 0, 2); grid.add(dtpNgaySinh, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(txtEmail, 1, 3);
        grid.add(new Label("Địa chỉ:"), 0, 4); grid.add(txtDiaChi, 1, 4);
        grid.add(new Label("Mã hạng khách hàng:"), 0, 5); grid.add(txtMaHang, 1, 5);

        dp.setContent(grid);
        ButtonType btnSubmit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnSubmit, btnCancel);
        styleDialogButtons(dp, btnSubmit, btnCancel);

        dialog.setResultConverter(btn -> {
            if (btn == btnSubmit) {
                String ngaySinhStr = dtpNgaySinh.getValue() != null ? dtpNgaySinh.getValue().toString() : "";
                
                String sql = "UPDATE KhachHang SET HoTen = ?, SDT = ?, DiaChi = ?, Email = ?, NgaySinh = ?, MaHangKH = ? WHERE MaKH = ?";
                try (Connection conn = ketnoicsdl.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, txtHoTen.getText().trim());
                    ps.setString(2, txtSDT.getText().trim());
                    ps.setString(3, txtDiaChi.getText().trim());
                    ps.setString(4, txtEmail.getText().trim());
                    
                    if (ngaySinhStr.isEmpty()) {
                        ps.setNull(5, java.sql.Types.DATE);
                    } else {
                        ps.setString(5, ngaySinhStr);
                    }
                    
                    ps.setString(6, txtMaHang.getText().trim()); 
                    ps.setString(7, selected.getMaKH());
                    
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
        if (lblTotal != null && lblActive != null && lblCategory != null) {
            updateStatValues();
        }
    }

    private void updateStatValues() {
        lblTotal.setText(String.valueOf(danhSach.size()));
        lblActive.setText(String.valueOf(danhSach.size()));
        
        long loaiHang = danhSach.stream()
                .map(Customer::getMaHangKH)
                .filter(ma -> ma != null && !ma.isEmpty())
                .distinct()
                .count();
        lblCategory.setText(String.valueOf(loaiHang));
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