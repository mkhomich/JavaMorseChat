package util;

import controller.ChatController;
import controller.InputThread;
import controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class Listener implements Runnable {
    private Socket socket;
    private final String hostname;
    private final Integer port;
    private final String username;
    private final ChatController controller;
    private static PrintWriter pw;
    private BufferedReader br;
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    public Listener(String hostname, Integer port, String username, ChatController controller) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.controller = controller;
    }


    @Override
    public void run() {
        try {
            // Попытка подключения к серверу
            connectToServer();

            // Если соединение установлено,
            // отправляем серверу свой логин (имя пользователя)
            pw.println(username.trim());
            pw.flush();

            // Закрытие окна подключения,
            // вывод окна чата
            LoginController.getInstance().showScene();

            // Создание и запуск нового слушателя входного потока сокета
            Thread inputThread = new InputThread(socket, br, controller);
            inputThread.start();

            // Если подключиться не удалось,
            // выводится сообщение об ошибки,
            // ошибка записывается в лог
        } catch (IOException e) {
            LoginController.getInstance().showErrorDialog("Could not connect to server");
            logger.error("Socket connection error: " + e.getMessage());
        }
    }

    // Метод может "выбрасывать" исключение ввода\вывода
    private void connectToServer() throws IOException {
        // Создание соединения с сокетом,
        // расположенным по адресу hostname:port
        socket = new Socket(hostname, port);
        // получение потоков записи и чтения сокета
        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void sendMessage(String msg) {
        // Если соединение с сервером установлено,
        // отправить сообщение на сервер
        if (pw != null) {
            pw.println(msg);
            pw.flush();
            // инача записать ошибку в лог
        } else {
            logger.warn("An attempt was made to send a message. No connection to the server.");
        }
    }

}

