package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.net.Socket;

public class InputThread extends Thread {
    private final Socket sock;
    private final BufferedReader br;
    private final ChatController controller;

    private static final Logger logger = LoggerFactory.getLogger(InputThread.class);

    public InputThread(Socket sock, BufferedReader br, ChatController controller) {
        this.sock = sock;
        this.br = br;
        this.controller = controller;
    }
    // Проблема была в том, что для обновления содержимого экрана использовать runLater(),
    // т.е. каждую операцию по обновление обрабатывать в новом потоке.

    public void run() {
        try {
            String line;
            // Если соединение установлено
            // и входящий поток не пустой,
            // то добавить сообщение в окно чата
            while (sock.isConnected() & (line = br.readLine()) != null) {
                controller.addToChat(line);
            }
            //В ином случае записать в лог
            // и вывести сообщение об ошибке
        } catch (Exception ex) {
            logger.error("Input Stream reading error: " + ex.getMessage());
        } finally {
            try {
                if (br != null)
                    // Принудительное закрытие потока чтения сокета
                    br.close();
            } catch (Exception ex) {
                logger.error("Input Stream Reader closing error: " + ex.getMessage());
            }
            try {
                if (sock != null)
                    sock.close();
            } catch (Exception ex) {
                logger.error("Socket closing error: " + ex.getMessage());
            }
            // Вывод сообщения об ошибке
            LoginController.getInstance().showErrorDialog("Could not connect to server");
        }
    }
}

