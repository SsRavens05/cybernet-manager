package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class QuanLyThietBiController {

    @FXML
    private TableView<ThietBi> tbThietBi;
    @FXML
    private TableColumn<ThietBi, String> colMaTB;
    @FXML
    private TableColumn<ThietBi, String> colTenTB;
    @FXML
    private TableColumn<ThietBi, String> colLoaiTB;
    @FXML
    private TableColumn<ThietBi, String> colTrangThai;
    @FXML
    private TableColumn<ThietBi, String> colNgayMua;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu
        colMaTB.setCellValueFactory(cellData -> cellData.getValue().maTBProperty());
        colTenTB.setCellValueFactory(cellData -> cellData.getValue().tenTBProperty());
        colLoaiTB.setCellValueFactory(cellData -> cellData.getValue().loaiTBProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colNgayMua.setCellValueFactory(cellData -> cellData.getValue().ngayMuaProperty());

        // 2. Vẽ huy hiệu cho cột Trạng Thái
        colTrangThai.setCellFactory(column -> new TableCell<ThietBi, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");

                    if (item.equals("HOATDONG")) {
                        badge.getStyleClass().add("badge-active"); // Xanh
                    } else if (item.equals("BAOTRI")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else if (item.equals("HONG")) {
                        badge.getStyleClass().add("badge-banned"); // Đỏ
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // 3. Dữ liệu giả
        ObservableList<ThietBi> list = FXCollections.observableArrayList(
                new ThietBi("TB1778342880001", "Chuột Logitech G102", "Chuột", "HOATDONG", "2025-05-09"),
                new ThietBi("TB1778342880002", "Bàn phím cơ DareU", "Bàn Phím", "HOATDONG", "2025-05-09"),
                new ThietBi("TB1778342880003", "Màn hình Samsung 27 inch", "Màn Hình", "BAOTRI", "2025-01-10"),
                new ThietBi("TB1778342880004", "Tai nghe DareU EH722X", "Tai Nghe", "HONG", "2024-11-20")
        );
        tbThietBi.setItems(list);
        bindActionButtons();
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbThietBi.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbThietBi.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        // Header
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Thiết Bị");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        // Body
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        // Sinh mã TB tự động bằng thời gian thực (Giống hệt số dài trong ảnh)
        String generatedMaTB = "TB" + System.currentTimeMillis();
        TextField txtMaTB = new TextField(generatedMaTB);
        txtMaTB.setDisable(true); // Khóa không cho sửa mã
        txtMaTB.setPrefWidth(400);

        TextField txtTenTB = new TextField();
        txtTenTB.setPromptText("Nhập tên thiết bị...");

        ComboBox<String> cbLoai = new ComboBox<>(FXCollections.observableArrayList("-- Chọn loại --", "Chuột", "Bàn Phím", "Màn Hình", "Tai Nghe", "PC"));
        cbLoai.setValue("-- Chọn loại --");
        cbLoai.setPrefWidth(400);

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("HOATDONG", "BAOTRI", "HONG"));
        cbTrangThai.setValue("HOATDONG");
        cbTrangThai.setPrefWidth(400);

        TextField txtNgayMua = new TextField(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));

        grid.add(new Label("Mã TB"), 0, 0);
        grid.add(txtMaTB, 0, 1);
        grid.add(new Label("Tên TB *"), 0, 2);
        grid.add(txtTenTB, 0, 3);
        grid.add(new Label("Loại TB *"), 0, 4);
        grid.add(cbLoai, 0, 5);
        grid.add(new Label("Trạng Thái"), 0, 6);
        grid.add(cbTrangThai, 0, 7);
        grid.add(new Label("Ngày Mua"), 0, 8);
        grid.add(txtNgayMua, 0, 9);

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Thêm mới"); // Nút ghi chữ "Thêm mới" như ảnh
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            tbThietBi.getItems().add(new ThietBi(txtMaTB.getText(), txtTenTB.getText(), cbLoai.getValue(), cbTrangThai.getValue(), txtNgayMua.getText()));
            stage.close();
        });

        footer.getChildren().addAll(bHuy, bLuu);

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
        ThietBi selectedItem = tbThietBi.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa thiết bị này?");
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
            tbThietBi.getItems().remove(selectedItem);
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
        ThietBi selectedItem = tbThietBi.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("Vui lòng chọn một thiết bị để cập nhật!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-thiet-bi.fxml"));
            Parent root = loader.load();

            // Lấy Controller và truyền dữ liệu
            UpdateThietBiController updateCtrl = loader.getController();
            updateCtrl.setThietBiData(selectedItem);

            // Hiện Popup
            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Thiết Bị");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Cập nhật lại UI bảng Thiết Bị
            tbThietBi.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
