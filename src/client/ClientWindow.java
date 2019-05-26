package client;

import message.ClientMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class ClientWindow
 */
class ClientWindow extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3443;
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStreamToServer;
    private ObjectInputStream objectInputStreamFromServer;
    private JTextField jtfMessage;
    private JTextArea jtaTextAreaMessage;
    private String clientName;
    private ClientMessage newMessage = new ClientMessage();


    /**
     * Constructor of the client
     *
     * @param nameClient Name of the client
     */
    ClientWindow(String nameClient) {
        try {
            this.clientSocket = new Socket(SERVER_HOST, SERVER_PORT);

            newMessage.name = nameClient;
            newMessage.message = nameClient;
            newMessage.clientPrivate = "";
            newMessage.conditionClient = ClientMessage.Condition.ENTER;

            this.objectOutputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectOutputStreamToServer.writeObject(newMessage);
            objectOutputStreamToServer.flush();
            objectInputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
            newMessage.conditionClient = ClientMessage.Condition.WORK;

        } catch (IOException e) {
            e.printStackTrace();
        }
        desktopGraph(nameClient);
        JLabel jlNumberOfClients = new JLabel("Личное сообщение:  СООБЩЕНИЕ:::ИМЯ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        workClient(nameClient);
        jtfMessage.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        new Thread(() -> {
            try {
                while (true) {
                    String inMes = (String) objectInputStreamFromServer.readObject();
                    jtaTextAreaMessage.append(inMes);
                    jtaTextAreaMessage.append("\n");
                }
            } catch (Exception e) {
            }
        }).start();

        /**
         * Closing windows if receiving commands to quit
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {

                    objectOutputStreamToServer.writeObject(clientName + " вышел из чата");
                    objectOutputStreamToServer.flush();
                    objectOutputStreamToServer.close();
                    objectInputStreamFromServer.close();
                    clientSocket.close();
                } catch (IOException ignored) {
                }
            }
        });
        setVisible(true);
    }

    /**
     * Send message from client
     */
    private void sendMsg() throws IOException {
        clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
        objectOutputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        String[] arrayMessage;

        newMessage.name = this.clientName;

        arrayMessage = jtfMessage.getText().split(":::");
        newMessage.message = arrayMessage[0];
        if (arrayMessage.length < 2) {
            newMessage.clientPrivate = "";
        } else {
            newMessage.clientPrivate = arrayMessage[1];
        }
        this.objectOutputStreamToServer.writeObject(newMessage);
        this.objectOutputStreamToServer.flush();
        jtfMessage.setText("");
    }

    /**
     * Creating graph of the client
     *
     * @param nameClient Name of the client
     */
    private void desktopGraph(String nameClient) {
        setBounds(400, 300, 300, 500);
        setTitle(nameClient);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
    }

    /***
     * Quit client from the chat
     */
    private void quitChat() throws IOException {
        clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
        this.objectOutputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());

        newMessage.conditionClient = ClientMessage.Condition.QUIT;
        newMessage.message = clientName + " вышел из чата";
        this.objectOutputStreamToServer.writeObject(newMessage);
        this.objectOutputStreamToServer.flush();
        this.objectInputStreamFromServer.close();

        setVisible(false);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create interface and work of the client
     *
     * @param nameClient Name of the client
     */
    private void workClient(String nameClient) {

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите ваше сообщение: ");

        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        jtaTextAreaMessage.setBackground(Color.LIGHT_GRAY);
        clientName = nameClient;
        jbSendMessage.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty()) {
                if ((jtfMessage.getText().equalsIgnoreCase("quit"))) {
                    try {
                        if (Thread.activeCount() > 1) {
                            quitChat();
                        } else {
                            quitChat();
                            System.exit(0);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        sendMsg();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                jtfMessage.grabFocus();
            }
        });
    }
}