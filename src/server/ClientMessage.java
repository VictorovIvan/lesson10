package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class ClientMessage
 */
public class ClientMessage implements Runnable {
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    private Socket clientSocket = null;
    private static int clients_count = 0;
    public String nameThisClient = "";

    /**
     * Handler of the client message
     *
     * @param socket Socket of the chat
     * @param server Server of the chat
     */
    public ClientMessage(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Run thread for the chat
     */
    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("Clients in chat = " + clients_count);
                break;
            }
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    if (clientMessage.contains("##name##this##client##")) {
                        clientMessage = enterNameClient(clientMessage);
                    }
                    if (clientMessage.contains("-To##client-")) {
                        messageToPrivateClient(clientMessage);
                    } else {
                        System.out.println(clientMessage);
                        server.sendMessageToAllClients(clientMessage);
                    }
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            this.close();
        }
    }

    /**
     * Write information about connect client
     *
     * @param clientMessageName Receive message for extract name
     */
    private String enterNameClient(String clientMessageName) {
        clientMessageName = clientMessageName.replaceAll("##name##this##client##", "");
        this.nameThisClient = clientMessageName;
        clientMessageName = "Клиент " + this.nameThisClient + " подключился к чату";
        server.clientsConcurrrentMap.put(this.nameThisClient, this);
        return clientMessageName;
    }

    /**
     * Send private message for client
     *
     * @param clientMessagePrivate Private meesage for some client
     */
    private void messageToPrivateClient(String clientMessagePrivate) {
        String currentClient = clientMessagePrivate;
        currentClient = currentClient.replaceAll(this.nameThisClient + ":", "");
        currentClient = currentClient.replaceAll("-To##client-", "~");
        String[] message = currentClient.split("~");
        ClientMessage clientMess = server.clientsConcurrrentMap.get(message[1]);
        server.sendMessageToClient("Персональное сообщение от " + this.nameThisClient + ":" + message[0], clientMess);
    }

    /**
     * Send message for client
     *
     * @param msg Message for sending
     */
    void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close client
     */
    private void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Clients in chat = " + clients_count);
    }
}