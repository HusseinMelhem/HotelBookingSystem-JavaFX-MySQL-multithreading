package hotel.booking;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class AdminGUI extends Application {
    private ListView<String> bookingListView = new ListView<>();
    
    private String loggedInUsername;  // 🔹 Store logged-in admin

    // 🔹 Constructor to receive logged-in username
    public AdminGUI(String username) {
        this.loggedInUsername = username;
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🛠 Admin Panel - " + loggedInUsername);

        Label titleLabel = new Label("📋 Admin: " + loggedInUsername);
        titleLabel.getStyleClass().add("label-title");

        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.getStyleClass().add("button");

        VBox layout = new VBox(10);
        layout.getStyleClass().add("vbox");
        layout.getChildren().addAll(titleLabel, bookingListView, refreshButton);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        loadAllBookings();
    }


    private void loadAllBookings() {
        bookingListView.getItems().clear();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookingListView.getItems().add("Booking " + rs.getInt("id") + ": " + rs.getString("guest_name") + " (Room " + rs.getInt("room_number") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
