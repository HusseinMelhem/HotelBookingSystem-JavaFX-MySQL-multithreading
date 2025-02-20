package hotel.booking;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HotelGUI extends Application {
    private ListView<String> roomListView = new ListView<>();
    private ListView<String> bookingListView = new ListView<>();
    private TextField guestNameField = new TextField();
    private DatePicker checkInDate = new DatePicker();
    private DatePicker checkOutDate = new DatePicker();
    
    private String loggedInUsername; 
    
    public HotelGUI(String username) {
        this.loggedInUsername = username;
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🏨 Hotel Booking - " + loggedInUsername);

        Label titleLabel = new Label("🏨 Welcome, " + loggedInUsername);
        titleLabel.getStyleClass().add("label-title");

        guestNameField.setPromptText("Enter Guest Name");
        checkInDate.setPromptText("Check-in Date");
        checkOutDate.setPromptText("Check-out Date");

        Button bookButton = new Button("📅 Book Room");
        bookButton.getStyleClass().add("button-primary");

        Button cancelButton = new Button("🗑 Cancel Booking");
        cancelButton.getStyleClass().add("button-danger");

        Button refreshButton = new Button("🔄 Refresh Rooms");
        refreshButton.getStyleClass().add("button");
        
        // 📌 Load rooms and bookings
        loadRooms();
        loadBookings();

        bookButton.setOnAction(e -> bookRoom());
        cancelButton.setOnAction(e -> cancelBooking());
        refreshButton.setOnAction(e -> loadRooms());

        
        VBox layout = new VBox(10);
        layout.getStyleClass().add("vbox");
        layout.getChildren().addAll(titleLabel, roomListView, guestNameField, checkInDate, checkOutDate, bookButton, bookingListView, cancelButton, refreshButton);

        Scene scene = new Scene(layout, 450, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

       
    private void loadRooms() {
        roomListView.getItems().clear();

        BackgroundTask.runInBackground(() -> {
            String sql = "SELECT * FROM rooms WHERE is_booked = FALSE";
            List<String> rooms = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    rooms.add("Room " + rs.getInt("room_number") + " - " + rs.getString("type") + " ($" + rs.getDouble("price") + ")");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rooms.toArray(new String[0]);  // ✅ Ensure a return statement is always reachable
        }, new BackgroundTask.TaskCallback<String[]>() {
            @Override
            public void onSuccess(String[] result) {
                roomListView.getItems().addAll(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                showAlert("Error", "Failed to load rooms.");
            }
        });
    }



    private void loadBookings() {
        bookingListView.getItems().clear();

        BackgroundTask.runInBackground(() -> {
            String sql = "SELECT * FROM bookings WHERE username = ?";
            StringBuilder bookings = new StringBuilder();

            try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                 
                stmt.setString(1, loggedInUsername);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    bookings.append("Booking ").append(rs.getInt("id"))
                            .append(": ").append(rs.getString("guest_name"))
                            .append(" (Room ").append(rs.getInt("room_number")).append(")\n");
                }
            }
            return bookings.toString().split("\n");
        }, new BackgroundTask.TaskCallback<String[]>() {
            @Override
            public void onSuccess(String[] result) {
                bookingListView.getItems().addAll(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                showAlert("Error", "Failed to load bookings.");
            }
        });
    }


    private void bookRoom() {
        String guestName = guestNameField.getText();
        LocalDate checkIn = checkInDate.getValue();
        LocalDate checkOut = checkOutDate.getValue();

        if (guestName.isEmpty() || checkIn == null || checkOut == null || roomListView.getSelectionModel().isEmpty()) {
            showAlert("Error", "Please enter all details and select a room.");
            return;
        }

        String selectedRoom = roomListView.getSelectionModel().getSelectedItem();
        int roomNumber = Integer.parseInt(selectedRoom.split(" ")[1]);

        BackgroundTask.runInBackground(() -> {
            String sql = "INSERT INTO bookings (guest_name, username, room_number, check_in, check_out) VALUES (?, ?, ?, ?, ?)";
            String updateRoom = "UPDATE rooms SET is_booked = TRUE WHERE room_number = ?";
            boolean success = false;

            try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateRoom)) {

                stmt.setString(1, guestName);
                stmt.setString(2, loggedInUsername);
                stmt.setInt(3, roomNumber);
                stmt.setDate(4, Date.valueOf(checkIn));
                stmt.setDate(5, Date.valueOf(checkOut));
                stmt.executeUpdate();

                updateStmt.setInt(1, roomNumber);
                updateStmt.executeUpdate();

                success = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return success ? "Success" : "Failed";  // ✅ Ensure a return statement is always reachable
        }, new BackgroundTask.TaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("Success")) {
                    showAlert("Success", "Room booked successfully!");
                    loadRooms();
                    loadBookings();
                } else {
                    showAlert("Error", "Failed to book room.");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                showAlert("Error", "Database error occurred.");
            }
        });
    }



    private void cancelBooking() {
        if (bookingListView.getSelectionModel().isEmpty()) {
            showAlert("Error", "Please select a booking to cancel.");
            return;
        }

        String selectedBooking = bookingListView.getSelectionModel().getSelectedItem();
        int bookingId = Integer.parseInt(selectedBooking.split(" ")[1].replace(":", "")); // Extract ID

        BackgroundTask.runInBackground(() -> {
            String sql = "DELETE FROM bookings WHERE id = ? AND username = ?";
            String updateRoom = "UPDATE rooms SET is_booked = FALSE WHERE room_number = (SELECT room_number FROM bookings WHERE id = ?)";
            boolean success = false;

            try (Connection conn = DriverManager.getConnection(HotelDatabase.URL, HotelDatabase.USER, HotelDatabase.PASSWORD);
                 PreparedStatement updateStmt = conn.prepareStatement(updateRoom);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, bookingId);
                stmt.setString(2, loggedInUsername);
                updateStmt.setInt(1, bookingId);
                stmt.executeUpdate();
                updateStmt.executeUpdate();
                
            } catch (SQLException e) {
                e.printStackTrace(); // Debugging
            }
            return success ? "Success" : "Failed";
        }, new BackgroundTask.TaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("Success")) {
                    showAlert("Success", "Booking canceled.");
                    loadRooms();   // ✅ Refresh room availability
                    loadBookings(); // ✅ Refresh booking list
                } else {
                    showAlert("Error", "You can only cancel your own bookings.");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace(); // Debugging
                showAlert("Error", "Database error occurred.");
            }
        });
    }


    // 📌 Show Alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
