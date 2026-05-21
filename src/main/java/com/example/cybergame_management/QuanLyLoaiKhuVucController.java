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

public class QuanLyLoaiKhuVucController {

    @FXML private Label lblTongLoaiKV;
    @FXML private Label lblTongSoLuong;
    @FXML private Label lblGiaThapNhat;
    
    @FXML private TableView<LoaiKhuVuc> tbLoaiKhuVuc;
    @FXML private TableColumn<LoaiKhuVuc, String> colMaLoaiKV;
    @FXML private TableColumn<LoaiKhuVuc, String> colTenLoaiKV;
    @FXML private TableColumn<LoaiKhuVuc, String> colSoLuong;
    @FXML private TableColumn<LoaiKhuVuc, String> colGia;
    @FXML private TableColumn<LoaiKhuVuc, String> colNgayTao;
    
    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;
    
    private final ObservableList<LoaiKhuVuc> data = DatabaseSeedData.loaiKhuVuc();

    @FXML
    public void initialize() {
        // Ánh xạ dữ liệu
        colMaLoaiKV.setCellValueFactory(cellData -> cellData.getValue().maLoaiKVProperty());
        colTenLoaiKV.setCellValueFactory(cellData -> cellData.getValue().tenLoaiKVProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());
        colGia.setCellValueFactory(cellData -> cellData.getValue().giaProperty());
        colNgayTao.setCellValueFactory(cellData -> cellData.getValue().ngayTaoProperty());

        // Định dạng cột Giá thành "15.000 đ"
        colGia.setCellFactory(column -> new TableCell<LoaiKhuVuc, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    long value = DisplayFormat.parseMoney(item);
                    String formatted = NumberFormat.getInstance(new Locale("vi", "VN")).format(value) + " đ";
                    setText(formatted);
                }
            }
        });

        FilteredList<LoaiKhuVuc> filtered = new FilteredList<>(data, item -> true);
        tbLoaiKhuVuc.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filtered.setPredicate(this::matchesFilter));
        bindActionButtons();
        updateStats();
    }

    private boolean matchesFilter(LoaiKhuVuc item) {
        return SearchMatcher.containsKeyword(txtSearch.getText(),
                item.getMaLoaiKV(), item.getTenLoaiKV(), item.getSoLuong(), item.getGia(), item.getNgayTao());
    }

    private void updateStats() {
        lblTongLoaiKV.setText(String.valueOf(data.size()));
        
        int totalQty = data.stream().mapToInt(lkv -> DisplayFormat.parseInt(lkv.getSoLuong())).sum();
        lblTongSoLuong.setText(String.valueOf(totalQty));
        
        long minPrice = data.stream()
                .mapToLong(lkv -> DisplayFormat.parseMoney(lkv.getGia()))
                .min()
                .orElse(0);
        String minPriceFormatted = NumberFormat.getInstance(new Locale("vi", "VN")).format(minPrice);
        lblGiaThapNhat.setText(minPriceFormatted);
    }

    private void bindActionButtons() {
        btnDelete.disableProperty().bind(tbLoaiKhuVuc.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tbLoaiKhuVuc.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void onInsertClick() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        // Khung nền popup
        VBox root = new VBox(20);
        root.getStyleClass().add("custom-dialog");
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        // Header
        BorderPane header = new BorderPane();
        Label lblTitle = new Label("Thêm Loại Khu Vực");
        lblTitle.getStyleClass().add("dialog-header-text");
        Button btnX = new Button("✕");
        btnX.getStyleClass().add("dialog-close-btn");
        btnX.setOnAction(e -> stage.close());
        header.setLeft(lblTitle);
        header.setRight(btnX);

        // Body (Các ô nhập liệu)
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtTenLoaiKV = new TextField(); txtTenLoaiKV.setPrefWidth(400);
        txtTenLoaiKV.setPromptText("Ví dụ: VIP, Thường...");
        TextField txtSoLuong = new TextField("0");
        TextField txtGia = new TextField("0");

        grid.add(new Label("Tên Loại Khu Vực *"), 0, 0); grid.add(txtTenLoaiKV, 0, 1);
        grid.add(new Label("Số Lượng"), 0, 2); grid.add(txtSoLuong, 0, 3);
        grid.add(new Label("Giá Thuê/h"), 0, 4); grid.add(txtGia, 0, 5);

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button bHuy = new Button("Hủy");
        bHuy.getStyleClass().add("btn-cancel");
        bHuy.setOnAction(e -> stage.close());

        Button bLuu = new Button("Lưu");
        bLuu.getStyleClass().add("btn-save");
        bLuu.setOnAction(e -> {
            String maMoi = "LKV" + String.format("%03d", data.size() + 1);
            String ngayTao = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            data.add(new LoaiKhuVuc(maMoi, txtTenLoaiKV.getText(), txtSoLuong.getText(), txtGia.getText(), ngayTao));
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
        LoaiKhuVuc selectedItem = tbLoaiKhuVuc.getSelectionModel().getSelectedItem();
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
        Label lblMessage = new Label("Bạn có chắc muốn xóa loại khu vực này?");
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
        LoaiKhuVuc selectedItem = tbLoaiKhuVuc.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-loai-khu-vuc.fxml"));
            Parent root = loader.load();

            UpdateLoaiKhuVucController updateCtrl = loader.getController();
            updateCtrl.setLoaiKhuVucData(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Loại Khu Vực");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tbLoaiKhuVuc.refresh();
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
