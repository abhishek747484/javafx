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
    private ComboBox<String> genreComboBox;
    private ComboBox<String> languageComboBox;
    private Slider ratingSlider;
    private TextArea recommendationsArea;
    private Label movieCountLabel;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(40, 40, 40, 40));
        root.setStyle("-fx-background-color: #2C3E50;");
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Movie Recommendation System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: #ECF0F1;");

        GridPane selectionBox = new GridPane();
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setPadding(new Insets(30));
        selectionBox.setHgap(15);
        selectionBox.setVgap(15);
        selectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");

        genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");
        genreComboBox.setPrefWidth(300);
        genreComboBox.getItems().add("Any");
        populateGenres(genreComboBox);

        languageComboBox = new ComboBox<>();
        languageComboBox.setPromptText("Select Language");
        languageComboBox.setPrefWidth(300);
        languageComboBox.getItems().add("Any");
        populateLanguages(languageComboBox);

        ratingSlider = new Slider(0, 10, 0);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setMajorTickUnit(2);
        ratingSlider.setMinorTickCount(1);
        ratingSlider.setPrefWidth(300);

        selectionBox.add(new Label("Choose your preferences:"), 0, 0, 2, 1);
        selectionBox.add(genreComboBox, 0, 1);
        selectionBox.add(languageComboBox, 1, 1);
        selectionBox.add(new Label("Minimum Rating:"), 0, 2);
        selectionBox.add(ratingSlider, 1, 2);
        selectionBox.setMaxSize(400, 400);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 20;");

        Button recommendButton = new Button("Get Recommendations");
        recommendButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        recommendButton.setMaxWidth(300);
        recommendButton.setTooltip(new Tooltip("Click to get movie recommendations based on your selections"));
        recommendButton.setOnAction(e -> getRecommendations(
            genreComboBox.getValue(), 
            languageComboBox.getValue(), 
            ratingSlider.getValue(), 
            recommendationsArea
        ));

        Button clearButton = new Button("Clear Filters");
        clearButton.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        clearButton.setMaxWidth(300);
        clearButton.setOnAction(e -> clearFilters());

        Button randomButton = new Button("Random Movie");
        randomButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        randomButton.setMaxWidth(300);
        randomButton.setOnAction(e -> suggestRandomMovie(recommendationsArea));

        buttonBox.getChildren().addAll(recommendButton, clearButton, randomButton);

        movieCountLabel = new Label("Movies found: 0");
        movieCountLabel.setStyle("-fx-text-fill: #ECF0F1; -fx-font-size: 14px;");

        recommendationsArea = new TextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setPrefRowCount(18);
        recommendationsArea.setWrapText(true);
        recommendationsArea.setStyle("-fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 15; -fx-border-radius: 15;");
        recommendationsArea.setMaxSize(600, 600);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15;");
        backButton.setMaxWidth(300);
        backButton.setTooltip(new Tooltip("Return to the previous screen"));
        backButton.setOnAction(e -> {
            try {
                new Main().start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to login: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(titleLabel, selectionBox, buttonBox, movieCountLabel, recommendationsArea, backButton);

        Scene scene = new Scene(root, 1000, 900);
        primaryStage.setTitle("Movie Recommender");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

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

    private void getRecommendations(String genre, String language, double minRating, TextArea area) {
        if ("Any".equals(genre)) genre = null;
        if ("Any".equals(language)) language = null;

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

            for (Movie movie : recommendations) {
                result.append(movie.getTitle()).append(" - Rating: ").append(movie.getRating()).append("\n");
            }
            area.setText(result.toString());
            movieCountLabel.setText("Movies found: " + recommendations.size());
        }
    }

    private List<Movie> getMoviesFromDatabase(String genre, String language, double minRating) {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT title, genre, language, rating FROM movies WHERE rating >= ?";
        if (genre != null) query += " AND genre = ?";
        if (language != null) query += " AND language = ?";
        query += " ORDER BY rating DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, minRating);
            int index = 2;
            if (genre != null) stmt.setString(index++, genre);
            if (language != null) stmt.setString(index++, language);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    double rating = rs.getDouble("rating");
                    movies.add(new Movie(title, rating));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error fetching movies: " + e.getMessage());
        }
        return movies;
    }

    private void clearFilters() {
        genreComboBox.setValue("Any");
        languageComboBox.setValue("Any");
        ratingSlider.setValue(0);
        recommendationsArea.clear();
        movieCountLabel.setText("Movies found: 0");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Random movie suggestion
    private void suggestRandomMovie(TextArea area) {
        List<Movie> randomMovies = getMoviesFromDatabase(null, null, 0);
        if (randomMovies != null && !randomMovies.isEmpty()) {
            Movie movie = randomMovies.get(new Random().nextInt(randomMovies.size()));
            area.setText("Random Movie: " + movie.getTitle() + "\nRating: " + movie.getRating());
            movieCountLabel.setText("Movies found: 1");
        } else {
            area.setText("No movies available.");
            movieCountLabel.setText("Movies found: 0");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Define the Movie class
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
