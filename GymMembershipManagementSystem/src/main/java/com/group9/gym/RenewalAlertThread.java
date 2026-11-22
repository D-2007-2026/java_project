package com.group9.gym;

import java.sql.*;

public class RenewalAlertThread extends Thread {
    public RenewalAlertThread() { setName("RenewalAlertThread"); setDaemon(true); }
    @Override public void run() {
        try {
            while (!isInterrupted()) {
                try (Connection con = Database.connect();
                     PreparedStatement ps = con.prepareStatement(
                         "SELECT COUNT(*) FROM members WHERE date(end_date) BETWEEN date('now') AND date('now','+7 day')");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        System.out.println("[Renewal Alert] " + rs.getInt(1) + " membership(s) expire within 7 days.");
                } catch (SQLException e) {
                    System.err.println("Renewal alert error: " + e.getMessage());
                }
                Thread.sleep(60_000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
