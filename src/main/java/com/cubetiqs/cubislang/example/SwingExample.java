package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

import javax.swing.*;
import java.awt.*;

/**
 * Example demonstrating CubisLang usage in a Swing application
 */
public class SwingExample {
    private CubisLang lang;
    private JFrame frame;
    private JLabel greetingLabel;
    private JButton saveButton;
    private JButton changeLocaleButton;
    
    public SwingExample() {
        // Initialize CubisLang
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(true)
                .setOnTranslationLoadedListener(locale -> {
                    SwingUtilities.invokeLater(this::updateUI);
                })
                .build()
        );
        
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());
        
        // Create UI components with translations
        greetingLabel = new JLabel();
        saveButton = new JButton();
        changeLocaleButton = new JButton("Change to Khmer");
        
        // Add action listener to change locale
        changeLocaleButton.addActionListener(e -> {
            String currentLocale = lang.getCurrentLocale();
            String newLocale = currentLocale.equals("en") ? "km" : "en";
            lang.setLocale(newLocale);
            changeLocaleButton.setText(
                newLocale.equals("en") ? "Change to Khmer" : "Change to English"
            );
        });
        
        // Add components to frame
        frame.add(greetingLabel);
        frame.add(saveButton);
        frame.add(changeLocaleButton);
        
        // Initial UI update
        updateUI();
        
        // Show frame
        frame.setVisible(true);
    }
    
    private void updateUI() {
        greetingLabel.setText(lang.get("greeting"));
        saveButton.setText(lang.getWithContext("button_save", "ui"));
        frame.setTitle(lang.get("Hello World!"));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingExample::new);
    }
}
