import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentLoginGUI extends JFrame {
    private static int failedAttempts = 0;
    private static int lockoutDuration = 60; // seconds
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private javax.swing.Timer lockoutTimer;

    public StudentLoginGUI() {
        initializeLoginFrame();
    }

    private void initializeLoginFrame() {
        setTitle("Attendify - Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(400, 500));

        // Create main panel with gradient background
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create login card panel with responsive sizing
        JPanel loginCard = new RoundedPanel(20);
        loginCard.setBackground(Color.WHITE);
        loginCard.setLayout(new BorderLayout());

        // Create content panel with proper padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("Attendify");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username section
        usernameField = new JTextField();
        JPanel usernamePanel = createInputSection("Enter your Username", usernameField, true);

        // Password section
        passwordField = new JPasswordField();
        JPanel passwordPanel = createInputSection("Enter your Password", passwordField, false);

        // Login button
        loginButton = new JButton("Log in");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(33, 37, 41));
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect with smooth transition
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(52, 58, 64));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(33, 37, 41));
            }
        });

        // Add components to content panel with proper spacing
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        contentPanel.add(usernamePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(passwordPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        contentPanel.add(loginButton);

        loginCard.add(contentPanel, BorderLayout.CENTER);

        // Create wrapper panel for responsive centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Make the card responsive
        loginCard.setPreferredSize(new Dimension(400, 450));
        loginCard.setMaximumSize(new Dimension(500, 600));
        
        wrapperPanel.add(loginCard, gbc);
        mainPanel.add(wrapperPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(new LoginActionListener());
        passwordField.addActionListener(new LoginActionListener());

        setVisible(true);
    }

    private JPanel createInputSection(String labelText, JTextField field, boolean isTextField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        // Changed from LEFT_ALIGNMENT to CENTER_ALIGNMENT for proper centering
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(73, 80, 87));
        // Changed from LEFT_ALIGNMENT to CENTER_ALIGNMENT
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Setup the provided field
        if (isTextField) {
            field.setText("Username"); // Placeholder-like text
            field.setForeground(Color.GRAY);
        } else {
            JPasswordField passField = (JPasswordField) field;
            passField.setText("Password");
            passField.setEchoChar((char) 0); // Show text initially
            passField.setForeground(Color.GRAY);
        }

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setBackground(new Color(248, 249, 250));
        // Add center alignment for the text field as well
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add focus listeners for placeholder effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (isTextField) {
                    if (field.getText().equals("Username")) {
                        field.setText("");
                        field.setForeground(Color.BLACK);
                    }
                } else {
                    JPasswordField passField = (JPasswordField) field;
                    if (String.valueOf(passField.getPassword()).equals("Password")) {
                        passField.setText("");
                        passField.setEchoChar('•');
                        passField.setForeground(Color.BLACK);
                    }
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                    BorderFactory.createEmptyBorder(14, 19, 14, 19)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (isTextField) {
                    if (field.getText().isEmpty()) {
                        field.setText("Username");
                        field.setForeground(Color.GRAY);
                    }
                } else {
                    JPasswordField passField = (JPasswordField) field;
                    if (String.valueOf(passField.getPassword()).isEmpty()) {
                        passField.setText("Password");
                        passField.setEchoChar((char) 0);
                        passField.setForeground(Color.GRAY);
                    }
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(field);

        return panel;
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Handle placeholder text
            if (username.equals("Username") || username.isEmpty()) {
                JOptionPane.showMessageDialog(StudentLoginGUI.this, 
                    "Please enter your username.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.equals("Password") || password.isEmpty()) {
                JOptionPane.showMessageDialog(StudentLoginGUI.this, 
                    "Please enter your password.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (validateStudentLogin(username, password)) {
                    JOptionPane.showMessageDialog(StudentLoginGUI.this, 
                        "Login successful!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open student dashboard
                    new StudentDashboard(username);
                    dispose();
                } else {
                    failedAttempts++;
                    JOptionPane.showMessageDialog(StudentLoginGUI.this, 
                        "Invalid credentials. Attempt " + failedAttempts + "/3", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);

                    if (failedAttempts % 3 == 0) {
                        lockoutUser();
                        lockoutDuration += 60;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(StudentLoginGUI.this, 
                    "Error reading student data: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lockoutUser() {
        loginButton.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);

        JOptionPane.showMessageDialog(this, 
            "Too many failed attempts. Try again after " + lockoutDuration + " seconds.", 
            "Account Locked", JOptionPane.WARNING_MESSAGE);

        lockoutTimer = new javax.swing.Timer(1000, new ActionListener() {
            int timeLeft = lockoutDuration;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                loginButton.setText("Wait " + timeLeft + "s");
                
                if (timeLeft <= 0) {
                    loginButton.setText("Log in");
                    loginButton.setEnabled(true);
                    usernameField.setEnabled(true);
                    passwordField.setEnabled(true);
                    lockoutTimer.stop();
                }
            }
        });
        lockoutTimer.start();
    }

    private boolean validateStudentLogin(String studentId, String password) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] studentData = line.split(":");
                if (studentData.length < 3) continue;

                String id = studentData[0].trim();
                String storedPassword = studentData[2].trim();

                if (id.equals(studentId) && storedPassword.equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Custom panel for gradient background
    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            Color color1 = new Color(173, 216, 230); // Light blue
            Color color2 = new Color(135, 206, 235); // Sky blue
            
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Custom panel with rounded corners
    private class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Add subtle shadow effect
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2d.dispose();
        }
    }

    // Student Dashboard Class
    private class StudentDashboard extends JFrame {
        private String studentId;

        public StudentDashboard(String studentId) {
            this.studentId = studentId;
            initializeDashboard();
        }

        private void initializeDashboard() {
            setTitle("Attendify - Student Dashboard");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 600);
            setLocationRelativeTo(null);
            setResizable(true);
            setMinimumSize(new Dimension(400, 500));

            // Create main panel with gradient background
            JPanel mainPanel = new GradientPanel();
            mainPanel.setLayout(new BorderLayout());

            // Create dashboard card panel with responsive sizing
            JPanel dashboardCard = new RoundedPanel(20);
            dashboardCard.setBackground(Color.WHITE);
            dashboardCard.setLayout(new BorderLayout());

            // Create content panel with proper padding
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

            // Title
            JLabel titleLabel = new JLabel("Student Dashboard");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            titleLabel.setForeground(new Color(33, 37, 41));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // View Attendance History button
            JButton viewAttendanceButton = createDashboardButton("View Attendance History", new Color(33, 37, 41));
            viewAttendanceButton.addActionListener(e -> {
                try {
                    displayAttendanceSummary();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error loading attendance data: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Exit button
            JButton exitButton = createDashboardButton("Exit", new Color(220, 53, 69));
            exitButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to exit?", 
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            });

            // Add components to content panel with proper spacing
            contentPanel.add(titleLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            contentPanel.add(viewAttendanceButton);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
            contentPanel.add(exitButton);

            dashboardCard.add(contentPanel, BorderLayout.CENTER);

            // Create wrapper panel for responsive centering
            JPanel wrapperPanel = new JPanel(new GridBagLayout());
            wrapperPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(20, 20, 20, 20);
            
            // Make the card responsive
            dashboardCard.setPreferredSize(new Dimension(400, 350));
            dashboardCard.setMaximumSize(new Dimension(500, 500));
            
            wrapperPanel.add(dashboardCard, gbc);
            mainPanel.add(wrapperPanel, BorderLayout.CENTER);

            add(mainPanel);
            setVisible(true);
        }

        private JButton createDashboardButton(String text, Color backgroundColor) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setForeground(Color.WHITE);
            button.setBackground(backgroundColor);
            button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            button.setFocusPainted(false);

            // Add hover effect
            Color originalColor = backgroundColor;
            Color hoverColor = backgroundColor.darker();
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalColor);
                }
            });

            return button;
        }

        private void displayAttendanceSummary() throws IOException {
            File subjectsFile = new File("subjects.txt");
            if (!subjectsFile.exists()) {
                JOptionPane.showMessageDialog(this, "No subjects found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder summaryText = new StringBuilder();
            boolean hasData = false;

            try (BufferedReader subjectReader = new BufferedReader(new FileReader(subjectsFile))) {
                String line;
                while ((line = subjectReader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length < 2) continue;

                    String subjectName = parts[0].trim();
                    String studentList = parts[1];

                    if (!studentList.contains(studentId)) continue;

                    String attendanceInfo = getAttendanceForSubject(studentId, subjectName);
                    summaryText.append(attendanceInfo).append("\n\n");
                    hasData = true;
                }
            }

            if (hasData) {
                // Create a scrollable text area for the attendance summary
                JTextArea textArea = new JTextArea(summaryText.toString());
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                textArea.setBackground(Color.WHITE);
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Attendance Summary", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No attendance data found for your ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private String getAttendanceForSubject(String studentId, String subjectName) throws IOException {
            String attendanceFileName = subjectName.toLowerCase() + ".txt";
            File attendanceFile = new File(attendanceFileName);

            if (!attendanceFile.exists()) {
                return subjectName + ": No attendance data available.";
            }

            Map<String, Set<String>> dateAttendanceMap = new HashMap<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());

            try (BufferedReader attendanceReader = new BufferedReader(new FileReader(attendanceFile))) {
                String line;
                while ((line = attendanceReader.readLine()) != null) {
                    String[] data = line.split(" - ");
                    if (data.length < 4) continue;

                    String id = data[1].trim();
                    String status = data[2].trim();
                    String datetime = data[3].trim();
                    String dateOnly = datetime.split(" ")[0];

                    dateAttendanceMap.putIfAbsent(dateOnly, new HashSet<>());
                    if (status.equalsIgnoreCase("Present")) {
                        dateAttendanceMap.get(dateOnly).add(id);
                    }
                }
            }

            int totalDays = dateAttendanceMap.size();
            int presentDays = 0;

            for (String date : dateAttendanceMap.keySet()) {
                if (dateAttendanceMap.get(date).contains(studentId)) {
                    presentDays++;
                }
            }

            double percentage = totalDays == 0 ? 0 : (presentDays * 100.0) / totalDays;

            StringBuilder summary = new StringBuilder();
            summary.append("Subject: ").append(subjectName).append("\n");
            summary.append("Total Days: ").append(totalDays).append("\n");
            summary.append("Present: ").append(presentDays).append("\n");
            summary.append("Absent: ").append(totalDays - presentDays).append("\n");
            summary.append("Attendance %: ").append(String.format("%.2f", percentage)).append("%\n");

            if (percentage >= 75) {
                summary.append("Status: Eligible for exams\n");
            } else {
                summary.append("Status: *** NOT ELIGIBLE for exams ***\n");
            }

            if (dateAttendanceMap.containsKey(today)) {
                if (!dateAttendanceMap.get(today).contains(studentId)) {
                    summary.append("Today's Status: Absent");
                } else {
                    summary.append("Today's Status: Present");
                }
            } else {
                summary.append("Today's Status: Not marked yet");
            }

            return summary.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new StudentLoginGUI();
        });
    }
}