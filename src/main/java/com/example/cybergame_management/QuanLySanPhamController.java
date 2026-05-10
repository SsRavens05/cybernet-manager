package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class QuanLySanPhamController {

    @FXML private TableView<SanPham> tbSanPham;
    @FXML private TableColumn<SanPham, String> colMaSP;
    @FXML private TableColumn<SanPham, String> colTenSP;
    @FXML private TableColumn<SanPham, String> colLoai;
    @FXML private TableColumn<SanPham, String> colGia;
    @FXML private TableColumn<SanPham, String> colSoLuong;
    @FXML private TableColumn<SanPham, String> colDonVi;
    @FXML private TableColumn<SanPham, String> colTrangThai;

    @FXML
    public void initialize() {
        // Ánh xạ dữ liệu
        colMaSP.setCellValueFactory(cellData -> cellData.getValue().maSPProperty());
        colTenSP.setCellValueFactory(cellData -> cellData.getValue().tenSPProperty());
        colLoai.setCellValueFactory(cellData -> cellData.getValue().loaiProperty());
        colGia.setCellValueFactory(cellData -> cellData.getValue().giaProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colDonVi.setCellValueFactory(cellData -> cellData.getValue().donViProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // CUSTOM CSS CHO CỘT SỐ LƯỢNG (Xanh / Đỏ)
        // Tối ưu CSS Huy hiệu cho cột Trạng Thái (Sản Phẩm)
        colTrangThai.setCellFactory(column -> new TableCell<SanPham, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge"); // Gọi class cha

                    if (item.equals("Còn Hàng")) {
                        badge.getStyleClass().add("badge-active"); // Xanh lá
                    } else if (item.equals("Hết Hàng")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else if (item.equals("Ngừng Bán")) {
                        badge.getStyleClass().add("badge-banned"); // Đỏ
                    } else {
                        badge.getStyleClass().add("badge-default"); // Xám
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Đổ dữ liệu giả y như hình Figma
        ObservableList<SanPham> list = FXCollections.observableArrayList(
                new SanPham("SP001", "Mì Hảo Hảo", "Đồ ăn", "5.000đ", "120", "Gói", "Còn Hàng"),
                new SanPham("SP002", "Pepsi Lon", "Đồ uống", "12.000đ", "60", "Lon", "Còn Hàng"),
                new SanPham("SP003", "Snack Oishi", "Đồ ăn", "10.000đ", "0", "Gói", "Hết Hàng"),
                new SanPham("SP004", "Trà Sữa", "Đồ uống", "25.000đ", "30", "Ly", "Còn Hàng"),
                new SanPham("SP005", "Bánh Mì", "Đồ ăn", "15.000đ", "20", "Cái", "Còn Hàng"),
                new SanPham("SP006", "Cà Phê Đen", "Đồ uống", "20.000đ", "50", "Ly", "Còn Hàng")
        );
        tbSanPham.setItems(list);
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
        root.setPrefWidth(450); // Set độ rộng cho nó thoáng giống Figma

        // Header (Tiêu đề + Nút X)
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Sản Phẩm");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        // Body (Các ô nhập liệu 1 cột thẳng hàng)
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtTen = new TextField(); txtTen.setPrefWidth(400);
        TextField txtLoai = new TextField();
        TextField txtGia = new TextField("0");
        TextField txtSL = new TextField("0");
        TextField txtDonVi = new TextField("Cái");
        ComboBox<String> cbTT = new ComboBox<>(FXCollections.observableArrayList("Còn Hàng", "Hết Hàng", "Ngừng Bán"));
        cbTT.setValue("Còn Hàng");
        cbTT.setPrefWidth(400);

        grid.add(new Label("Tên SP *"), 0, 0); grid.add(txtTen, 0, 1);
        grid.add(new Label("Loại"), 0, 2); grid.add(txtLoai, 0, 3);
        grid.add(new Label("Giá"), 0, 4); grid.add(txtGia, 0, 5);
        grid.add(new Label("Số Lượng Tồn Kho"), 0, 6); grid.add(txtSL, 0, 7);
        grid.add(new Label("Đơn Vị"), 0, 8); grid.add(txtDonVi, 0, 9);
        grid.add(new Label("Trạng Thái"), 0, 10); grid.add(cbTT, 0, 11);

        // Footer (Nút Hủy - Lưu)
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            // Tự động sinh mã SP mới
            String maMoi = "SP" + String.format("%03d", tbSanPham.getItems().size() + 1);
            // Ném dữ liệu vào bảng
            tbSanPham.getItems().add(new SanPham(maMoi, txtTen.getText(), txtLoai.getText(), txtGia.getText(), txtSL.getText(), txtDonVi.getText(), cbTT.getValue()));
            stage.close();
        });

        footer.getChildren().addAll(bHuy, bLuu);

        // Ghép mọi thứ lại và show
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
        SanPham selectedItem = tbSanPham.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa sản phẩm này?");
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
            tbSanPham.getItems().remove(selectedItem);
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
}