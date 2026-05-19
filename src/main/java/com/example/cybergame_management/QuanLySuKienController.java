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

public class QuanLySuKienController {
    @FXML private Label lblTongSK;
    @FXML private Label lblDangDienRa;
    @FXML private Label lblTongNguoi;
    @FXML private TextField txtSearch;
    @FXML private TableView<SuKien> tbSuKien;
    @FXML private TableColumn<SuKien, String> colMaSK;
    @FXML private TableColumn<SuKien, String> colTenSK;
    @FXML private TableColumn<SuKien, String> colNgayTC;
    @FXML private TableColumn<SuKien, String> colGioBD;
    @FXML private TableColumn<SuKien, String> colGioKT;
    @FXML private TableColumn<SuKien, String> colSoNguoi;
    @FXML private TableColumn<SuKien, String> colGiaiThuong;
    @FXML private TableColumn<SuKien, String> colTrangThai;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<SuKien> data = FXCollections.observableArrayList(
            new SuKien("SK001", "Giải Tốc Độ Gõ Phím", "2025-05-15", "09:00", "12:00", "24", "500.000đ", "SAP_DIEN_RA"),
            new SuKien("SK002", "Giải PUBG Tháng 5", "2025-05-10", "18:00", "22:00", "16", "1.000.000đ", "DANG_DIEN_RA"),
            new SuKien("SK003", "Liên Minh Huyền Thoại Cup", "2025-04-20", "14:00", "20:00", "30", "2.000.000đ", "DA_KET_THUC")
    );

