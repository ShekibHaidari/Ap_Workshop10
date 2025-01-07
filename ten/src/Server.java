import java.io.*;
import java.net.*;
import java.util.*;
/*
 * This is a simple chat server that listens on port 9091 and accepts
 * @author Ahmad Shekib Haidari 
 * @version 1.0
 * intellij IDE auto complete code
 * ai auto complete code
 * 
 * Ap Wokshop10 
 */
public class Server {
    private static Set<Socket> clientList = Collections.synchronizedSet(new HashSet<>());
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9091)) {
            System.out.println("<-----------------Server started on port 9091:------------------- >");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientList.add(clientSocket);
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                out.println("Please write your username: ");
                String username = in.readLine();
                System.out.println(username + " add to the chat .");

                String message;
                while ((message = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        System.out.println(username + " left our chat.");
                        clientList.remove(clientSocket);
                        break;
                    }

                    synchronized (clientList) {
                        for (Socket socket : clientList) {
                            if (socket != clientSocket) {
                                PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
                                socketOut.println(username + ": " + message);
                            }
                        }
                    }
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
    }
}
