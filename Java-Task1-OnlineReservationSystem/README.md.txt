# Online Reservation System

## Project Overview
The Online Reservation System is a Java-based application that allows users to log in, book tickets, and cancel existing reservations. It provides a simple graphical user interface and uses an SQLite database to store reservation details.

## Features
- User Login
- Ticket Booking
- Ticket Cancellation
- Database Connectivity
- Graphical User Interface (GUI)
- Reservation data storage using SQLite

## Technologies Used
- **Programming Language:** Java
- **GUI:** Java Swing
- **Database:** SQLite
- **Database Connectivity:** JDBC

## Project Structure
```text
Java-Task1-OnlineReservationSystem/
│
├── OnlineReservationSystem.java
├── reservation.db
├── lib/
│   └── SQLite JDBC Driver
└── README.md
```

## Requirements
- Java JDK installed
- SQLite JDBC Driver
- Any Java IDE (VS Code, Eclipse, or IntelliJ IDEA)

## How to Run

1. Clone or download this repository.
2. Open the project folder in VS Code.
3. Make sure the SQLite JDBC driver is available inside the `lib` folder.
4. Open the terminal in the project directory.
5. Compile the Java program:

```bash
javac -cp ".;lib/*" OnlineReservationSystem.java
```

6. Run the application:

```bash
java -cp ".;lib/*" OnlineReservationSystem
```

*Note: The above classpath commands are for Windows.*

## Database
The application uses SQLite to store reservation information. The database file is `reservation.db`.

## Author
Developed as part of the Oasis Infobyte Internship (OIBSIP).

## Repository
GitHub: https://github.com/jaishree-23/OIBSIP
