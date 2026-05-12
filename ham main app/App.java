package com.mycompany.test5;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class App extends Application {

    private BorderPane mainLayout;
    private VBox sidebar;
    private Button activeMenuBtn = null;

    private static final String SIDEBAR_BG     = "#8B0000";
    private static final String SIDEBAR_ACTIVE = "#A30000";
    private static final String SIDEBAR_HOVER  = "#700000";
    private static final String WHITE          = "#FFFFFF";
    private static final String LIGHT_GRAY     = "#F5F5F5";

    @Override
    public void start(Stage primaryStage) {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        // Sidebar
        sidebar = buildSidebar();
        mainLayout.setLeft(sidebar);

        // Top bar
        HBox topBar = buildTopBar();
        mainLayout.setTop(topBar);

        // Default: show QuanLyThietBi
        showView(new QuanLyThietBi().getView(), null);

        Scene scene = new Scene(mainLayout, 1280, 800);
        primaryStage.setTitle("Vuon Sao Bang - Quản Lý Hệ Thống");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    // ===================== TOP BAR =====================
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent transparent #E0E0E0 transparent;" +
            "-fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 2);"
        );
        bar.setSpacing(12);

        // Search
        TextField search = new TextField();
        search.setPromptText("🔍  Search here ...");
        search.setPrefWidth(340);
        search.setStyle(
            "-fx-background-radius: 20; -fx-border-radius: 20;" +
            "-fx-border-color: #DDD; -fx-border-width: 1;" +
            "-fx-font-size: 13px; -fx-padding: 7 16;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bell icon
        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 18px; -fx-cursor: hand;");

        // Avatar
        Circle avatar = new Circle(18, Color.web("#8B0000"));
        Label avatarLbl = new Label("A");
        avatarLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane avatarPane = new StackPane(avatar, avatarLbl);

        bar.getChildren().addAll(search, spacer, bell, avatarPane);
        return bar;
    }

    // ===================== SIDEBAR =====================
    private VBox buildSidebar() {
        VBox sb = new VBox(0);
        sb.setPrefWidth(240);
        sb.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        // Logo
        HBox logo = buildLogo();
        sb.getChildren().add(logo);

        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.15);");
        sb.getChildren().add(sep);

        // Menu items
        sb.getChildren().addAll(
            buildMenuGroup("🖥  Quản Lý Thiết Bị", false,
                "⚙  Thiết Bị", (Runnable) () -> showView(new QuanLyThietBi().getView(), null),
                "💻  PC",    (Runnable)  () -> showView(new PC().getView(), null)),
            
            buildMenuGroup("👤  Quản Lý Khách Hàng", false,
                "👤  Khách Hàng",      (Runnable) () -> showView(new khachhang().getView(), null),
                "🏷  Hạng Khách Hàng", (Runnable) () -> showView(new hangkhachhang().getView(), null),
                "💵  Nạp Tiền",        (Runnable) () -> showView(new naptien().getView(), null)
            ),
            buildMenuGroup("👥  Quản Lý Nhân Viên", false,
                "👤  Nhân Viên", (Runnable) () -> showView(new nhanvien().getView(), null),
                "⏱  Ca Làm",           (Runnable) () -> showView(new calam().getView(), null),
                "🌴  Số Ngày Nghỉ",     (Runnable) () -> showView(new songaynghi().getView(), null),
                "💤  Nghỉ Phép",        (Runnable) () -> showView(new nghiphep().getView(), null),
                "🏢  Loại Nhân Viên",   (Runnable) () -> showView(new loainhanvien().getView(), null)
            ),
            buildMenuGroup("📍  Quản Lý Khu Vực", false,
                "🗺  Khu Vực",      (Runnable) () -> showView(new khuvuc().getView(), null),
                "🏷  Loại Khu Vực", (Runnable) () -> showView(new loaikhuvuc().getView(), null)
            ),
            buildMenuGroup("📦  Quản Lý Sản Phẩm", false,
                "🍔  Sản Phẩm",        (Runnable) () -> showView(new sanpham().getView(), null)
            ),
            buildMenuGroup("💰  Quản Lý Tài Chính", false,
                "📊  Doanh Thu",      (Runnable) () -> showView(new doanhthu().getView(), null)
            ), 
            
            buildMenuGroup("📅  Quản Lý Sự Kiện", false,
                "🎊  Sự Kiện", (Runnable) () -> showView(new sukien().getView(), null)
            ),
            // Tìm đến đoạn Quản Lý Nhập Hàng trong App.java và sửa lại:
            buildMenuGroup("📥  Quản Lý Nhập Hàng", false,
                "🚚  Nhà Cung Cấp", (Runnable) () -> showView(new nhacungcap().getView(), null)    
            ) 
        );

        // THONG TIN section
        Label infoLabel = new Label("THÔNG TIN");
        infoLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 14 16 6 16;");
        sb.getChildren().add(infoLabel);

        sb.getChildren().addAll(
            buildMenuItem("⭐  Icons",       () -> showPlaceholder("Icons")),
            buildMenuItem("📄  Sample Page", () -> showPlaceholder("Sample Page"))
        );

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        // Footer - Admin
        HBox adminRow = buildAdminRow();
        sb.getChildren().add(adminRow);

        return sb;
    }

    private HBox buildLogo() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(16, 16, 16, 16));

        Circle logoIcon = new Circle(18, Color.web("#FFFFFF", 0.2));
        Label logoIconLbl = new Label("🖥");
        logoIconLbl.setStyle("-fx-font-size: 16px;");
        StackPane iconPane = new StackPane(logoIcon, logoIconLbl);

        Label logoText = new Label("Vuon Sao Bang");
        logoText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        hbox.getChildren().addAll(iconPane, logoText);
        return hbox;
    }

    private VBox buildMenuGroup(String groupTitle, boolean expanded, Object... subItems) {
        VBox group = new VBox(0);

        // Group header button
        Button header = new Button(groupTitle);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.setStyle(menuBtnStyle(false));

        // Sub-items container
        VBox subMenu = new VBox(0);
        subMenu.setVisible(expanded);
        subMenu.setManaged(expanded);
        subMenu.setStyle("-fx-background-color: rgba(0,0,0,0.12);");

        // Build sub-items: alternating (label, action)
        for (int i = 0; i < subItems.length; i += 2) {
            String subLabel = (String) subItems[i];
            Runnable action = (Runnable) subItems[i + 1];
            Button subBtn = new Button("    " + subLabel);
            subBtn.setMaxWidth(Double.MAX_VALUE);
            subBtn.setAlignment(Pos.CENTER_LEFT);
            subBtn.setPadding(new Insets(8, 16, 8, 28));
            subBtn.setStyle(menuBtnStyle(false));
            subBtn.setOnAction(e -> {
                action.run();
                setActiveBtn(subBtn);
            });
            subBtn.setOnMouseEntered(ev -> { if (subBtn != activeMenuBtn) subBtn.setStyle(menuBtnHoverStyle()); });
            subBtn.setOnMouseExited(ev -> { if (subBtn != activeMenuBtn) subBtn.setStyle(menuBtnStyle(false)); });
            subMenu.getChildren().add(subBtn);
        }

        header.setOnAction(e -> {
            boolean vis = subMenu.isVisible();
            subMenu.setVisible(!vis);
            subMenu.setManaged(!vis);
        });
        header.setOnMouseEntered(ev -> { if (header != activeMenuBtn) header.setStyle(menuBtnHoverStyle()); });
        header.setOnMouseExited(ev -> { if (header != activeMenuBtn) header.setStyle(menuBtnStyle(false)); });

        group.getChildren().addAll(header, subMenu);
        return group;
    }

    private Button buildMenuItem(String label, Runnable action) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 16, 10, 16));
        btn.setStyle(menuBtnStyle(false));

        btn.setOnAction(e -> {
            action.run();
            setActiveBtn(btn);
        });
        btn.setOnMouseEntered(ev -> { if (btn != activeMenuBtn) btn.setStyle(menuBtnHoverStyle()); });
        btn.setOnMouseExited(ev -> { if (btn != activeMenuBtn) btn.setStyle(menuBtnStyle(false)); });
        return btn;
    }

    private void setActiveBtn(Button btn) {
        if (activeMenuBtn != null) activeMenuBtn.setStyle(menuBtnStyle(false));
        activeMenuBtn = btn;
        btn.setStyle(menuBtnActiveStyle());
    }

    private HBox buildAdminRow() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(14, 16, 14, 16));
        hbox.setStyle("-fx-background-color: rgba(0,0,0,0.2);");

        Circle av = new Circle(16, Color.web("#FFFFFF", 0.25));
        Label avLbl = new Label("A");
        avLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane avPane = new StackPane(av, avLbl);

        Label adminLbl = new Label("Admin");
        adminLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;");

        hbox.getChildren().addAll(avPane, adminLbl);
        return hbox;
    }

    // ===================== VIEW SWITCHING =====================
    private void showView(Node view, Button trigger) {
        mainLayout.setCenter(view);
        if (trigger != null) setActiveBtn(trigger);
    }

    private void showPlaceholder(String name) {
        VBox ph = new VBox();
        ph.setAlignment(Pos.CENTER);
        ph.setSpacing(16);
        ph.setStyle("-fx-background-color: #F5F5F5;");

        Label icon = new Label("🚧");
        icon.setStyle("-fx-font-size: 48px;");

        Label lbl = new Label("Giao diện: " + name);
        lbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #555;");

        Label sub = new Label("Đang phát triển...");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");

        ph.getChildren().addAll(icon, lbl, sub);
        mainLayout.setCenter(ph);
    }

    // ===================== STYLE HELPERS =====================
    private String menuBtnStyle(boolean active) {
        return "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.85);" +
               "-fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: transparent;";
    }
    private String menuBtnActiveStyle() {
        return "-fx-background-color: " + SIDEBAR_ACTIVE + "; -fx-text-fill: white;" +
               "-fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: transparent;" +
               "-fx-border-left-color: white; -fx-border-left-width: 3;";
    }
    private String menuBtnHoverStyle() {
        return "-fx-background-color: " + SIDEBAR_HOVER + "; -fx-text-fill: white;" +
               "-fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: transparent;";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
