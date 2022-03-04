import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        // Чтение файла ресурсов с алфавитом для азбуки Морзе
        MorseConverter.alphabetMapper();

        ServerSocket server = null;
        Socket socket = null;
        try {
            // Создание сервер-сокета, который "слушает" входящие подключения
            server = new ServerSocket(PORT);
            // Коллекция типа Map для сохранения пользователей
            Map<String, PrintWriter> clients = new HashMap<>();

            // Запись сообщения о старте сервера в лог-файл
            logger.trace("Server is started. Waiting for connection");

            // "Прослушивание" сервер-сокета, принятие подключений
            // и создание нового потока для клиента
            while (true) {
                socket = server.accept();
                startChatClientThread(socket, clients);
            }

            // Отлов ошибок
        } catch (IOException e) {
            logger.debug("Connection error: " + e.getMessage());
        } finally {
            try {
                server.close();
                socket.close();
            } catch (IOException e) {
                logger.error("Socket closing error: " + e.getMessage());
            }
        }
    }

    // Метод создания и запуска нового потока для клиента
    private static void startChatClientThread(Socket socket, Map<String, PrintWriter> clients) {
        ChatClientThread chatClientThread = new ChatClientThread(socket, clients);
        chatClientThread.start();
    }
}


