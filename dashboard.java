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

// Main class extending JavaFX Application for GUI
public class dashboard extends Application {
    // UI components as private instance variables
    private ComboBox<String> genreComboBox;      // Dropdown for genre selection
    private ComboBox<String> languageComboBox;   // Dropdown for language selection
    private Slider ratingSlider;                 // Slider for minimum rating
    private TextArea recommendationsArea;        // Area to display movie recommendations
    private Label movieCountLabel;              // Label showing number of movies found

    @Override
    public void start(Stage primaryStage) {
        // Create main vertical layout container
        VBox root = new VBox(30);  // 30px spacing between children
        root.setPadding(new Insets(40, 40, 40, 40));  // 40px padding on all sides
        root.setStyle("-fx-background-color: #2C3E50;");  // Dark blue background
        root.setAlignment(Pos.CENTER);  // Center all children

        // Header section
        Label titleLabel = new Label("Movie Recommendation System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));  // Bold 36pt Arial font
        titleLabel.setStyle("-fx-text-fill: #ECF0F1;");  // Light gray text color

        // Selection controls grid
        GridPane selectionBox = new GridPane();
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setPadding(new Insets(30));  // 30px internal padding
        selectionBox.setHgap(15);  // 15px horizontal gap between columns
        selectionBox.setVgap(15);  // 15px vertical gap between rows
        selectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");  // Semi-transparent white with rounded corners

        // Initialize genre selection dropdown
        genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");  // Placeholder text
        genreComboBox.setPrefWidth(300);  // Fixed width of 300px
        genreComboBox.getItems().add("Any");  // Default "Any" option
        populateGenres(genreComboBox);  // Fill with genres from database

        // Initialize language selection dropdown
        languageComboBox = new ComboBox<>();
        languageComboBox.setPromptText("Select Language");
        languageComboBox.setPrefWidth(300);
        languageComboBox.getItems().add("Any");
        populateLanguages(languageComboBox);  // Fill with languages from database

        // Initialize rating slider (0-10 range)
        ratingSlider = new Slider(0, 10, 0);  // min=0, max=10, initial=0
        ratingSlider.setShowTickMarks(true);   // Show major tick marks
        ratingSlider.setShowTickLabels(true);  // Show numeric labels
        ratingSlider.setMajorTickUnit(2);      // Major ticks every 2 units
        ratingSlider.setMinorTickCount(1);     // Minor ticks every 1 unit
        ratingSlider.setPrefWidth(300);

        // Add components to selection grid
        selectionBox.add(new Label("Choose your preferences:"), 0, 0, 2, 1);  // Spans 2 columns
        selectionBox.add(genreComboBox, 0, 1);
        selectionBox.add(languageComboBox, 1, 1);
        selectionBox.add(new Label("Minimum Rating:"), 0, 2);
        selectionBox.add(ratingSlider, 1, 2);
        selectionBox.setMaxSize(400, 400);  // Maximum size constraint

        // Button container
        HBox buttonBox = new HBox(20);  // 20px spacing between buttons
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 20;");

        // Recommend button with action handler
        Button recommendButton = new Button("Get Recommendations");
        recommendButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");  // Green button styling
        recommendButton.setMaxWidth(300);
        recommendButton.setTooltip(new Tooltip("Click to get movie recommendations based on your selections"));
        recommendButton.setOnAction(e -> getRecommendations(
            genreComboBox.getValue(), 
            languageComboBox.getValue(), 
            ratingSlider.getValue(), 
            recommendationsArea
        ));

        // Clear filters button
        Button clearButton = new Button("Clear Filters");
        clearButton.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");  // Orange button styling
        clearButton.setMaxWidth(300);
        clearButton.setOnAction(e -> clearFilters());

        // Random movie button
        Button randomButton = new Button("Random Movie");
        randomButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");  // Blue button styling
        randomButton.setMaxWidth(300);
        randomButton.setOnAction(e -> suggestRandomMovie(recommendationsArea));

        buttonBox.getChildren().addAll(recommendButton, clearButton, randomButton);

        // Movie count display
        movieCountLabel = new Label("Movies found: 0");
        movieCountLabel.setStyle("-fx-text-fill: #ECF0F1; -fx-font-size: 14px;");

        // Recommendations display area
        recommendationsArea = new TextArea();
        recommendationsArea.setEditable(false);  // Read-only
        recommendationsArea.setPrefRowCount(18);  // Initial height in rows
        recommendationsArea.setWrapText(true);    // Word wrap enabled
        recommendationsArea.setStyle("-fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 15; -fx-border-radius: 15;");
        recommendationsArea.setMaxSize(600, 600);

        // Back button to return to main screen
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");  // Red button styling
        backButton.setMaxWidth(300);
        backButton.setTooltip(new Tooltip("Return to the previous screen"));
        backButton.setOnAction(e -> {
            try {
                new Main().start(new Stage());  // Open main screen
                primaryStage.close();          // Close current window
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to login: " + ex.getMessage());
            }
        });

        // Add all components to root layout
        root.getChildren().addAll(titleLabel, selectionBox, buttonBox, movieCountLabel, recommendationsArea, backButton);

        // Set up and show the scene
        Scene scene = new Scene(root, 1000, 900);  // Initial window size
        primaryStage.setTitle("Movie Recommender");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);  // Start maximized
        primaryStage.show();
    }

