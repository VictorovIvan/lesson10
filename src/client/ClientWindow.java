package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class ClientWindow
 */
public class ClientWindow extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3443;
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;
    private JTextField jtfMessage;
    private JTextArea jtaTextAreaMessage;
    private String clientName = "";
    private String cntInChat = "";

    /**
     * Get name of the client
     *
     * @return clientName Name of the client
     */
    public String getClientName() {
        return this.clientName;
    }

    /**
     * Constructor of the client
     *
     * @param nameClient Name of the client
     */
    public ClientWindow(String nameClient) {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
            outMessage.println("##name##this##client##" + nameClient);
            outMessage.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        desktopGraph(nameClient);
        JLabel jlNumberOfClients = new JLabel("Clients in chat: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        workClient(nameClient);
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (inMessage.hasNext()) {
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Clients in chat = ";
                            cntInChat = inMes;
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                jtaTextAreaMessage.append(inMes);
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
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
                    outMessage.println(clientName + " вышел из чата");
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {
                }
            }
        });
        setVisible(true);
    }

    /**
     * Send message from client
     */
    public void sendMsg() {
        String messageStr = clientName + ": " + jtfMessage.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }

    /***
     * Quit client from the chat
     */
    public void quitChat() {
        outMessage.println(clientName + " вышел из чата");
        outMessage.println("##session##end##");
        outMessage.flush();
        outMessage.close();
        inMessage.close();
        setVisible(false);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtfMessage.getText().trim().isEmpty()) {
                    if ((jtfMessage.getText().equalsIgnoreCase("quit"))) {
                        if (cntInChat.equalsIgnoreCase("Clients in chat = 1")) {
                            System.exit(0);
                        } else {
                            quitChat();
                        }
                    } else {
                        sendMsg();
                    }
                    jtfMessage.grabFocus();
                }
            }
        });
    }
}