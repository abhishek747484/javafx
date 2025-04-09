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
    // Declare table views for displaying movies and members
    private TableView<String[]> movieTable;
    private ObservableList<String[]> movieList;
    private TableView<String[]> memberTable;
    private ObservableList<String[]> memberList;
    
    // Regular expression for validating the movie rating input
    private static final Pattern RATING_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

    @Override
    public void start(Stage primaryStage) {
        VBox root = createRootLayout(); // Create the layout for the scene
        Scene scene = new Scene(root, 800, 800); // Define the scene with the root layout
        primaryStage.setTitle("Admin Dashboard"); // Set the window title
        primaryStage.setScene(scene);  // Set the scene to the stage
        
        primaryStage.setMaximized(true);  // Maximize the window by default
        primaryStage.show();  // Display the stage
        
        loadMoviesFromDatabase();  // Load movies from the database into the movie list
        loadMembersFromDatabase(); // Load members from the database into the member list
    }

    // Create the root layout containing all UI components
    private VBox createRootLayout() {
        VBox root = new VBox(20); // Vertical box to hold UI components
        root.setPadding(new Insets(40)); // Set padding around the edges
        root.setAlignment(Pos.CENTER);  // Center align the components
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2C3E50, #4CA1AF);"); // Set background style

        // Title label for the admin dashboard
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        // Set up the movie table and its layout
        movieTable = new TableView<>();
        movieList = FXCollections.observableArrayList(); // Create observable list for movie data
        movieTable.setItems(movieList); // Bind the movie table to the movie list
        movieTable.setStyle("-fx-background-color: white; -fx-border-radius: 10px;"); // Set style for the table
        movieTable.setMaxSize(600, 200); // Set maximum size for the table
        setupMovieTableColumns(); // Set up the columns for the movie table

        // Input fields and buttons for adding and removing movies
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

        // Button to add a new movie to the database
        Button addMovieButton = new Button("Add Movie");
        addMovieButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        addMovieButton.setOnAction(e -> addMovie(titleField, genreField, languageField, ratingField));

        // Button to remove the selected movie
        Button removeMovieButton = new Button("Remove Selected Movie");
        removeMovieButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        removeMovieButton.setOnAction(e -> removeMovie());

        // Layout for the movie input fields and buttons
        HBox movieInputBox = new HBox(10, titleField, genreField, languageField, ratingField, addMovieButton);
        movieInputBox.setAlignment(Pos.CENTER);

        VBox movieBox = new VBox(15, new Label("Movies"), movieTable, movieInputBox, removeMovieButton);
        movieBox.setAlignment(Pos.CENTER);  // Center align the movie section

        // Set up the member table and its layout
        memberTable = new TableView<>();
        memberList = FXCollections.observableArrayList(); // Create observable list for member data
        memberTable.setItems(memberList); // Bind the member table to the member list
        memberTable.setStyle("-fx-background-color: white; -fx-border-radius: 10px;"); // Set style for the table
        memberTable.setMaxSize(600, 200); // Set maximum size for the table
        setupMemberTableColumns(); // Set up the columns for the member table

        // Input fields and buttons for adding and removing members
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

        // Button to add a new member to the database
        Button addMemberButton = new Button("Add Member");
        addMemberButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        addMemberButton.setOnAction(e -> addMember(usernameField, firstNameField, lastNameField, emailField, phoneField, passwordField));

        // Button to remove the selected member
        Button removeMemberButton = new Button("Remove Selected Member");
        removeMemberButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        removeMemberButton.setOnAction(e -> removeMember());

        // Layout for the member input fields and buttons
        HBox memberInputBox = new HBox(10, usernameField, firstNameField, lastNameField, emailField, phoneField, passwordField, addMemberButton);
        memberInputBox.setAlignment(Pos.CENTER);

        VBox memberBox = new VBox(15, new Label("Members"), memberTable, memberInputBox, removeMemberButton);
        memberBox.setAlignment(Pos.CENTER);  // Center align the member section

        // Logout button to log out of the admin dashboard
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10px;");
        logoutButton.setOnAction(e -> {
            try {
                new Main().start(new Stage());  // Return to the login screen
                ((Node) e.getSource()).getScene().getWindow().hide();  // Hide the admin dashboard
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout and return to login screen.");
            }
        });

        // Main content layout
        VBox contentBox = new VBox(20, movieBox, memberBox, logoutButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(10));

        root.getChildren().addAll(titleLabel, contentBox);  // Add components to the root layout
        return root;
    }

    // Set up columns for the movie table
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

        movieTable.getColumns().addAll(titleColumn, genreColumn, languageColumn, ratingColumn);  // Add columns to the movie table
    }

    // Set up columns for the member table
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

        memberTable.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, emailColumn, phoneColumn, passwordColumn);  // Add columns to the member table
    }

    // Load movies from the database and add to the movie list
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

    // Load members from the database and add to the member list
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

    // Add a new movie to the database
    private void addMovie(TextField titleField, TextField genreField, TextField languageField, TextField ratingField) {
        String title = titleField.getText();
        String genre = genreField.getText();
        String language = languageField.getText();
        String rating = ratingField.getText();

        if (title.isEmpty() || genre.isEmpty() || language.isEmpty() || rating.isEmpty() || !RATING_PATTERN.matcher(rating).matches()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill in all fields with valid data.");
            return;
        }

        String query = "INSERT INTO movies (title, genre, language, rating) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, genre);
            statement.setString(3, language);
            statement.setString(4, rating);
            statement.executeUpdate();
            movieList.add(new String[]{title, genre, language, rating});  // Add new movie to the list
            titleField.clear();  // Clear input fields
            genreField.clear();
            languageField.clear();
            ratingField.clear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add movie: " + e.getMessage());
        }
    }

    // Remove the selected movie from the database and list
    private void removeMovie() {
        String[] selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to remove.");
            return;
        }

        String title = selectedMovie[0];
        String query = "DELETE FROM movies WHERE title = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.executeUpdate();
            movieList.remove(selectedMovie);  // Remove movie from the list
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove movie: " + e.getMessage());
        }
    }

    // Add a new member to the database
    private void addMember(TextField usernameField, TextField firstNameField, TextField lastNameField,
                           TextField emailField, TextField phoneField, TextField passwordField) {
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill in all fields with valid data.");
            return;
        }

        String query = "INSERT INTO members (username, first_name, last_name, email, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setString(6, password);
            statement.executeUpdate();
            memberList.add(new String[]{username, firstName, lastName, email, phone, password});  // Add new member to the list
            usernameField.clear();  // Clear input fields
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            phoneField.clear();
            passwordField.clear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add member: " + e.getMessage());
        }
    }

    // Remove the selected member from the database and list
    private void removeMember() {
        String[] selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to remove.");
            return;
        }

        String username = selectedMember[0];
        String query = "DELETE FROM members WHERE username = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
            memberList.remove(selectedMember);  // Remove member from the list
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove member: " + e.getMessage());
        }
    }

    // Show an alert with a specified type, title, and message
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
