package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

/**
 * Example demonstrating CubisLang usage in a JavaFX application
 * 
 * Note: This example requires JavaFX to be added as a dependency.
 * Uncomment the JavaFX dependencies in build.gradle to run this example.
 * 
 * Add to build.gradle:
 * dependencies {
 *     implementation 'org.openjfx:javafx-controls:17.0.2'
 *     implementation 'org.openjfx:javafx-fxml:17.0.2'
 * }
 */
public class JavaFXExample {
    
    /*
    private CubisLang lang;
    private Label greetingLabel;
    private Button saveButton;
    private Button changeLocaleButton;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize CubisLang
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(true)
                .setOnTranslationLoadedListener(locale -> {
                    Platform.runLater(this::updateUI);
                })
                .build()
        );
        
        // Create UI
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        greetingLabel = new Label();
        saveButton = new Button();
        changeLocaleButton = new Button("Change to Khmer");
        
        changeLocaleButton.setOnAction(e -> {
            String currentLocale = lang.getCurrentLocale();
            String newLocale = currentLocale.equals("en") ? "km" : "en";
            lang.setLocale(newLocale);
            changeLocaleButton.setText(
                newLocale.equals("en") ? "Change to Khmer" : "Change to English"
            );
        });
        
        root.getChildren().addAll(greetingLabel, saveButton, changeLocaleButton);
        
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        
        // Initial UI update
        updateUI();
        primaryStage.show();
    }
    
    private void updateUI() {
        greetingLabel.setText(lang.get("greeting"));
        saveButton.setText(lang.getWithContext("button_save", "ui"));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    */
    
    public static void main(String[] args) {
        System.out.println("=== JavaFX Example ===");
        System.out.println("This example requires JavaFX dependencies.");
        System.out.println("Please add JavaFX to your build.gradle and uncomment the code above.");
        System.out.println("\nAdd to build.gradle:");
        System.out.println("dependencies {");
        System.out.println("    implementation 'org.openjfx:javafx-controls:17.0.2'");
        System.out.println("    implementation 'org.openjfx:javafx-fxml:17.0.2'");
        System.out.println("}");
        
        // Demonstrate CubisLang without JavaFX UI
        CubisLang lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .build()
        );
        
        System.out.println("\nTranslations:");
        System.out.println("greeting: " + lang.get("greeting"));
        System.out.println("ui.button_save: " + lang.getWithContext("button_save", "ui"));
    }
}
