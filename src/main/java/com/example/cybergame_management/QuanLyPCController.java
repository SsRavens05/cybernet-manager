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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuanLyPCController {

    @FXML private Label lblTongPC;
    @FXML private Label lblPCHoatDong;
    @FXML private Label lblPCBaoTri;
    @FXML private TableView<PC> tbPC;
    @FXML private TableColumn<PC, String> colMaPC;
    @FXML private TableColumn<PC, String> colMaKV;
    @FXML private TableColumn<PC, String> colCpu;
    @FXML private TableColumn<PC, String> colRam;
    @FXML private TableColumn<PC, String> colVga;
    @FXML private TableColumn<PC, String> colRom;
    @FXML private TableColumn<PC, String> colSoMay;
    @FXML private TableColumn<PC, String> colLoaiPC;
    @FXML private TableColumn<PC, String> colTrangThai;
    @FXML private TableColumn<PC, String> colCreateAt;
    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<PC> data = DatabaseSeedData.pc();

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu các cột
        colMaPC.setCellValueFactory(cellData -> cellData.getValue().maPCProperty());
        colMaKV.setCellValueFactory(cellData -> cellData.getValue().maKVProperty());
        colCpu.setCellValueFactory(cellData -> cellData.getValue().cpuProperty());
        colRam.setCellValueFactory(cellData -> cellData.getValue().ramProperty());
        colVga.setCellValueFactory(cellData -> cellData.getValue().vgaProperty());
        colRom.setCellValueFactory(cellData -> cellData.getValue().romProperty());
        colSoMay.setCellValueFactory(cellData -> cellData.getValue().soMayProperty());
        colLoaiPC.setCellValueFactory(cellData -> cellData.getValue().loaiPCProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colCreateAt.setCellValueFactory(cellData -> cellData.getValue().createAtProperty());

        // 2. Tạo badge hiển thị trạng thái PC
        colTrangThai.setCellFactory(column -> new TableCell<PC, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    badge.getStyleClass().add("badge");
                    if (item.equalsIgnoreCase("HOATDONG") || item.equalsIgnoreCase("Hoạt động")) {
                        badge.setText("Hoạt động");
                        badge.getStyleClass().add("badge-active"); // Xanh
                    } else if (item.equalsIgnoreCase("BAOTRI") || item.equalsIgnoreCase("Bảo trì")) {
                        badge.setText("Bảo trì");
                        badge.getStyleClass().add("badge-warning"); // Cam
                    } else {
                        badge.setText(item);
                        badge.getStyleClass().add("badge-default"); // Xám
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Tải dữ liệu từ database (nếu có)
        loadData();

        // 3. Khởi tạo bộ lọc tìm kiếm
        FilteredList<PC> filtered = new FilteredList<>(data, item -> true);
        tbPC.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        // 4. Ràng buộc các nút bấm
        bindActionButtons();
        updateStats();

        // 5. Kiểm tra phân quyền (Ẩn nút thao tác nếu là nhân viên)
        if (!UserSession.isAdmin()) {
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

    private void loadData() {
        if (!PCRepository.isDatabaseEnabled()) {
            return;
        }
        try {
            data.setAll(PCRepository.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Không thể tải danh sách PC từ database. Ứng dụng sẽ dùng dữ liệu mẫu.");
        }
    }

    private boolean matchesFilter(PC item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaPC(), item.getMaKV(), item.getCpu(), item.getRam(), item.getVga(),
                item.getRom(), item.getSoMay(), item.getLoaiPC(), item.getTrangThai());
    }

    private void updateStats() {
        lblTongPC.setText(String.valueOf(data.size()));
        lblPCHoatDong.setText(String.valueOf(data.stream()
                .filter(pc -> "HOATDONG".equalsIgnoreCase(pc.getTrangThai()) || "Hoạt động".equalsIgnoreCase(pc.getTrangThai()))
                .count()));
        lblPCBaoTri.setText(String.valueOf(data.stream()
                .filter(pc -> "BAOTRI".equalsIgnoreCase(pc.getTrangThai()) || "Bảo trì".equalsIgnoreCase(pc.getTrangThai()))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbPC.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbPC.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(480);

        // Header
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm PC Mới");
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

        // Sinh mã PC tự động bằng thời gian thực
        String generatedMaPC = "PC" + System.currentTimeMillis() % 100000;
        TextField txtMaPC = new TextField(generatedMaPC);
        txtMaPC.setPrefWidth(400);

        // Tải danh sách Khu Vực khả dụng
        ObservableList<String> kvList = FXCollections.observableArrayList();
        if (PCRepository.isDatabaseEnabled()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT MAKV FROM KHUVUC WHERE NVL(IS_DELETE, 0) = 0 ORDER BY MAKV");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kvList.add(rs.getString("MAKV"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (kvList.isEmpty()) {
            for (KhuVuc kv : DatabaseSeedData.khuVuc()) {
                kvList.add(kv.getMaKV());
            }
        }

        ComboBox<String> cbMaKV = new ComboBox<>(kvList);
        if (!kvList.isEmpty()) cbMaKV.setValue(kvList.get(0));
        cbMaKV.setPrefWidth(400);

        TextField txtCpu = new TextField(); txtCpu.setPromptText("Ví dụ: Intel Core i7-12700K");
        TextField txtRam = new TextField(); txtRam.setPromptText("Ví dụ: 16GB");
        TextField txtVga = new TextField(); txtVga.setPromptText("Ví dụ: RTX 3070");
        TextField txtRom = new TextField(); txtRom.setPromptText("Ví dụ: 512GB SSD");
        TextField txtSoMay = new TextField(String.valueOf(data.size() + 1));
        TextField txtLoaiPC = new TextField("VIP");

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Hoạt động", "Bảo trì"));
        cbTrangThai.setValue("Hoạt động");
        cbTrangThai.setPrefWidth(400);

        grid.add(new Label("Mã PC *"), 0, 0); grid.add(txtMaPC, 0, 1);
        grid.add(new Label("Mã Khu Vực *"), 0, 2); grid.add(cbMaKV, 0, 3);
        grid.add(new Label("CPU"), 0, 4); grid.add(txtCpu, 0, 5);
        grid.add(new Label("RAM"), 0, 6); grid.add(txtRam, 0, 7);
        grid.add(new Label("VGA"), 0, 8); grid.add(txtVga, 0, 9);
        grid.add(new Label("ROM"), 0, 10); grid.add(txtRom, 0, 11);
        grid.add(new Label("Số Máy *"), 0, 12); grid.add(txtSoMay, 0, 13);
        grid.add(new Label("Loại PC"), 0, 14); grid.add(txtLoaiPC, 0, 15);
        grid.add(new Label("Trạng Thái"), 0, 16); grid.add(cbTrangThai, 0, 17);

        // Áp dụng định dạng thẩm mỹ cho các phần tử trong hộp thoại
        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else if (n instanceof TextField) {
                TextField tf = (TextField) n;
                tf.setPrefHeight(38.0);
                tf.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
                Integer rowIndex = GridPane.getRowIndex(n);
                if (rowIndex != null && rowIndex == 1) { // txtMaPC
                    tf.setEditable(false);
                    tf.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #6b7280; -fx-padding: 8;");
                }
            } else if (n instanceof ComboBox) {
                ComboBox<?> cb = (ComboBox<?>) n;
                cb.setPrefHeight(38.0);
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
            }
        }

        // Đóng hộp thoại cuộn lại vì nhiều phần tử
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setPrefHeight(350);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent; -fx-viewport-background: transparent;");

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Thêm mới");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            if (txtMaPC.getText().trim().isEmpty() || txtSoMay.getText().trim().isEmpty()) {
                showError("Vui lòng điền đầy đủ các thông tin bắt buộc!");
                return;
            }

            PC newPc = new PC(
                    txtMaPC.getText().trim(),
                    cbMaKV.getValue(),
                    txtCpu.getText().trim(),
                    txtRam.getText().trim(),
                    txtVga.getText().trim(),
                    txtRom.getText().trim(),
                    txtSoMay.getText().trim(),
                    txtLoaiPC.getText().trim(),
                    "Bảo trì".equalsIgnoreCase(cbTrangThai.getValue()) ? "BAOTRI" : "HOATDONG",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            if (PCRepository.isDatabaseEnabled()) {
                try {
                    PCRepository.insert(newPc);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showError("Lỗi cơ sở dữ liệu: Không thể tạo mới PC!");
                    return;
                }
            }

            data.add(newPc);
            updateStats();
            stage.close();
        });

        footer.getChildren().addAll(bHuy, bLuu);

        root.getChildren().addAll(header, scrollPane, footer);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void onDeleteClick() {
        PC selectedItem = tbPC.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

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
        Label lblMessage = new Label("Bạn có chắc muốn xóa PC này?");
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
            if (PCRepository.isDatabaseEnabled()) {
                try {
                    PCRepository.softDelete(selectedItem.getMaPC());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showError("Lỗi cơ sở dữ liệu: Không thể xóa PC!");
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
        PC selectedItem = tbPC.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("Vui lòng chọn một PC để cập nhật!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-pc.fxml"));
            Parent root = loader.load();

            UpdatePCController updateCtrl = loader.getController();
            updateCtrl.setPCData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật PC");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (PCRepository.isDatabaseEnabled()) {
                try {
                    PCRepository.update(selectedItem);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showError("Lỗi cơ sở dữ liệu: Không thể cập nhật PC!");
                }
            }

            tbPC.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi hệ thống");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
