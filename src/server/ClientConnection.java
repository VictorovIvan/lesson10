package server;

import message.ClientMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Class ClientConnection
 */
public class ClientConnection implements Runnable {
    private Server server;
    private ObjectOutputStream outOutputStreamToClient;
    private ObjectInputStream inputStreamFromClient;

    /**
     * Handler of the client message
     *
     * @param socket Socket of the chat
     * @param server Server of the chat
     */
    ClientConnection(Socket socket, Server server) {
        try {
            this.server = server;
            this.outOutputStreamToClient = new ObjectOutputStream(socket.getOutputStream());
            this.inputStreamFromClient = new ObjectInputStream(socket.getInputStream());

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
            ClientMessage message = (ClientMessage) inputStreamFromClient.readObject();
            boolean statusHandler = true;
            while (statusHandler) {
                if (!message.conditionClient.equals(ClientMessage.Condition.QUIT)) {
                    server.clientsConcurrrentMap.putIfAbsent(message.name, this);
                    if (Thread.activeCount() + 1 > server.clientsArrayList.size()) {
                        server.clientsArrayList.add(this);
                    }
                    if (message.conditionClient != ClientMessage.Condition.ENTER) {
                        if (message.clientPrivate.equals("")) {
                            server.sendMessageToAllClients(message.name + ": " + message.message);
                        } else {
                            if (!server.clientsConcurrrentMap.get(message.clientPrivate).equals(null)) {
                                if (!message.name.equals(message.clientPrivate)) {
                                    ClientConnection clientConnectionPrivate = server.clientsConcurrrentMap.get(message.clientPrivate);
                                    server.sendMessageToClient("Персональное сообщение от " + message.name + ":" + message.message, clientConnectionPrivate);
                                }
                            }
                        }
                    }
                } else {
                    server.sendMessageToAllClients(message.name + ": " + message.message);
                    server.removeClient(server.clientsConcurrrentMap.get(message.name));
                }
                statusHandler = false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }


    /**
     * Send message for client
     *
     * @param msg Message for sending
     */
    void sendMsg(String msg) {
        try {
            this.outOutputStreamToClient.writeObject(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close client
     */
    private void close() {
        server.removeClient(this);
    }
}