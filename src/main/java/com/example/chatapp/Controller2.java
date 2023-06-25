package com.example.chatapp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.io.*;
import java.net.Socket;

public class Controller2 {
    @FXML
    private TextField portField, hostField, msgField;
    @FXML
    private ListView<String> chatListView;
    @FXML

    private PrintWriter writer;






    @FXML
    protected void onConnect() {
        String host = hostField.getText();
        String portText = portField.getText();

        if (host.isEmpty() || portText.isEmpty()) {
            showError("Empty fields", "Host and Port fields must not be empty.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            showError("Invalid Port", "Please enter a valid port number.");
            System.out.println("Host: " + host);
            System.out.println("Port: " + portText);

            return;
        }

        try {
            Socket socket = new Socket(host, port);
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            String remoteAddress = socket.getRemoteSocketAddress().toString();
            writer = new PrintWriter(os, true);

            new Thread(() -> {
                try {
                    String response;
                    while ((response = br.readLine()) != null) {
                        String finalResponse = response; // Required for lambda expression
                        Platform.runLater(() -> chatListView.getItems().add(finalResponse));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            showError("Connection Error", "Could not establish a connection to the server.");
        }
    }

    public void onSend(ActionEvent actionEvent) {
        String message = msgField.getText();
        if (writer != null && message != null && !message.isEmpty()) {
            writer.println(message);
            chatListView.getItems().add(message);
            msgField.clear();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
