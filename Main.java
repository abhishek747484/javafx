// Package for JavaFX application
package application;

// Importing required classes
import database.DatabaseConnection;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.sql.*;

// Main class extends JavaFX Application
public class Main extends Application {

    // Entry point for JavaFX Application
    @Override
    public void start(Stage primaryStage) {
        // Main container with spacing and padding
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #34495E, #2ECC71);");

        // Title Label
        Label titleLabel = new Label("Welcome! Please Login");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Username Label and TextField
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(250);

        // Password Label and PasswordField
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(250);

        // Login button setup
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setStyle("-fx-background-color: #1ABC9C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        loginButton.setOnAction(e -> login(usernameField.getText().trim(), passwordField.getText().trim(), primaryStage));

        // Sign up button setup
        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(120);
        signUpButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        signUpButton.setOnAction(e -> signup.show()); // Call to signup window

        // Add buttons to a VBox
        VBox buttonBox = new VBox(10, loginButton, signUpButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add all components to the root layout
        root.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, buttonBox);

        // Create and show scene
        Scene scene = new Scene(root, 450, 450);
        primaryStage.setTitle("Login System");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Fullscreen
        primaryStage.show();
    }

    // Login method to authenticate user
    private void login(String username, String password, Stage stage) {
        // Basic input validation
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username and password must not be empty.");
            return;
        }

        // Check if user is an admin
        if (isAdminCredentials(username, password)) {
            try {
                new admindashboard().start(new Stage()); // Open admin dashboard
                stage.close(); // Close login window
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open admin dashboard: " + e.getMessage());
            }
            return;
        }

        // Check if user is a registered member
        if (isValidMemberCredentials(username, password)) {
            try {
                new dashboard().start(new Stage()); // Open user dashboard
                stage.close(); // Close login window
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open user dashboard: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
        }
    }

    // Method to check if the credentials match an admin
    private boolean isAdminCredentials(String username, String password) {
        String query = "SELECT * FROM admins WHERE admin_username = ? AND admin_password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // True if record exists
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to validate admin credentials: " + e.getMessage());
            return false;
        }
    }

    // Method to check if the credentials match a member
    private boolean isValidMemberCredentials(String username, String password) {
        String query = "SELECT * FROM members WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // True if record exists
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to validate member credentials: " + e.getMessage());
            return false;
        }
    }

    // Override stop method to close DB connection when app exits
    @Override
    public void stop() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    // Utility method to show alert popups
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // JavaFX main method
    public static void main(String[] args) {
        launch(args);
    }
}
