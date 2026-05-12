package com.example.cybergame_management;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    // Xử lý khi nhấn nút Đăng nhập bằng chuột
    @FXML
    public void handleLogin(ActionEvent event) {
        processLogin();
    }

    // Xử lý khi nhấn phím Enter lúc đang gõ phím
    @FXML
    public void onEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processLogin();
        }
    }

    // Hàm dùng chung cho cả click chuột và Enter
    private void processLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        // Truyền thẳng cái username vào hàm loadMainScene luôn
        loadMainScene(username);
    }

    // Hàm chuyển sang màn hình chính
    // Thêm tham số username vào đây
    private void loadMainScene(String username) {
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load(); // Phải load root trước

            // --- ĐOẠN QUAN TRỌNG NHẤT ĐỂ BẮN DỮ LIỆU ---
            // Lấy cái MainController vừa được tạo ra
            MainController mainController = fxmlLoader.getController();
            // Gọi hàm setGreeting để nhét cái tên đăng nhập vào
            mainController.setGreeting(username);
            // -------------------------------------------

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể tải giao diện quản lý!");
        }
    }

    // Hàm phụ trợ tạo thông báo popup
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}