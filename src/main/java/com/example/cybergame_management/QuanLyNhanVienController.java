package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    @FXML private Label lblTongNV;
    @FXML private Label lblNhanVienDangLam;
    @FXML private Label lblNhanVienNghiPhep;
    @FXML private TableView<NhanVien> tbNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colMaSoThue;
    @FXML private TableColumn<NhanVien, String> colSoBHYT;
    @FXML private TableColumn<NhanVien, String> colNgayVao;
    @FXML private TableColumn<NhanVien, String> colNgayThoiViec;
    @FXML private TableColumn<NhanVien, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private ObservableList<NhanVien> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = NhanVienRepository.findAll();
            } else {
                data = DatabaseSeedData.nhanVien();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.nhanVien();
        }

        colMaNV.setCellValueFactory(cellData -> cellData.getValue().maNVProperty());
        colHoTen.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());
        colMaSoThue.setCellValueFactory(cellData -> cellData.getValue().maSoThueProperty());
        colSoBHYT.setCellValueFactory(cellData -> cellData.getValue().soBHYTProperty());
        colNgayVao.setCellValueFactory(cellData -> cellData.getValue().ngayVaoProperty());
        colNgayThoiViec.setCellValueFactory(cellData -> cellData.getValue().ngayThoiViecProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // Custom badges for TrangThai Column
        colTrangThai.setCellFactory(column -> new TableCell<NhanVien, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");

                    if (item.equals("Đang làm") || item.equals("DANG_LAM")) {
                        badge.getStyleClass().add("badge-active");
                    } else if (item.equals("Nghỉ việc") || item.equals("NGHI_VIEC")) {
                        badge.getStyleClass().add("badge-banned");
                    } else if (item.equals("Nghỉ phép") || item.equals("NGHI_PHEP")) {
                        badge.getStyleClass().add("badge-warning");
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        FilteredList<NhanVien> filtered = new FilteredList<>(data, item -> true);
        tbNhanVien.setItems(filtered);

        // Bind filter change events
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> applyFilters(filtered));

        bindActionButtons();
        updateStats();
    }

    private void applyFilters(FilteredList<NhanVien> filtered) {
        filtered.setPredicate(item -> {
            // Text Filter
            String text = txtSearch.getText();
            return SearchMatcher.containsKeyword(text,
                    item.getMaNV(), item.getHoTen(), item.getMaSoThue(), item.getSoBHYT());
        });
    }

    private void updateStats() {
        lblTongNV.setText(String.valueOf(data.size()));
        lblNhanVienDangLam.setText(String.valueOf(data.stream()
                .filter(nv -> nv.getTrangThai().equalsIgnoreCase("DANG_LAM") || nv.getTrangThai().equalsIgnoreCase("Đang làm"))
                .count()));
        lblNhanVienNghiPhep.setText(String.valueOf(data.stream()
                .filter(nv -> nv.getTrangThai().equalsIgnoreCase("NGHI_PHEP") || nv.getTrangThai().equalsIgnoreCase("Nghỉ phép"))
                .count()));
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
        TextField txtMST = new TextField(); txtMST.setPromptText("Nhập mã số thuế...");
        TextField txtBHYT = new TextField(); txtBHYT.setPromptText("Nhập số BHYT...");
        TextField txtNgayVao = new TextField(java.time.LocalDate.now().toString());
        TextField txtNgayThoi = new TextField("—");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Đang làm", "Nghỉ phép", "Nghỉ việc"));
        cbTrangThai.setValue("Đang làm");

        grid.add(new Label("Mã NV (Tự động)"), 0, 0); grid.add(new TextField("NV" + String.format("%03d", data.size()+1)), 0, 1);
        grid.add(new Label("Họ Tên *"), 0, 2); grid.add(txtTen, 0, 3);
        grid.add(new Label("Mã số thuế"), 0, 4); grid.add(txtMST, 0, 5);
        grid.add(new Label("Số BHYT"), 0, 6); grid.add(txtBHYT, 0, 7);
        grid.add(new Label("Ngày vào làm"), 0, 8); grid.add(txtNgayVao, 0, 9);
        grid.add(new Label("Ngày thôi việc"), 0, 10); grid.add(txtNgayThoi, 0, 11);
        grid.add(new Label("Trạng thái"), 0, 12); grid.add(cbTrangThai, 0, 13);

        // Styling elements in popup
        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else if (n instanceof TextField) {
                TextField tf = (TextField) n;
                tf.setPrefHeight(38.0);
                tf.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
                if (grid.getRowIndex(n) == 1) {
                    tf.setEditable(false);
                    tf.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #6b7280;");
                }
            } else if (n instanceof ComboBox) {
                ComboBox<?> cb = (ComboBox<?>) n;
                cb.setPrefHeight(38.0);
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
            }
        }

        HBox footer = new HBox(10); footer.setAlignment(Pos.CENTER_RIGHT);
        Button bHuy = new Button("Hủy"); bHuy.getStyleClass().add("btn-cancel"); bHuy.setOnAction(e->stage.close());
        Button bLuu = new Button("Lưu"); bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String mst = txtMST.getText() == null || txtMST.getText().trim().isEmpty() ? "—" : txtMST.getText().trim();
            String bhyt = txtBHYT.getText() == null || txtBHYT.getText().trim().isEmpty() ? "—" : txtBHYT.getText().trim();
            String ngayV = txtNgayVao.getText() == null || txtNgayVao.getText().trim().isEmpty() ? java.time.LocalDate.now().toString() : txtNgayVao.getText().trim();
            String ngayT = txtNgayThoi.getText() == null || txtNgayThoi.getText().trim().isEmpty() ? "—" : txtNgayThoi.getText().trim();

            NhanVien newNV = new NhanVien("NV" + String.format("%03d", data.size() + 1), txtTen.getText(), mst, bhyt, ngayV, ngayT, cbTrangThai.getValue());
            try {
                if (DatabaseConnection.isConfigured()) {
                    NhanVienRepository.insert(newNV);
                }
                data.add(newNV);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu nhân viên vào Database: " + ex.getMessage());
                alert.showAndWait();
            }
        });
        footer.getChildren().addAll(bHuy, bLuu);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root); scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene); stage.showAndWait();
    }

    @FXML
    public void onDeleteClick() {
        NhanVien selectedItem = tbNhanVien.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa nhân viên này?");
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
            try {
                if (DatabaseConnection.isConfigured()) {
                    NhanVienRepository.softDelete(selectedItem.getMaNV());
                }
                data.remove(selectedItem);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa nhân viên từ Database: " + ex.getMessage());
                alert.showAndWait();
            }
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
    void onUpdateClick(ActionEvent event) {
        NhanVien selectedNV = tbNhanVien.getSelectionModel().getSelectedItem();
        if (selectedNV == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-nhan-vien.fxml"));
            Parent root = loader.load();

            UpdateNhanVienController controller = loader.getController();
            controller.setNhanVienData(selectedNV);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Nhân Viên - CyberNet Manager");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            if (DatabaseConnection.isConfigured()) {
                try {
                    NhanVienRepository.update(selectedNV);
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi Database");
                    alert.setHeaderText(null);
                    alert.setContentText("Không thể cập nhật nhân viên trong database: " + ex.getMessage());
                    alert.showAndWait();
                }
            }

            tbNhanVien.refresh();
            updateStats();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
