# Dental Appointment Scheduling System

This application is designed to streamline the management of dental appointments. It offers functionalities such as user registration, login, appointment scheduling, and appointment management, all presented through a user-friendly graphical interface created with Java Swing.

## Features

### User Registration and Login
- Users can sign up and log in with distinct roles—clients, staff, or dentists.

### Appointment Scheduling
- Easily view available time slots and book appointments.

### Appointment Management
- View, schedule, or cancel appointments as needed.

### Calendar View
- Navigate through a calendar interface to select dates.

### File Persistence
- Appointment data is saved and loaded from a file to ensure continuity between sessions.

## Installation

To get started with the Dental Appointment Scheduling System, follow these steps:

1. **Clone the repository**: 
    ```bash
    git clone https://github.com/Thaisbaramar/Dentist-Appointment-Callendar-GUI
    ```

2. **Navigate to the project directory**: 
    ```bash
    cd dental-scheduling-system
    ```

3. **Compile the Java files**: 
    ```bash
    javac Everything/*.java
    ```

4. **Run the application**: 
    ```bash
    java Everything.Main
    ```

## Usage

### Running the Application
- When you start the application, the login screen will be the first interface you see.
- New users should head to the registration page to create an account.
- After logging in, you'll be directed to the calendar page where you can select a date and view available time slots.
- From here, you can schedule, view, or cancel your appointments.

### Scheduling Appointments
1. Pick a date using the calendar.
2. Choose an available time slot to schedule your appointment.
3. Enter your name and confirm the booking.

### Managing Appointments
- Review scheduled appointments for the selected date and time slot.
- Cancel any appointment if necessary.

## Class Overview

### `Main`
- The main entry point of the application. Sets up the graphical user interface and manages the overall execution flow using a thread pool.

### `UserManager`
- Manages user registration and login functionalities. Differentiates between clients, staff, and dentists, handling each appropriately.

### `LoginPage`
- Provides the graphical interface for user login. Works with `UserManager` to authenticate users.

### `RegistrationPage`
- Handles new user registrations. Interfaces with `UserManager` to add new clients to the system.

### `CalendarPage`
- Displays a calendar, allowing users to pick dates. Integrates with `TimeSlots` to show available slots for a chosen date.

### `TimeSlots`
- Presents available time slots for the selected date. Enables users to schedule or manage their appointments. Disables slots that have already been booked.

### `ScheduleManaging`
- Manages the scheduling and cancellation of appointments for a specific time slot. Updates the map that tracks scheduled appointments.

### `ScheduledAppointmentFrame`
- Provides detailed information about scheduled appointments for a selected date and time slot. Allows users to schedule, cancel, or return to the calendar view. Saves updated appointment details to a file.

### `TimeSlotLoader`
- Manages the loading and saving of scheduled appointments to a file. Ensures that the application can save and restore state between sessions.

### `TimeSlotsRunnable`
- Implements the `Runnable` interface to simulate the process of loading time slots based on user type and date. Introduces a brief delay to simulate loading time, which can improve the user experience by running in a separate thread.

### `UserType`
- An enum that defines different user roles: `CLIENT`, `DENTIST`, and `STAFF`. Helps categorize users and manage permissions within the application.

## File Persistence

### Saving Appointments
- The `ScheduledAppointmentFrame` class automatically saves scheduled appointments to a file named `scheduledAppointments.txt` whenever changes are made.

### Loading Appointments
- The `TimeSlotLoader` class is responsible for loading scheduled appointments from a file when the application starts, ensuring that any previously saved appointments are available for the user.

The ScheduledAppointmentFrame class automatically saves scheduled appointments to a file named scheduledAppointments.txt whenever changes are made.

Loading Appointments

The TimeSlotLoader class is responsible for loading scheduled appointments from a file when the application starts, ensuring that any previously saved appointments are available for the user.
