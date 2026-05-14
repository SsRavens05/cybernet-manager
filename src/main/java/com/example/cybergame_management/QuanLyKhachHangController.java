package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import java.time.LocalDate;

public class QuanLyKhachHangController {

    @FXML private TableView<KhachHang> tbKhachHang;
    @FXML private TableColumn<KhachHang, String> colMaKH;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSDT;
    @FXML private TableColumn<KhachHang, String> colEmail;
    @FXML private TableColumn<KhachHang, String> colSoDu;
    @FXML private TableColumn<KhachHang, String> colHang;
    @FXML private TableColumn<KhachHang, String> colTrangThai;
    @FXML private TableColumn<KhachHang, String> colNgayDK;

    @FXML
    public void initialize() {
        // Ánh xạ cột
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colHoTen.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());
        colSDT.setCellValueFactory(cellData -> cellData.getValue().sdtProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colSoDu.setCellValueFactory(cellData -> cellData.getValue().soDuProperty());
        colHang.setCellValueFactory(cellData -> cellData.getValue().hangProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colNgayDK.setCellValueFactory(cellData -> cellData.getValue().ngayDKProperty());

        colTrangThai.setCellFactory(column -> new TableCell<KhachHang, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge"); // Lấy class gốc từ style.css

                    // Phân loại màu theo trạng thái
                    if (item.equals("ACTIVE")) {
                        badge.getStyleClass().add("badge-active"); // Xanh lá
                    } else if (item.equals("BANNED")) {
                        badge.getStyleClass().add("badge-banned"); // Đỏ
                    } else if (item.equals("INACTIVE")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else {
                        badge.getStyleClass().add("badge-default"); // Xám
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Dữ liệu giả y hệt Figma
        ObservableList<KhachHang> list = FXCollections.observableArrayList(
                new KhachHang("KH001", "Nguyễn Văn An", "0901234567", "an@email.com", "510.000đ", "Vàng", "ACTIVE", "2025-01-10"),
                new KhachHang("KH002", "Trần Thị Bình", "0912345678", "binh@email.com", "200.000đ", "Bạc", "ACTIVE", "2025-02-15"),
                new KhachHang("KH003", "Lê Minh Cường", "0923456789", "cuong@email.com", "0đ", "Thường", "INACTIVE", "2025-03-20")
        );
        tbKhachHang.setItems(list);
    }

    @FXML
    public void onInsertClick() {
        // 1. Tạo một cửa sổ "vô hình" (không có viền Windows mặc định)
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        // 2. Vẽ cái khung trắng bo góc (đóng vai trò là cửa sổ mới)
        VBox root = new VBox();
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        // --- PHẦN HEADER (Tiêu đề + Nút X) ---
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Khách Hàng");
        lblTitle.getStyleClass().add("dialog-header-text");

        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("dialog-close-btn");
        btnClose.setOnAction(e -> stage.close()); // Bấm X là tắt

        header.setLeft(lblTitle);
        header.setRight(btnClose);

        // --- PHẦN BODY (Các ô nhập liệu y chang Figma) ---
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);

        TextField txtHoTen = new TextField(); txtHoTen.setPrefWidth(350);
        TextField txtSDT = new TextField();
        TextField txtEmail = new TextField();
        TextField txtSoDu = new TextField("0");
        ComboBox<String> cbHang = new ComboBox<>(FXCollections.observableArrayList("Thường", "Bạc", "Vàng", "VIP")); cbHang.setValue("Thường"); cbHang.setPrefWidth(165);
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "BANNED")); cbTrangThai.setValue("ACTIVE"); cbTrangThai.setPrefWidth(165);

        grid.add(new Label("Họ Tên *"), 0, 0, 2, 1); grid.add(txtHoTen, 0, 1, 2, 1);
        grid.add(new Label("Số Điện Thoại"), 0, 2, 2, 1); grid.add(txtSDT, 0, 3, 2, 1);
        grid.add(new Label("Email"), 0, 4, 2, 1); grid.add(txtEmail, 0, 5, 2, 1);
        grid.add(new Label("Số Dư Tài Khoản"), 0, 6, 2, 1); grid.add(txtSoDu, 0, 7, 2, 1);
        grid.add(new Label("Hạng Khách Hàng"), 0, 8); grid.add(cbHang, 0, 9);
        grid.add(new Label("Trạng Thái"), 1, 8); grid.add(cbTrangThai, 1, 9);

        // --- PHẦN FOOTER (Nút Hủy - Lưu) ---
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-cancel");
        btnHuy.setOnAction(e -> stage.close());

        Button btnLuu = new Button("Lưu");
        btnLuu.getStyleClass().add("btn-save");
        btnLuu.setOnAction(e -> {
            if(txtHoTen.getText().isEmpty()){
                // Optional: Show error alert here if needed
                return;
            }
            // Thêm vào bảng
            String maKH = "KH" + String.format("%03d", tbKhachHang.getItems().size() + 1);
            tbKhachHang.getItems().add(new KhachHang(maKH, txtHoTen.getText(), txtSDT.getText(), txtEmail.getText(), txtSoDu.getText()+"đ", cbHang.getValue(), cbTrangThai.getValue(), LocalDate.now().toString()));
            stage.close(); // Lưu xong thì đóng
        });

        footer.getChildren().addAll(btnHuy, btnLuu);

        // 3. Lắp ráp lại và hiển thị
        root.getChildren().addAll(header, grid, footer);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Bắt buộc: Xóa nền đen của Scene để thấy được viền bo tròn
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);
        stage.showAndWait();
    }


    @FXML
    public void onDeleteClick() {
        // 1. KIỂM TRA ĐIỀU KIỆN: Chỉ kích hoạt khi có 1 dòng đang được chọn
        KhachHang selectedItem = tbKhachHang.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa khách hàng này?");
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
            tbKhachHang.getItems().remove(selectedItem);
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
        // Lấy khách hàng đang chọn trong bảng tbKhachHang
        KhachHang selectedItem = tbKhachHang.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("Vui lòng chọn một khách hàng để cập nhật!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-khach-hang.fxml"));
            Parent root = loader.load();

            UpdateKhachHangController updateCtrl = loader.getController();
            updateCtrl.setKhachHangData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Khách Hàng");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Cập nhật lại bảng Khách Hàng sau khi tắt popup
            tbKhachHang.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}