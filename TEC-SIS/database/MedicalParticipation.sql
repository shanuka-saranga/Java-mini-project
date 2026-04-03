CREATE TABLE MedicalParticipation (
    MedicalID INT NOT NULL,
    LectureID INT NOT NULL,
    PRIMARY KEY (MedicalID, LectureID),
    FOREIGN KEY (MedicalID) REFERENCES Medicals(MedicalID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (LectureID) REFERENCES Lecture(LectureID)
        ON DELETE CASCADE ON UPDATE CASCADE
);