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

import java.sql.SQLException;

public class QuanLyQuaTangController {

    @FXML private Label lblTongQuaTang;
    @FXML private Label lblDiemCaoNhat;
    @FXML private Label lblDiemThapNhat;

    @FXML private TableView<QuaTang> tbQuaTang;
    @FXML private TableColumn<QuaTang, String> colMaQT;
    @FXML private TableColumn<QuaTang, String> colNoiDung;
    @FXML private TableColumn<QuaTang, String> colSoDiemTieuHao;

    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private ObservableList<QuaTang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (DatabaseConnection.isConfigured()) {
                data = QuaTangRepository.findAll();
            } else {
                data = DatabaseSeedData.quaTang();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            data = DatabaseSeedData.quaTang();
        }

        colMaQT.setCellValueFactory(cellData -> cellData.getValue().maQTProperty());
        colNoiDung.setCellValueFactory(cellData -> cellData.getValue().noiDungProperty());
        colSoDiemTieuHao.setCellValueFactory(cellData -> cellData.getValue().soDiemTieuHaoProperty());

        colSoDiemTieuHao.setCellFactory(column -> new TableCell<QuaTang, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item + " ⭐");
                    badge.getStyleClass().add("point-badge");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        FilteredList<QuaTang> filtered = new FilteredList<>(data, item -> true);
        tbQuaTang.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));

        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(QuaTang item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaQT(), item.getNoiDung(), item.getSoDiemTieuHao());
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbQuaTang.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbQuaTang.getSelectionModel().selectedItemProperty().isNull());
    }

    private void updateStats() {
        lblTongQuaTang.setText(String.valueOf(data.size()));
        
        long maxDiem = data.stream()
                .mapToLong(qt -> {
                    try {
                        return Long.parseLong(qt.getSoDiemTieuHao().trim());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        long minDiem = data.stream()
                .mapToLong(qt -> {
                    try {
                        return Long.parseLong(qt.getSoDiemTieuHao().trim());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .min()
                .orElse(0);

        lblDiemCaoNhat.setText(String.valueOf(maxDiem));
        lblDiemThapNhat.setText(String.valueOf(minDiem));
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
        Label lblTitle = new Label("Thêm Quà Tặng Mới");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtMaQT = new TextField(); txtMaQT.setPromptText("Ví dụ: QT06");
        TextField txtNoiDung = new TextField(); txtNoiDung.setPromptText("Ví dụ: Nước ngọt Sting dâu");
        TextField txtSoDiem = new TextField(); txtSoDiem.setPromptText("Ví dụ: 30");

        for (Control c : new Control[]{txtMaQT, txtNoiDung, txtSoDiem}) {
            c.setPrefWidth(400);
        }

        grid.add(new Label("Mã Quà Tặng *"), 0, 0); grid.add(txtMaQT, 0, 1);
        grid.add(new Label("Nội Dung *"), 0, 2); grid.add(txtNoiDung, 0, 3);
        grid.add(new Label("Số Điểm Tiêu Hao *"), 0, 4); grid.add(txtSoDiem, 0, 5);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String ma = txtMaQT.getText().trim().toUpperCase();
            String noiDung = txtNoiDung.getText().trim();
            String soDiemStr = txtSoDiem.getText().trim();

            if (ma.isEmpty() || noiDung.isEmpty() || soDiemStr.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                alert.showAndWait();
                return;
            }

            // Kiểm tra trùng mã
            boolean exists = data.stream().anyMatch(qt -> qt.getMaQT().equalsIgnoreCase(ma));
            if (exists) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Mã quà tặng đã tồn tại trên hệ thống!");
                alert.showAndWait();
                return;
            }

            long soDiem;
            try {
                soDiem = Long.parseLong(soDiemStr);
                if (soDiem <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Số điểm tiêu hao phải là một số nguyên dương (> 0)!");
                alert.showAndWait();
                return;
            }

            QuaTang newQT = new QuaTang(ma, noiDung, String.valueOf(soDiem));
            try {
                if (DatabaseConnection.isConfigured()) {
                    QuaTangRepository.insert(newQT);
                }
                data.add(newQT);
                updateStats();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu quà tặng vào Database: " + ex.getMessage());
                alert.showAndWait();
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
        QuaTang selectedItem = tbQuaTang.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa quà tặng này?");
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
                    QuaTangRepository.delete(selectedItem.getMaQT());
                }
                data.remove(selectedItem);
                updateStats();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa quà tặng: " + ex.getMessage());
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
    public void onUpdateClick() {
        QuaTang selectedItem = tbQuaTang.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Cập Nhật Quà Tặng");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtNoiDung = new TextField(selectedItem.getNoiDung());
        TextField txtSoDiem = new TextField(selectedItem.getSoDiemTieuHao());

        for (Control c : new Control[]{txtNoiDung, txtSoDiem}) {
            c.setPrefWidth(400);
        }

        grid.add(new Label("Nội Dung Chi Tiết *"), 0, 0); grid.add(txtNoiDung, 0, 1);
        grid.add(new Label("Số Điểm Tiêu Hao *"), 0, 2); grid.add(txtSoDiem, 0, 3);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String noiDung = txtNoiDung.getText().trim();
            String soDiemStr = txtSoDiem.getText().trim();

            if (noiDung.isEmpty() || soDiemStr.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                alert.showAndWait();
                return;
            }

            long soDiem;
            try {
                soDiem = Long.parseLong(soDiemStr);
                if (soDiem <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Số điểm tiêu hao phải là một số nguyên dương (> 0)!");
                alert.showAndWait();
                return;
            }

            selectedItem.setNoiDung(noiDung);
            selectedItem.setSoDiemTieuHao(String.valueOf(soDiem));

            try {
                if (DatabaseConnection.isConfigured()) {
                    QuaTangRepository.update(selectedItem);
                }
                tbQuaTang.refresh();
                updateStats();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật quà tặng vào Database: " + ex.getMessage());
                alert.showAndWait();
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