    // Populate genre dropdown from database with fallback options
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
            // Add default genres if database is empty or has insufficient data
            if (genreComboBox.getItems().size() <= 1) {
                genreComboBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch genres: " + e.getMessage());
            genreComboBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");  // Fallback options
        }
    }

    // Populate language dropdown from database with fallback options
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
            // Add default languages if database is empty or has insufficient data
            if (languageComboBox.getItems().size() <= 1) {
                languageComboBox.getItems().addAll("English", "Spanish", "Hindi", "Korean", "Japanese");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch languages: " + e.getMessage());
            languageComboBox.getItems().addAll("English", "Spanish", "Hindi", "Korean", "Japanese");  // Fallback options
        }
    }

    // Fetch and display movie recommendations based on filters
    private void getRecommendations(String genre, String language, double minRating, TextArea area) {
        // Convert "Any" selections to null for database query
        if ("Any".equals(genre)) genre = null;
        if ("Any".equals(language)) language = null;

        // Validate that at least one filter is applied
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
            // Build formatted output string
            StringBuilder result = new StringBuilder("Recommended Movies:\n\n");
            // Add filter description
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
            } else {
                result.append("Movies with rating ≥ ").append(String.format("%.1f", minRating)).append("\n\n");
            }
            // List movies
            for (Movie movie : recommendations) {
                result.append("• ").append(movie.getTitle()).append(" (Rating: ").append(movie.getRating()).append(")\n");
            }
            area.setText(result.toString());
            movieCountLabel.setText("Movies found: " + recommendations.size());
        }
    }

    // Query database for movies matching criteria
    private List<Movie> getMoviesFromDatabase(String genre, String language, double minRating) {
        List<Movie> movieList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT title, genre, language, rating FROM movies");
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        // Build dynamic WHERE clause
        if (genre != null || language != null || minRating > 0) {
            query.append(" WHERE ");
            if (genre != null) {
                conditions.add("genre = ?");
                parameters.add(genre);
            }
            if (language != null) {
                conditions.add("language = ?");
                parameters.add(language);
            }
            if (minRating > 0) {
                conditions.add("rating >= ?");
                parameters.add(minRating);
            }
            query.append(String.join(" AND ", conditions));
        }

        // Execute parameterized query
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movieList.add(new Movie(
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getString("language"),
                    rs.getDouble("rating")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch movies: " + e.getMessage());
        }
        return movieList;
    }

    // Reset all filters to default values
    private void clearFilters() {
        genreComboBox.setValue("Any");
        languageComboBox.setValue("Any");
        ratingSlider.setValue(0);
        recommendationsArea.setText("");
        movieCountLabel.setText("Movies found: 0");
    }

    // Suggest a random movie from the database
    private void suggestRandomMovie(TextArea area) {
        List<Movie> allMovies = getMoviesFromDatabase(null, null, 0);
        if (allMovies.isEmpty()) {
            area.setText("No movies available in the database.");
            movieCountLabel.setText("Movies found: 0");
        } else {
            Random rand = new Random();
            Movie randomMovie = allMovies.get(rand.nextInt(allMovies.size()));
            area.setText("Random Movie Suggestion:\n\n• " + randomMovie.getTitle() + 
                         "\n  - Genre: " + randomMovie.getGenre() + 
                         "\n  - Language: " + randomMovie.getLanguage() + 
                         "\n  - Rating: " + randomMovie.getRating());
            movieCountLabel.setText("Movies found: 1");
        }
    }

    // Utility method to show alert dialogs
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Entry point for JavaFX application
    public static void main(String[] args) {
        launch(args);
    }

    // Inner class to represent a Movie object
    class Movie {
        private final String title, genre, language;
        private final double rating;

        Movie(String title, String genre, String language, double rating) {
            this.title = title;
            this.genre = genre;
            this.language = language;
            this.rating = rating;
        }

        // Getter methods
        String getTitle() { return title; }
        String getGenre() { return genre; }
        String getLanguage() { return language; }
        double getRating() { return rating; }
    }
}
