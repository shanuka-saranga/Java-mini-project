CREATE TABLE Course (
    CourseID INT AUTO_INCREMENT PRIMARY KEY,
    CourseCode VARCHAR(10) UNIQUE NOT NULL,
    CourseName VARCHAR(100) NOT NULL,
    Credits INT NOT NULL,
    TotalHours INT NOT NULL,
    SessionType ENUM('Theory', 'Practical', 'Both') NOT NULL DEFAULT 'Theory',
    DepartmentID INT NOT NULL,
    LecturerInChargeID INT,
    FOREIGN KEY (DepartmentID) REFERENCES Department (DepartmentID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (LecturerInChargeID) REFERENCES Lecturer (UserID) ON DELETE SET NULL ON UPDATE CASCADE
)
