# Hotel Booking System - JavaFX & MySQL

A hotel management system built with **JavaFX** and **MySQL** that allows users to book, view, and cancel rooms.

## 🚀 Features
✅ User authentication (Signup/Login)  
✅ Room booking system  
✅ Admin panel to manage bookings  
✅ Smooth UI with CSS styles  
✅ Database integration using MySQL  
✅ Multithreading for smooth UI performance  

## ⚡ Installation
1. **Clone the repository:**
   ```sh
   git clone https://github.com/HusseinMelhem/HotelBookingSystem-JavaFX-MySQL.git
Set up MySQL Database:


✅ Step 1: Create the Database

CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;


✅ Step 2: Create users Table
Stores user authentication (admins & regular users).

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') NOT NULL
);


📝 Sample Data

INSERT INTO users (username, password, role) VALUES
('admin', 'adminpass', 'admin'),
('testuser', 'testpass', 'user');
✔ This ensures we have one admin and one regular user.



✅ Step 3: Create rooms Table
Stores available hotel rooms.

CREATE TABLE IF NOT EXISTS rooms (
    room_number INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT FALSE,
    price DOUBLE NOT NULL
);

INSERT INTO rooms (room_number, type, is_booked, price) VALUES
(101, 'Single', FALSE, 100.0),
(102, 'Double', FALSE, 150.0),
(103, 'Suite', FALSE, 250.0);
✔ This ensures the hotel has available rooms.



✅ Step 4: Create bookings Table
Stores room bookings made by users.

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


📝 Sample Data
INSERT INTO bookings (guest_name, username, room_number, check_in, check_out) VALUES
('John Doe', 'testuser', 101, '2025-02-18', '2025-02-20');



Run the Java project in Eclipse or IntelliJ.
🔧 Built With
Java 17+
JavaFX 23
MySQL
Eclipse / VS Code

👨‍💻 Author
Hussein Melhem
