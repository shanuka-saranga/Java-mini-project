src/com.fot.system/
├── com.fot.system.main/
│   └── MainApp.java             # Entry point of the application (Launches Login Frame).
│
├── config/
│   └── DBConnection.java        # Singleton class to manage the MySQL database connection.
│
├── com.fot.system.model/                       # Data Entities (Encapsulation & Inheritance focus).
│   ├── User.java                # Abstract base class for all users. [cite: 11]
│   ├── Student.java             # Inherits User; stores grades, attendance, etc. [cite: 37]
│   ├── Lecturer.java            # Inherits User; manages course materials/marks. [cite: 23]
│   ├── TechnicalOfficer.java    # Inherits User; manages attendance/medicals. [cite: 31]
│   ├── Course.java              # Stores course names, credits, and types (T/P). [cite: 12, 60]
│   └── Attendance.java          # Stores daily attendance records and session types. [cite: 49]
│
├── com.fot.system.view/                        # GUI Components (Java Swing/JavaFX).
│   ├── login/                   # Login screen and authentication UI.
│   ├── admin/                   # Dashboard for Admin (User & Course maintenance). [cite: 18]
│   ├── lecturer/                # Dashboard for Lecturers (Marks & Eligibility). [cite: 23]
│   ├── student/                 # Dashboard for Undergraduates (GPA & Timetables). [cite: 37]
│   ├── technical_officer/       # Dashboard for TO (Attendance & Medicals). [cite: 31]
│   └── components/              # Reusable UI elements like Custom Tables or Buttons.
│
├── com.fot.system.controller/                  # Event Handlers (Connects View and Service).
│   ├── AuthController.java      # Handles login logic and session routing.
│   ├── AdminController.java     # Manages requests for creating/updating users/courses. [cite: 19, 20]
│   └── StudentController.java   # Handles student requests to com.fot.system.view GPA/Attendance. [cite: 39, 42]
│
├── com.fot.system.service/                     # Business Logic Layer (The "Brain" of the system).
│   ├── AttendanceService.java   # Logic for 80% attendance and medical approval. [cite: 51, 53, 58]
│   ├── MarksService.java        # Logic for GPA, SGPA, CGPA, and Grading (UGC Circular).
│   ├── EligibilityService.java  # Logic to check CA (>=40%) and Attendance (>=80%). [cite: 63, 65, 69]
│   └── NoticeService.java       # Logic for managing and displaying faculty notices. [cite: 21, 44]
│
├── com.fot.system.repository/                  # Data Access Layer (DAO).
│   ├── UserRepository.java      # CRUD operations for the User table in MySQL.
│   ├── CourseRepository.java    # CRUD operations for Courses and Timetables.
│   └── MarksRepository.java     # SQL logic to fetch and update undergraduate marks. [cite: 26]
│
└── com.fot.system.util/                        # Helper Classes.
├── InputValidator.java      # Checks for valid email formats and non-empty fields.
└── ExceptionHandler.java    # Custom error handling for the system. [cite: 103]