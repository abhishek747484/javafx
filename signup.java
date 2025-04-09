package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import database.DatabaseConnection;
import java.sql.*;
import java.util.regex.Pattern;

public class signup {

    // Regular expression patterns for validating email and phone number
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{10}");

    // Field declarations
    private static TextField firstNameField;
    private static TextField lastNameField;
    private static TextField emailField;
    private static TextField phoneField;

    // Method to launch the Sign Up window
    public static void show() {
        Stage signupStage = new Stage();
        signupStage.setTitle("Sign Up");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #F5F5F5;");

        StackPane headerPane = new StackPane();
        Label headerLabel = new Label("SIGN UP");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        headerLabel.setTextFill(Color.WHITE);
        headerPane.setStyle("-fx-background-color: #3F51B5; -fx-padding: 30;");
        headerPane.getChildren().add(headerLabel);
        mainLayout.setTop(headerPane);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(50));
        grid.setStyle("-fx-background-color: white; -fx-border-color: #B0BEC5; -fx-border-width: 3; -fx-border-radius: 15;");

        String labelStyle = "-fx-font-size: 20px; -fx-font-weight: bold;";
        String fieldStyle = "-fx-font-size: 18px; -fx-padding: 12;";

        // USERNAME FIELD
        Label usernameLabel = new Label("USERNAME:");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setStyle(fieldStyle);
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        // FIRST NAME FIELD
        Label firstNameLabel = new Label("FIRST NAME:");
        firstNameLabel.setStyle(labelStyle);
        firstNameField = new TextField();
        firstNameField.setStyle(fieldStyle);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);

        // LAST NAME FIELD
        Label lastNameLabel = new Label("LAST NAME:");
        lastNameLabel.setStyle(labelStyle);
        lastNameField = new TextField();
        lastNameField.setStyle(fieldStyle);
        grid.add(lastNameLabel, 0, 2);
        grid.add(lastNameField, 1, 2);

        // EMAIL FIELD
        Label emailLabel = new Label("EMAIL:");
        emailLabel.setStyle(labelStyle);
        emailField = new TextField();
        emailField.setStyle(fieldStyle);
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);

        // PHONE FIELD
        Label phoneLabel = new Label("PHONE:");
        phoneLabel.setStyle(labelStyle);
        phoneField = new TextField();
        phoneField.setStyle(fieldStyle);
        grid.add(phoneLabel, 0, 4);
        grid.add(phoneField, 1, 4);

        // PASSWORD FIELD
        Label passwordLabel = new Label("PASSWORD:");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle(fieldStyle);
        grid.add(passwordLabel, 0, 5);
        grid.add(passwordField, 1, 5);

        // CONFIRM PASSWORD FIELD
        Label confirmPasswordLabel = new Label("CONFIRM PASSWORD:");
        confirmPasswordLabel.setStyle(labelStyle);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle(fieldStyle);
        grid.add(confirmPasswordLabel, 0, 6);
        grid.add(confirmPasswordField, 1, 6);

        // BUTTONS
        Button registerButton = new Button("REGISTER");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Button cancelButton = new Button("CANCEL");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(40, cancelButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 1, 7);

        cancelButton.setOnAction(e -> signupStage.close());

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Validation before registration
            if (validateInput(username, firstName, lastName, email, phone, password, confirmPassword)) {
                registerUser(username, firstName, lastName, email, phone, password, confirmPassword, signupStage);
            }
        });

        mainLayout.setCenter(grid);
        Scene scene = new Scene(mainLayout, 800, 600);
        signupStage.setScene(scene);
        signupStage.setMaximized(true);
        signupStage.show();
    }

    // Validation method
    private static boolean validateInput(String username, String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email format.");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Phone number must be 10 digits.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return false;
        }
        return true;
    }

    // Method to perform registration
    private static void registerUser(String username, String firstName, String lastName, String email, String phone, String password, String confirmPassword, Stage stage) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try {
            String query = "INSERT INTO members (username, first_name, last_name, email, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, password); // Consider hashing passwords in production
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register user: " + e.getMessage());
        }
    }

    // Utility method to show alerts
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
