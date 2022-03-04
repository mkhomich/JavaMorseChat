import java.net.*;
import java.io.*;

public class ChatClient {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage : java ChatClient <username> <server-ip> <port>");
            System.exit(1);
        }


        String username = args[0];
        String hostname = args[1];
        int PORT = Integer.parseInt(args[2]);

        Socket socket;
        try {
            socket = new Socket(hostname, PORT);
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

                pw.println(username);
                pw.flush();


                startInputThread(socket, br);

                System.out.println("Welcome to the Morse Chat!");
                String line;
                while (socket.isConnected() & (line = keyboard.readLine()) != null) {
                    pw.println(line);
                    pw.flush();
                    if (line.equals("/quit")) {
                        break;
                    }
                }
                System.out.println("Connection is closed.");
            }
        } catch (IOException ex) {
            System.out.println("Failed connection: " + ex.getMessage());
        }
    }

    private static void startInputThread(Socket socket, BufferedReader br) {
        InputThread it = new InputThread(socket, br);
        it.start();
    }

}


class InputThread extends Thread {
    private final Socket sock;
    private final BufferedReader br;

    public InputThread(Socket sock, BufferedReader br) {
        this.sock = sock;
        this.br = br;
    }

    public void run() {
        try {
            String line;
            while (sock.isConnected() & (line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.out.println("Error");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception ex) {
                System.out.println("Error");
            }
            try {
                if (sock != null)
                    sock.close();
            } catch (Exception ex) {
                System.out.println("Error");
            }
        }
    }
}

