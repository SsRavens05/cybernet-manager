package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
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

public class QuanLyTaiKhoanController {

    @FXML private Label lblTongTK;
    @FXML private Label lblAdminCount;
    @FXML private Label lblNhanVienHoatDongCount;

    @FXML private TableView<TaiKhoan> tbTaiKhoan;
    @FXML private TableColumn<TaiKhoan, String> colTenDangNhap;
    @FXML private TableColumn<TaiKhoan, String> colMatKhau;
    @FXML private TableColumn<TaiKhoan, String> colVaiTro;
    @FXML private TableColumn<TaiKhoan, String> colTrangThai;
    @FXML private TableColumn<TaiKhoan, String> colNgayTao;

    @FXML private ComboBox<String> cbVaiTroFilter;
    @FXML private ComboBox<String> cbTrangThaiFilter;
    @FXML private TextField txtSearch;

    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<TaiKhoan> data = DatabaseSeedData.taiKhoan();

    @FXML
    public void initialize() {
        colTenDangNhap.setCellValueFactory(cellData -> cellData.getValue().tenDangNhapProperty());
        colMatKhau.setCellValueFactory(cellData -> cellData.getValue().matKhauProperty());
        colVaiTro.setCellValueFactory(cellData -> cellData.getValue().vaiTroProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());
        colNgayTao.setCellValueFactory(cellData -> cellData.getValue().ngayTaoProperty());

        // Cell factory for Password: Mask with bullets
        colMatKhau.setCellFactory(column -> new TableCell<TaiKhoan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("•".repeat(Math.max(5, item.length())));
                }
            }
        });

        // Cell factory for Role: Custom badges
        colVaiTro.setCellFactory(column -> new TableCell<TaiKhoan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if (item.equalsIgnoreCase("Admin")) {
                        badge.getStyleClass().add("badge-admin");
                    } else {
                        badge.getStyleClass().add("badge-staff");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Cell factory for Status: Custom badges
        colTrangThai.setCellFactory(column -> new TableCell<TaiKhoan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if (item.equalsIgnoreCase("Hoạt động")) {
                        badge.getStyleClass().add("badge-active");
                    } else {
                        badge.getStyleClass().add("badge-default");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Setup filter choices
        cbVaiTroFilter.getItems().setAll("Tất cả vai trò", "Admin", "Nhân viên");
        cbVaiTroFilter.setValue("Tất cả vai trò");

        cbTrangThaiFilter.getItems().setAll("Tất cả trạng thái", "Hoạt động", "Vô hiệu");
        cbTrangThaiFilter.setValue("Tất cả trạng thái");

        FilteredList<TaiKhoan> filtered = new FilteredList<>(data, item -> true);
        tbTaiKhoan.setItems(filtered);

        // Bind filter change events
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> applyFilters(filtered));
        cbVaiTroFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters(filtered));
        cbTrangThaiFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters(filtered));

        bindActionButtons();
        updateStats();
    }

    private void applyFilters(FilteredList<TaiKhoan> filtered) {
        filtered.setPredicate(item -> {
            // Text Search
            String text = txtSearch.getText();
            boolean matchesText = SearchMatcher.containsKeyword(text, item.getTenDangNhap());

            // Role Filter
            String roleFilter = cbVaiTroFilter.getValue();
            boolean matchesRole = roleFilter == null || roleFilter.equals("Tất cả vai trò") ||
                    item.getVaiTro().equalsIgnoreCase(roleFilter);

            // Status Filter
            String statusFilter = cbTrangThaiFilter.getValue();
            boolean matchesStatus = statusFilter == null || statusFilter.equals("Tất cả trạng thái") ||
                    item.getTrangThai().equalsIgnoreCase(statusFilter);

            return matchesText && matchesRole && matchesStatus;
        });
    }

    private void updateStats() {
        lblTongTK.setText(String.valueOf(data.size()));
        
        lblAdminCount.setText(String.valueOf(data.stream()
                .filter(tk -> tk.getVaiTro().equalsIgnoreCase("Admin"))
                .count()));
                
        lblNhanVienHoatDongCount.setText(String.valueOf(data.stream()
                .filter(tk -> tk.getVaiTro().equalsIgnoreCase("Nhân viên") && tk.getTrangThai().equalsIgnoreCase("Hoạt động"))
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbTaiKhoan.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbTaiKhoan.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Tài Khoản");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nhập tên đăng nhập...");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Nhập mật khẩu...");

        ComboBox<String> cbVaiTro = new ComboBox<>(FXCollections.observableArrayList("Admin", "Nhân viên"));
        cbVaiTro.setValue("Nhân viên");

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Hoạt động", "Vô hiệu"));
        cbTrangThai.setValue("Hoạt động");

        grid.add(new Label("Tên đăng nhập *"), 0, 0);
        grid.add(txtUsername, 0, 1);
        grid.add(new Label("Mật khẩu *"), 0, 2);
        grid.add(txtPass, 0, 3);
        grid.add(new Label("Vai trò"), 0, 4);
        grid.add(cbVaiTro, 0, 5);
        grid.add(new Label("Trạng thái"), 0, 6);
        grid.add(cbTrangThai, 0, 7);

        // Styling inline popup elements
        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else if (n instanceof TextField) {
                TextField tf = (TextField) n;
                tf.setPrefHeight(38.0);
                tf.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
            } else if (n instanceof ComboBox) {
                ComboBox<?> cb = (ComboBox<?>) n;
                cb.setPrefHeight(38.0);
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
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
            String username = txtUsername.getText();
            String password = txtPass.getText();

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                alert.showAndWait();
                return;
            }

            String cleanUser = username.trim().toLowerCase();
            boolean exists = data.stream().anyMatch(tk -> tk.getTenDangNhap().toLowerCase().equals(cleanUser));
            if (exists) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tên đăng nhập đã tồn tại trong hệ thống!");
                alert.showAndWait();
                return;
            }

            data.add(new TaiKhoan(cleanUser, password, cbVaiTro.getValue(), cbTrangThai.getValue(), java.time.LocalDate.now().toString()));
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
    public void onUpdateClick() {
        TaiKhoan selected = tbTaiKhoan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Cập Nhật Tài Khoản");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField txtUsername = new TextField(selected.getTenDangNhap());
        txtUsername.setEditable(false);

        PasswordField txtPass = new PasswordField();
        txtPass.setText(selected.getMatKhau());

        ComboBox<String> cbVaiTro = new ComboBox<>(FXCollections.observableArrayList("Admin", "Nhân viên"));
        cbVaiTro.setValue(selected.getVaiTro());

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Hoạt động", "Vô hiệu"));
        cbTrangThai.setValue(selected.getTrangThai());

        grid.add(new Label("Tên đăng nhập (Khóa)"), 0, 0);
        grid.add(txtUsername, 0, 1);
        grid.add(new Label("Mật khẩu *"), 0, 2);
        grid.add(txtPass, 0, 3);
        grid.add(new Label("Vai trò"), 0, 4);
        grid.add(cbVaiTro, 0, 5);
        grid.add(new Label("Trạng thái"), 0, 6);
        grid.add(cbTrangThai, 0, 7);

        // Styling inline popup elements
        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else if (n instanceof TextField) {
                TextField tf = (TextField) n;
                tf.setPrefHeight(38.0);
                if (tf == txtUsername) {
                    tf.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #6b7280;");
                } else {
                    tf.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
                }
            } else if (n instanceof ComboBox) {
                ComboBox<?> cb = (ComboBox<?>) n;
                cb.setPrefHeight(38.0);
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
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
            String password = txtPass.getText();

            if (password == null || password.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Mật khẩu không được để trống!");
                alert.showAndWait();
                return;
            }

            selected.setMatKhau(password);
            selected.setVaiTro(cbVaiTro.getValue());
            selected.setTrangThai(cbTrangThai.getValue());

            tbTaiKhoan.refresh();
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
        TaiKhoan selected = tbTaiKhoan.getSelectionModel().getSelectedItem();
        if (selected == null) {
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
        Label lblMessage = new Label("Bạn có chắc chắn muốn xóa tài khoản này?");
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
            data.remove(selected);
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
}
