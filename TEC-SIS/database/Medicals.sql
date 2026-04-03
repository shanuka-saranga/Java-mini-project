CREATE TABLE Medicals (
    MedicalID INT AUTO_INCREMENT PRIMARY KEY,
    StudentRegNo VARCHAR(15) NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    SubmittedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DocumentPath VARCHAR(255) DEFAULT NULL,
    ApprovalStatus ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    FOREIGN KEY (StudentRegNo) REFERENCES Student(StudentRegNo)
        ON DELETE CASCADE ON UPDATE CASCADE
);