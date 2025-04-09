package application;

import database.DatabaseConnection;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import java.sql.*;
import java.util.*;

public class dashboard extends Application {

    // UI elements
    private ComboBox<String> genreComboBox;        // ComboBox for selecting movie genre
    private ComboBox<String> languageComboBox;     // ComboBox for selecting movie language
    private Slider ratingSlider;                   // Slider to set minimum rating filter
    private TextArea recommendationsArea;          // TextArea to display movie recommendations
    private Label movieCountLabel;                 // Label to display the number of movies found

    // Start method, initializes the primary stage and UI components
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(30);  // VBox layout with 30px spacing
        root.setPadding(new Insets(40, 40, 40, 40));  // Padding for the root container
        root.setStyle("-fx-background-color: #2C3E50;"); // Dark background color
        root.setAlignment(Pos.CENTER);  // Align the children in the center

        // Title label
        Label titleLabel = new Label("Movie Recommendation System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));  // Title font style
        titleLabel.setStyle("-fx-text-fill: #ECF0F1;");  // Light text color

        // GridPane for selecting genre, language, and rating
        GridPane selectionBox = new GridPane();
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setPadding(new Insets(30));  // Padding around the selection grid
        selectionBox.setHgap(15);  // Horizontal gap between components
        selectionBox.setVgap(15);  // Vertical gap between components
        selectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");

        // Genre ComboBox
        genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");
        genreComboBox.setPrefWidth(300);  // Set the preferred width
        genreComboBox.getItems().add("Any");  // Default option "Any"
        populateGenres(genreComboBox);  // Populate genre options from the database

        // Language ComboBox
        languageComboBox = new ComboBox<>();
        languageComboBox.setPromptText("Select Language");
        languageComboBox.setPrefWidth(300);  // Set the preferred width
        languageComboBox.getItems().add("Any");  // Default option "Any"
        populateLanguages(languageComboBox);  // Populate language options from the database

        // Rating slider for selecting minimum movie rating
        ratingSlider = new Slider(0, 10, 0);
        ratingSlider.setShowTickMarks(true);  // Show tick marks on the slider
        ratingSlider.setShowTickLabels(true);  // Show tick labels on the slider
        ratingSlider.setMajorTickUnit(2);  // Major tick step size
        ratingSlider.setMinorTickCount(1);  // Minor tick count
        ratingSlider.setPrefWidth(300);  // Set the preferred width

        // Add components to the selection box (grid layout)
        selectionBox.add(new Label("Choose your preferences:"), 0, 0, 2, 1);
        selectionBox.add(genreComboBox, 0, 1);
        selectionBox.add(languageComboBox, 1, 1);
        selectionBox.add(new Label("Minimum Rating:"), 0, 2);
        selectionBox.add(ratingSlider, 1, 2);
        selectionBox.setMaxSize(400, 400);  // Max size of the selection box

        // HBox for buttons (recommendation, clear filters, and random movie)
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 20;");

        // Button to get movie recommendations based on user preferences
        Button recommendButton = new Button("Get Recommendations");
        recommendButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        recommendButton.setMaxWidth(300);  // Set max width for the button
        recommendButton.setTooltip(new Tooltip("Click to get movie recommendations based on your selections"));
        recommendButton.setOnAction(e -> getRecommendations(genreComboBox.getValue(), languageComboBox.getValue(), ratingSlider.getValue(), recommendationsArea));

        // Button to clear all filters
        Button clearButton = new Button("Clear Filters");
        clearButton.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        clearButton.setMaxWidth(300);
        clearButton.setOnAction(e -> clearFilters());

        // Button to suggest a random movie
        Button randomButton = new Button("Random Movie");
        randomButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        randomButton.setMaxWidth(300);
        randomButton.setOnAction(e -> suggestRandomMovie(recommendationsArea));

        // Add the buttons to the button box
        buttonBox.getChildren().addAll(recommendButton, clearButton, randomButton);

        // Label to show the number of movies found
        movieCountLabel = new Label("Movies found: 0");
        movieCountLabel.setStyle("-fx-text-fill: #ECF0F1; -fx-font-size: 14px;");

        // TextArea to display the list of recommended movies
        recommendationsArea = new TextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setPrefRowCount(18);  // Set the number of rows for the text area
        recommendationsArea.setWrapText(true);  // Enable text wrapping
        recommendationsArea.setStyle("-fx-font-size: 14px; -fx-padding: 15;");
        recommendationsArea.setMaxSize(600, 600);  // Max size for the text area

        // Button to go back to the previous screen
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        backButton.setMaxWidth(300);
        backButton.setTooltip(new Tooltip("Return to the previous screen"));
        backButton.setOnAction(e -> {
            try {
                new Main().start(new Stage());  // Open the previous screen
                primaryStage.close();  // Close the current stage
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to login: " + ex.getMessage());
            }
        });

        // Add all components to the root layout
        root.getChildren().addAll(titleLabel, selectionBox, buttonBox, movieCountLabel, recommendationsArea, backButton);

        // Create and set the scene
        Scene scene = new Scene(root, 1000, 900);
        primaryStage.setTitle("Movie Recommender");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);  // Maximize the window
        primaryStage.show();
    }

    // Method to populate the genre ComboBox from the database
    private void populateGenres(ComboBox<String> genreComboBox) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT genre FROM movies WHERE genre IS NOT NULL ORDER BY genre")) {
            while (rs.next()) {
                String genre = rs.getString("genre");
                if (genre != null && !genre.trim().isEmpty()) {
                    genreComboBox.getItems().add(genre);
                }
            }
            if (genreComboBox.getItems().size() <= 1) {
                genreComboBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch genres: " + e.getMessage());
            genreComboBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");
        }
    }

    // Method to populate the language ComboBox from the database
    private void populateLanguages(ComboBox<String> languageComboBox) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT language FROM movies WHERE language IS NOT NULL ORDER BY language")) {
            while (rs.next()) {
                String language = rs.getString("language");
                if (language != null && !language.trim().isEmpty()) {
                    languageComboBox.getItems().add(language);
                }
            }
            if (languageComboBox.getItems().size() <= 1) {
                languageComboBox.getItems().addAll("English", "Spanish", "Hindi", "Korean", "Japanese");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch languages: " + e.getMessage());
            languageComboBox.getItems().addAll("English", "Spanish", "Hindi", "Korean", "Japanese");
        }
    }

    // Method to get movie recommendations based on user input
    private void getRecommendations(String genre, String language, double minRating, TextArea area) {
        if ("Any".equals(genre)) genre = null;  // Handle "Any" as null filter
        if ("Any".equals(language)) language = null;  // Handle "Any" as null filter

        // If no filters are selected, prompt the user
        if (genre == null && language == null && minRating == 0) {
            area.setText("Please select at least one filter (genre, language, or minimum rating).");
            movieCountLabel.setText("Movies found: 0");
            return;
        }

        List<Movie> recommendations = getMoviesFromDatabase(genre, language, minRating);
        if (recommendations.isEmpty()) {
            area.setText("No recommendations available for the selected criteria.");
            movieCountLabel.setText("Movies found: 0");
        } else {
            StringBuilder result = new StringBuilder("Recommended Movies:\n\n");
            if (genre != null && language != null) {
                result.append("Based on genre: ").append(genre).append(" and language: ").append(language);
                if (minRating > 0) result.append(" with rating ≥ ").append(String.format("%.1f", minRating));
                result.append("\n\n");
            } else if (genre != null) {
                result.append("Based on genre: ").append(genre);
                if (minRating > 0) result.append(" with rating ≥ ").append(String.format("%.1f", minRating));
                result.append("\n\n");
            } else if (language != null) {
                result.append("Based on language: ").append(language);
                if (minRating > 0) result.append(" with rating ≥ ").append(String.format("%.1f", minRating));
                result.append("\n\n");
            }

            // Display the recommended movies
            for (Movie movie : recommendations) {
                result.append(movie.getTitle()).append(" - Rating: ").append(movie.getRating()).append("\n");
            }
            area.setText(result.toString());
            movieCountLabel.setText("Movies found: " + recommendations.size());
        }
    }

    // Method to fetch movies from the database based on filters
    private List<Movie> getMoviesFromDatabase(String genre, String language, double minRating) {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT title, genre, language, rating FROM movies WHERE rating >= ?";  // Base query with rating filter
        if (genre != null) query += " AND genre = ?";  // Add genre filter if specified
        if (language != null) query += " AND language = ?";  // Add language filter if specified
        query += " ORDER BY rating DESC";  // Order results by rating in descending order

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, minRating);  // Set minimum rating
            int index = 2;  // Start setting other parameters (genre and language)
            if (genre != null) stmt.setString(index++, genre);
            if (language != null) stmt.setString(index++, language);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    double rating = rs.getDouble("rating");
                    movies.add(new Movie(title, rating));  // Add movie to the list
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error fetching movies: " + e.getMessage());
        }
        return movies;
    }

    // Method to clear all selected filters and reset UI components
    private void clearFilters() {
        genreComboBox.setValue("Any");
        languageComboBox.setValue("Any");
        ratingSlider.setValue(0);
        recommendationsArea.clear();
        movieCountLabel.setText("Movies found: 0");
    }

    // Method to show alert messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to suggest a random movie
    private void suggestRandomMovie(TextArea area) {
        List<Movie> randomMovies = getMoviesFromDatabase(null, null, 0);  // Fetch movies without any filter
        if (randomMovies != null && !randomMovies.isEmpty()) {
            Movie movie = randomMovies.get(new Random().nextInt(randomMovies.size()));  // Select random movie
            area.setText("Random Movie: " + movie.getTitle() + "\nRating: " + movie.getRating());
            movieCountLabel.setText("Movies found: 1");
        } else {
            area.setText("No movies available.");
            movieCountLabel.setText("Movies found: 0");
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}

// Movie class to store movie details
class Movie {
    private String title;
    private double rating;

    public Movie(String title, double rating) {
        this.title = title;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }
}
