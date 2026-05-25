package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

import java.time.LocalDate;

public class QuanLyDoiQuaController {

    @FXML private Label lblTongDoiQua;
    @FXML private Label lblDangCho;
    @FXML private Label lblSoLoaiQua;

    @FXML private TableView<DoiQua> tbDoiQua;
    @FXML private TableColumn<DoiQua, String> colMaDQ;
    @FXML private TableColumn<DoiQua, String> colMaKH;
    @FXML private TableColumn<DoiQua, String> colMaQT;
    @FXML private TableColumn<DoiQua, String> colNgayDoi;
    @FXML private TableColumn<DoiQua, String> colSoLuong;
    @FXML private TableColumn<DoiQua, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private ObservableList<DoiQua> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = DoiQuaRepository.findAll();
            } else {
                data = DatabaseSeedData.doiQua();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.doiQua();
        }
        colMaDQ.setCellValueFactory(cellData -> cellData.getValue().maDQProperty());
        colMaKH.setCellValueFactory(cellData -> cellData.getValue().maKHProperty());
        colMaQT.setCellValueFactory(cellData -> cellData.getValue().maQTProperty());
        colNgayDoi.setCellValueFactory(cellData -> cellData.getValue().ngayDoiProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // Custom styling for TrangThai Column with badges
        colTrangThai.setCellFactory(column -> new TableCell<DoiQua, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");

                    if (item.equalsIgnoreCase("Completed")) {
                        badge.getStyleClass().add("badge-active");
                    } else if (item.equalsIgnoreCase("Pending")) {
                        badge.getStyleClass().add("badge-warning");
                    } else if (item.equalsIgnoreCase("Cancelled")) {
                        badge.getStyleClass().add("badge-banned");
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        FilteredList<DoiQua> filtered = new FilteredList<>(data, item -> true);
        tbDoiQua.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(DoiQua item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaDQ(), item.getMaKH(), item.getMaQT(), item.getNgayDoi(), item.getSoLuong(), item.getTrangThai());
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbDoiQua.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbDoiQua.getSelectionModel().selectedItemProperty().isNull());
    }

    private void updateStats() {
        lblTongDoiQua.setText(String.valueOf(data.size()));
        lblDangCho.setText(String.valueOf(data.stream().filter(dq -> "Pending".equalsIgnoreCase(dq.getTrangThai())).count()));
        lblSoLoaiQua.setText(String.valueOf(data.stream().map(DoiQua::getMaQT).distinct().count()));
    }

    @FXML
    public void onInsertClick() {
        ObservableList<KhachHang> customers;
        ObservableList<QuaTang> gifts;
        try {
            if (DatabaseConnection.isConfigured()) {
                customers = KhachHangRepository.findAll();
                gifts = QuaTangRepository.findAll();
            } else {
                customers = DatabaseSeedData.khachHang();
                gifts = DatabaseSeedData.quaTang();
            }
        } catch (Exception ex) {
            customers = DatabaseSeedData.khachHang();
            gifts = DatabaseSeedData.quaTang();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Lịch Sử Đổi Quà");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ComboBox<String> cbMaKH = new ComboBox<>();
        for (KhachHang kh : customers) {
            cbMaKH.getItems().add(kh.getMaKH() + " - " + kh.getHoTen());
        }
        cbMaKH.setPromptText("Chọn Khách Hàng *");
        cbMaKH.setPrefWidth(400);

        ComboBox<String> cbMaQT = new ComboBox<>();
        for (QuaTang qt : gifts) {
            cbMaQT.getItems().add(qt.getMaQT() + " - " + qt.getNoiDung() + " (" + qt.getSoDiemTieuHao() + " ⭐)");
        }
        cbMaQT.setPromptText("Chọn Quà Tặng *");
        cbMaQT.setPrefWidth(400);

        TextField txtSoLuong = new TextField("1");
        TextField txtNgay = new TextField(LocalDate.now().toString());

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Pending", "Completed", "Cancelled"));
        cbTrangThai.setValue("Pending");
        cbTrangThai.setPrefWidth(400);

        for (Control c : new Control[]{cbMaKH, cbMaQT, txtSoLuong, txtNgay, cbTrangThai}) {
            c.setPrefWidth(400);
        }

        grid.add(new Label("Khách Hàng *"), 0, 0); grid.add(cbMaKH, 0, 1);
        grid.add(new Label("Quà Tặng *"), 0, 2); grid.add(cbMaQT, 0, 3);
        grid.add(new Label("Số Lượng *"), 0, 4); grid.add(txtSoLuong, 0, 5);
        grid.add(new Label("Ngày Đổi"), 0, 6); grid.add(txtNgay, 0, 7);
        grid.add(new Label("Trạng Thái"), 0, 8); grid.add(cbTrangThai, 0, 9);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            if (cbMaKH.getValue() == null || cbMaQT.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn đầy đủ Khách Hàng và Quà Tặng!");
                alert.showAndWait();
                return;
            }

            String maKH = cbMaKH.getValue().split(" - ")[0].trim();
            String maQT = cbMaQT.getValue().split(" - ")[0].trim();
            
            long count = data.stream()
                    .filter(dq -> dq.getMaKH().equalsIgnoreCase(maKH))
                    .count();
            if (count >= 30) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R17");
                alert.setContentText("Mỗi khách hàng đang hoạt động chỉ được đổi tối đa 30 quà tặng!");
                alert.showAndWait();
                return;
            }

            String randomMillis = String.valueOf(System.currentTimeMillis()).substring(7);
            String maMoi = "DQ" + LocalDate.now().toString().replace("-", "") + randomMillis;
            DoiQua newDQ = new DoiQua(maMoi, maKH, maQT,
                    txtNgay.getText(), txtSoLuong.getText(), cbTrangThai.getValue());
            try {
                if (DatabaseConnection.isConfigured()) {
                    DoiQuaRepository.insert(newDQ);
                }
                data.add(newDQ);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                String msg = ex.getMessage();
                if (msg != null && msg.contains("20003")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Không Đủ Điểm Tích Lũy");
                    alert.setHeaderText("Đổi quà thất bại!");
                    alert.setContentText("Khách hàng không đủ điểm tích lũy để thực hiện đổi quà này.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu đổi quà vào Database: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
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
        DoiQua selectedItem = tbDoiQua.getSelectionModel().getSelectedItem();
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

        Label lblIcon = new Label("🗑");
        lblIcon.getStyleClass().add("icon-trash");
        StackPane iconCircle = new StackPane(lblIcon);
        iconCircle.getStyleClass().add("icon-circle");

        VBox textCtn = new VBox(5);
        Label lblTitle = new Label("Xác nhận xóa");
        lblTitle.getStyleClass().add("text-title");
        Label lblMessage = new Label("Bạn có chắc muốn xóa lịch sử đổi quà này?");
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
        DoiQua selectedItem = tbDoiQua.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        ObservableList<KhachHang> customers;
        ObservableList<QuaTang> gifts;
        try {
            if (DatabaseConnection.isConfigured()) {
                customers = KhachHangRepository.findAll();
                gifts = QuaTangRepository.findAll();
            } else {
                customers = DatabaseSeedData.khachHang();
                gifts = DatabaseSeedData.quaTang();
            }
        } catch (Exception ex) {
            customers = DatabaseSeedData.khachHang();
            gifts = DatabaseSeedData.quaTang();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Cập Nhật Lịch Sử Đổi Quà");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ComboBox<String> cbMaKH = new ComboBox<>();
        String selectedKhEntry = null;
        for (KhachHang kh : customers) {
            String entry = kh.getMaKH() + " - " + kh.getHoTen();
            cbMaKH.getItems().add(entry);
            if (kh.getMaKH().equalsIgnoreCase(selectedItem.getMaKH())) {
                selectedKhEntry = entry;
            }
        }
        if (selectedKhEntry != null) {
            cbMaKH.setValue(selectedKhEntry);
        } else {
            cbMaKH.setValue(selectedItem.getMaKH());
        }
        cbMaKH.setPrefWidth(400);

        ComboBox<String> cbMaQT = new ComboBox<>();
        String selectedQtEntry = null;
        for (QuaTang qt : gifts) {
            String entry = qt.getMaQT() + " - " + qt.getNoiDung() + " (" + qt.getSoDiemTieuHao() + " ⭐)";
            cbMaQT.getItems().add(entry);
            if (qt.getMaQT().equalsIgnoreCase(selectedItem.getMaQT())) {
                selectedQtEntry = entry;
            }
        }
        if (selectedQtEntry != null) {
            cbMaQT.setValue(selectedQtEntry);
        } else {
            cbMaQT.setValue(selectedItem.getMaQT());
        }
        cbMaQT.setPrefWidth(400);

        TextField txtSoLuong = new TextField(selectedItem.getSoLuong());
        txtSoLuong.setPrefWidth(400);

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Pending", "Completed", "Cancelled"));
        cbTrangThai.setValue(selectedItem.getTrangThai());
        cbTrangThai.setPrefWidth(400);

        for (Control c : new Control[]{cbMaKH, cbMaQT, txtSoLuong, cbTrangThai}) {
            c.setPrefWidth(400);
        }

        grid.add(new Label("Khách Hàng *"), 0, 0); grid.add(cbMaKH, 0, 1);
        grid.add(new Label("Quà Tặng *"), 0, 2); grid.add(cbMaQT, 0, 3);
        grid.add(new Label("Số Lượng *"), 0, 4); grid.add(txtSoLuong, 0, 5);
        grid.add(new Label("Trạng Thái *"), 0, 6); grid.add(cbTrangThai, 0, 7);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            if (cbMaKH.getValue() == null || cbMaQT.getValue() == null || txtSoLuong.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn đầy đủ các thông tin bắt buộc!");
                alert.showAndWait();
                return;
            }

            String maKH = cbMaKH.getValue().split(" - ")[0].trim();
            String maQT = cbMaQT.getValue().split(" - ")[0].trim();
            String soLuongStr = txtSoLuong.getText().trim();

            try {
                long sl = Long.parseLong(soLuongStr);
                if (sl <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Số lượng phải là một số nguyên dương (> 0)!");
                alert.showAndWait();
                return;
            }

            selectedItem.setMaKH(maKH);
            selectedItem.setMaQT(maQT);
            selectedItem.setSoLuong(soLuongStr);
            selectedItem.setTrangThai(cbTrangThai.getValue());

            try {
                if (DatabaseConnection.isConfigured()) {
                    DoiQuaRepository.update(selectedItem);
                }
                tbDoiQua.refresh();
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                String msg = ex.getMessage();
                if (msg != null && msg.contains("20003")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Không Đủ Điểm Tích Lũy");
                    alert.setHeaderText("Cập nhật đổi quà thất bại!");
                    alert.setContentText("Khách hàng không đủ điểm tích lũy để thực hiện đổi quà này.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật đổi quà vào Database: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        footer.getChildren().addAll(bHuy, bLuu);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
