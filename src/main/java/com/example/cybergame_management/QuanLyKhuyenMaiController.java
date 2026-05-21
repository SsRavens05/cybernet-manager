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

public class QuanLyKhuyenMaiController {
    @FXML private Label lblTongKM;
    @FXML private Label lblDangApDung;
    @FXML private Label lblHetHan;
    @FXML private ComboBox<String> cbLoaiFilter;
    @FXML private ComboBox<String> cbTrangThaiFilter;
    @FXML private TextField txtSearch;
    @FXML private TableView<KhuyenMai> tbKhuyenMai;
    @FXML private TableColumn<KhuyenMai, String> colMaCTR;
    @FXML private TableColumn<KhuyenMai, String> colTenCTR;
    @FXML private TableColumn<KhuyenMai, String> colLoaiCTR;
    @FXML private TableColumn<KhuyenMai, String> colChietKhau;
    @FXML private TableColumn<KhuyenMai, String> colNgayBD;
    @FXML private TableColumn<KhuyenMai, String> colNgayKT;
    @FXML private TableColumn<KhuyenMai, String> colTrangThai;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<KhuyenMai> data = DatabaseSeedData.khuyenMai();

    @FXML
    public void initialize() {
        colMaCTR.setCellValueFactory(cellData -> cellData.getValue().maCTRProperty());
        colTenCTR.setCellValueFactory(cellData -> cellData.getValue().tenCTRProperty());
        colLoaiCTR.setCellValueFactory(cellData -> cellData.getValue().loaiCTRProperty());
        colChietKhau.setCellValueFactory(cellData -> cellData.getValue().chietKhauProperty());
        colNgayBD.setCellValueFactory(cellData -> cellData.getValue().ngayBDProperty());
        colNgayKT.setCellValueFactory(cellData -> cellData.getValue().ngayKTProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        // Setup filter combobox options
        cbLoaiFilter.setItems(FXCollections.observableArrayList("Tất cả loại", "GIAM_GIA", "TANG_QUA", "TANG_GIO"));
        cbLoaiFilter.setValue("Tất cả loại");
        cbTrangThaiFilter.setItems(FXCollections.observableArrayList("Tất cả trạng thái", "DANG_AP_DUNG", "HET_HAN"));
        cbTrangThaiFilter.setValue("Tất cả trạng thái");

        // Custom Cell Factory for Types
        colLoaiCTR.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                if ("GIAM_GIA".equals(item)) {
                    badge.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else if ("TANG_QUA".equals(item)) {
                    badge.setStyle("-fx-background-color: #a855f7; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else if ("TANG_GIO".equals(item)) {
                    badge.setStyle("-fx-background-color: #06b6d4; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    badge.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        // Custom Cell Factory for Statuses
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                if ("DANG_AP_DUNG".equals(item)) {
                    badge.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else if ("HET_HAN".equals(item)) {
                    badge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    badge.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10; -fx-font-weight: bold; -fx-font-size: 11px;");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        FilteredList<KhuyenMai> filtered = new FilteredList<>(data, item -> true);
        tbKhuyenMai.setItems(filtered);

        Runnable refreshFilter = () -> filtered.setPredicate(this::matchesFilter);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());
        cbLoaiFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());
        cbTrangThaiFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());

        btnDelete.disableProperty().bind(tbKhuyenMai.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbKhuyenMai.getSelectionModel().selectedItemProperty().isNull());
        updateStats();
    }

    private boolean matchesFilter(KhuyenMai item) {
        String search = txtSearch.getText();
        String loai = cbLoaiFilter.getValue();
        String trangThai = cbTrangThaiFilter.getValue();

        boolean matchSearch = SearchMatcher.containsKeyword(search,
                item.getMaCTR(), item.getTenCTR(), item.getLoaiCTR(),
                item.getChietKhau(), item.getNgayBD(), item.getNgayKT(), item.getTrangThai());

        boolean matchLoai = (loai == null || "Tất cả loại".equals(loai) || loai.equals(item.getLoaiCTR()));
        boolean matchTrangThai = (trangThai == null || "Tất cả trạng thái".equals(trangThai) || trangThai.equals(item.getTrangThai()));

        return matchSearch && matchLoai && matchTrangThai;
    }

    private void updateStats() {
        lblTongKM.setText(String.valueOf(data.size()));
        lblDangApDung.setText(String.valueOf(data.stream().filter(km -> km.getTrangThai().equals("DANG_AP_DUNG")).count()));
        lblHetHan.setText(String.valueOf(data.stream().filter(km -> km.getTrangThai().equals("HET_HAN")).count()));
    }

    @FXML
    public void onInsertClick() {
        showEditor(null);
    }

    @FXML
    public void onUpdateClick() {
        KhuyenMai selected = tbKhuyenMai.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditor(selected);
        }
    }

    private void showEditor(KhuyenMai item) {
        boolean isUpdate = item != null;
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(460);

        BorderPane header = new BorderPane();
        Label title = new Label(isUpdate ? "Cập Nhật Chương Trình" : "Thêm Chương Trình");
        title.getStyleClass().add("dialog-header-text");
        Button close = new Button("X");
        close.getStyleClass().add("dialog-close-btn");
        close.setOnAction(e -> stage.close());
        header.setLeft(title);
        header.setRight(close);

        GridPane grid = new GridPane();
        grid.setVgap(12);

        TextField txtMa = new TextField(isUpdate ? item.getMaCTR() : "");
        txtMa.setDisable(isUpdate);
        TextField txtTen = new TextField(isUpdate ? item.getTenCTR() : "");
        ComboBox<String> cbLoai = new ComboBox<>(FXCollections.observableArrayList("GIAM_GIA", "TANG_QUA", "TANG_GIO"));
        cbLoai.setValue(isUpdate ? item.getLoaiCTR() : "GIAM_GIA");
        TextField txtChietKhau = new TextField(isUpdate ? item.getChietKhau() : "");
        TextField txtNgayBD = new TextField(isUpdate ? item.getNgayBD() : "2025-05-01");
        TextField txtNgayKT = new TextField(isUpdate ? item.getNgayKT() : "2025-05-31");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("DANG_AP_DUNG", "HET_HAN"));
        cbTrangThai.setValue(isUpdate ? item.getTrangThai() : "DANG_AP_DUNG");

        for (Control control : new Control[]{txtMa, txtTen, cbLoai, txtChietKhau, txtNgayBD, txtNgayKT, cbTrangThai}) {
            control.setPrefWidth(410);
        }

        addRow(grid, "Mã CTR *", txtMa, 0);
        addRow(grid, "Tên chương trình *", txtTen, 2);
        addRow(grid, "Loại CTR", cbLoai, 4);
        addRow(grid, "Chiết khấu *", txtChietKhau, 6);
        addRow(grid, "Ngày bắt đầu", txtNgayBD, 8);
        addRow(grid, "Ngày kết thúc", txtNgayKT, 10);
        addRow(grid, "Trạng thái", cbTrangThai, 12);

        Button cancel = new Button("Hủy");
        cancel.getStyleClass().add("btn-cancel");
        cancel.setOnAction(e -> stage.close());
        Button save = new Button("Lưu");
        save.getStyleClass().add("btn-save");
        save.setOnAction(e -> {
            if (isUpdate) {
                item.setTenCTR(txtTen.getText());
                item.setLoaiCTR(cbLoai.getValue());
                item.setChietKhau(txtChietKhau.getText());
                item.setNgayBD(txtNgayBD.getText());
                item.setNgayKT(txtNgayKT.getText());
                item.setTrangThai(cbTrangThai.getValue());
                tbKhuyenMai.refresh();
            } else {
                String ma = txtMa.getText().trim();
                if (ma.isEmpty()) {
                    ma = "CTR" + String.format("%03d", data.size() + 1);
                }
                data.add(new KhuyenMai(ma, txtTen.getText(), cbLoai.getValue(), txtChietKhau.getText(),
                        txtNgayBD.getText(), txtNgayKT.getText(), cbTrangThai.getValue()));
            }
            updateStats();
            stage.close();
        });
        HBox footer = new HBox(10, cancel, save);
        footer.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void addRow(GridPane grid, String label, Control control, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(control, 0, row + 1);
    }

    @FXML
    public void onDeleteClick() {
        KhuyenMai selected = tbKhuyenMai.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        confirmDelete("Bạn có chắc muốn xóa chương trình này?", () -> {
            data.remove(selected);
            updateStats();
        });
    }

    private void confirmDelete(String message, Runnable action) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        VBox root = new VBox(25);
        root.getStyleClass().add("delete-dialog");
        root.setPrefWidth(420);
        HBox top = new HBox(15);
        top.setAlignment(Pos.CENTER_LEFT);
        StackPane iconCircle = new StackPane(new Label("X"));
        iconCircle.getStyleClass().add("icon-circle");
        VBox text = new VBox(5, new Label("Xác nhận xóa"), new Label(message));
        text.getChildren().get(0).getStyleClass().add("text-title");
        text.getChildren().get(1).getStyleClass().add("text-message");
        top.getChildren().addAll(iconCircle, text);
        Button cancel = new Button("Hủy");
        cancel.getStyleClass().add("btn-outline");
        cancel.setOnAction(e -> stage.close());
        Button delete = new Button("Xóa");
        delete.getStyleClass().add("btn-danger");
        delete.setOnAction(e -> {
            action.run();
            stage.close();
        });
        HBox bottom = new HBox(10, cancel, delete);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(top, bottom);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
