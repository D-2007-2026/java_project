package com.group9.gym.ui;

import com.group9.gym.Service;
import com.group9.gym.exception.MembershipExpiredException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GymFrame extends JFrame {
    private final DefaultTableModel membersModel = model("ID","Name","Phone","Plan","Start Date","End Date");
    private final DefaultTableModel paymentsModel = model("Payment ID","Member ID","Member","Amount (₹)","Date","Note");
    private final DefaultTableModel attendanceModel = model("Log ID","Member ID","Member","Date");
    private final DefaultTableModel expiryModel = model("ID","Name","Phone","Plan","Expiry Date");

    public GymFrame() {
        setTitle("Group 9 | Gym Membership Management System");
        setSize(1000,650); setMinimumSize(new Dimension(800,550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", dashboard());
        tabs.addTab("Member Registration", registration());
        tabs.addTab("Renew Membership", renewal());
        tabs.addTab("Attendance", attendance());
        tabs.addTab("Reports", reports());
        add(tabs);
        refreshAll();
    }

    private static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
    }
    private JPanel page() { JPanel p=new JPanel(new BorderLayout(12,12)); p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18)); return p; }
    private JPanel dashboard() {
        JPanel p=page(); p.add(new JLabel("<html><h1>Gym Membership Management System</h1><p>Group 9 • Java OOP Mini Project</p></html>"),BorderLayout.NORTH);
        JPanel center=new JPanel(new GridLayout(2,2,12,12));
        center.add(info("Member Registration","Register members and assign a membership plan."));
        center.add(info("Membership Plans","Monthly ₹1,000 • Quarterly ₹2,700 • Yearly ₹10,000"));
        center.add(info("Payments & Renewal","Record fee payments and extend memberships."));
        center.add(info("Attendance & Expiry","Mark attendance and review upcoming expiries."));
        p.add(center,BorderLayout.CENTER);
        JButton refresh=new JButton("Refresh Data"); refresh.addActionListener(e->refreshAll()); p.add(refresh,BorderLayout.SOUTH);
        return p;
    }
    private JPanel info(String title,String body) {
        JPanel p=new JPanel(new BorderLayout()); p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190,190,190)),BorderFactory.createEmptyBorder(18,18,18,18)));
        p.add(new JLabel("<html><h2>"+title+"</h2><p>"+body+"</p></html>"),BorderLayout.CENTER); return p;
    }
    private JPanel registration() {
        JPanel p=page(); JPanel form=new JPanel(new GridLayout(5,2,10,12));
        JTextField name=new JTextField(), phone=new JTextField(), email=new JTextField();
        JComboBox<String> plan=new JComboBox<>(new String[]{"Monthly","Quarterly","Yearly"});
        JLabel fee=new JLabel("₹1,000");
        form.add(new JLabel("Full name:")); form.add(name);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Email:")); form.add(email);
        form.add(new JLabel("Membership plan:")); form.add(plan);
        form.add(new JLabel("Plan fee:")); form.add(fee);
        plan.addActionListener(e->fee.setText(switch((String)plan.getSelectedItem()){case "Quarterly"->"₹2,700";case "Yearly"->"₹10,000";default->"₹1,000";}));
        JButton save=new JButton("Register Member"); save.addActionListener(e->{
            if(name.getText().isBlank()||phone.getText().isBlank()){message("Please enter name and phone.");return;}
            try { int id=Service.addMember(name.getText().trim(),phone.getText().trim(),email.getText().trim(),(String)plan.getSelectedItem());
                message("Member registered successfully. Member ID: "+id); name.setText("");phone.setText("");email.setText("");refreshAll();
            } catch(SQLException ex){message("Database error: "+ex.getMessage());}
        });
        JPanel top=new JPanel(new BorderLayout(10,10)); top.add(new JLabel("Register a new gym member"),BorderLayout.NORTH); top.add(form,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.LEFT)); bottom.add(save);
        p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(new JTable(membersModel)),BorderLayout.CENTER); p.add(bottom,BorderLayout.SOUTH); return p;
    }
    private JPanel renewal() {
        JPanel p=page(); JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField id=new JTextField(10); JButton renew=new JButton("Renew Membership");
        controls.add(new JLabel("Member ID:"));controls.add(id);controls.add(renew);
        renew.addActionListener(e->{try{Service.renewMember(Integer.parseInt(id.getText().trim()));message("Membership renewed and payment recorded.");id.setText("");refreshAll();}
            catch(NumberFormatException ex){message("Enter a valid numeric member ID.");}
            catch(SQLException|MembershipExpiredException ex){message(ex.getMessage());}});
        p.add(new JLabel("<html><h2>Renew an existing membership</h2><p>Renewal uses the member's current plan and records a payment.</p></html>"),BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(membersModel)),BorderLayout.CENTER);p.add(controls,BorderLayout.SOUTH);return p;
    }
    private JPanel attendance() {
        JPanel p=page(); JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField id=new JTextField(10); JButton mark=new JButton("Mark Attendance");
        controls.add(new JLabel("Member ID:"));controls.add(id);controls.add(mark);
        mark.addActionListener(e->{try{Service.markAttendance(Integer.parseInt(id.getText().trim()));message("Attendance marked for today.");id.setText("");refreshAll();}
            catch(NumberFormatException ex){message("Enter a valid numeric member ID.");}
            catch(SQLException|MembershipExpiredException ex){message(ex.getMessage());}});
        p.add(new JLabel("<html><h2>Attendance Log</h2><p>Only members with active memberships can be marked present.</p></html>"),BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(attendanceModel)),BorderLayout.CENTER);p.add(controls,BorderLayout.SOUTH);return p;
    }
    private JPanel reports() {
        JPanel p=page(); JTabbedPane reports=new JTabbedPane();
        reports.addTab("All Members",new JScrollPane(new JTable(membersModel)));
        reports.addTab("Payments",new JScrollPane(new JTable(paymentsModel)));
        reports.addTab("Expiring within 7 days",new JScrollPane(new JTable(expiryModel)));
        p.add(reports,BorderLayout.CENTER); JButton refresh=new JButton("Refresh Reports");
        refresh.addActionListener(e->refreshAll());p.add(refresh,BorderLayout.SOUTH);return p;
    }
    private void refreshAll() {
        try { fill(membersModel,Service.getMembers());fill(paymentsModel,Service.getPayments());
            fill(attendanceModel,Service.getAttendance());fill(expiryModel,Service.getExpiryReport()); }
        catch(SQLException e){message("Could not load records: "+e.getMessage());}
    }
    private void fill(DefaultTableModel m,List<String[]> rows){m.setRowCount(0);for(String[] r:rows)m.addRow(r);}
    private void message(String s){JOptionPane.showMessageDialog(this,s,"Gym Management System",JOptionPane.INFORMATION_MESSAGE);}
}
