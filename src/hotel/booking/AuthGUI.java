package hotel.booking;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class AuthGUI extends Application {
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label messageLabel = new Label();
    
    
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔑 Hotel Booking - Login / Signup");

        Label titleLabel = new Label("🔑 Welcome to Hotel Booking System");
        titleLabel.getStyleClass().add("label-title");

        usernameField.setPromptText("Enter username");
        passwordField.setPromptText("Enter password");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button-primary");

        Button signupButton = new Button("Signup");
        signupButton.getStyleClass().add("button");

        loginButton.setOnAction(e -> login(primaryStage));
        signupButton.setOnAction(e -> signup());

        VBox layout = new VBox(10);
        layout.getStyleClass().add("vbox");
        layout.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, signupButton, messageLabel);

        Scene scene = new Scene(layout, 350, 300);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void login(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                messageLabel.setText("✅ Login successful!");
                primaryStage.close();
                if (role.equals("admin")) {
                    new AdminGUI(username).start(new Stage());
                } else {
                    new HotelGUI(username).start(new Stage());
                }
            } else {
                messageLabel.setText("❌ Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void signup() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ Fields cannot be empty.");
            return;
        }

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')";
        try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            messageLabel.setText("✅ Signup successful! You can now login.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
