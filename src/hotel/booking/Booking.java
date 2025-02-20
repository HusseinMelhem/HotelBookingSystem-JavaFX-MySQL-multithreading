package hotel.booking;

import java.time.LocalDate;

public class Booking {
    private int id;
    private String guestName;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Booking(int id, String guestName, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getId() { return id; }
    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    @Override
    public String toString() {
        return "Booking ID: " + id + " | Guest: " + guestName + " | Room: " + roomNumber + " | Check-in: " + checkIn + " | Check-out: " + checkOut;
    }
}
