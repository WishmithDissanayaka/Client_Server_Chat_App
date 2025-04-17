/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.examplehttpsocket1;

/* Please import necessary classes for creating the GUI components */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.Scanner;

/* Please import necessary classes for layout management */


/* Please import necessary classes for handling events */


/* Please import necessary classes for input and output operations */


/* Please import necessary classes for networking operations */

public class Client extends JFrame implements ActionListener, Runnable {

    private JTextField messageField;

    private JTextArea chatArea;

    private JButton sendButton, exitButton;

    private JList<String> userList;

    private DefaultListModel<String> listModel;

    private String username;

    /* Please create a Socket for connecting to the server */
    private Socket socket;

    /* Please create a BufferedReader to read input from the server */
    private BufferedReader input;

    /* Please create a PrintWriter to send output to the server */
    private PrintWriter output;

    public Client() {

        super("Chat Client");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 400);

        setLayout(new BorderLayout());

        chatArea = new JTextArea();

        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        listModel = new DefaultListModel<>();

        userList = new JList<>(listModel);

        JScrollPane userScrollPane = new JScrollPane(userList);

        userScrollPane.setPreferredSize(new Dimension(150, getHeight())); // Adjust width as needed

        add(userScrollPane, BorderLayout.WEST);

        exitButton = new JButton("Exit Chat");

        exitButton.addActionListener(this);

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        exitPanel.add(exitButton);

        add(exitPanel, BorderLayout.NORTH);

        setVisible(true);

        username = JOptionPane.showInputDialog(this, "Enter your username:");

        if (username == null || username.isEmpty()) {

            System.exit(0);
        }

        setTitle("Chat Client - " + username);

        try {
            /* Please create a Socket to connect to the server */
            Socket socket = new Socket("localhost", 12345);

            /* Please initialize the BufferedReader to read input from the server */
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /* Please initialize the PrintWriter to send output to the server */
            output = new PrintWriter(socket.getOutputStream(), true);

            /* Please send the username to the server */
            output.println(username);

            new Thread(this).start();
        } catch (IOException e) {

            e.printStackTrace();

            System.exit(1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton) {

            String message = messageField.getText();

            if (!message.isEmpty()) {
                String recipient = null;

                if (userList.getSelectedValue() != null && !userList.getSelectedValue().equals(username)) {
                    recipient = userList.getSelectedValue();

                    /* Please send a private message to the selected user */
                    output.println("PRIVATE:" + recipient + ":" + message);

                    chatArea.append("You (to " + recipient + "): " + message + "\n");
                } else {
                    /* Please send the message to all users */
                    output.println("PUBLIC:" + message);

                    chatArea.append("You: " + message + "\n");
                }

                messageField.setText("");
            }
        } else if (e.getSource() == exitButton) {
            try {
                /* Please notify the server of exit */
                output.println("exit");

                /* Please close the input stream */
               input.close();

                /* Please close the output stream */
               output.close();

                /* Please close the socket */
                socket.close();
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        try {
            String message;

            while ((message = input.readLine()) != null) {

                if (message.startsWith("USERLIST:")) {

                    String[] users = message.substring("USERLIST:".length()).split(",");

                    listModel.removeAllElements();

                    for (String user : users) {
                        listModel.addElement(user);
                    }
                } else {

                    chatArea.append(message + "\n");
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new Client();
    }
}
