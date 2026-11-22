# Group 9 – Gym Membership Management System

A beginner-friendly Java Swing + JDBC mini project. It uses SQLite, so no separate database server is needed.

## Requirements
- JDK 17 or later
- Maven 3.8+ (VS Code: Extension Pack for Java is helpful)

## Run
1. Extract this ZIP.
2. Open the `GymMembershipManagementSystem` folder in VS Code or IntelliJ.
3. Open a terminal in that folder.
4. Run: `mvn clean compile`
5. Run: `mvn exec:java -Dexec.mainClass="com.group9.gym.App"`  
   If the exec plugin is not available, run `com.group9.gym.App` from the IDE after Maven dependencies finish downloading.

The SQLite database `gym.db` is created automatically in the project folder on first run.

## Features
- Register members
- Choose Monthly, Quarterly or Yearly membership
- Calculate plan fee and save payment
- Renew an existing membership
- Mark attendance
- View members and expiry report

## Database tables
- `members`: member details and membership start/end dates
- `plans`: available membership plans
- `payments`: payment records
- `attendance`: attendance log

## OOP concepts demonstrated
- Classes and objects: `Member`, `Payment`, and plan classes
- Constructors and encapsulation
- Inheritance: `MonthlyPlan`, `QuarterlyPlan`, `YearlyPlan` extend `Plan`
- Method overriding: each plan overrides `calculateFee()`
- Interface: `Renewable`
- Custom exception: `MembershipExpiredException`
- Multithreading: `RenewalAlertThread`
- JDBC: `PreparedStatement` for database operations
- Swing: `JFrame`, `JLabel`, `JButton`, `JTextField`, `JComboBox`, `JTabbedPane`, layouts and action events

## Notes
This is an academic demonstration project. Payment is recorded locally; it does not process real online payments.
