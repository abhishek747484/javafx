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
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{10}");

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
        grid.setStyle("-fx-background-color: white; -fx-border-color: #B0BEC5; -fx-border-width: 3; -fx-border-radius: 15; -fx-padding: 40;");

        String labelStyle = "-fx-font-size: 20px; -fx-font-weight: bold;";
        String fieldStyle = "-fx-font-size: 18px; -fx-padding: 12;";

        Label usernameLabel = new Label("USERNAME:");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setStyle(fieldStyle);
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label firstNameLabel = new Label("FIRST NAME:");
        firstNameLabel.setStyle(labelStyle);
        TextField firstNameField = new TextField();
        firstNameField.setStyle(fieldStyle);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);

        Label lastNameLabel = new Label("LAST NAME:");
        lastNameLabel.setStyle(labelStyle);
        TextField lastNameField = new TextField();
        lastNameField.setStyle(fieldStyle);
        grid.add(lastNameLabel, 0, 2);
        grid.add(lastNameField, 1, 2);

        Label emailLabel = new Label("EMAIL:");
        emailLabel.setStyle(labelStyle);
        TextField emailField = new TextField();
        emailField.setStyle(fieldStyle);
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);

        Label phoneLabel = new Label("PHONE NUMBER:");
        phoneLabel.setStyle(labelStyle);
        TextField phoneField = new TextField();
        phoneField.setStyle(fieldStyle);
        grid.add(phoneLabel, 0, 4);
        grid.add(phoneField, 1, 4);

        Label passwordLabel = new Label("PASSWORD:");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle(fieldStyle);
        grid.add(passwordLabel, 0, 5);
        grid.add(passwordField, 1, 5);

        Label confirmPasswordLabel = new Label("CONFIRM PASSWORD:");
        confirmPasswordLabel.setStyle(labelStyle);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle(fieldStyle);
        grid.add(confirmPasswordLabel, 0, 6);
        grid.add(confirmPasswordField, 1, 6);

        Button registerButton = new Button("REGISTER");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10;");

        Button cancelButton = new Button("CANCEL");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10;");

        HBox buttonBox = new HBox(40, cancelButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 1, 7);

        cancelButton.setOnAction(e -> signupStage.close());

        registerButton.setOnAction(e -> registerUser(
            usernameField.getText().trim(),
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            passwordField.getText().trim(),
            confirmPasswordField.getText().trim(),
            signupStage
        ));

        mainLayout.setCenter(grid);
        Scene scene = new Scene(mainLayout, 800, 600);
        signupStage.setScene(scene);
        signupStage.setMaximized(true);
        signupStage.show();
    }

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
            stmt.setString(6, password);
            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register user: " + e.getMessage());
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
