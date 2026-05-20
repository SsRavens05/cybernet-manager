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
    @FXML private TextField txtSearch;
    @FXML private TableView<KhuyenMai> tbKhuyenMai;
    @FXML private TableColumn<KhuyenMai, String> colMaKM;
    @FXML private TableColumn<KhuyenMai, String> colTenKM;
    @FXML private TableColumn<KhuyenMai, String> colLoai;
    @FXML private TableColumn<KhuyenMai, String> colGiaTri;
    @FXML private TableColumn<KhuyenMai, String> colDieuKien;
    @FXML private TableColumn<KhuyenMai, String> colNgayBD;
    @FXML private TableColumn<KhuyenMai, String> colNgayKT;
    @FXML private TableColumn<KhuyenMai, String> colTrangThai;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<KhuyenMai> data = DatabaseSeedData.khuyenMai();

    @FXML
    public void initialize() {
        colMaKM.setCellValueFactory(cellData -> cellData.getValue().maKMProperty());
        colTenKM.setCellValueFactory(cellData -> cellData.getValue().tenKMProperty());
        colLoai.setCellValueFactory(cellData -> cellData.getValue().loaiProperty());
        colGiaTri.setCellValueFactory(cellData -> cellData.getValue().giaTriProperty());
        colDieuKien.setCellValueFactory(cellData -> cellData.getValue().dieuKienProperty());
        colNgayBD.setCellValueFactory(cellData -> cellData.getValue().ngayBDProperty());
        colNgayKT.setCellValueFactory(cellData -> cellData.getValue().ngayKTProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        colLoai.setCellFactory(column -> badgeCell("GIAM_GIA", "TANG_QUA", "badge-active", "badge-purple", "badge-info"));
        colTrangThai.setCellFactory(column -> badgeCell("DANG_AP_DUNG", "HET_HAN", "badge-active", "badge-banned", "badge-default"));

        FilteredList<KhuyenMai> filtered = new FilteredList<>(data, item -> true);
        tbKhuyenMai.setItems(filtered);
        Runnable refreshFilter = () -> filtered.setPredicate(this::matchesFilter);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());

        btnDelete.disableProperty().bind(tbKhuyenMai.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbKhuyenMai.getSelectionModel().selectedItemProperty().isNull());
        updateStats();
    }

    private TableCell<KhuyenMai, String> badgeCell(String first, String second, String firstClass, String secondClass, String otherClass) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(item.equals(first) ? firstClass : item.equals(second) ? secondClass : otherClass);
                setText(null);
                setGraphic(badge);
            }
        };
    }

    private boolean matchesFilter(KhuyenMai item) {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        boolean matchText = keyword.isEmpty()
                || item.maKMProperty().get().toLowerCase().contains(keyword)
                || item.tenKMProperty().get().toLowerCase().contains(keyword)
                || item.loaiProperty().get().toLowerCase().contains(keyword);
        return matchText;
    }

    private void updateStats() {
        lblTongKM.setText(String.valueOf(data.size()));
        lblDangApDung.setText(String.valueOf(data.stream().filter(km -> km.trangThaiProperty().get().equals("DANG_AP_DUNG")).count()));
        lblHetHan.setText(String.valueOf(data.stream().filter(km -> km.trangThaiProperty().get().equals("HET_HAN")).count()));
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
        Label title = new Label(isUpdate ? "Cập Nhật Khuyến Mãi" : "Thêm Khuyến Mãi");
        title.getStyleClass().add("dialog-header-text");
        Button close = new Button("X");
        close.getStyleClass().add("dialog-close-btn");
        close.setOnAction(e -> stage.close());
        header.setLeft(title);
        header.setRight(close);

        GridPane grid = new GridPane();
        grid.setVgap(12);
        TextField txtTen = new TextField(isUpdate ? item.tenKMProperty().get() : "");
        ComboBox<String> cbLoai = new ComboBox<>(FXCollections.observableArrayList("GIAM_GIA", "TANG_QUA", "TANG_GIO"));
        cbLoai.setValue(isUpdate ? item.loaiProperty().get() : "GIAM_GIA");
        TextField txtGiaTri = new TextField(isUpdate ? item.giaTriProperty().get() : "");
        TextField txtDieuKien = new TextField(isUpdate ? item.dieuKienProperty().get() : "");
        TextField txtNgayBD = new TextField(isUpdate ? item.ngayBDProperty().get() : "2025-05-01");
        TextField txtNgayKT = new TextField(isUpdate ? item.ngayKTProperty().get() : "2025-05-31");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("DANG_AP_DUNG", "HET_HAN"));
        cbTrangThai.setValue(isUpdate ? item.trangThaiProperty().get() : "DANG_AP_DUNG");
        for (Control control : new Control[]{txtTen, cbLoai, txtGiaTri, txtDieuKien, txtNgayBD, txtNgayKT, cbTrangThai}) {
            control.setPrefWidth(410);
        }

        addRow(grid, "Tên khuyến mãi *", txtTen, 0);
        addRow(grid, "Loại", cbLoai, 2);
        addRow(grid, "Giá trị", txtGiaTri, 4);
        addRow(grid, "Điều kiện", txtDieuKien, 6);
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
                item.tenKMProperty().set(txtTen.getText());
                item.loaiProperty().set(cbLoai.getValue());
                item.giaTriProperty().set(txtGiaTri.getText());
                item.dieuKienProperty().set(txtDieuKien.getText());
                item.ngayBDProperty().set(txtNgayBD.getText());
                item.ngayKTProperty().set(txtNgayKT.getText());
                item.trangThaiProperty().set(cbTrangThai.getValue());
                tbKhuyenMai.refresh();
            } else {
                String maMoi = "KM" + String.format("%03d", data.size() + 1);
                data.add(new KhuyenMai(maMoi, txtTen.getText(), cbLoai.getValue(), txtGiaTri.getText(),
                        txtDieuKien.getText(), txtNgayBD.getText(), txtNgayKT.getText(), cbTrangThai.getValue()));
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
        confirmDelete("Bạn có chắc muốn xóa khuyến mãi này?", () -> {
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
