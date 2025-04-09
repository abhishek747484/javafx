package application;

import database.DatabaseConnection;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.sql.*;
import java.util.regex.Pattern;

public class admindashboard extends Application {
    private TableView<String[]> movieTable;
    private ObservableList<String[]> movieList;
    private TableView<String[]> memberTable;
    private ObservableList<String[]> memberList;
    private static final Pattern RATING_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

    @Override
    public void start(Stage primaryStage) {
        VBox root = createRootLayout();
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        
        // Maximizing the window
        primaryStage.setMaximized(true);  // This ensures the window is maximized
        
        primaryStage.show();
        loadMoviesFromDatabase();
        loadMembersFromDatabase();
    }

    private VBox createRootLayout() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2C3E50, #4CA1AF);");

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        movieTable = new TableView<>();
        movieList = FXCollections.observableArrayList();
        movieTable.setItems(movieList);
        movieTable.setStyle("-fx-background-color: white; -fx-border-radius: 10px;");
        movieTable.setMaxSize(600, 200);
        setupMovieTableColumns();

        TextField titleField = new TextField();
        titleField.setPromptText("Movie Title");
        titleField.setMaxWidth(150);
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");
        genreField.setMaxWidth(150);
        TextField languageField = new TextField();
        languageField.setPromptText("Language");
        languageField.setMaxWidth(150);
        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (e.g., 7.5)");
        ratingField.setMaxWidth(150);

        Button addMovieButton = new Button("Add Movie");
        addMovieButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        addMovieButton.setOnAction(e -> addMovie(titleField, genreField, languageField, ratingField));

        Button removeMovieButton = new Button("Remove Selected Movie");
        removeMovieButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        removeMovieButton.setOnAction(e -> removeMovie());

        HBox movieInputBox = new HBox(10, titleField, genreField, languageField, ratingField, addMovieButton);
        movieInputBox.setAlignment(Pos.CENTER);

        VBox movieBox = new VBox(15, new Label("Movies"), movieTable, movieInputBox, removeMovieButton);
        movieBox.setAlignment(Pos.CENTER);

        memberTable = new TableView<>();
        memberList = FXCollections.observableArrayList();
        memberTable.setItems(memberList);
        memberTable.setStyle("-fx-background-color: white; -fx-border-radius: 10px;");
        memberTable.setMaxSize(600, 200);
        setupMemberTableColumns();

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(150);
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setMaxWidth(150);
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setMaxWidth(150);
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(150);
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.setMaxWidth(150);
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(150);

        Button addMemberButton = new Button("Add Member");
        addMemberButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        addMemberButton.setOnAction(e -> addMember(usernameField, firstNameField, lastNameField, emailField, phoneField, passwordField));

        Button removeMemberButton = new Button("Remove Selected Member");
        removeMemberButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        removeMemberButton.setOnAction(e -> removeMember());

        HBox memberInputBox = new HBox(10, usernameField, firstNameField, lastNameField, emailField, phoneField, passwordField, addMemberButton);
        memberInputBox.setAlignment(Pos.CENTER);

        VBox memberBox = new VBox(15, new Label("Members"), memberTable, memberInputBox, removeMemberButton);
        memberBox.setAlignment(Pos.CENTER);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        logoutButton.setOnAction(e -> {
            try {
                new Main().start(new Stage());
                ((Node) e.getSource()).getScene().getWindow().hide();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout and return to login screen.");
            }
        });

        VBox contentBox = new VBox(20, movieBox, memberBox, logoutButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(10));

        root.getChildren().addAll(titleLabel, contentBox);
        return root;
    }

    private void setupMovieTableColumns() {
        TableColumn<String[], String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        titleColumn.setPrefWidth(150);

        TableColumn<String[], String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));
        genreColumn.setPrefWidth(150);

        TableColumn<String[], String> languageColumn = new TableColumn<>("Language");
        languageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));
        languageColumn.setPrefWidth(150);

        TableColumn<String[], String> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[3]));
        ratingColumn.setPrefWidth(100);

        movieTable.getColumns().addAll(titleColumn, genreColumn, languageColumn, ratingColumn);
    }

    private void setupMemberTableColumns() {
        TableColumn<String[], String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        usernameColumn.setPrefWidth(100);

        TableColumn<String[], String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));
        firstNameColumn.setPrefWidth(100);

        TableColumn<String[], String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));
        lastNameColumn.setPrefWidth(100);

        TableColumn<String[], String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[3]));
        emailColumn.setPrefWidth(150);

        TableColumn<String[], String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[4]));
        phoneColumn.setPrefWidth(100);

        TableColumn<String[], String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[5]));
        passwordColumn.setPrefWidth(100);

        memberTable.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, emailColumn, phoneColumn, passwordColumn);
    }

    private void loadMoviesFromDatabase() {
        String query = "SELECT * FROM movies";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                movieList.add(new String[]{
                    resultSet.getString("title"),
                    resultSet.getString("genre"),
                    resultSet.getString("language"),
                    resultSet.getString("rating")
                });
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load movies: " + e.getMessage());
        }
    }

    private void loadMembersFromDatabase() {
        String query = "SELECT * FROM members";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                memberList.add(new String[]{
                    resultSet.getString("username"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("password")
                });
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load members: " + e.getMessage());
        }
    }

    private void addMovie(TextField titleField, TextField genreField, TextField languageField, TextField ratingField) {
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        String language = languageField.getText().trim();
        String ratingStr = ratingField.getText().trim();

        if (title.isEmpty() || genre.isEmpty() || language.isEmpty() || ratingStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "All fields must be filled.");
            return;
        }

        if (!RATING_PATTERN.matcher(ratingStr).matches()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Rating must be a valid number (e.g., 7.5).");
            return;
        }

        try {
            double rating = Double.parseDouble(ratingStr);
            movieList.add(new String[]{title, genre, language, ratingStr});
            saveMovieToDatabase(title, genre, language, rating);
            clearFields(titleField, genreField, languageField, ratingField);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid rating value: " + e.getMessage());
        }
    }

    private void saveMovieToDatabase(String title, String genre, String language, double rating) {
        String query = "INSERT INTO movies (title, genre, language, rating) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setString(3, language);
            stmt.setDouble(4, rating);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add movie: " + e.getMessage());
        }
    }

    private void removeMovie() {
        String[] selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a movie to remove.");
            return;
        }
        movieList.remove(selectedMovie);
        deleteMovieFromDatabase(selectedMovie[0]);
    }

    private void deleteMovieFromDatabase(String title) {
        String query = "DELETE FROM movies WHERE title = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove movie: " + e.getMessage());
        }
    }

    private void addMember(TextField usernameField, TextField firstNameField, TextField lastNameField, 
                         TextField emailField, TextField phoneField, TextField passwordField) {
        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "All fields must be filled.");
            return;
        }

        memberList.add(new String[]{username, firstName, lastName, email, phone, password});
        saveMemberToDatabase(username, firstName, lastName, email, phone, password);
        clearFields(usernameField, firstNameField, lastNameField, emailField, phoneField, passwordField);
    }

    private void saveMemberToDatabase(String username, String firstName, String lastName, String email, String phone, String password) {
        String query = "INSERT INTO members (username, first_name, last_name, email, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add member: " + e.getMessage());
        }
    }

    private void removeMember() {
        String[] selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a member to remove.");
            return;
        }
        memberList.remove(selectedMember);
        deleteMemberFromDatabase(selectedMember[0]);
    }

    private void deleteMemberFromDatabase(String username) {
        String query = "DELETE FROM members WHERE username = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove member: " + e.getMessage());
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) field.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
