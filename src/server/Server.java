package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

class Server {
    private static final int PORT = 3443;
    CopyOnWriteArrayList<ClientConnection> clientsArrayList = new CopyOnWriteArrayList<>();
    ConcurrentMap<String, ClientConnection> clientsConcurrrentMap = new ConcurrentHashMap();

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
                ClientConnection client = new ClientConnection(clientSocket, this);
                clientsArrayList.add(client);
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
        for (ClientConnection obj : clientsArrayList) {
            obj.sendMsg(msg);
        }
    }

    /**
     * Send to client message in NET
     *
     * @param msg Message for sending
     */
    void sendMessageToClient(String msg, ClientConnection clientMessage) {
        clientMessage.sendMsg(msg);
    }

    /**
     * Remove client from the chat
     *
     * @param client Client, which we will delete
     */
    void removeClient(ClientConnection client) {
        clientsArrayList.remove(client);
    }
}