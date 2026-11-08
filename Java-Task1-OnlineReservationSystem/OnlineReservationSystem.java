
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class OnlineReservationSystem extends JFrame {

    static final String URL = "jdbc:sqlite:reservation.db";

    CardLayout layout;
    JPanel mainPanel;

    JTextField passenger, trainNo, trainName;
    JTextField journeyDate, source, destination;
    JComboBox<String> classType;

    JTextField pnrInput;
    JTextArea bookingDetails;
    String fetchedPNR = null;

    JTable bookingTable;
    DefaultTableModel tableModel;

    Color DARK = new Color(25, 42, 65);
    Color BLUE = new Color(42, 112, 190);
    Color LIGHT = new Color(240, 245, 250);
    Color GREEN = new Color(35, 145, 105);
    Color WHITE = Color.WHITE;

    Font headingFont = new Font("Arial", Font.BOLD, 24);
    Font normalFont = new Font("Arial", Font.PLAIN, 15);

    OnlineReservationSystem() {

        setTitle("Online Reservation System");
        setSize(950, 650);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboard(), "DASHBOARD");
        mainPanel.add(createBookingPanel(), "BOOK");
        mainPanel.add(createViewPanel(), "VIEW");
        mainPanel.add(createCancellationPanel(), "CANCEL");

        add(mainPanel);
        layout.show(mainPanel, "LOGIN");
    }

    // DATABASE

    static void initializeDatabase() {

        try (Connection con = DriverManager.getConnection(URL);
             Statement st = con.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, password TEXT NOT NULL)"
            );

            st.executeUpdate(
                "INSERT OR IGNORE INTO users VALUES ('admin','admin123')"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trains (" +
                "train_no INTEGER PRIMARY KEY, train_name TEXT)"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr TEXT PRIMARY KEY, passenger TEXT, train_no INTEGER, " +
                "train_name TEXT, class_type TEXT, journey_date TEXT, " +
                "source TEXT, destination TEXT)"
            );

            st.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12631,'Nellai Express')"
            );

            st.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12633,'Kanyakumari Express')"
            );

            st.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12635,'Vaigai Express')"
            );

            st.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12637,'Pandian Express')"
            );

            System.out.println("Database Ready!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // COMMON UI

    JPanel createBasePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT);
        return panel;
    }

    JButton createButton(String text, Color color) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 42));

        return button;
    }

    JLabel createHeading(String text) {

        JLabel label = new JLabel(text);
        label.setFont(headingFont);
        label.setForeground(DARK);
        label.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 15, 25)
        );

        return label;
    }

    // PAGE HEADER WITH BACK AND LOGOUT

    void addPageHeader(JPanel panel, String title) {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(LIGHT);

        JPanel navigation = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 15, 10)
        );

        navigation.setBackground(DARK);

        JButton back = createButton("Back", BLUE);
        JButton logout = createButton(
            "Logout", new Color(190, 65, 65)
        );

        navigation.add(back);
        navigation.add(logout);

        back.addActionListener(e -> {
            layout.show(mainPanel, "DASHBOARD");
        });

        logout.addActionListener(e -> logout());

        header.add(navigation, BorderLayout.NORTH);
        header.add(createHeading(title), BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
    }

    void logout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            layout.show(mainPanel, "LOGIN");
        }
    }

    void addField(JPanel panel, String label, JComponent field) {

        JLabel l = new JLabel(label);
        l.setFont(normalFont);
        field.setFont(normalFont);

        panel.add(l);
        panel.add(field);
    }

    // LOGIN

    JPanel createLoginPanel() {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(DARK);

        JPanel card = new JPanel(new GridLayout(0, 1, 10, 15));

        card.setBackground(WHITE);
        card.setBorder(
            BorderFactory.createEmptyBorder(35, 45, 35, 45)
        );
        card.setPreferredSize(new Dimension(390, 360));

        JLabel title = new JLabel(
            "ONLINE RESERVATION",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
            "Login to continue",
            SwingConstants.CENTER
        );

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JButton login = createButton("LOGIN", BLUE);

        card.add(title);
        card.add(subtitle);
        card.add(new JLabel("Username"));
        card.add(username);
        card.add(new JLabel("Password"));
        card.add(password);
        card.add(login);

        outer.add(card);

        login.addActionListener(e -> {

            try (Connection con = DriverManager.getConnection(URL);
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM users WHERE username=? AND password=?"
                 )) {

                ps.setString(1, username.getText().trim());
                ps.setString(2, new String(password.getPassword()));

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    JOptionPane.showMessageDialog(
                        this, "Login Successful!"
                    );

                    username.setText("");
                    password.setText("");

                    layout.show(mainPanel, "DASHBOARD");

                } else {
                    JOptionPane.showMessageDialog(
                        this, "Invalid Username or Password!"
                    );
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        password.addActionListener(e -> login.doClick());

        return outer;
    }

    // DASHBOARD

    JPanel createDashboard() {

        JPanel panel = createBasePanel();

        JPanel nav = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 15, 10)
        );
        nav.setBackground(DARK);

        JButton logout = createButton(
            "Logout", new Color(190, 65, 65)
        );

        nav.add(logout);
        logout.addActionListener(e -> logout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(LIGHT);
        header.add(nav, BorderLayout.NORTH);
        header.add(
            createHeading("Welcome to Online Reservation System"),
            BorderLayout.CENTER
        );

        JLabel subtitle = new JLabel(
            "Choose an option to continue",
            SwingConstants.CENTER
        );

        subtitle.setFont(normalFont);

        JPanel center = new JPanel(new GridLayout(2, 2, 25, 25));
        center.setBackground(LIGHT);
        center.setBorder(
            BorderFactory.createEmptyBorder(35, 90, 45, 90)
        );

        JButton book = createButton("Book Ticket", BLUE);
        JButton view = createButton("View Bookings", GREEN);
        JButton cancel = createButton(
            "Cancel Ticket", new Color(205, 100, 65)
        );

        JButton exit = createButton(
            "Exit", new Color(100, 100, 110)
        );

        center.add(book);
        center.add(view);
        center.add(cancel);
        center.add(exit);

        book.addActionListener(e ->
            layout.show(mainPanel, "BOOK")
        );

        view.addActionListener(e -> {
            loadBookings();
            layout.show(mainPanel, "VIEW");
        });

        cancel.addActionListener(e ->
            layout.show(mainPanel, "CANCEL")
        );

        exit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this, "Do you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(LIGHT);
        content.add(subtitle, BorderLayout.NORTH);
        content.add(center, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // BOOK TICKET PAGE

    JPanel createBookingPanel() {

        JPanel panel = createBasePanel();

        addPageHeader(panel, "Book Your Ticket");

        JPanel form = new JPanel(new GridLayout(8, 2, 15, 15));
        form.setBackground(WHITE);
        form.setBorder(
            BorderFactory.createEmptyBorder(25, 80, 25, 80)
        );

        passenger = new JTextField();
        trainNo = new JTextField();
        trainName = new JTextField();
        trainName.setEditable(false);

        classType = new JComboBox<>(new String[]{
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });

        journeyDate = new JTextField();
        source = new JTextField();
        destination = new JTextField();

        JButton findTrain = createButton("Find Train", GREEN);
        JButton book = createButton("Book Ticket", BLUE);

        addField(form, "Passenger Name:", passenger);
        addField(form, "Train Number:", trainNo);
        addField(form, "Train Name:", trainName);

        form.add(new JLabel("Find Train:"));
        form.add(findTrain);

        addField(form, "Class Type:", classType);
        addField(form, "Journey Date (YYYY-MM-DD):", journeyDate);
        addField(form, "Source Station:", source);
        addField(form, "Destination Station:", destination);

        JPanel bottom = new JPanel();
        bottom.setBackground(LIGHT);
        bottom.add(book);

        findTrain.addActionListener(e -> findTrain());
        book.addActionListener(e -> bookTicket());

        panel.add(form, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // FIND TRAIN

    void findTrain() {

        String number = trainNo.getText().trim();

        if (!number.matches("\\d+")) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid train number!"
            );
            return;
        }

        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT train_name FROM trains WHERE train_no=?"
             )) {

            ps.setInt(1, Integer.parseInt(number));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                trainName.setText(rs.getString("train_name"));
                JOptionPane.showMessageDialog(
                    this, "Train Found Successfully!"
                );
            } else {
                trainName.setText("");
                JOptionPane.showMessageDialog(this, "Train not found!");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // BOOK TICKET

    void bookTicket() {

        String name = passenger.getText().trim();
        String number = trainNo.getText().trim();
        String train = trainName.getText().trim();
        String type = (String) classType.getSelectedItem();
        String date = journeyDate.getText().trim();
        String from = source.getText().trim();
        String to = destination.getText().trim();

        if (name.isEmpty() || number.isEmpty() || train.isEmpty()
            || date.isEmpty() || from.isEmpty() || to.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please fill all fields!"
            );
            return;
        }

        if (!number.matches("\\d+")) {
            JOptionPane.showMessageDialog(
                this, "Train number must be numeric!"
            );
            return;
        }

        if (from.equalsIgnoreCase(to)) {
            JOptionPane.showMessageDialog(
                this, "Source and destination cannot be the same!"
            );
            return;
        }

        try {
            LocalDate journey = LocalDate.parse(date);

            if (journey.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                    this, "Journey date cannot be in the past!"
                );
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Enter date in YYYY-MM-DD format!"
            );
            return;
        }

        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement check = con.prepareStatement(
                 "SELECT train_name FROM trains WHERE train_no=?"
             )) {

            check.setInt(1, Integer.parseInt(number));

            ResultSet rs = check.executeQuery();

            if (!rs.next() ||
                !train.equals(rs.getString("train_name"))) {

                JOptionPane.showMessageDialog(
                    this, "Please find a valid train first!"
                );
                return;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        String pnr = UUID.randomUUID().toString()
            .replace("-", "").substring(0, 12).toUpperCase();

        String sql = "INSERT INTO reservations VALUES (?,?,?,?,?,?,?,?)";

        try (Connection con = DriverManager.getConnection(URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pnr);
            ps.setString(2, name);
            ps.setInt(3, Integer.parseInt(number));
            ps.setString(4, train);
            ps.setString(5, type);
            ps.setString(6, date);
            ps.setString(7, from);
            ps.setString(8, to);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "TICKET BOOKED SUCCESSFULLY!\n\n" +
                "PNR: " + pnr +
                "\nPassenger: " + name +
                "\nTrain: " + train +
                "\nClass: " + type +
                "\nDate: " + date +
                "\nFrom: " + from +
                "\nTo: " + to
            );

            passenger.setText("");
            trainNo.setText("");
            trainName.setText("");
            journeyDate.setText("");
            source.setText("");
            destination.setText("");
            classType.setSelectedIndex(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this, "Booking failed: " + ex.getMessage()
            );
        }
    }

    // VIEW BOOKINGS PAGE

    JPanel createViewPanel() {

        JPanel panel = createBasePanel();

        addPageHeader(panel, "All Bookings");

        String[] columns = {
            "PNR", "Passenger", "Train No",
            "Train Name", "Class", "Date",
            "Source", "Destination"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setRowHeight(30);
        bookingTable.setFont(normalFont);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JButton refresh = createButton("Refresh", GREEN);

        JPanel bottom = new JPanel();
        bottom.setBackground(LIGHT);
        bottom.add(refresh);

        refresh.addActionListener(e -> loadBookings());

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    void loadBookings() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM reservations ORDER BY journey_date";

        try (Connection con = DriverManager.getConnection(URL);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Object[] row = {
                    rs.getString("pnr"),
                    rs.getString("passenger"),
                    rs.getInt("train_no"),
                    rs.getString("train_name"),
                    rs.getString("class_type"),
                    rs.getString("journey_date"),
                    rs.getString("source"),
                    rs.getString("destination")
                };

                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                    this, "No bookings available!"
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // CANCELLATION PAGE

    JPanel createCancellationPanel() {

        JPanel panel = createBasePanel();

        addPageHeader(panel, "Cancel Your Ticket");

        JPanel content = new JPanel(new BorderLayout(15, 20));
        content.setBackground(WHITE);
        content.setBorder(
            BorderFactory.createEmptyBorder(35, 60, 35, 60)
        );

        JPanel top = new JPanel(new GridLayout(2, 2, 15, 15));
        top.setBackground(WHITE);

        pnrInput = new JTextField();
        JButton fetch = createButton("Fetch Booking", BLUE);

        top.add(new JLabel("Enter PNR Number:"));
        top.add(pnrInput);
        top.add(new JLabel(""));
        top.add(fetch);

        bookingDetails = new JTextArea();
        bookingDetails.setEditable(false);
        bookingDetails.setFont(
            new Font("Monospaced", Font.PLAIN, 15)
        );

        bookingDetails.setBackground(LIGHT);

        JButton cancel = createButton(
            "Confirm Cancellation",
            new Color(195, 65, 65)
        );

        cancel.setEnabled(false);

        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(bookingDetails), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(WHITE);
        bottom.add(cancel);

        content.add(bottom, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);

        fetch.addActionListener(e -> {

            String pnr = pnrInput.getText().trim();

            if (pnr.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please enter PNR number!"
                );
                return;
            }

            try (Connection con = DriverManager.getConnection(URL);
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM reservations WHERE pnr=?"
                 )) {

                ps.setString(1, pnr);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    fetchedPNR = rs.getString("pnr");

                    bookingDetails.setText(
                        "BOOKING DETAILS\n\n" +
                        "PNR: " + rs.getString("pnr") +
                        "\nPassenger: " + rs.getString("passenger") +
                        "\nTrain Number: " + rs.getInt("train_no") +
                        "\nTrain Name: " + rs.getString("train_name") +
                        "\nClass: " + rs.getString("class_type") +
                        "\nDate: " + rs.getString("journey_date") +
                        "\nSource: " + rs.getString("source") +
                        "\nDestination: " + rs.getString("destination")
                    );

                    cancel.setEnabled(true);

                } else {

                    fetchedPNR = null;
                    bookingDetails.setText("Booking not found!");
                    cancel.setEnabled(false);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        cancel.addActionListener(e -> {

            if (fetchedPNR == null) return;

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this ticket?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                try (Connection con = DriverManager.getConnection(URL);
                     PreparedStatement ps = con.prepareStatement(
                         "DELETE FROM reservations WHERE pnr=?"
                     )) {

                    ps.setString(1, fetchedPNR);

                    int result = ps.executeUpdate();

                    if (result > 0) {

                        JOptionPane.showMessageDialog(
                            this, "Ticket Cancelled Successfully!"
                        );

                        bookingDetails.setText("");
                        pnrInput.setText("");
                        fetchedPNR = null;
                        cancel.setEnabled(false);
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                        this, ex.getMessage()
                    );
                }
            }
        });

        return panel;
    }

    // MAIN

    public static void main(String[] args) {

        initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            new OnlineReservationSystem().setVisible(true);
        });
    }
}
