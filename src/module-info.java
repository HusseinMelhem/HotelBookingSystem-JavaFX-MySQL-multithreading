/**
 * 
 */
/**
 * 
 */
module HotelBookingSystem {
	   requires javafx.controls;
	    requires javafx.fxml;
	    requires java.sql; // Needed for MySQL Connection

	    opens hotel.booking to javafx.fxml;
	    exports hotel.booking;
}