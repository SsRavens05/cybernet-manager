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

public class QuanLyKhuVucController {

    @FXML private Label lblTongKV;
    @FXML private Label lblTongSoMay;
    @FXML private Label lblKhuVucHoatDong;
    @FXML private TableView<KhuVuc> tbKhuVuc;
    @FXML private TableColumn<KhuVuc, String> colMaKV;
    @FXML private TableColumn<KhuVuc, String> colTenKV;
    @FXML private TableColumn<KhuVuc, String> colSoMay;
    @FXML private TableColumn<KhuVuc, String> colGiaThue;
    @FXML private TableColumn<KhuVuc, String> colTrangThai;
    @FXML private TableColumn<KhuVuc, String> colMoTa;
    @FXML private TextField txtSearch;
    @FXML private Button btnInsert;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;
    private final ObservableList<KhuVuc> data = DatabaseSeedData.khuVuc();

    @FXML
    public void initialize() {
        // Ánh xạ dữ liệu
        colMaKV.setCellValueFactory(cellData -> cellData.getValue().maKVProperty());
        colTenKV.setCellValueFactory(cellData -> cellData.getValue().tenKVProperty());
        colSoMay.setCellValueFactory(cellData -> cellData.getValue().soMayProperty());
        colGiaThue.setCellValueFactory(cellData -> cellData.getValue().giaThueProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colMoTa.setCellValueFactory(cellData -> cellData.getValue().moTaProperty());

        // Tạo huy hiệu cho cột Trạng Thái
        colTrangThai.setCellFactory(column -> new TableCell<KhuVuc, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge"); // Lấy class gốc từ style.css

                    if (item.equals("HOATDONG")) {
                        badge.getStyleClass().add("badge-active"); // Xanh lá
                    } else if (item.equals("BAOTRI")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else {
                        badge.getStyleClass().add("badge-default"); // Xám (hoặc Đỏ cho DONG_CUA)
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Dữ liệu giả y hệt thiết kế
        FilteredList<KhuVuc> filtered = new FilteredList<>(data, item -> true);
        tbKhuVuc.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
        bindActionButtons();
        updateStats();

        if (!UserSession.isAdmin()) {
            if (btnInsert != null) {
                btnInsert.setVisible(false);
                btnInsert.setManaged(false);
            }
            if (btnDelete != null) {
                btnDelete.setVisible(false);
                btnDelete.setManaged(false);
            }
            if (btnUpdate != null) {
                btnUpdate.setVisible(false);
                btnUpdate.setManaged(false);
            }
        }
    }

    private boolean matchesFilter(KhuVuc item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaKV(), item.getTenKV(), item.getSoMay(), item.getGiaThue(), item.getTrangThai(), item.getMoTa());
    }

    private void updateStats() {
        lblTongKV.setText(String.valueOf(data.size()));
        lblTongSoMay.setText(String.valueOf(data.stream().mapToInt(kv -> DisplayFormat.parseInt(kv.getSoMay())).sum()));
        lblKhuVucHoatDong.setText(String.valueOf(data.stream()
                .filter(kv -> "HOATDONG".equalsIgnoreCase(kv.getTrangThai()))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbKhuVuc.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbKhuVuc.getSelectionModel().selectedItemProperty().isNull());
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
        Label lblTitle = new Label("Thêm Khu Vực");
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

        TextField txtTenKV = new TextField(); txtTenKV.setPrefWidth(400);
        TextField txtSoMay = new TextField("0");
        TextField txtGiaThue = new TextField("0");
        TextField txtMoTa = new TextField();

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("HOATDONG", "BAOTRI", "DONG_CUA"));
        cbTrangThai.setValue("HOATDONG");
        cbTrangThai.setPrefWidth(400);

        grid.add(new Label("Tên Khu Vực *"), 0, 0); grid.add(txtTenKV, 0, 1);
        grid.add(new Label("Số Máy"), 0, 2); grid.add(txtSoMay, 0, 3);
        grid.add(new Label("Giá Thuê/h"), 0, 4); grid.add(txtGiaThue, 0, 5);
        grid.add(new Label("Mô Tả"), 0, 6); grid.add(txtMoTa, 0, 7);
        grid.add(new Label("Trạng Thái"), 0, 8); grid.add(cbTrangThai, 0, 9);

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel"); // Ăn CSS màu Đỏ
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save"); // Ăn CSS màu Xanh Lá
        bLuu.setOnAction(e -> {
            // Tự động sinh mã KV mới
            String maMoi = "KV" + String.format("%03d", data.size() + 1);

            // Xử lý thêm chữ "đ" vào giá thuê nếu người dùng gõ số không
            String gia = txtGiaThue.getText();
            if(!gia.endsWith("đ")) gia += "đ";

            // Thêm vào bảng
            data.add(new KhuVuc(maMoi, txtTenKV.getText(), txtSoMay.getText(), gia, cbTrangThai.getValue(), txtMoTa.getText()));
            updateStats();
            stage.close();
        });

        footer.getChildren().addAll(bHuy, bLuu);

        // Show hàng
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
        KhuVuc selectedItem = tbKhuVuc.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa khu vực này?");
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
            // THỰC HIỆN XÓA KHỎI BẢNG (Và sau này là xóa khỏi Database)
            data.remove(selectedItem);
            updateStats();
            stage.close(); // Xóa xong thì đóng popup
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
        KhuVuc selectedItem = tbKhuVuc.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("Vui lòng chọn một khu vực để cập nhật!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-khu-vuc.fxml"));
            Parent root = loader.load();

            UpdateKhuVucController updateCtrl = loader.getController();
            updateCtrl.setKhuVucData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Khu Vực");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tbKhuVuc.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
