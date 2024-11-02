import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class iclient extends JFrame {
    private JTextField serverAddressField;
    private JTextField portField;
    private JTextField filePathField;
    private JTextArea statusArea;
    private JButton browseButton;
    private JButton sendButton;

    public iclient() {
        setTitle("File Sender Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel for connection details
        JPanel connectionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        connectionPanel.add(new JLabel("Server Address:"));
        serverAddressField = new JTextField("localhost");
        connectionPanel.add(serverAddressField);
        
        connectionPanel.add(new JLabel("Port:"));
        portField = new JTextField("1234");
        connectionPanel.add(portField);
        
        connectionPanel.add(new JLabel("File Path:"));
        filePathField = new JTextField();
        connectionPanel.add(filePathField);
        
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseAction());
        connectionPanel.add(browseButton);

        add(connectionPanel, BorderLayout.NORTH);

        // Status area for messages
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);

        // Send button
        sendButton = new JButton("Send File");
        sendButton.addActionListener(new SendAction());
        add(sendButton, BorderLayout.SOUTH);
    }

    private class BrowseAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(iclient.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class SendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String serverAddress = serverAddressField.getText();
            int port = Integer.parseInt(portField.getText());
            String filePath = filePathField.getText();
            
            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(iclient.this, "Please select a file to send.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Start a new thread to send the file
            new Thread(() -> sendFile(serverAddress, port, filePath)).start();
        }
    }

    private void sendFile(String serverAddress, int port, String filePath) {
        try {
            statusArea.append("Connecting to server...\n");
            Socket socket = new Socket(serverAddress, port);
            statusArea.append("Connected to server.\n");

            File file = new File(filePath);
            if (!file.exists()) {
                statusArea.append("File not found: " + filePath + "\n");
                return;
            }

            // Send file to server
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = socket.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            statusArea.append("Sending file: " + file.getName() + "\n");

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            statusArea.append("File sent successfully.\n");

            // Close resources
            fileInputStream.close();
            outputStream.close();
            socket.close();
            statusArea.append("Connection closed.\n");

        } catch (IOException e) {
            statusArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            iclient clientGUI = new iclient();
            clientGUI.setVisible(true);
        });
    }
}
