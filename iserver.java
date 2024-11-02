import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class iserver extends JFrame {
    private JTextField portField;
    private JTextArea statusArea;
    private JButton startButton;

    public iserver() {
        setTitle("File Receiver Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel for port input
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Port:"));
        portField = new JTextField("1234", 10);
        inputPanel.add(portField);
        
        startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        // Status area for messages
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
    }

    private void startServer() {
        int port = Integer.parseInt(portField.getText());
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                statusArea.append("Server started on port " + port + ". Waiting for clients...\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    statusArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");
                    receiveFile(clientSocket);
                    clientSocket.close();
                }
            } catch (IOException e) {
                statusArea.append("Error: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void receiveFile(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("FILE_FROM_CLIENT_SIDE.txt");
            byte[] buffer = new byte[4096];
            int bytesRead;

            statusArea.append("Receiving file...\n");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            statusArea.append("File received successfully.\n");
            fileOutputStream.close();
        } catch (IOException e) {
            statusArea.append("Error receiving file: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            iserver serverGUI = new iserver();
            serverGUI.setVisible(true);
        });
    }
}
