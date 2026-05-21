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
import java.text.NumberFormat;
import java.util.Locale;

public class QuanLyLoaiNhanVienController {

    @FXML private Label lblTongLoaiNV;
    @FXML private Label lblLuongTrungBinh;
    @FXML private Label lblLuongCaoNhat;

    @FXML private TableView<LoaiNhanVien> tbLoaiNhanVien;
    @FXML private TableColumn<LoaiNhanVien, String> colMaLoaiNV;
    @FXML private TableColumn<LoaiNhanVien, String> colViTri;
    @FXML private TableColumn<LoaiNhanVien, String> colMucLuong;

    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<LoaiNhanVien> data = DatabaseSeedData.loaiNhanVien();

    @FXML
    public void initialize() {
        colMaLoaiNV.setCellValueFactory(cellData -> cellData.getValue().maLoaiNVProperty());
        colViTri.setCellValueFactory(cellData -> cellData.getValue().viTriProperty());
        colMucLuong.setCellValueFactory(cellData -> cellData.getValue().mucLuongProperty());

        // Custom formatting for salary column
        colMucLuong.setCellFactory(column -> new TableCell<LoaiNhanVien, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    long value = DisplayFormat.parseMoney(item);
                    setText(DisplayFormat.money(value));
                }
            }
        });

        FilteredList<LoaiNhanVien> filtered = new FilteredList<>(data, item -> true);
        tbLoaiNhanVien.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(LoaiNhanVien item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaLoaiNV(), item.getViTri(), item.getMucLuong());
    }

    private void updateStats() {
        lblTongLoaiNV.setText(String.valueOf(data.size()));

        double avgSalary = data.stream()
                .mapToLong(lnv -> DisplayFormat.parseMoney(lnv.getMucLuong()))
                .average()
                .orElse(0);
        lblLuongTrungBinh.setText(DisplayFormat.money((long) avgSalary));

        long maxSalary = data.stream()
                .mapToLong(lnv -> DisplayFormat.parseMoney(lnv.getMucLuong()))
                .max()
                .orElse(0);
        lblLuongCaoNhat.setText(DisplayFormat.money(maxSalary));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbLoaiNhanVien.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbLoaiNhanVien.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Loại Nhân Viên");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtViTri = new TextField();
        txtViTri.setPromptText("Ví dụ: Quản Lý, Thu Ngân...");
        TextField txtMucLuong = new TextField();
        txtMucLuong.setPromptText("Ví dụ: 8.000.000");

        grid.add(new Label("Vị trí / Chức danh *"), 0, 0); grid.add(txtViTri, 0, 1);
        grid.add(new Label("Mức lương *"), 0, 2); grid.add(txtMucLuong, 0, 3);

        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 13px;");
            } else if (n instanceof TextField) {
                TextField tf = (TextField) n;
                tf.setPrefHeight(40.0);
                tf.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 15 0 15;");
            }
        }

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            if (txtViTri.getText().trim().isEmpty() || txtMucLuong.getText().trim().isEmpty()) {
                return;
            }
            String maMoi = "LNV" + String.format("%03d", data.size() + 1);
            long luongVal = DisplayFormat.parseMoney(txtMucLuong.getText());
            data.add(new LoaiNhanVien(maMoi, txtViTri.getText().trim(), DisplayFormat.money(luongVal)));
            updateStats();
            stage.close();
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
        LoaiNhanVien selectedItem = tbLoaiNhanVien.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa loại nhân viên này?");
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
        LoaiNhanVien selectedItem = tbLoaiNhanVien.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-loai-nhan-vien.fxml"));
            Parent root = loader.load();

            UpdateLoaiNhanVienController updateCtrl = loader.getController();
            updateCtrl.setLoaiNhanVienData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Loại Nhân Viên");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.showAndWait();

            tbLoaiNhanVien.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
