package hotel.booking;

public class Room {
    private int roomNumber;
    private String type;
    private boolean isBooked;
    private double price;

    public Room(int roomNumber, String type, boolean isBooked, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.isBooked = isBooked;
        this.price = price;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public boolean isBooked() { return isBooked; }
    public double getPrice() { return price; }
    
    public void setBooked(boolean booked) { isBooked = booked; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - " + (isBooked ? "Booked" : "Available");
    }
}
