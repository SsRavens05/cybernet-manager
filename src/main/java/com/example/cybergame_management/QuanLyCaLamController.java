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

public class QuanLyCaLamController {

    @FXML private Label lblTongCa;
    @FXML private Label lblDangTrongCa;
    @FXML private Label lblCaSapToi;

    @FXML private TableView<CaLam> tbCaLam;
    @FXML private TableColumn<CaLam, String> colMaCa;
    @FXML private TableColumn<CaLam, String> colThoiGianBD;
    @FXML private TableColumn<CaLam, String> colThoiGianKT;
    @FXML private TableColumn<CaLam, String> colSoGioLam;
    @FXML private TableColumn<CaLam, String> colTrangThai;
    @FXML private TableColumn<CaLam, String> colSoGioTangCa;

    @FXML private TextField txtSearch;
    @FXML private Button btnInsert;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<CaLam> data = DatabaseSeedData.caLamShifts();

    @FXML
    public void initialize() {
        colMaCa.setCellValueFactory(cellData -> cellData.getValue().maCaProperty());
        colThoiGianBD.setCellValueFactory(cellData -> cellData.getValue().thoiGianBDProperty());
        colThoiGianKT.setCellValueFactory(cellData -> cellData.getValue().thoiGianKTProperty());
        colSoGioLam.setCellValueFactory(cellData -> cellData.getValue().soGioLamProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colSoGioTangCa.setCellValueFactory(cellData -> cellData.getValue().soGioTangCaProperty());

        // Custom badges for TrangThai Column
        colTrangThai.setCellFactory(column -> new TableCell<CaLam, String>() {
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
                    } else if (item.equals("Đã kết thúc") || item.equals("DA_KET_THUC")) {
                        badge.getStyleClass().add("badge-default");
                    } else if (item.equals("Sắp tới") || item.equals("SAP_TOI")) {
                        badge.getStyleClass().add("badge-warning");
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Custom styling for Overtime Hours column: "+1h", "+2h" render in bold red/orange
        colSoGioTangCa.setCellFactory(column -> new TableCell<CaLam, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("+")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        FilteredList<CaLam> filtered = new FilteredList<>(data, item -> true);
        tbCaLam.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> applyFilters(filtered));

        bindActionButtons();
        updateStats();

        if (!UserSession.isAdmin()) {
            if (btnInsert != null) {
                btnInsert.setVisible(false);
                btnInsert.setManaged(false);
            }
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

    private void applyFilters(FilteredList<CaLam> filtered) {
        filtered.setPredicate(item -> {
            // Text Filter
            String text = txtSearch.getText();
            return SearchMatcher.containsKeyword(text,
                    item.getMaCa(), item.getThoiGianBD(), item.getThoiGianKT(), item.getSoGioLam(), item.getSoGioTangCa());
        });
    }

    private void updateStats() {
        lblTongCa.setText(String.valueOf(data.size()));
        lblDangTrongCa.setText(String.valueOf(data.stream()
                .filter(c -> c.getTrangThai().equalsIgnoreCase("Đang làm") || c.getTrangThai().equalsIgnoreCase("DANG_LAM"))
                .count()));
        lblCaSapToi.setText(String.valueOf(data.stream()
                .filter(c -> c.getTrangThai().equalsIgnoreCase("Sắp tới") || c.getTrangThai().equalsIgnoreCase("SAP_TOI"))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbCaLam.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbCaLam.getSelectionModel().selectedItemProperty().isNull());
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
        Label lblTitle = new Label("Thêm Ca Làm Việc");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕"); btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle); header.setRight(btnX);

        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(15);
        TextField txtBD = new TextField(); txtBD.setPromptText("Ví dụ: 08:00");
        TextField txtKT = new TextField(); txtKT.setPromptText("Ví dụ: 14:00");
        TextField txtSoGio = new TextField("6h");
        TextField txtTangCa = new TextField("—");
        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Đang làm", "Sắp tới", "Đã kết thúc"));
        cbTrangThai.setValue("Sắp tới");

        grid.add(new Label("Mã Ca (Tự động)"), 0, 0); grid.add(new TextField("CA" + String.format("%03d", data.size()+1)), 0, 1);
        grid.add(new Label("Giờ Bắt Đầu *"), 0, 2); grid.add(txtBD, 0, 3);
        grid.add(new Label("Giờ Kết Thúc *"), 0, 4); grid.add(txtKT, 0, 5);
        grid.add(new Label("Số Giờ Làm"), 0, 6); grid.add(txtSoGio, 0, 7);
        grid.add(new Label("Số Giờ Tăng Ca"), 0, 8); grid.add(txtTangCa, 0, 9);
        grid.add(new Label("Trạng thái"), 0, 10); grid.add(cbTrangThai, 0, 11);

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
            if (txtBD.getText().trim().isEmpty() || txtKT.getText().trim().isEmpty()) {
                return;
            }
            String bd = txtBD.getText().trim();
            String kt = txtKT.getText().trim();
            String sg = txtSoGio.getText().trim().isEmpty() ? "6h" : txtSoGio.getText().trim();
            String tc = txtTangCa.getText().trim().isEmpty() ? "—" : txtTangCa.getText().trim();

            if (!validateThoiGianCaLam(bd, kt)) {
                return;
            }

            try {
                String clean = sg.replaceAll("[^0-9.-]", "").trim();
                if (clean.isEmpty()) {
                    throw new NumberFormatException();
                }
                double gio = Double.parseDouble(clean);
                if (gio <= 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo ràng buộc");
                    alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R4");
                    alert.setContentText("Số giờ làm của ca làm phải lớn hơn 0!");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Dữ liệu không hợp lệ");
                alert.setContentText("Số giờ làm phải là một số hợp lệ!");
                alert.showAndWait();
                return;
            }

            data.add(new CaLam("CA" + String.format("%03d", data.size()+1), bd, kt, sg, cbTrangThai.getValue(), tc));
            updateStats();
            stage.close();
        });
        footer.getChildren().addAll(bHuy, bLuu);

        root.getChildren().addAll(header, grid, footer);
        Scene scene = new Scene(root); scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene); stage.showAndWait();
    }

    private boolean validateThoiGianCaLam(String bd, String kt) {
        try {
            java.time.LocalTime tBD = java.time.LocalTime.parse(bd);
            java.time.LocalTime tKT = java.time.LocalTime.parse(kt);
            if (!tKT.isAfter(tBD)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R7");
                alert.setContentText("Thời gian kết thúc ca làm phải lớn hơn thời gian bắt đầu ca làm!");
                alert.showAndWait();
                return false;
            }
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dữ liệu không hợp lệ");
            alert.setHeaderText("Sai định dạng thời gian");
            alert.setContentText("Thời gian phải đúng định dạng HH:mm (Ví dụ: 08:00)!");
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    public void onDeleteClick() {
        CaLam selectedItem = tbCaLam.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa ca làm này?");
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
        CaLam selectedItem = tbCaLam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-ca-lam.fxml"));
            Parent root = loader.load();

            UpdateCaLamController updateCtrl = loader.getController();
            updateCtrl.setCaLamData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Ca Làm - CyberNet Manager");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.showAndWait();

            tbCaLam.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
