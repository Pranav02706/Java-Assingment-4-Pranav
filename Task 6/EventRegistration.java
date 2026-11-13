
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.List;

class DBConnection {

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/campus_events",
            "root",
            "pranav0272006"
        );
    }
}

class EventService {

    public void register(
            String name,
            String regNo,
            String event,
            String email) throws Exception {

        if (name.trim().isEmpty()
                || regNo.trim().isEmpty()
                || email.trim().isEmpty()) {
            throw new Exception("All fields are required");
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new Exception("Invalid email address");
        }

        String sql =
            "INSERT INTO registrations " +
            "(student_name, register_number, event_name, email) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, regNo);
            ps.setString(3, event);
            ps.setString(4, email);

            ps.executeUpdate();
        }
    }

    public ResultSet getRegistrations(String event)
            throws SQLException {

        Connection con = DBConnection.getConnection();

        String sql;

        if (event.equals("All")) {
            sql = "SELECT * FROM registrations " +
                  "ORDER BY id DESC LIMIT 100 OFFSET 0";
        } else {
            sql = "SELECT * FROM registrations " +
                  "WHERE event_name = ? " +
                  "ORDER BY id DESC LIMIT 100 OFFSET 0";
        }

        PreparedStatement ps = con.prepareStatement(sql);

        if (!event.equals("All")) {
            ps.setString(1, event);
        }

        return ps.executeQuery();
    }
}

public class EventRegistration extends JFrame {

    JTextField name = new JTextField(15);
    JTextField regNo = new JTextField(15);
    JTextField email = new JTextField(15);

    JComboBox<String> event = new JComboBox<>(
        new String[]{
            "Technical Quiz",
            "Coding Contest",
            "Cultural Fest"
        }
    );

    JComboBox<String> filter = new JComboBox<>(
        new String[]{
            "All",
            "Technical Quiz",
            "Coding Contest",
            "Cultural Fest"
        }
    );

    DefaultTableModel model = new DefaultTableModel(
        new String[]{
            "ID", "Name", "Register Number",
            "Event", "Email"
        }, 0
    );

    JTable table = new JTable(model);

    EventService service = new EventService();

    EventRegistration() {

        setTitle("Campus Event Registration");
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));

        form.add(new JLabel("Student Name"));
        form.add(name);

        form.add(new JLabel("Register Number"));
        form.add(regNo);

        form.add(new JLabel("Email"));
        form.add(email);

        form.add(new JLabel("Event"));
        form.add(event);

        JButton register = new JButton("Register");
        JButton refresh = new JButton("Refresh");

        form.add(register);
        form.add(refresh);

        JPanel top = new JPanel(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");

        JMenuItem export = new JMenuItem("Export CSV");
        JMenuItem exit = new JMenuItem("Exit");

        menu.add(export);
        menu.add(exit);
        menuBar.add(menu);

        top.add(menuBar, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel();

        JButton filterButton = new JButton("Filter");

        filterPanel.add(new JLabel("Filter by Event"));
        filterPanel.add(filter);
        filterPanel.add(filterButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(filterPanel, BorderLayout.SOUTH);

        register.addActionListener(e -> registerStudent());

        refresh.addActionListener(e ->
            loadRegistrations("All")
        );

        filterButton.addActionListener(e ->
            loadRegistrations(
                (String) filter.getSelectedItem()
            )
        );

        export.addActionListener(e -> exportCSV());

        exit.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);

        loadRegistrations("All");
    }

    void registerStudent() {

        String studentName = name.getText();
        String registerNumber = regNo.getText();
        String selectedEvent =
            (String) event.getSelectedItem();
        String studentEmail = email.getText();

        new SwingWorker<Void, Void>() {

            protected Void doInBackground() throws Exception {
                service.register(
                    studentName,
                    registerNumber,
                    selectedEvent,
                    studentEmail
                );

                return null;
            }

            protected void done() {
                try {
                    get();

                    JOptionPane.showMessageDialog(
                        EventRegistration.this,
                        "Registration successful"
                    );

                    name.setText("");
                    regNo.setText("");
                    email.setText("");

                    loadRegistrations("All");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        EventRegistration.this,
                        "Registration failed: "
                        + e.getCause().getMessage()
                    );
                }
            }
        }.execute();
    }

    void loadRegistrations(String selectedEvent) {

        new SwingWorker<Object[][], Void>() {

            protected Object[][] doInBackground()
                    throws Exception {

                java.util.List<Object[]> rows =
                    new java.util.ArrayList<>();

                try (Connection con =
                        DBConnection.getConnection()) {

                    String sql;

                    if (selectedEvent.equals("All")) {
                        sql = "SELECT * FROM registrations " +
                              "ORDER BY id DESC LIMIT 100 OFFSET 0";
                    } else {
                        sql = "SELECT * FROM registrations " +
                              "WHERE event_name = ? " +
                              "ORDER BY id DESC LIMIT 100 OFFSET 0";
                    }

                    try (PreparedStatement ps =
                            con.prepareStatement(sql)) {

                        if (!selectedEvent.equals("All")) {
                            ps.setString(1, selectedEvent);
                        }

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                rows.add(new Object[]{
                                    rs.getInt("id"),
                                    rs.getString("student_name"),
                                    rs.getString("register_number"),
                                    rs.getString("event_name"),
                                    rs.getString("email")
                                });
                            }
                        }
                    }
                }

                return rows.toArray(new Object[0][]);
            }

            protected void done() {
                try {
                    Object[][] rows = get();

                    model.setRowCount(0);

                    for (Object[] row : rows) {
                        model.addRow(row);
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        EventRegistration.this,
                        "Unable to load registrations"
                    );
                }
            }
        }.execute();
    }

    void exportCSV() {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(chooser.getSelectedFile()))) {

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print(model.getColumnName(i));

                if (i < model.getColumnCount() - 1) {
                    writer.print(",");
                }
            }

            writer.println();

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    String value =
                        String.valueOf(model.getValueAt(i, j));

                    writer.print("\"" +
                        value.replace("\"", "\"\"") + "\"");

                    if (j < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }

                writer.println();
            }

            JOptionPane.showMessageDialog(
                this, "CSV exported successfully"
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, "Export failed"
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new EventRegistration().setVisible(true)
        );
    }
}