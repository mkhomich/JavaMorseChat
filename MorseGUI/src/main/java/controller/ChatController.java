package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import util.Listener;


public class ChatController {

    @FXML
    private TextArea input;
    @FXML
    private TextArea display;
    @FXML
    private Button sendButton;
    @FXML
    private Text username;
    @FXML
    private Button dotButton;
    @FXML
    private Button dashButton;

    @FXML
    void initialize() {
        sendButton.setOnAction(actionEvent -> sendButtonAction());

        dotButton.setOnAction(actionEvent -> dotButtonAction());

        dashButton.setOnAction(actionEvent -> dashButtonAction());

        input.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                sendButtonAction();
                ke.consume();
            }

            else if (ke.getCode().equals(KeyCode.UP)) {
                dotButtonAction();
                ke.consume();
            }

            else if (ke.getCode().equals(KeyCode.DOWN)) {
                dashButtonAction();
                ke.consume();
            }

        });
    }

    private void sendButtonAction(){
        String msg = input.getText();
        if (!msg.equals("")) {
            Listener.sendMessage(msg);
            addToChat("You: " + msg);
            input.setText("");
            input.requestFocus();
        }
    }

    private void dotButtonAction(){
        input.appendText("•");
        input.requestFocus();
    }

    private void dashButtonAction(){
        input.appendText("−");
        input.requestFocus();
    }

    // Метод синхронизован,
    // т.к. идет работа с общими ресурсами
    public synchronized void addToChat(String msg) {
        /* Каждое новое сообщение ставится в очередь
        на добавление, т.к.
        общий ресурс, текстовое поле "display",
        одновременно может модифицировать только один поток
        */
        Platform.runLater(() -> display.appendText(msg + "\n"));
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

}

