# Attendify - GUI-Based QR Attendance Management System

> Built with Java | Swing GUI | OpenCV | ZXing | File-Based Storage

---

## Overview

**Attendify** is a **modern, GUI-powered QR Attendance System** designed for educational institutions. It empowers teachers to **scan, record, and manage attendance** with the help of real-time **QR code scanning** and provides students with a **personalized dashboard** to track their attendance.

This version is a complete **console-to-GUI** transformation using only **two main files**:  
`Main.java` – For teacher authentication, dashboard, and QR scanning  
`StudentLoginGUI.java` – For student login and attendance summary display

---

## Key Features

### Teacher Panel (`Main.java`)
- **Secure GUI login** with username/password
- **Progressive lockout** after 3 failed attempts with increasing wait times
- **Dashboard View:**
  - View attendance summary by class or subject
  - Start a new attendance session
- **QR Attendance Mode:**
  - Uses webcam for **real-time QR scanning**
  - **Timestamp recording** with duplicate prevention
  - **Auto-timeout** after 2 minutes of inactivity

### Student Portal (`StudentLoginGUI.java`)
- **Student GUI login** with ID and password
- Displays:
  - Subject-wise attendance report
  - **Real-time percentage calculation**
  - **Exam eligibility status** (Eligible / Not Eligible)

---

## GUI Screenshots

### Teacher Login  
<img width="826" height="647" alt="image" src="https://github.com/user-attachments/assets/ce87e4ec-396b-4f1b-9ee5-272589959a65" />

### Teacher Dashboard
<img width="848" height="652" alt="image" src="https://github.com/user-attachments/assets/bef49665-e5e6-46f8-a871-e637bc9c8b7c" />

### Attendance History
<img width="654" height="490" alt="image" src="https://github.com/user-attachments/assets/ca2ffdfa-09f3-4446-a8a6-4a02d6bab28c" />

### Attendance Summary
<img width="671" height="498" alt="image" src="https://github.com/user-attachments/assets/01d293ba-e9f7-473e-b9ab-2615533b671a" />

### Attendance Detail
<img width="733" height="598" alt="image" src="https://github.com/user-attachments/assets/0718c335-0fc3-4bdf-b185-0dd9f96f6601" />


### QR Code Scanner  
<img width="850" height="747" alt="image" src="https://github.com/user-attachments/assets/27474c8f-3897-4a17-9724-42d616166d96" />


### Student Login  
<img width="849" height="795" alt="image" src="https://github.com/user-attachments/assets/61c1688f-959e-460a-bf80-b678e060a00f" />

### Student Dashboard
<img width="724" height="699" alt="image" src="https://github.com/user-attachments/assets/4af16893-9a91-4ad5-b954-97996f23556a" />

### Attendance Report  
<img width="843" height="713" alt="image" src="https://github.com/user-attachments/assets/85fededc-7d57-4d35-9f78-3a5502fa5442" />


---

## Technologies Used

- **Java (Swing GUI)**
- **OpenCV (Webcam Integration)**
- **ZXing (QR Code Decoding)**
- **File-based persistence** for student & attendance data
- **OOP Concepts**: Encapsulation, Inheritance, Abstraction, Polymorphism

---

## Setup Instructions

### Requirements

- Java 8+
- Visual Studio Code / IntelliJ / Eclipse
- Webcam (for QR scanning)
- Dependencies:
  - OpenCV (Java)
  - ZXing `core.jar` and `javase.jar`

---

### Installation Guide

1. **Clone or Download** the project:
   ```bash
   git clone https://github.com/yourusername/Attendify-GUI.git
   cd Attendify-GUI
   ```

2. **Open the project in your IDE**

3. **Add Libraries to Classpath**:
   - OpenCV JARs and native `.dll/.so` files
   - ZXing `core` and `javase` jars

4. **Prepare Data Files**:
   - `students.txt` → Format: `ID:Name:Password`
   - `teachers.txt` → Format: `Username,Password,Subject`
   - `subjects.txt` → Format: `Subject:Student1,Student2,...`
   - Create attendance logs as empty `.txt` files

