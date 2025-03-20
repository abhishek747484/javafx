package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup {

    public static void show() {
        Stage signupStage = new Stage();

        
        Label titleLabel = new Label("Sign Up");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 18px;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(250);
        usernameField.setStyle("-fx-font-size: 16px;");

        Label firstNameLabel = new Label("First Name:");
        firstNameLabel.setStyle("-fx-font-size: 18px;");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setMaxWidth(250); // Increased width
        firstNameField.setStyle("-fx-font-size: 16px;");

        Label lastNameLabel = new Label("Last Name:");
        lastNameLabel.setStyle("-fx-font-size: 18px;");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");
        lastNameField.setMaxWidth(250); // Increased width
        lastNameField.setStyle("-fx-font-size: 16px;");

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 18px;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setMaxWidth(250);
        emailField.setStyle("-fx-font-size: 16px;");

        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-size: 18px;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setMaxWidth(250);
        phoneField.setStyle("-fx-font-size: 16px;");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 18px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-font-size: 16px;");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-font-size: 18px;");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setMaxWidth(250);
        confirmPasswordField.setStyle("-fx-font-size: 16px;");

        // Create buttons
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(150);
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        registerButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                    lastNameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                    confirmPasswordField.getText().isEmpty()) {

                showAlert("Missing Information", "All fields should be filled");
            } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Password Mismatch", "Passwords do not match");
            } else if (!isValidEmail(emailField.getText())) {
                showAlert("Invalid Email", "Please enter a valid email address");
            } else if (!isValidPhoneNumber(phoneField.getText())) {
                showAlert("Invalid Phone Number", "Please enter a valid phone number");
            } else {
                
                showAlert("Registration Complete", "You have successfully registered!");

                
                signupStage.close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(150);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        cancelButton.setOnAction(e -> signupStage.close());

        
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.getChildren().addAll(registerButton, cancelButton);

        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(40); 
        formGrid.setVgap(15); 
        formGrid.setAlignment(Pos.CENTER);

       
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);  
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(60);  
        formGrid.getColumnConstraints().addAll(column1, column2);

        // Add fields and labels to the grid
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);

        formGrid.add(firstNameLabel, 0, 1);
        formGrid.add(firstNameField, 1, 1);

        formGrid.add(lastNameLabel, 0, 2);
        formGrid.add(lastNameField, 1, 2);

        formGrid.add(emailLabel, 0, 3);
        formGrid.add(emailField, 1, 3);

        formGrid.add(phoneLabel, 0, 4);
        formGrid.add(phoneField, 1, 4);

        formGrid.add(passwordLabel, 0, 5);
        formGrid.add(passwordField, 1, 5);

        formGrid.add(confirmPasswordLabel, 0, 6);
        formGrid.add(confirmPasswordField, 1, 6);

      
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 20, 0));
        headerBox.setStyle("-fx-background-color: #3f51b5; -fx-background-radius: 5px;");
        headerBox.setPrefHeight(70);
        headerBox.getChildren().add(titleLabel);

        
        vbox.getChildren().addAll(headerBox, new Spacer(10), formGrid, buttonBox);

        
        Scene scene = new Scene(vbox, 650, 700);

        
        signupStage.setTitle("Sign Up Form");
        signupStage.setScene(scene);
        signupStage.show();
    }

    
    private static void showAlert(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

   
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    
    private static boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}"); // Only allows 10 digits 
    }

    
    private static class Spacer extends VBox {
        public Spacer(double height) {
            super();
            setMinHeight(height);
            setPrefHeight(height);
            setMaxHeight(height);
        }
    }
}
