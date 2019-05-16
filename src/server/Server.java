package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Server {
    private static final int PORT = 3443;
    private ArrayList<ClientMessage> clients = new ArrayList<>();
    public ConcurrentMap<String, ClientMessage> clientsConcurrrentMap = new ConcurrentHashMap();

    /**
     * Constructor of the Server
     */
    Server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                clientSocket = serverSocket.accept();
                ClientMessage client = new ClientMessage(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert clientSocket != null;
                clientSocket.close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Send all client in NET message
     *
     * @param msg Message for sending
     */
    void sendMessageToAllClients(String msg) {
        for (ClientMessage obj : clients) {
            obj.sendMsg(msg);
            System.out.println("Name :" + obj.nameThisClient);
        }
    }

    /**
     * Send to client message in NET
     *
     * @param msg Message for sending
     */
    void sendMessageToClient(String msg, ClientMessage clientMessage) {
        clientMessage.sendMsg(msg);
    }

    /**
     * Remove client from the chat
     *
     * @param client Client, which we will delete
     */
    void removeClient(ClientMessage client) {
        clients.remove(client);
    }
}