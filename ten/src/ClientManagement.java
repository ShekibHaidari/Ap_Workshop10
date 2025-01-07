import java.io.*;
import java.net.*;

/**
 * This class is used to manage the client-side of the chat application.
 * It connects to the server and sends and receives messages.
 * @author Ahmad Shekib haidari
 * @version 1.0
 * ai and intellig code generator
 * 
 */
public class ClientManagement {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1111);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("<-------------Connect to the chat server successfully!---------------->");

            System.out.print("Enter your NAME: ");
            String username = consoleInput.readLine();
            out.println(username);

            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String clientMessage;
            while ((clientMessage = consoleInput.readLine()) != null) {
                out.println(clientMessage);
                if ("exit".equalsIgnoreCase(clientMessage)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
