import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;
import javax.imageio.ImageIO;

public class Main extends JFrame {
    // Static variables for camera functionality
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private static final VideoCapture camera = new VideoCapture(0);
    
    // GUI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    // QR Scanner components
    private JLabel cameraLabel;
    private JLabel scanStatusLabel;
    private javax.swing.Timer cameraTimer;
    private javax.swing.Timer timeoutTimer;
    private boolean isScanning = false;
    
    // Login tracking variables
    private int failedAttempts = 0;
    private int lockMultiplier = 0;
    private String currentSubject = null;
    private javax.swing.Timer lockoutTimer;

    public Main() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Attendify - Teacher Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create CardLayout for switching between screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create login panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        
        // Create dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        mainPanel.add(dashboardPanel, "DASHBOARD");
        
        // Create QR scanner panel
        JPanel qrScannerPanel = createQRScannerPanel();
        mainPanel.add(qrScannerPanel, "QR_SCANNER");
        
        add(mainPanel);
        
        // Show login panel first
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(176, 196, 222)); // Light blue background
        panel.setLayout(null);

        // Create centered white container
        JPanel container = new JPanel();
        container.setBackground(Color.WHITE);
        container.setBounds(200, 150, 400, 300);
        container.setLayout(null);
        container.setBorder(createRoundedBorder(20));

        // Title
        JLabel titleLabel = new JLabel("Attendify", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(0, 30, 400, 40);
        container.add(titleLabel);

        // Username label and field
        JLabel usernameLabel = new JLabel("Enter your Username");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setBounds(30, 90, 340, 20);
        container.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(30, 115, 340, 35);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setBackground(new Color(248, 248, 248));
        container.add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Enter your Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setBounds(30, 165, 340, 20);
        container.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(30, 190, 340, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setBackground(new Color(248, 248, 248));
        container.add(passwordField);

        // Login button
        loginButton = new JButton("Log in");
        loginButton.setBounds(30, 240, 340, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(33, 37, 41)); // Dark background
        loginButton.setBorder(createRoundedBorder(8));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(23, 27, 31));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(33, 37, 41));
            }
        });

        loginButton.addActionListener(new LoginActionListener());
        container.add(loginButton);

        // Status label for error messages
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(30, 285, 340, 20);
        container.add(statusLabel);

        panel.add(container);

        // Add Enter key functionality
        getRootPane().setDefaultButton(loginButton);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(176, 196, 222)); // Light blue background
        panel.setLayout(null);

        // Create centered white container
        JPanel container = new JPanel();
        container.setBackground(Color.WHITE);
        container.setBounds(200, 150, 400, 300);
        container.setLayout(null);
        container.setBorder(createRoundedBorder(20));

        // Title
        JLabel titleLabel = new JLabel("Teacher Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(0, 30, 400, 40);
        container.add(titleLabel);

        // View Attendance History button
        JButton viewHistoryButton = createDashboardButton("View Attendance History", 80);
        viewHistoryButton.addActionListener(e -> {
            showDashboard(currentSubject);
        });
        container.add(viewHistoryButton);

        // Start Attendance button
        JButton startAttendanceButton = createDashboardButton("Start Attendance", 130);
        startAttendanceButton.addActionListener(e -> {
            startAttendanceProcess();
        });
        container.add(startAttendanceButton);

        // Exit button (red)
        JButton exitButton = createDashboardButton("Exit", 180);
        exitButton.setBackground(new Color(220, 53, 69)); // Red background
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(200, 33, 49));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(220, 53, 69));
            }
        });
        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                releaseCamera();
                System.exit(0);
            }
        });
        container.add(exitButton);

        panel.add(container);
        return panel;
    }

    private JPanel createQRScannerPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(176, 196, 222)); // Light blue background
        panel.setLayout(null);

        // Create centered white container
        JPanel container = new JPanel();
        container.setBackground(Color.WHITE);
        container.setBounds(150, 100, 500, 450);
        container.setLayout(null);
        container.setBorder(createRoundedBorder(20));

        // Title
        JLabel titleLabel = new JLabel("Attendify", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(0, 20, 500, 40);
        container.add(titleLabel);

        // Camera view area with dashed border
        JPanel cameraPanel = new JPanel();
        cameraPanel.setBounds(100, 80, 300, 200);
        cameraPanel.setBackground(new Color(248, 248, 248));
        cameraPanel.setBorder(createDashedBorder());
        cameraPanel.setLayout(new BorderLayout());

        // Camera label for displaying video feed
        cameraLabel = new JLabel("Waiting for QR code", SwingConstants.CENTER);
        cameraLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        cameraLabel.setForeground(Color.GRAY);
        cameraPanel.add(cameraLabel, BorderLayout.CENTER);

        container.add(cameraPanel);

        // Scan status label (for showing student marked messages)
        scanStatusLabel = new JLabel("", SwingConstants.CENTER);
        scanStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scanStatusLabel.setForeground(new Color(34, 139, 34)); // Green color
        scanStatusLabel.setBounds(50, 290, 400, 25);
        container.add(scanStatusLabel);

        // Stop Attendance button
        JButton stopAttendanceButton = createQRScannerButton("Stop Attendance", 330);
        stopAttendanceButton.addActionListener(e -> {
            stopQRScanning();
            cardLayout.show(mainPanel, "DASHBOARD");
        });
        container.add(stopAttendanceButton);

        // Exit button (red)
        JButton exitButton = createQRScannerButton("Exit", 380);
        exitButton.setBackground(new Color(220, 53, 69)); // Red background
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(200, 33, 49));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(220, 53, 69));
            }
        });
        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                stopQRScanning();
                releaseCamera();
                System.exit(0);
            }
        });
        container.add(exitButton);

        panel.add(container);
        return panel;
    }

    private JButton createQRScannerButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(80, yPosition, 340, 40);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(33, 37, 41)); // Dark background
        button.setBorder(createRoundedBorder(8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect (will be overridden for exit button)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(33, 37, 41))) {
                    button.setBackground(new Color(23, 27, 31));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(23, 27, 31))) {
                    button.setBackground(new Color(33, 37, 41));
                }
            }
        });

        return button;
    }

    private void startQRScanning() {
        isScanning = true;
        scanStatusLabel.setText("");
        cameraLabel.setText("Initializing camera...");

        try {
            Map<String, String> studentMap = loadStudents();
            
            // Start camera timer for continuous frame capture
            cameraTimer = new javax.swing.Timer(100, e -> {
                if (isScanning) {
                    processQRFrame(studentMap);
                }
            });
            cameraTimer.start();

            // Start timeout timer (2 minutes)
            timeoutTimer = new javax.swing.Timer(120000, e -> {
                stopQRScanning();
                scanStatusLabel.setText("Timeout: Returning to dashboard...");
                scanStatusLabel.setForeground(Color.RED);
                javax.swing.Timer returnTimer = new javax.swing.Timer(2000, event -> {
                    cardLayout.show(mainPanel, "DASHBOARD");
                    scanStatusLabel.setText("");
                    scanStatusLabel.setForeground(new Color(34, 139, 34));
                });
                returnTimer.setRepeats(false);
                returnTimer.start();
            });
            timeoutTimer.setRepeats(false);
            timeoutTimer.start();

        } catch (Exception ex) {
            scanStatusLabel.setText("Error loading student data: " + ex.getMessage());
            scanStatusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void processQRFrame(Map<String, String> studentMap) {
        try {
            BufferedImage frame = captureFrame();

            if (frame != null) {
                // Scale and display the frame
                ImageIcon icon = new ImageIcon(frame.getScaledInstance(300, 200, Image.SCALE_FAST));
                cameraLabel.setIcon(icon);
                cameraLabel.setText("");

                String qrData = decode(frame);

                if (qrData != null && !qrData.isEmpty()) {
                    String studentId = extractStudentId(qrData);
                    
                    if (studentId == null || !studentMap.containsKey(studentId)) {
                        showScanResult("Student not found in records. Try again.", Color.RED);
                        return;
                    }

                    boolean enrolled = isStudentEnrolledInSubject(studentId, currentSubject);
                    if (!enrolled) {
                        showScanResult("Student not enrolled in " + currentSubject + ". Try again.", Color.RED);
                        return;
                    }

                    String studentName = studentMap.get(studentId);
                    
                    // Check if already marked today
                    if (isMarkedToday(studentId, currentSubject)) {
                        showScanResult(studentName + " already marked present today!", Color.ORANGE);
                        return;
                    }

                    // Mark attendance
                    markSubjectAttendance(studentId, studentName, currentSubject);
                    
                    // Get current date and time
                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    
                    // Show success message
                    String successMessage = studentName + " marked present for " + currentSubject + " on " + dateTime;
                    showScanResult(successMessage, new Color(34, 139, 34));
                    
                    // Brief pause after successful scan
                    pauseScanning(3000);
                }
            } else {
                cameraLabel.setIcon(null);
                cameraLabel.setText("Camera not available");
            }

        } catch (Exception ex) {
            scanStatusLabel.setText("Error during scanning: " + ex.getMessage());
            scanStatusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void showScanResult(String message, Color color) {
        scanStatusLabel.setText(message);
        scanStatusLabel.setForeground(color);
        
        // Auto-clear message after 3 seconds
        javax.swing.Timer clearTimer = new javax.swing.Timer(3000, e -> {
            if (!scanStatusLabel.getText().contains("marked present")) {
                scanStatusLabel.setText("");
            }
        });
        clearTimer.setRepeats(false);
        clearTimer.start();
    }

    private void pauseScanning(int milliseconds) {
        if (cameraTimer != null) {
            cameraTimer.stop();
        }
        
        javax.swing.Timer resumeTimer = new javax.swing.Timer(milliseconds, e -> {
            if (isScanning && cameraTimer != null) {
                cameraTimer.start();
            }
        });
        resumeTimer.setRepeats(false);
        resumeTimer.start();
    }

    private void stopQRScanning() {
        isScanning = false;
        
        if (cameraTimer != null) {
            cameraTimer.stop();
            cameraTimer = null;
        }
        
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }
        
        cameraLabel.setIcon(null);
        cameraLabel.setText("Waiting for QR code");
        scanStatusLabel.setText("");
    }

    // Create dashed border for camera area
    private javax.swing.border.Border createDashedBorder() {
        return new javax.swing.border.Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 180, 180));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 10, 10);
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 5, 5, 5);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }

    private JButton createDashboardButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(30, yPosition, 340, 40);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(33, 37, 41)); // Dark background
        button.setBorder(createRoundedBorder(8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect (will be overridden for exit button)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(33, 37, 41))) {
                    button.setBackground(new Color(23, 27, 31));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(23, 27, 31))) {
                    button.setBackground(new Color(33, 37, 41));
                }
            }
        });

        return button;
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty()) {
                statusLabel.setText("Username cannot be empty");
                return;
            }

            if (password.isEmpty()) {
                statusLabel.setText("Password cannot be empty");
                return;
            }

            try {
                String subject = login(username, password);

                if (subject != null) {
                    currentSubject = subject;
                    statusLabel.setText("Login successful!");
                    statusLabel.setForeground(Color.GREEN);
                    
                    // Reset fields
                    usernameField.setText("");
                    passwordField.setText("");
                    failedAttempts = 0;
                    lockMultiplier = 0;
                    
                    // Switch to dashboard after a brief delay
                    javax.swing.Timer timer = new javax.swing.Timer(1000, event -> {
                        cardLayout.show(mainPanel, "DASHBOARD");
                        statusLabel.setText("");
                        statusLabel.setForeground(Color.RED);
                    });
                    timer.setRepeats(false);
                    timer.start();

                } else {
                    failedAttempts++;
                    statusLabel.setText("Invalid login attempt. Attempt " + failedAttempts + "/3");

                    if (failedAttempts % 3 == 0) {
                        lockMultiplier++;
                        int lockTimeSeconds = lockMultiplier * 60;
                        
                        // Disable login components
                        loginButton.setEnabled(false);
                        usernameField.setEnabled(false);
                        passwordField.setEnabled(false);
                        
                        // Start countdown
                        startLockoutCountdown(lockTimeSeconds);
                    }
                }
            } catch (Exception ex) {
                statusLabel.setText("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void startLockoutCountdown(int seconds) {
        lockoutTimer = new javax.swing.Timer(1000, new ActionListener() {
            int remainingSeconds = seconds;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    statusLabel.setText("Account locked. Try again after: " + remainingSeconds + " seconds");
                    remainingSeconds--;
                } else {
                    // Re-enable login components
                    loginButton.setEnabled(true);
                    usernameField.setEnabled(true);
                    passwordField.setEnabled(true);
                    statusLabel.setText("");
                    
                    lockoutTimer.stop();
                }
            }
        });
        lockoutTimer.start();
    }

    private void startAttendanceProcess() {
        String enrollmentFileName = "student_" + currentSubject.toLowerCase() + ".txt";
        File enrollmentFile = new File(enrollmentFileName);

        if (!enrollmentFile.exists()) {
            try {
                if (enrollmentFile.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Created enrollment file: " + enrollmentFileName);
                }
                JOptionPane.showMessageDialog(this, 
                    "No students enrolled for " + currentSubject + ". Please enroll students to start attendance.",
                    "No Students Enrolled", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create enrollment file: " + enrollmentFileName,
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            return;
        }

        if (enrollmentFile.length() == 0) {
            JOptionPane.showMessageDialog(this, 
                enrollmentFileName + " is empty. Please enroll students before starting attendance.",
                "No Students Enrolled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Switch to QR scanner screen
        cardLayout.show(mainPanel, "QR_SCANNER");
        startQRScanning();
    }

    // TeacherAuth functionality
    public static String login(String username, String password) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("teachers.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String user = parts[0].trim();
                String pass = parts[1].trim();
                String subject = parts[2].trim();

                if (user.equals(username) && pass.equals(password)) {
                    reader.close();
                    return subject;
                }
            }
        }
        reader.close();
        return null; // Invalid login
    }

    // TeacherDashboard functionality - converted to GUI dialog
    public void showDashboard(String subject) {
        String enrolledFile = "student_" + subject.toLowerCase() + ".txt";
        String attendanceFile = subject + ".txt";

        Set<String> enrolledStudentIds = new HashSet<>();
        Map<String, String> studentIdToName = new HashMap<>();
        Map<String, java.util.List<String>> attendanceByDate = new TreeMap<>();

        // Load enrolled students
        try (BufferedReader reader = new BufferedReader(new FileReader(enrolledFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    enrolledStudentIds.add(id);
                    studentIdToName.put(id, name);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading enrolled students file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Group attendance entries by date
        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length >= 4) {
                    String dateOnly = parts[3].split(" ")[0];
                    attendanceByDate.putIfAbsent(dateOnly, new ArrayList<>());
                    attendanceByDate.get(dateOnly).add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading attendance file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.List<String> dateList = new ArrayList<>(attendanceByDate.keySet());

        if (dateList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No attendance records found.", "No Records", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show date selection dialog
        String[] dateArray = dateList.toArray(new String[0]);
        String selectedDate = (String) JOptionPane.showInputDialog(
            this,
            "Select a class date to view attendance:",
            "Class Attendance History",
            JOptionPane.QUESTION_MESSAGE,
            null,
            dateArray,
            dateArray[0]
        );

        if (selectedDate != null) {
            showAttendanceDetails(selectedDate, attendanceByDate.get(selectedDate), enrolledStudentIds, studentIdToName);
        }
    }

    private void showAttendanceDetails(String selectedDate, java.util.List<String> entries, 
                                     Set<String> enrolledStudentIds, Map<String, String> studentIdToName) {
        int present = 0, leave = 0;
        Set<String> markedIds = new HashSet<>();
        java.util.List<String> presentList = new ArrayList<>();
        java.util.List<String> leaveList = new ArrayList<>();

        for (String entry : entries) {
            String[] parts = entry.split(" - ");
            if (parts.length >= 4) {
                String name = parts[0].trim();
                String studentId = parts[1].trim();
                String status = parts[2].trim();

                markedIds.add(studentId);

                if (status.equalsIgnoreCase("Present")) {
                    present++;
                    presentList.add(name + " (" + studentId + ")");
                } else if (status.equalsIgnoreCase("Leave")) {
                    leave++;
                    leaveList.add(name + " (" + studentId + ")");
                }
            }
        }

        int totalEnrolled = enrolledStudentIds.size();
        int absent = totalEnrolled - present - leave;

        java.util.List<String> absentList = new ArrayList<>();
        for (String id : enrolledStudentIds) {
            if (!markedIds.contains(id)) {
                String name = studentIdToName.getOrDefault(id, "Unknown");
                absentList.add(name + " (" + id + ")");
            }
        }

        // Create summary message
        StringBuilder message = new StringBuilder();
        message.append("Class: ").append(selectedDate).append("\n");
        message.append("Present: ").append(present).append(" | Absent: ").append(absent).append(" | Leave: ").append(leave).append("\n\n");

        int result = JOptionPane.showConfirmDialog(
            this,
            message.toString() + "Do you want to view detailed student lists?",
            "Attendance Summary",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            StringBuilder details = new StringBuilder();
            details.append(">> Present Students:\n");
            if (presentList.isEmpty()) {
                details.append("  None\n");
            } else {
                presentList.forEach(s -> details.append("  ").append(s).append("\n"));
            }

            details.append("\n>> Leave Students:\n");
            if (leaveList.isEmpty()) {
                details.append("  None\n");
            } else {
                leaveList.forEach(s -> details.append("  ").append(s).append("\n"));
            }

            details.append("\n>> Absent Students:\n");
            if (absentList.isEmpty()) {
                details.append("  None\n");
            } else {
                absentList.forEach(s -> details.append("  ").append(s).append("\n"));
            }

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Detailed Attendance - " + selectedDate, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // AttendanceMarker functionality (keeping all original methods for future use)
    public static boolean checkStudent(String studentId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] studentData = line.split(":");
                if (studentData[0].equals(studentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void markAttendance(String studentId, String subject, String teacherId) throws IOException {
        String fileName = subject + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        if (isMarkedToday(studentId, subject)) {
            System.out.println("Attendance already marked today for " + studentId);
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = studentId + " - Present - " + date;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry + "\n");
            System.out.println("Attendance marked for student " + studentId);
        }
    }

    public static boolean isMarkedToday(String studentId, String subject) throws IOException {
        String fileName = subject + ".txt";
        File file = new File(fileName);
        if (!file.exists()) return false;

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length >= 3) {
                    String id = parts[0].contains(" ") ? parts[1].trim() : parts[0].trim();
                    String datePart = parts[parts.length - 1].split(" ")[0];
                    if (id.equals(studentId) && datePart.equals(today)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String getAttendance(String studentId, String subject) throws IOException {
        String fileName = subject + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            return "No attendance data available for " + subject;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int totalClasses = 0;
            int presentClasses = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" - ");
                if (data.length >= 2) {
                    String id = data[0].contains(" ") ? data[1].trim() : data[0].trim();
                    if (id.equals(studentId)) {
                        totalClasses++;
                        if (data[1].equals("Present") || data[2].equals("Present")) {
                            presentClasses++;
                        }
                    }
                }
            }

            if (totalClasses == 0) {
                return "No classes attended yet.";
            }

            double attendancePercentage = ((double) presentClasses / totalClasses) * 100;
            return "Attendance: " + presentClasses + "/" + totalClasses + " (" + String.format("%.2f", attendancePercentage) + "%)";
        }
    }

    public static Map<String, String> loadStudents() throws IOException {
        Map<String, String> studentMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String studentId = parts[0].trim();
                    String studentName = parts[1].trim();
                    studentMap.put(studentId, studentName);
                }
            }
        }
        return studentMap;
    }

    public static boolean isStudentEnrolledInSubject(String studentId, String subject) {
        String fileName = "student_" + subject.toLowerCase() + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Enrollment file " + fileName + " does not exist.");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split(":");
                if (parts[0].equals(studentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void markSubjectAttendance(String studentId, String studentName, String subject) throws IOException {
        String fileName = subject + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        if (isMarkedToday(studentId, subject)) {
            System.out.println("Attendance already marked today for " + studentId);
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = studentName + " - " + studentId + " - Present - " + date;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry + "\n");
        }
    }

    // QRDecoder functionality
    public static String decode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            return null;
        }
    }

    // WebcamReader functionality
    public static BufferedImage captureFrame() {
        if (!camera.isOpened()) {
            System.out.println("Failed to open webcam.");
            return null;
        }

        Mat frame = new Mat();
        boolean success = camera.read(frame);

        if (success && !frame.empty()) {
            return matToBufferedImage(frame);
        } else {
            System.out.println("Failed to capture frame.");
            return null;
        }
    }

    public static void releaseCamera() {
        if (camera.isOpened()) {
            camera.release();
        }
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mob);
        byte[] ba = mob.toArray();
        try {
            return ImageIO.read(new ByteArrayInputStream(ba));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility method from original Main class
    private static String extractStudentId(String qrData) {
        try {
            if (qrData != null && qrData.startsWith("MECARD:N:")) {
                qrData = qrData.substring(9, qrData.length() - 2).trim();
                return qrData;
            }
            return qrData.trim();
        } catch (Exception e) {
            System.out.println("Failed to parse student ID from QR data.");
        }
        return null;
    }

    // Custom rounded border class
    static class RoundedBorder implements javax.swing.border.Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Helper method to create rounded borders
    public static javax.swing.border.Border createRoundedBorder(int radius) {
        return new RoundedBorder(radius);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new Main().setVisible(true);
        });
    }
}