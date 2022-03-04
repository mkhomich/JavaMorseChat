package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Listener;

import java.io.IOException;


public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static LoginController instance;
    private Scene scene;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    @FXML
    private TextField loginF;

    @FXML
    private Button loginButton;

    @FXML
    private TextField hostnameF;

    @FXML
    private TextField portF;

    @FXML
    void initialize() {
        loginF.setOnMouseClicked(event -> loginF.setStyle(""));

        loginF.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginButtonAction();
            }
        });

        hostnameF.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginButtonAction();
            }
        });

        portF.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginButtonAction();
            }
        });

        loginButton.setOnAction(event -> loginButtonAction());
    }

    private void loginButtonAction() {
        String username = null;
        String hostname;
        int port;

        if (loginF.getText().equals("") | loginF.getText().length() > 18) {
            loginF.setStyle("-fx-border-color: red;");
            loginF.requestFocus();
        } else {
            username = loginF.getText().trim();
        }

        if (username != null) {

            if (hostnameF.getText().equals("")) {
                hostname = "localhost";
            } else {
                hostname = hostnameF.getText().trim();
            }

            if (portF.getText().equals("")) {
                port = 10000;
            } else {
                port = Integer.parseInt(portF.getText().trim());
            }

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/ChatView.fxml"));

                Parent root = loader.load();
                this.scene = new Scene(root);

                ChatController controller = loader.getController();
                controller.setUsername(username);
                Listener listener = new Listener(hostname, port, username, controller);
                Thread listenerThread = new Thread(listener);
                listenerThread.start();

            } catch (IOException ex) {
                logger.error("View loading exception: " + ex.getMessage());
                System.exit(1);
            }

        }
    }

    public void showScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage) loginF.getScene().getWindow();
            stage.setScene(this.scene);
            stage.setTitle("Morse Chat");

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
                stage.showAndWait();
            });


        });
    }

    public void showErrorDialog(String message) {
        // Вывод сообщений об ошибке будет происходить по очереди
        Platform.runLater(() -> {
            // Создание нового диалогового окна с типом WARNING (предупреждение)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            // Установка заголовка окна
            alert.setTitle("Warning!");
            // Установка сообщения для окна
            alert.setHeaderText(message);
            alert.setContentText("Please check if the server is running.");
            // Вывод окна на экран
            alert.showAndWait();
        });
    }
}

