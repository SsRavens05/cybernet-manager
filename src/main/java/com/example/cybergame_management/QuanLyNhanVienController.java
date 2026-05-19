package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class QuanLyNhanVienController {

    @FXML private TableView<NhanVien> tbNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colSDT;
    @FXML private TableColumn<NhanVien, String> colLuong; // Có cái này rồi nè
    @FXML private TableColumn<NhanVien, String> colCaLam;
    @FXML private TableColumn<NhanVien, String> colTrangThai;
    @FXML private TableColumn<NhanVien, String> colNgayVao;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    @FXML
    public void initialize() {
        colMaNV.setCellValueFactory(cellData -> cellData.getValue().maNVProperty());
        colHoTen.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());
        colChucVu.setCellValueFactory(cellData -> cellData.getValue().chucVuProperty());
        colSDT.setCellValueFactory(cellData -> cellData.getValue().sdtProperty());
        colLuong.setCellValueFactory(cellData -> cellData.getValue().luongProperty());
        colCaLam.setCellValueFactory(cellData -> cellData.getValue().caLamProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colNgayVao.setCellValueFactory(cellData -> cellData.getValue().ngayVaoProperty());

        // Tối ưu CSS Huy hiệu cho cột Trạng Thái (Nhân Viên)
        colTrangThai.setCellFactory(column -> new TableCell<NhanVien, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge"); // Gọi class cha để bo góc, chỉnh font

                    if (item.equals("Đang làm") || item.equals("DANG_LAM")) {
                        badge.getStyleClass().add("badge-active"); // Xanh lá
                    } else if (item.equals("Nghỉ việc") || item.equals("NGHI_VIEC")) {
                        badge.getStyleClass().add("badge-banned"); // Đỏ
                    } else if (item.equals("Nghỉ phép") || item.equals("NGHI_PHEP")) {
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else {
                        badge.getStyleClass().add("badge-default"); // Xám (phòng hờ lỗi data)
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        ObservableList<NhanVien> list = FXCollections.observableArrayList(
                new NhanVien("NV001", "Nguyễn Thành Long", "Quản lý", "0901111222", "8.000.000đ", "Sáng", "Đang làm", "2024-01-15"),
                new NhanVien("NV002", "Trần Minh Khoa", "Nhân viên", "0912222333", "5.000.000đ", "Chiều", "Đang làm", "2024-03-20"),
                new NhanVien("NV003", "Lê Thị Hoa", "Thu Ngân", "0923333444", "5.500.000đ", "Tối", "Nghỉ phép", "2024-06-01"),
                new NhanVien("NV004", "Phạm Văn Tú", "Kỹ Thuật", "0934444555", "6.000.000đ", "Sáng", "Đang làm", "2024-09-10"),
                new NhanVien("NV005", "Hoàng Thị Mai", "Nhân viên", "0945555666", "4.800.000đ", "Toàn ca", "Nghỉ việc", "2023-11-05")
        );
        tbNhanVien.setItems(list);
        bindActionButtons();
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbNhanVien.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbNhanVien.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Nhân Viên");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕"); btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle); header.setRight(btnX);

        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(15);
        TextField txtTen = new TextField(); txtTen.setPromptText("Nhập họ tên...");
        ComboBox<String> cbCV = new ComboBox<>(FXCollections.observableArrayList("Quản Lý", "Thu Ngân", "Kỹ Thuật")); cbCV.setValue("Nhân Viên");
        TextField txtSDT = new TextField();
        ComboBox<String> cbCa = new ComboBox<>(FXCollections.observableArrayList("Sáng", "Chiều", "Tối")); cbCa.setValue("Sáng");

        grid.add(new Label("Mã NV (Tự động)"), 0, 0); grid.add(new TextField("NV" + String.format("%03d", tbNhanVien.getItems().size()+1)), 0, 1);
        grid.add(new Label("Họ Tên *"), 0, 2); grid.add(txtTen, 0, 3);
        grid.add(new Label("Chức Vụ"), 0, 4); grid.add(cbCV, 0, 5);
        grid.add(new Label("Số Điện Thoại"), 0, 6); grid.add(txtSDT, 0, 7);
        grid.add(new Label("Ca Làm"), 0, 8); grid.add(cbCa, 0, 9);

        HBox footer = new HBox(10); footer.setAlignment(Pos.CENTER_RIGHT);
        Button bHuy = new Button("Hủy"); bHuy.getStyleClass().add("btn-cancel"); bHuy.setOnAction(e->stage.close());
        Button bLuu = new Button("Lưu"); bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            tbNhanVien.getItems().add(new NhanVien("NV" + String.format("%03d", tbNhanVien.getItems().size()+1), txtTen.getText(), cbCV.getValue(), txtSDT.getText(), "5.000.000đ", cbCa.getValue(), "DANG_LAM", java.time.LocalDate.now().toString()));
            stage.close();
        });
        footer.getChildren().addAll(bHuy, bLuu);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root); scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene); stage.showAndWait();
    }


    @FXML
    public void onDeleteClick() {
        // 1. KIỂM TRA ĐIỀU KIỆN: Chỉ kích hoạt khi có 1 dòng đang được chọn
        NhanVien selectedItem = tbNhanVien.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa nhân viên này?");
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
            tbNhanVien.getItems().remove(selectedItem);
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
    void onUpdateClick(ActionEvent event) {
        // 1. Lấy nhân viên đang chọn từ TableView
        NhanVien selectedNV = tbNhanVien.getSelectionModel().getSelectedItem();

        if (selectedNV == null) {
            // (Hiện Alert cảnh báo chưa chọn nhân viên)
            return;
        }

        try {
            // 2. Load đúng file FXML của nhân viên
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-nhan-vien.fxml"));
            Parent root = loader.load();

            // 3. Lấy Controller và truyền data
            UpdateNhanVienController controller = loader.getController();
            controller.setNhanVienData(selectedNV);

            // 4. Bật form lên (Khóa màn hình dưới)
            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Nhân Viên - CyberNet Manager");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // TODO: Refresh lại bảng tableNhanVien sau khi tắt popup
            // tableNhanVien.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