5. **Run the application**:
   - `Main.java` for Teacher interface
   - `StudentLoginGUI.java` for Student interface

## Security Measures

- **Login Lockout**: After 3 failed attempts, user is locked out with a countdown timer (1 min, 2 mins, etc.)
- **Prevents brute-force** via progressive delays
- **Password & data validation** from flat files

## Attendance Logic

- Attendance marked via **QR Code**
- Timestamp recorded automatically
- Prevents duplicate attendance per session
- Calculates **attendance %**
- Warns if below **75% eligibility threshold**

## Core Features

### For Teachers
- Real-time QR code scanning
- View all registered students
- Generate attendance reports
- Subject-wise attendance tracking
- Attendance analytics

### For Students
- View attendance history
- Check attendance percentage
- Exam eligibility status
- Daily attendance status

## Technical Stack

- **Frontend**: Java Swing with custom UI components
- **Backend**: File-based data storage
- **QR Processing**: ZXing library
- **Camera**: OpenCV integration
- **Architecture**: MVC pattern

## Project Structure

```
attendify/
├── src/
│   ├── Main.java                 # Teacher interface entry point
│   ├── StudentLoginGUI.java      # Student interface
│   └── ...
├── data/
│   ├── students.txt             # Student credentials
│   ├── teachers.txt             # Teacher credentials
│   ├── subjects.txt             # Subject enrollments
│   └── *.txt                    # Attendance logs
├── lib/
│   ├── opencv/                  # OpenCV libraries
│   └── zxing/                   # ZXing libraries
├── screenshots/                 # GUI screenshots
└── README.md
```

## Configuration

### Data File Formats

**students.txt**
```
STU001:John Doe:password123
STU002:Jane Smith:mypass456
STU003:Mike Johnson:securepass
```

**teachers.txt**
```
teacher1,password123,Mathematics
teacher2,securepass,Physics
admin,adminpass,Computer Science
```

**subjects.txt**
```
Mathematics:STU001,STU002,STU003
Physics:STU001,STU004,STU005
Computer Science:STU002,STU003,STU006
```

## UI Features

- Modern gradient backgrounds
- Responsive design
- Rounded corners and shadows
- Smooth hover effects
- Mobile-friendly layout
- Professional styling with custom components

## System Requirements

| Component | Requirement |
|-----------|-------------|
| **Java Version** | 8+ (recommended: 11+) |
| **RAM** | Minimum 2GB |
| **Storage** | 100MB free space |
| **Camera** | Any USB/built-in webcam |
| **OS** | Windows, macOS, Linux |

## Troubleshooting

### Common Issues

1. **Camera not detected**
   - Ensure OpenCV native libraries are in classpath
   - Check camera permissions
   - Verify webcam is not used by another application

2. **QR codes not scanning**
   - Verify ZXing libraries are properly loaded
   - Ensure good lighting conditions
   - Check QR code format and quality

3. **File not found errors**
   - Create all required `.txt` files in project root
   - Check file permissions
   - Verify file formats match specifications

4. **Login issues**
   - Check data file formats
   - Ensure no extra spaces in credentials
   - Verify file encoding (UTF-8 recommended)

## Usage Guide

### Teacher Workflow
1. Launch `Main.java`
2. Login with teacher credentials
3. Select "Start Attendance Session"
4. Use webcam to scan student QR codes
5. View attendance reports

### Student Workflow
1. Launch `StudentLoginGUI.java`
2. Login with student ID and password
3. View attendance summary
4. Check exam eligibility status

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contribution Guidelines
- Follow Java coding standards
- Add comments for complex logic
- Test your changes thoroughly
- Update documentation if needed
  
## Future Enhancements

- [ ] Database integration (MySQL/PostgreSQL)
- [ ] Web-based dashboard
- [ ] Mobile app companion
- [ ] Advanced analytics and reporting
- [ ] Multi-language support
- [ ] Cloud backup and sync

---

**Star this repository if you find it helpful!**
