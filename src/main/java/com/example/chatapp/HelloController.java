package com.example.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/app";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "zakaria";

    // Strings which hold css elements to easily re-use in the application
    protected String successMessage = "-fx-text-fill: GREEN;";
    protected String errorMessage = "-fx-text-fill: RED;";
    protected String errorStyle = "-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;";
    protected String successStyle = "-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;";

    @FXML
    private Label invalidLoginCredentials;
    @FXML
    private Label invalidSignupCredentials;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField loginUsernameTextField;
    @FXML
    private TextField loginPasswordPasswordField;
    @FXML
    private TextField signUpUsernameTextField;
    @FXML
    private TextField signUpEmailTextField;
    @FXML
    private TextField signUpPasswordPasswordField;
    @FXML
    private TextField signUpRepeatPasswordPasswordField;

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onLoginButtonClick() {
        String email = loginUsernameTextField.getText();
        String password = loginPasswordPasswordField.getText();

        if (email.isBlank() || password.isBlank()) {
            invalidLoginCredentials.setText("The Login fields are required!");
            invalidLoginCredentials.setStyle(errorMessage);
            invalidSignupCredentials.setText("");

            if (email.isBlank()) {
                loginUsernameTextField.setStyle(errorStyle);
            } else if (password.isBlank()) {
                loginPasswordPasswordField.setStyle(errorStyle);
            }
        } else {
            if (checkLoginCredentials(email, password)) {
                invalidLoginCredentials.setText("Login Successful!");
                invalidLoginCredentials.setStyle(successMessage);
                loginUsernameTextField.setStyle(successStyle);
                loginPasswordPasswordField.setStyle(successStyle);
                invalidSignupCredentials.setText("");
                loadScene2();
            } else {
                invalidLoginCredentials.setText("Invalid email or password!");
                invalidLoginCredentials.setStyle(errorMessage);
                loginUsernameTextField.setStyle(errorStyle);
                loginPasswordPasswordField.setStyle(errorStyle);
                invalidSignupCredentials.setText("");
            }
        }
    }

    @FXML
    protected void onSignUpButtonClick() {
        String username = signUpUsernameTextField.getText();
        String email = signUpEmailTextField.getText();
        String password = signUpPasswordPasswordField.getText();
        String repeatPassword = signUpRepeatPasswordPasswordField.getText();

        if (username.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            invalidSignupCredentials.setText("Please fill in all fields!");
            invalidSignupCredentials.setStyle(errorMessage);
            invalidLoginCredentials.setText("");

            if (username.isBlank()) {
                signUpUsernameTextField.setStyle(errorStyle);
            } else if (email.isBlank()) {
                signUpEmailTextField.setStyle(errorStyle);
            } else if (password.isBlank()) {
                signUpPasswordPasswordField.setStyle(errorStyle);
            } else if (repeatPassword.isBlank()) {
                signUpRepeatPasswordPasswordField.setStyle(errorStyle);
            }
        } else if (!password.equals(repeatPassword)) {
            invalidSignupCredentials.setText("The passwords don't match!");
            invalidSignupCredentials.setStyle(errorMessage);
            signUpPasswordPasswordField.setStyle(errorStyle);
            signUpRepeatPasswordPasswordField.setStyle(errorStyle);
            invalidLoginCredentials.setText("");
        } else {
            if (createUser(username, email, password)) {
                invalidSignupCredentials.setText("User registered successfully!");
                invalidSignupCredentials.setStyle(successMessage);
                signUpUsernameTextField.setStyle(successStyle);
                signUpEmailTextField.setStyle(successStyle);
                signUpPasswordPasswordField.setStyle(successStyle);
                signUpRepeatPasswordPasswordField.setStyle(successStyle);
                invalidLoginCredentials.setText("");
            } else {
                invalidSignupCredentials.setText("Failed to register user.");
                invalidSignupCredentials.setStyle(errorMessage);
                signUpUsernameTextField.setStyle(errorStyle);
                signUpEmailTextField.setStyle(errorStyle);
                signUpPasswordPasswordField.setStyle(errorStyle);
                signUpRepeatPasswordPasswordField.setStyle(errorStyle);
                invalidLoginCredentials.setText("");
            }
        }
    }

    private boolean checkLoginCredentials(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadScene2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene2.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Fermez la fenêtre de connexion
            Stage loginStage = (Stage) cancelButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
