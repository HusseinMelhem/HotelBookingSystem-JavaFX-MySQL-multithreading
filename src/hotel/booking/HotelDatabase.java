package hotel.booking;

import java.sql.*;

public class HotelDatabase {
    public static final String URL = "jdbc:mysql://gamingpc:3306/hotel_db";
    public static final String USER = "root";
    public static final String PASSWORD = "has1212san";

    static {
        createTables();
    }

    public static void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role ENUM('admin', 'user') NOT NULL
            );
        """;

        String createRoomsTable = """
            CREATE TABLE IF NOT EXISTS rooms (
                room_number INT PRIMARY KEY,
                type VARCHAR(50) NOT NULL,
                is_booked BOOLEAN NOT NULL DEFAULT FALSE,
                price DOUBLE NOT NULL
            );
        """;

        String createBookingsTable = """
            CREATE TABLE IF NOT EXISTS bookings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                guest_name VARCHAR(100) NOT NULL,
                username VARCHAR(50) NOT NULL,
                room_number INT,
                check_in DATE NOT NULL,
                check_out DATE NOT NULL,
                FOREIGN KEY (room_number) REFERENCES rooms(room_number),
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createRoomsTable);
            stmt.execute(createBookingsTable);
            System.out.println("✅ Tables checked/created successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to create tables: " + e.getMessage());
        }
    }
}
