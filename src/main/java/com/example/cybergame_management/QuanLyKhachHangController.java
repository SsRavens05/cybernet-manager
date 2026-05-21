package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.time.LocalDate;

public class QuanLyKhachHangController {

    @FXML private Label lblTongKH;
    @FXML private Label lblKhachActive;
    @FXML private Label lblKhachInactive;
    @FXML private TableView<KhachHang> tbKhachHang;
    @FXML private TableColumn<KhachHang, String> colMaKH;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSoDu;
    @FXML private TableColumn<KhachHang, String> colSoDiemTichLuy;
    @FXML private TableColumn<KhachHang, String> colTrangThai;
    @FXML private TableColumn<KhachHang, String> colNgayDK;
    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<KhachHang> data = DatabaseSeedData.khachHang();

    @FXML
    public void initialize() {
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colHoTen.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());
        colSoDu.setCellValueFactory(cellData -> new SimpleStringProperty(
                DisplayFormat.money(DisplayFormat.parseMoney(cellData.getValue().getSoDu()))));
        colSoDiemTichLuy.setCellValueFactory(cellData -> cellData.getValue().soDiemTichLuyProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colNgayDK.setCellValueFactory(cellData -> cellData.getValue().ngayDKProperty());

        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                if ("ACTIVE".equals(item)) {
                    badge.getStyleClass().add("badge-active");
                } else if ("BANNED".equals(item)) {
                    badge.getStyleClass().add("badge-banned");
                } else if ("INACTIVE".equals(item)) {
                    badge.getStyleClass().add("badge-warning");
                } else {
                    badge.getStyleClass().add("badge-default");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        loadData();

        FilteredList<KhachHang> filtered = new FilteredList<>(data, item -> true);
        tbKhachHang.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(KhachHang item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaKH(), item.getHoTen(), item.getSoDu(), item.getSoDiemTichLuy(), item.getTrangThai(), item.getNgayDK());
    }

    private void loadData() {
        if (!KhachHangRepository.isDatabaseEnabled()) {
            return;
        }
        try {
            data.setAll(KhachHangRepository.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Không thể tải danh sách khách hàng từ database. Ứng dụng sẽ dùng dữ liệu mẫu.");
        }
    }

    private void updateStats() {
        lblTongKH.setText(String.valueOf(data.size()));
        lblKhachActive.setText(String.valueOf(data.stream()
                .filter(kh -> "ACTIVE".equalsIgnoreCase(kh.getTrangThai()))
                .count()));
        lblKhachInactive.setText(String.valueOf(data.stream()
                .filter(kh -> !"ACTIVE".equalsIgnoreCase(kh.getTrangThai()))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbKhachHang.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbKhachHang.getSelectionModel().selectedItemProperty().isNull());
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
        Label lblTitle = new Label("Thêm Khách Hàng");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnClose = new Button("x");
        btnClose.getStyleClass().add("dialog-close-btn");
        btnClose.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnClose);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtHoTen = new TextField();
        txtHoTen.setPrefWidth(350);
        TextField txtSoDu = new TextField("0");
        TextField txtSoDiemTichLuy = new TextField("0");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "BANNED"));
        cbTrangThai.setValue("ACTIVE");
        cbTrangThai.setPrefWidth(165);

        grid.add(new Label("Họ Tên *"), 0, 0, 2, 1);
        grid.add(txtHoTen, 0, 1, 2, 1);
        grid.add(new Label("Số Dư Tài Khoản"), 0, 2, 2, 1);
        grid.add(txtSoDu, 0, 3, 2, 1);
        grid.add(new Label("Số Điểm Tích Lũy"), 0, 4);
        grid.add(txtSoDiemTichLuy, 0, 5);
        grid.add(new Label("Trạng Thái"), 1, 4);
        grid.add(cbTrangThai, 1, 5);

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-cancel");
        btnHuy.setOnAction(e -> stage.close());
        Button btnLuu = new Button("Lưu");
        btnLuu.getStyleClass().add("btn-save");
        btnLuu.setOnAction(e -> {
            if (txtHoTen.getText().isBlank()) {
                return;
            }
            String maKH = "KH" + String.format("%03d", data.size() + 1);
            data.add(new KhachHang(maKH, txtHoTen.getText(), String.valueOf(DisplayFormat.parseMoney(txtSoDu.getText())),
                    txtSoDiemTichLuy.getText(), cbTrangThai.getValue(), LocalDate.now().toString()));
            updateStats();
            stage.close();
        });
        footer.getChildren().addAll(btnHuy, btnLuu);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void onDeleteClick() {
        KhachHang selectedItem = tbKhachHang.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(25);
        root.getStyleClass().add("delete-dialog");
        root.setPrefWidth(420);

        HBox topBox = new HBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label lblIcon = new Label("X");
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

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnHuy = new Button("Hủy");
        btnHuy.getStyleClass().add("btn-outline");
        btnHuy.setOnAction(e -> stage.close());
        Button btnXoa = new Button("Xóa");
        btnXoa.getStyleClass().add("btn-danger");
        btnXoa.setOnAction(e -> {
            if (KhachHangRepository.isDatabaseEnabled()) {
                try {
                    KhachHangRepository.softDelete(selectedItem.getMaKH());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showError("Không thể xóa khách hàng trong database.");
                    return;
                }
            }
            data.remove(selectedItem);
            updateStats();
            stage.close();
        });
        bottomBox.getChildren().addAll(btnHuy, btnXoa);

        root.getChildren().addAll(topBox, bottomBox);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void onUpdateClick() {
        KhachHang selectedItem = tbKhachHang.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
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

            if (KhachHangRepository.isDatabaseEnabled()) {
                KhachHangRepository.update(selectedItem);
            }
            tbKhachHang.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể cập nhật khách hàng trong database.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi database");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
