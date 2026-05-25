package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QuanLySanPhamController {

    @FXML private Label lblTongSP;
    @FXML private Label lblConHang;
    @FXML private Label lblHetHang;
    @FXML private TableView<SanPham> tbSanPham;
    @FXML private TableColumn<SanPham, String> colMaSP;
    @FXML private TableColumn<SanPham, String> colTenSP;
    @FXML private TableColumn<SanPham, String> colLoai;
    @FXML private TableColumn<SanPham, String> colGia;
    @FXML private TableColumn<SanPham, String> colSoLuong;
    @FXML private TableColumn<SanPham, String> colDonVi;
    @FXML private TableColumn<SanPham, String> colSoDiemTichLuy;
    @FXML private TextField txtSearch;
    @FXML private Button btnInsert;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    private final ObservableList<SanPham> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (SanPhamRepository.isDatabaseEnabled()) {
                data.setAll(SanPhamRepository.findAll());
            } else {
                data.setAll(DatabaseSeedData.sanPham());
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            data.setAll(DatabaseSeedData.sanPham());
        }

        colMaSP.setCellValueFactory(cellData -> cellData.getValue().maSPProperty());
        colTenSP.setCellValueFactory(cellData -> cellData.getValue().tenSPProperty());
        colLoai.setCellValueFactory(cellData -> cellData.getValue().loaiProperty());
        colGia.setCellValueFactory(cellData -> cellData.getValue().giaProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colDonVi.setCellValueFactory(cellData -> cellData.getValue().donViProperty());
        colSoDiemTichLuy.setCellValueFactory(cellData -> cellData.getValue().soDiemTichLuyProperty());

        FilteredList<SanPham> filtered = new FilteredList<>(data, item -> true);
        tbSanPham.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
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

    private boolean matchesFilter(SanPham item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.maSPProperty().get(), item.tenSPProperty().get(), item.loaiProperty().get(),
                item.giaProperty().get(), item.soLuongProperty().get(), item.donViProperty().get(),
                item.soDiemTichLuyProperty().get());
    }

    private void updateStats() {
        lblTongSP.setText(String.valueOf(data.size()));
        lblConHang.setText(String.valueOf(data.stream()
                .filter(sp -> DisplayFormat.parseInt(sp.soLuongProperty().get()) > 0)
                .count()));
        lblHetHang.setText(String.valueOf(data.stream()
                .filter(sp -> DisplayFormat.parseInt(sp.soLuongProperty().get()) <= 0)
                .count()));
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbSanPham.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbSanPham.getSelectionModel().selectedItemProperty().isNull());
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
        Label lblTitle = new Label("Thêm Sản Phẩm");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("X");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtTen = new TextField();
        txtTen.setPrefWidth(400);
        TextField txtLoai = new TextField();
        TextField txtGia = new TextField("0");
        TextField txtSL = new TextField("0");
        TextField txtDonVi = new TextField("Cái");
        TextField txtDiem = new TextField("0");

        grid.add(new Label("Tên SP *"), 0, 0);
        grid.add(txtTen, 0, 1);
        grid.add(new Label("Loại SP"), 0, 2);
        grid.add(txtLoai, 0, 3);
        grid.add(new Label("Đơn giá"), 0, 4);
        grid.add(txtGia, 0, 5);
        grid.add(new Label("Số Lượng Tồn Kho"), 0, 6);
        grid.add(txtSL, 0, 7);
        grid.add(new Label("Đơn Vị"), 0, 8);
        grid.add(txtDonVi, 0, 9);
        grid.add(new Label("Số Điểm Tích Lũy"), 0, 10);
        grid.add(txtDiem, 0, 11);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String slStr = txtSL.getText().trim();
            try {
                long sl = Long.parseLong(slStr);
                if (sl < 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo ràng buộc");
                    alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R3");
                    alert.setContentText("Số lượng tồn kho của sản phẩm phải lớn hơn hoặc bằng 0!");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Dữ liệu không hợp lệ");
                alert.setContentText("Số lượng tồn kho phải là một số nguyên hợp lệ!");
                alert.showAndWait();
                return;
            }

            String maMoi = SanPhamRepository.getNextMaSP();
            SanPham newSP = new SanPham(maMoi, txtTen.getText(), txtLoai.getText(), txtGia.getText(),
                    slStr, txtDonVi.getText(), txtDiem.getText());
            try {
                if (SanPhamRepository.isDatabaseEnabled()) {
                    SanPhamRepository.insert(newSP);
                }
                data.add(newSP);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu sản phẩm vào Database: " + ex.getMessage());
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
        SanPham selectedItem = tbSanPham.getSelectionModel().getSelectedItem();
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

        Label lblIcon = new Label("X");
        lblIcon.getStyleClass().add("icon-trash");
        StackPane iconCircle = new StackPane(lblIcon);
        iconCircle.getStyleClass().add("icon-circle");

        VBox textCtn = new VBox(5);
        Label lblTitle = new Label("Xác nhận xóa");
        lblTitle.getStyleClass().add("text-title");
        Label lblMessage = new Label("Bạn có chắc muốn xóa sản phẩm này?");
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
                if (SanPhamRepository.isDatabaseEnabled()) {
                    SanPhamRepository.softDelete(selectedItem.maSPProperty().get());
                }
                data.remove(selectedItem);
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa sản phẩm từ Database: " + ex.getMessage());
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
        SanPham selectedItem = tbSanPham.getSelectionModel().getSelectedItem();
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
        Label lblTitle = new Label("Cập Nhật Sản Phẩm");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("X");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtMa = new TextField(selectedItem.maSPProperty().get());
        txtMa.setDisable(true);
        txtMa.setPrefWidth(400);

        TextField txtTen = new TextField(selectedItem.tenSPProperty().get());
        TextField txtLoai = new TextField(selectedItem.loaiProperty().get());
        TextField txtGia = new TextField(selectedItem.giaProperty().get());
        TextField txtSL = new TextField(selectedItem.soLuongProperty().get());
        TextField txtDonVi = new TextField(selectedItem.donViProperty().get());
        TextField txtDiem = new TextField(selectedItem.soDiemTichLuyProperty().get());

        grid.add(new Label("Mã SP"), 0, 0);
        grid.add(txtMa, 0, 1);
        grid.add(new Label("Tên SP *"), 0, 2);
        grid.add(txtTen, 0, 3);
        grid.add(new Label("Loại SP"), 0, 4);
        grid.add(txtLoai, 0, 5);
        grid.add(new Label("Đơn giá"), 0, 6);
        grid.add(txtGia, 0, 7);
        grid.add(new Label("Số Lượng Tồn Kho"), 0, 8);
        grid.add(txtSL, 0, 9);
        grid.add(new Label("Đơn Vị"), 0, 10);
        grid.add(txtDonVi, 0, 11);
        grid.add(new Label("Số Điểm Tích Lũy"), 0, 12);
        grid.add(txtDiem, 0, 13);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String slStr = txtSL.getText().trim();
            try {
                long sl = Long.parseLong(slStr);
                if (sl < 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo ràng buộc");
                    alert.setHeaderText("Vi phạm ràng buộc toàn vẹn R3");
                    alert.setContentText("Số lượng tồn kho của sản phẩm phải lớn hơn hoặc bằng 0!");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo ràng buộc");
                alert.setHeaderText("Dữ liệu không hợp lệ");
                alert.setContentText("Số lượng tồn kho phải là một số nguyên hợp lệ!");
                alert.showAndWait();
                return;
            }

            String oldTen = selectedItem.tenSPProperty().get();
            String oldLoai = selectedItem.loaiProperty().get();
            String oldGia = selectedItem.giaProperty().get();
            String oldSL = selectedItem.soLuongProperty().get();
            String oldDonVi = selectedItem.donViProperty().get();
            String oldDiem = selectedItem.soDiemTichLuyProperty().get();

            selectedItem.tenSPProperty().set(txtTen.getText());
            selectedItem.loaiProperty().set(txtLoai.getText());
            selectedItem.giaProperty().set(txtGia.getText());
            selectedItem.soLuongProperty().set(slStr);
            selectedItem.donViProperty().set(txtDonVi.getText());
            selectedItem.soDiemTichLuyProperty().set(txtDiem.getText());

            try {
                if (SanPhamRepository.isDatabaseEnabled()) {
                    SanPhamRepository.update(selectedItem);
                }
                tbSanPham.refresh();
                updateStats();
                stage.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                selectedItem.tenSPProperty().set(oldTen);
                selectedItem.loaiProperty().set(oldLoai);
                selectedItem.giaProperty().set(oldGia);
                selectedItem.soLuongProperty().set(oldSL);
                selectedItem.donViProperty().set(oldDonVi);
                selectedItem.soDiemTichLuyProperty().set(oldDiem);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật sản phẩm vào Database: " + ex.getMessage());
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