    @FXML
    public void initialize() {
        colMaSK.setCellValueFactory(cellData -> cellData.getValue().maSKProperty());
        colTenSK.setCellValueFactory(cellData -> cellData.getValue().tenSKProperty());
        colNgayTC.setCellValueFactory(cellData -> cellData.getValue().ngayTCProperty());
        colGioBD.setCellValueFactory(cellData -> cellData.getValue().gioBDProperty());
        colGioKT.setCellValueFactory(cellData -> cellData.getValue().gioKTProperty());
        colSoNguoi.setCellValueFactory(cellData -> cellData.getValue().soNguoiProperty());
        colGiaiThuong.setCellValueFactory(cellData -> cellData.getValue().giaiThuongProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                if (item.equals("DANG_DIEN_RA")) {
                    badge.getStyleClass().add("badge-active");
                } else if (item.equals("SAP_DIEN_RA")) {
                    badge.getStyleClass().add("badge-warning");
                } else {
                    badge.getStyleClass().add("badge-default");
                }
                setText(null);
                setGraphic(badge);
            }
        });

        FilteredList<SuKien> filtered = new FilteredList<>(data, item -> true);
        tbSuKien.setItems(filtered);
        Runnable refreshFilter = () -> filtered.setPredicate(this::matchesFilter);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());

        btnDelete.disableProperty().bind(tbSuKien.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbSuKien.getSelectionModel().selectedItemProperty().isNull());
        updateStats();
    }

    private boolean matchesFilter(SuKien item) {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        boolean matchText = keyword.isEmpty()
                || item.maSKProperty().get().toLowerCase().contains(keyword)
                || item.tenSKProperty().get().toLowerCase().contains(keyword);
        return matchText;
    }

    private void updateStats() {
        lblTongSK.setText(String.valueOf(data.size()));
        lblDangDienRa.setText(String.valueOf(data.stream().filter(sk -> sk.trangThaiProperty().get().equals("DANG_DIEN_RA")).count()));
        int totalPeople = data.stream().mapToInt(sk -> parseInt(sk.soNguoiProperty().get())).sum();
        lblTongNguoi.setText(String.valueOf(totalPeople));
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    public void onInsertClick() {
        showEditor(null);
    }

    @FXML
    public void onUpdateClick() {
        SuKien selected = tbSuKien.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditor(selected);
        }
    }

    private void showEditor(SuKien item) {
        boolean isUpdate = item != null;
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(460);

        BorderPane header = new BorderPane();
        Label title = new Label(isUpdate ? "Cập Nhật Sự Kiện" : "Thêm Sự Kiện");
        title.getStyleClass().add("dialog-header-text");
        Button close = new Button("X");
        close.getStyleClass().add("dialog-close-btn");
        close.setOnAction(e -> stage.close());
        header.setLeft(title);
        header.setRight(close);

        GridPane grid = new GridPane();
        grid.setVgap(12);
        TextField txtTen = new TextField(isUpdate ? item.tenSKProperty().get() : "");
        TextField txtNgayTC = new TextField(isUpdate ? item.ngayTCProperty().get() : "2025-05-15");
        TextField txtGioBD = new TextField(isUpdate ? item.gioBDProperty().get() : "09:00");
        TextField txtGioKT = new TextField(isUpdate ? item.gioKTProperty().get() : "12:00");
        TextField txtSoNguoi = new TextField(isUpdate ? item.soNguoiProperty().get() : "0");
        TextField txtGiaiThuong = new TextField(isUpdate ? item.giaiThuongProperty().get() : "0đ");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("SAP_DIEN_RA", "DANG_DIEN_RA", "DA_KET_THUC"));
        cbTrangThai.setValue(isUpdate ? item.trangThaiProperty().get() : "SAP_DIEN_RA");
        for (Control control : new Control[]{txtTen, txtNgayTC, txtGioBD, txtGioKT, txtSoNguoi, txtGiaiThuong, cbTrangThai}) {
            control.setPrefWidth(410);
        }

        addRow(grid, "Tên sự kiện *", txtTen, 0);
        addRow(grid, "Ngày tổ chức", txtNgayTC, 2);
        addRow(grid, "Giờ bắt đầu", txtGioBD, 4);
        addRow(grid, "Giờ kết thúc", txtGioKT, 6);
        addRow(grid, "Số người", txtSoNguoi, 8);
        addRow(grid, "Giải thưởng", txtGiaiThuong, 10);
        addRow(grid, "Trạng thái", cbTrangThai, 12);

        Button cancel = new Button("Hủy");
        cancel.getStyleClass().add("btn-cancel");
        cancel.setOnAction(e -> stage.close());
        Button save = new Button("Lưu");
        save.getStyleClass().add("btn-save");
        save.setOnAction(e -> {
            if (isUpdate) {
                item.tenSKProperty().set(txtTen.getText());
                item.ngayTCProperty().set(txtNgayTC.getText());
                item.gioBDProperty().set(txtGioBD.getText());
                item.gioKTProperty().set(txtGioKT.getText());
                item.soNguoiProperty().set(txtSoNguoi.getText());
                item.giaiThuongProperty().set(txtGiaiThuong.getText());
                item.trangThaiProperty().set(cbTrangThai.getValue());
                tbSuKien.refresh();
            } else {
                String maMoi = "SK" + String.format("%03d", data.size() + 1);
                data.add(new SuKien(maMoi, txtTen.getText(), txtNgayTC.getText(), txtGioBD.getText(),
                        txtGioKT.getText(), txtSoNguoi.getText(), txtGiaiThuong.getText(), cbTrangThai.getValue()));
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
        SuKien selected = tbSuKien.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
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
        VBox text = new VBox(5, new Label("Xác nhận xóa"), new Label("Bạn có chắc muốn xóa sự kiện này?"));
        text.getChildren().get(0).getStyleClass().add("text-title");
        text.getChildren().get(1).getStyleClass().add("text-message");
        top.getChildren().addAll(iconCircle, text);
        Button cancel = new Button("Hủy");
        cancel.getStyleClass().add("btn-outline");
        cancel.setOnAction(e -> stage.close());
        Button delete = new Button("Xóa");
        delete.getStyleClass().add("btn-danger");
        delete.setOnAction(e -> {
            data.remove(selected);
            updateStats();
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
