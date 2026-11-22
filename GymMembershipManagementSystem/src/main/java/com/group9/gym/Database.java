package com.group9.gym;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:gym.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        String[] sql = {
            "CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT NOT NULL, email TEXT, plan TEXT NOT NULL, start_date TEXT NOT NULL, end_date TEXT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS plans (id INTEGER PRIMARY KEY AUTOINCREMENT, plan_name TEXT UNIQUE NOT NULL, duration_months INTEGER NOT NULL, fee REAL NOT NULL)",
            "CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY AUTOINCREMENT, member_id INTEGER NOT NULL, amount REAL NOT NULL, payment_date TEXT NOT NULL, note TEXT, FOREIGN KEY(member_id) REFERENCES members(id))",
            "CREATE TABLE IF NOT EXISTS attendance (id INTEGER PRIMARY KEY AUTOINCREMENT, member_id INTEGER NOT NULL, attendance_date TEXT NOT NULL, FOREIGN KEY(member_id) REFERENCES members(id))"
        };
        try (Connection con = connect(); Statement st = con.createStatement()) {
            for (String query : sql) st.execute(query);
            String[] plans = {
                "INSERT OR IGNORE INTO plans(plan_name,duration_months,fee) VALUES('Monthly',1,1000)",
                "INSERT OR IGNORE INTO plans(plan_name,duration_months,fee) VALUES('Quarterly',3,2700)",
                "INSERT OR IGNORE INTO plans(plan_name,duration_months,fee) VALUES('Yearly',12,10000)"
            };
            for (String query : plans) st.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database: " + e.getMessage(), e);
        }
    }
}
