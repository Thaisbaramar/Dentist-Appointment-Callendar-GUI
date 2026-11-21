# Dental Appointment Scheduling System

This application is designed to streamline the management of dental appointments. It offers functionalities such as user registration, login, appointment scheduling, and appointment management, all presented through a user-friendly graphical interface created with Java Swing.

## Features

### User Registration and Login
- Four account types with different access levels: **Client** (patient), **Dentist**, **Secretary** and **Staff** (see the table below).
- Anyone can sign up from the login page, but public sign-up always creates a **Client** account. Secretaries register patients, and Staff create accounts of any type.

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
    javac -d out Everything/*.java Printing/*.java
    ```

4. **Run the application**: 
    ```bash
    java -cp out Everything.Main
    ```

5. **Run the access-control tests** (optional):
    ```bash
    javac -cp out -d out tests/AccessControlTest.java
    java -cp out AccessControlTest
    ```

## Access levels

| Permission | Client | Dentist | Secretary | Staff |
|---|:-:|:-:|:-:|:-:|
| See own appointments | yes | | | yes |
| See all appointments (with patient names) | | yes | yes | yes |
| Book for themself | yes | | | yes |
| Book for any patient | | | yes | yes |
| Cancel own appointments | yes | | | yes |
| Confirm an appointment (patient is notified) | | yes | | yes |
| Cancel any appointment | | yes (with a reason; patient is notified) | yes | yes |
| Register patients | | | yes | yes |
| Create accounts of any type | | | | yes |

A new appointment starts as **pending**. The dentist confirms it or cancels it with a reason, and the patient sees a notification the next time they log in (or under the **Notifications** button). A patient is also notified when the secretary books or cancels for them. A patient canceling their own appointment sends no notice.

The rules live in `Permission` and `UserType`. They are enforced in `AppointmentStore` and `UserManager`, not only by hiding buttons, so a screen that forgets to check still cannot break them (a disallowed call throws `SecurityException`).

Accounts for trying it out (created on first run): `patient1@example.com` / `patient1`, `dentist1@example.com` / `teeth1`, `secretary1@example.com` / `desk1`, `staff1@` / `admin1`.

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

### `UserType` and `Permission`
- `UserType` lists the roles: `CLIENT`, `DENTIST`, `SECRETARY` and `STAFF`. Each role has a set of `Permission` values (see the table above).

### `AppointmentStore`
- Holds all appointments and patient notifications, saves them to `TxtFiles/appointments.txt` and `TxtFiles/notifications.txt`, and checks the user's permissions on every list, book, confirm and cancel.

## File Persistence

### Appointments
- `AppointmentStore` saves every change to `TxtFiles/appointments.txt` (`date|time|patient email|patient name|status`) and loads it when the application starts. Appointments are now keyed by date and time.

### Accounts
- `TxtFiles/SavedInfo.txt` uses `email:password:TYPE:name`. Old three-field lines still load (the name defaults to the email). Passwords are still stored as plain text, which is fine for a class project but should be hashed before real use.
- `ScheduledAppointmentFrame` and `TimeSlotLoader` are from the first version and are no longer used by the main flow.
