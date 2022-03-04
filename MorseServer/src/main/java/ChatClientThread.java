import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

class ChatClientThread extends Thread {
    private static final Object clock = new Object();
    private final Socket socket;
    private String username;
    private BufferedReader br;
    private final Map<String, PrintWriter> clients;

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public ChatClientThread(Socket socket, Map<String, PrintWriter> clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            username = br.readLine();
            systemBroadcast(username + " entered.");

            logger.debug("[Server] User ({}) entered.", username);

            synchronized (clock) {
                clients.put(this.username, pw);
            }

        } catch (IOException e) {
            logger.error("Socket I\\O error" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String line;

            // Чтение входного потока сокета,
            // пока его содержимое не равно нулю
            while ((line = br.readLine()) != null) {
                // Если пустая строка - игнорировать
                if (line.equals(""))
                    continue;
                // Спец. последовательность для отключения от сервера
                if (line.equals("/quit"))
                    break;
                    // Спец. последовательность для вывода списка подключенных клиентов
                else if (line.equals("/userlist")) {
                    sendUserList();
                    // Спец. последовательность для отправки личного сообщения
                } else if (line.indexOf("/to ") == 0) {
                    sendPrivateMessage(line);
                    // Если пользователь отправил обычное сообщение,
                    // то его отправляют (транслируют) всем
                } else {
                    // Метод трансляции сообщения
                    userBroadcast(line);
                }
            }

        } catch (IOException ex) {
            logger.error("Input Socket reading error: " + ex.getMessage());
        } finally {
            // В случае ошибки закрыть соединение
            closeConnection();
        }
    }

    private void closeConnection() {
        synchronized (clock) {
            clients.remove(username);
            logger.debug("User {} is removed from clients Map", username);
        }

        systemBroadcast(username + " left the chat.");

        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.error("Socket closing error:" + e.getMessage());
        }
    }


    private void sendUserList() {
        PrintWriter pw;
        PrintWriter currentUser;
        Iterator<String> iterator;

        synchronized (clock) {
            iterator = clients.keySet().iterator();
            currentUser = clients.get(username);
        }


        pw = currentUser;
        pw.println("\n<User list>");

        int i = 1;
        while (iterator.hasNext()) {
            String list = iterator.next();
            pw.println(i + ". " + list);
            i++;
        }

        --i;
        pw.println("Total: " + i);
        pw.flush();
    }


    public void sendPrivateMessage(String msg) {
        int start = msg.indexOf(" ") + 1;
        int end = msg.indexOf(" ", start);

        if (end != -1) {
            String recipientUsername = msg.substring(start, end);
            String msg2 = msg.substring(end + 1);
            PrintWriter recipient = clients.get(recipientUsername);

            if (recipient != null) {
                recipient.println(username + " whispered: " + msg2);
                recipient.flush();
            }
        }
    }

    // Метод синхронизован для многопоточной работы,
    // поскольку в нем идет работа с общими ресурсами
    public synchronized void userBroadcast(String msg) {
        // Получение списка входных потоков всех пользователей
        Collection<PrintWriter> users = clients.values();
        // Конвертация сообщения в азбуку Морзе
        msg = MorseConverter.englishToMorse(msg);

        // Рассылка зашифрованного сообщения всем,
        // кроме его автора
        for (PrintWriter user : users) {
            PrintWriter currentUser = clients.get(username);
            if (user.equals(currentUser)) continue;
            user.println(username + ":" + msg);
            user.flush();
        }
    }

    public void systemBroadcast(String msg) {
        synchronized (clock) {
            Collection<PrintWriter> users = clients.values();

            for (PrintWriter user : users) {
                PrintWriter currentUser = clients.get(username);
                if (user.equals(currentUser)) continue;
                user.println(msg);
                user.flush();
            }
        }
    }
}
