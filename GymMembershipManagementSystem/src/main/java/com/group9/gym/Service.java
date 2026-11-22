package com.group9.gym;

import com.group9.gym.model.*;
import com.group9.gym.exception.MembershipExpiredException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class Service {
    public static Plan getPlan(String name) {
        return switch (name) {
            case "Quarterly" -> new QuarterlyPlan();
            case "Yearly" -> new YearlyPlan();
            default -> new MonthlyPlan();
        };
    }

    public static int addMember(String name, String phone, String email, String planName) throws SQLException {
        Plan plan = getPlan(planName);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(plan.getDurationMonths());
        String memberSql = "INSERT INTO members(name,phone,email,plan,start_date,end_date) VALUES(?,?,?,?,?,?)";
        try (Connection con = Database.connect()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(memberSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,name); ps.setString(2,phone); ps.setString(3,email); ps.setString(4,planName);
                ps.setString(5,start.toString()); ps.setString(6,end.toString()); ps.executeUpdate();
                int id;
                try (ResultSet rs = ps.getGeneratedKeys()) { id = rs.next() ? rs.getInt(1) : 0; }
                savePayment(con, new Payment(id, plan.calculateFee(), LocalDate.now(), "New membership"));
                con.commit();
                return id;
            } catch (SQLException e) { con.rollback(); throw e; }
        }
    }

    private static void savePayment(Connection con, Payment p) throws SQLException {
        String sql = "INSERT INTO payments(member_id,amount,payment_date,note) VALUES(?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,p.getMemberId()); ps.setDouble(2,p.getAmount());
            ps.setString(3,p.getPaymentDate().toString()); ps.setString(4,p.getNote()); ps.executeUpdate();
        }
    }

    public static void renewMember(int id) throws SQLException, MembershipExpiredException {
        String find = "SELECT plan,end_date FROM members WHERE id=?";
        try (Connection con = Database.connect();
             PreparedStatement ps = con.prepareStatement(find)) {
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new MembershipExpiredException("Member ID not found.");
                String planName = rs.getString("plan");
                Plan plan = getPlan(planName);
                LocalDate oldEnd = LocalDate.parse(rs.getString("end_date"));
                LocalDate newStart = oldEnd.isBefore(LocalDate.now()) ? LocalDate.now() : oldEnd;
                LocalDate newEnd = newStart.plusMonths(plan.getDurationMonths());
                try (PreparedStatement update = con.prepareStatement(
                    "UPDATE members SET start_date=?, end_date=? WHERE id=?")) {
                    update.setString(1,newStart.toString()); update.setString(2,newEnd.toString());
                    update.setInt(3,id); update.executeUpdate();
                }
                savePayment(con, new Payment(id, plan.calculateFee(), LocalDate.now(), "Membership renewal"));
            }
        }
    }

    public static void markAttendance(int id) throws SQLException, MembershipExpiredException {
        try (Connection con = Database.connect();
             PreparedStatement check = con.prepareStatement("SELECT end_date FROM members WHERE id=?")) {
            check.setInt(1,id);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) throw new MembershipExpiredException("Member ID not found.");
                LocalDate expiry = LocalDate.parse(rs.getString(1));
                if (expiry.isBefore(LocalDate.now())) throw new MembershipExpiredException("Membership expired on " + expiry);
            }
            try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO attendance(member_id,attendance_date) VALUES(?,?)")) {
                insert.setInt(1,id); insert.setString(2,LocalDate.now().toString()); insert.executeUpdate();
            }
        }
    }

    public static List<String[]> getMembers() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT id,name,phone,plan,start_date,end_date FROM members ORDER BY id DESC";
        try (Connection con = Database.connect(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString("id"),rs.getString("name"),
                rs.getString("phone"),rs.getString("plan"),rs.getString("start_date"),rs.getString("end_date")});
        }
        return rows;
    }

    public static List<String[]> getPayments() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT p.id,p.member_id,m.name,p.amount,p.payment_date,p.note FROM payments p JOIN members m ON m.id=p.member_id ORDER BY p.id DESC";
        try (Connection con = Database.connect(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),
                String.format("%.2f",rs.getDouble(4)),rs.getString(5),rs.getString(6)});
        }
        return rows;
    }

    public static List<String[]> getAttendance() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT a.id,a.member_id,m.name,a.attendance_date FROM attendance a JOIN members m ON m.id=a.member_id ORDER BY a.id DESC";
        try (Connection con = Database.connect(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
        }
        return rows;
    }

    public static List<String[]> getExpiryReport() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT id,name,phone,plan,end_date FROM members WHERE date(end_date) <= date('now','+7 day') ORDER BY end_date";
        try (Connection con = Database.connect(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)});
        }
        return rows;
    }
}
