-- SQLite schema (also created automatically by Database.java)
CREATE TABLE members (
 id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT NOT NULL,
 email TEXT, plan TEXT NOT NULL, start_date TEXT NOT NULL, end_date TEXT NOT NULL
);
CREATE TABLE plans (
 id INTEGER PRIMARY KEY AUTOINCREMENT, plan_name TEXT UNIQUE NOT NULL,
 duration_months INTEGER NOT NULL, fee REAL NOT NULL
);
CREATE TABLE payments (
 id INTEGER PRIMARY KEY AUTOINCREMENT, member_id INTEGER NOT NULL,
 amount REAL NOT NULL, payment_date TEXT NOT NULL, note TEXT,
 FOREIGN KEY(member_id) REFERENCES members(id)
);
CREATE TABLE attendance (
 id INTEGER PRIMARY KEY AUTOINCREMENT, member_id INTEGER NOT NULL,
 attendance_date TEXT NOT NULL, FOREIGN KEY(member_id) REFERENCES members(id)
);
