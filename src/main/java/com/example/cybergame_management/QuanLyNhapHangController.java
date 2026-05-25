package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QuanLyNhapHangController {

    @FXML private Label lblTongPhieuNhap;
    @FXML private Label lblTongChiNhap;
    @FXML private Label lblChoDuyet;
    @FXML private TableView<NhapHang> tbNhapHang;
    @FXML private TableColumn<NhapHang, String> colMaPN;
    @FXML private TableColumn<NhapHang, String> colNgayNhap;
    @FXML private TableColumn<NhapHang, String> colSoLuong;
    @FXML private TableColumn<NhapHang, String> colTongTienNhap;
    @FXML private TableColumn<NhapHang, String> colLoaiHang;
    @FXML private TableColumn<NhapHang, String> colNhaCC;
    @FXML private TableColumn<NhapHang, String> colTrangThai;
    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;
    private ObservableList<NhapHang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = NhapHangRepository.findAll();
            } else {
                data = DatabaseSeedData.nhapHang();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.nhapHang();
        }
        // Ánh xạ dữ liệu
        colMaPN.setCellValueFactory(cellData -> cellData.getValue().maPNProperty());
        colNgayNhap.setCellValueFactory(cellData -> cellData.getValue().ngayNhapProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colTongTienNhap.setCellValueFactory(cellData -> cellData.getValue().tongTienNhapProperty());
        colLoaiHang.setCellValueFactory(cellData -> cellData.getValue().loaiHangProperty());
        colNhaCC.setCellValueFactory(cellData -> cellData.getValue().nhaCCProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // Tạo huy hiệu cho cột Trạng Thái
        // Custom CSS cho cột Trạng Thái (Nhập Hàng)
        colTrangThai.setCellFactory(column -> new TableCell<NhapHang, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge"); // Lấy class gốc từ style.css

                    if (item.equals("DA_NHAP")) {
                        badge.getStyleClass().add("badge-active"); // Xanh lá
                    } else if (item.equals("CHO_DUYET")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else if (item.equals("HUY")) {
                        badge.getStyleClass().add("badge-banned"); // Đỏ
                    } else {
                        badge.getStyleClass().add("badge-default"); // Xám
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Dữ liệu giả y hệt thiết kế
        FilteredList<NhapHang> filtered = new FilteredList<>(data, item -> true);
        tbNhapHang.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(NhapHang item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaPN(), item.getLoaiHang(), item.getNhaCC(), item.getSoLuong(),
                item.getDonGia(), item.getTongTienNhap(), item.getNgayNhap(), item.getNguoiNhap(), item.getTrangThai());
    }

    private void updateStats() {
        lblTongPhieuNhap.setText(String.valueOf(data.size()));
        lblTongChiNhap.setText(DisplayFormat.money(data.stream()
                .mapToLong(nh -> DisplayFormat.parseMoney(nh.getTongTienNhap()))
                .sum()));
        lblChoDuyet.setText(String.valueOf(data.stream()
                .filter(nh -> "CHO_DUYET".equalsIgnoreCase(nh.getTrangThai()))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbNhapHang.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbNhapHang.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        // Khung nền popup
        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        // Header
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Phiếu Nhập");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        // Body (Các ô nhập liệu)
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtLoaiHang = new TextField(); txtLoaiHang.setPrefWidth(400);
        TextField txtNCC = new TextField();
        TextField txtSL = new TextField("0");
        TextField txtDonGia = new TextField("0");
        TextField txtNguoiNhap = new TextField("NV001");

        // Ô TỔNG TIỀN (Nền xám giống thiết kế)
        HBox boxTongTien = new HBox();
        boxTongTien.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");
        Label lblTongTien = new Label("Tổng tiền nhập: 0đ");
        lblTongTien.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold;");
        boxTongTien.getChildren().add(lblTongTien);

        // LOGIC TỰ ĐỘNG TÍNH TIỀN (Số Lượng * Đơn Giá)
        javafx.beans.value.ChangeListener<String> calcTotal = (obs, oldVal, newVal) -> {
            try {
                long sl = Long.parseLong(txtSL.getText().trim());
                long dg = Long.parseLong(txtDonGia.getText().trim());
                // Format số có dấu chấm cho đẹp (VD: 1.200.000đ)
                lblTongTien.setText(String.format("Tổng tiền nhập: %,dđ", sl * dg).replace(",", "."));
            } catch (NumberFormatException e) {
                lblTongTien.setText("Tổng tiền nhập: 0đ"); // Gõ bậy chữ cái thì về 0
            }
        };
        txtSL.textProperty().addListener(calcTotal);
        txtDonGia.textProperty().addListener(calcTotal);

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("CHO_DUYET", "DA_NHAP", "HUY"));
        cbTrangThai.setValue("CHO_DUYET");
        cbTrangThai.setPrefWidth(400);

        grid.add(new Label("Loại Hàng *"), 0, 0); grid.add(txtLoaiHang, 0, 1);
        grid.add(new Label("Nhà Cung Cấp"), 0, 2); grid.add(txtNCC, 0, 3);
        grid.add(new Label("Số Lượng"), 0, 4); grid.add(txtSL, 0, 5);
        grid.add(new Label("Đơn Giá"), 0, 6); grid.add(txtDonGia, 0, 7);
        grid.add(new Label("Người Nhập"), 0, 8); grid.add(txtNguoiNhap, 0, 9);
        grid.add(boxTongTien, 0, 10); // Đưa cục tổng tiền vào
        grid.add(new Label("Trạng Thái"), 0, 11); grid.add(cbTrangThai, 0, 12);

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            try {
                String maMoi;
                if (DatabaseConnection.isConfigured()) {
                    maMoi = NhapHangRepository.getNextMaPN();
                } else {
                    maMoi = "PN" + String.format("%03d", data.size() + 1);
                }
                
                String tongTienStr = lblTongTien.getText().replace("Tổng tiền nhập: ", "");

                NhapHang nh = new NhapHang(maMoi, txtLoaiHang.getText(), txtNCC.getText(), txtSL.getText(), txtDonGia.getText() + "đ", tongTienStr, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), txtNguoiNhap.getText(), cbTrangThai.getValue());
                
                if (DatabaseConnection.isConfigured()) {
                    NhapHangRepository.insert(nh);
                }
                data.add(nh);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu phiếu nhập vào Database: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        footer.getChildren().addAll(bHuy, bLuu);

        // Show giao diện
        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }


    @FXML
    public void onDeleteClick() {
        // 1. KIỂM TRA ĐIỀU KIỆN: Chỉ kích hoạt khi có 1 dòng đang được chọn
        NhapHang selectedItem = tbNhapHang.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return; // Nếu chưa chọn gì thì im lặng thoát, không hiện popup
        }

        // 2. VẼ UI POPUP THEO FIGMA
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(25);
        root.getStyleClass().add("delete-dialog");
        root.setPrefWidth(420);

        // --- Phần trên: Icon Thùng rác + Text ---
        HBox topBox = new HBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);

        // Vẽ vòng tròn đỏ nhạt chứa icon thùng rác
        Label lblIcon = new Label("🗑");
        lblIcon.getStyleClass().add("icon-trash");
        StackPane iconCircle = new StackPane(lblIcon);
        iconCircle.getStyleClass().add("icon-circle");

        VBox textCtn = new VBox(5);
        Label lblTitle = new Label("Xác nhận xóa");
        lblTitle.getStyleClass().add("text-title");
        Label lblMessage = new Label("Bạn có chắc muốn xóa đơn nhập hàng này?");
        lblMessage.getStyleClass().add("text-message");
        textCtn.getChildren().addAll(lblTitle, lblMessage);

        topBox.getChildren().addAll(iconCircle, textCtn);

        // --- Phần dưới: Nút bấm ---
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-outline");
        btnHuy.setOnAction(e -> stage.close());

        Button btnXoa = new Button("Xóa");
        btnXoa.getStyleClass().add("btn-danger");
        btnXoa.setOnAction(e -> {
            try {
                if (DatabaseConnection.isConfigured()) {
                    NhapHangRepository.delete(selectedItem.getMaPN());
                }
                data.remove(selectedItem);
                updateStats();
                stage.close(); // Xóa xong thì đóng popup
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa phiếu nhập khỏi Database: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        bottomBox.getChildren().addAll(btnHuy, btnXoa);

        // Lắp ráp & Hiển thị
        root.getChildren().addAll(topBox, bottomBox);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void onUpdateClick() {
        NhapHang selectedItem = tbNhapHang.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("Vui lòng chọn một phiếu nhập để cập nhật!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-nhap-hang.fxml"));
            Parent root = loader.load();

            UpdateNhapHangController updateCtrl = loader.getController();
            updateCtrl.setNhapHangData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Nhập Hàng");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tbNhapHang.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
