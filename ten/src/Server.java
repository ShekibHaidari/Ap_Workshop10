import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Author: Ahmad Shekib Haidari
 * with help of ai and intellij idea auto code generator
 * Server class for the chat application.
 * It listens for incoming connections and creates a new thread for each client.
 *
 */
public class Server {
    private static Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1111)) {
            System.out.println("Server started on port 1111");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("Enter your Name: ");
                String username = in.readLine();
                System.out.println(username + " conect with the server.");
                broadcastMessage(username + " joined the chat.", clientSocket);

                String message;
                while ((message = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        System.out.println(username + " leave chat.");
                        broadcastMessage(username + " left the chat.", clientSocket);
                        clientSockets.remove(clientSocket);
                        break;
                    }

                    System.out.println(username + ": " + message);
                    broadcastMessage(username + ": " + message, clientSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message, Socket senderSocket) {
            synchronized (clientSockets) {
                for (Socket socket : clientSockets) {
                    if (socket != senderSocket) {
                        try {
                            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
                            socketOut.println(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
