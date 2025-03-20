package application;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class dashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // UI
        VBox root = new VBox(40);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1F1C2C, #928DAB);");
        root.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Movie Recommendation System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 3);");

        // genre and language selection
        VBox selectionBox = new VBox(25);
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-padding: 35; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        selectionBox.setMaxWidth(550);

        // Genre
        ComboBox<String> genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");
        genreComboBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");
        genreComboBox.setPrefWidth(320);

        // Language
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.setPromptText("Select Language");
        languageComboBox.getItems().addAll("English", "Spanish", "Hindi", "Korean", "Japanese");
        languageComboBox.setPrefWidth(320);

        // Button recommendations
        Button recommendButton = new Button("Get Recommendations");
        recommendButton.setStyle(
            "-fx-background-color: #27AE60;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-padding: 15 35;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"
        );
        recommendButton.setMaxWidth(270);

        // TextArea
        TextArea recommendationsArea = new TextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setPrefRowCount(10);
        recommendationsArea.setStyle(
            "-fx-control-inner-background: rgba(255, 255, 255, 0.95);" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 10;"
        );
        recommendationsArea.setWrapText(true);
        recommendationsArea.setMaxWidth(550);

        // Sample
        List<Movie> movies = Arrays.asList(
            new Movie("Inception", "Sci-Fi", "English"),
            new Movie("Parasite", "Drama", "Korean"),
            new Movie("The Dark Knight", "Action", "English"),
            new Movie("Amélie", "Comedy", "French"),
            new Movie("Train to Busan", "Horror", "Korean"),
            new Movie("Interstellar", "Sci-Fi", "English")
        );

        // Event handler
        recommendButton.setOnAction(e -> {
            String genre = genreComboBox.getValue();
            String language = languageComboBox.getValue();

            if (genre == null || language == null) {
                recommendationsArea.setText("Please select both genre and language.");
                return;
            }

            List<Movie> recommendations = movies.stream()
                .filter(movie -> movie.getGenre().equals(genre) && movie.getLanguage().equals(language))
                .collect(Collectors.toList());

            if (recommendations.isEmpty()) {
                recommendationsArea.setText("No recommendations available for the selected genre and language.");
            } else {
                StringBuilder result = new StringBuilder("Recommended Movies:\n\n");
                for (Movie movie : recommendations) {
                    result.append("• ").append(movie.getTitle()).append("\n");
                }
                recommendationsArea.setText(result.toString());
            }
        });

        // Back
        Button backButton = new Button("Back");
        backButton.setStyle(
            "-fx-background-color: #C0392B;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-padding: 15 35;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"
        );
        backButton.setMaxWidth(270);

        // Make Back button return 
        backButton.setOnAction(e -> {
            Main mainApp = new Main();
            try {
                mainApp.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        
        selectionBox.getChildren().addAll(
            createLabel("Select Genre and Language:"),
            genreComboBox,
            languageComboBox,
            recommendButton
        );

       
        root.getChildren().addAll(
            titleLabel,
            selectionBox,
            recommendationsArea,
            backButton
        );

        //Scene and Stage
        Scene scene = new Scene(root, 700, 900);
        primaryStage.setTitle("Movie Recommender");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Movie class
class Movie {
    private final String title;
    private final String genre;
    private final String language;

    public Movie(String title, String genre, String language) {
        this.title = title;
        this.genre = genre;
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }
}
