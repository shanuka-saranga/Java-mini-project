
CREATE TABLE lecture (
     lecture_id INT AUTO_INCREMENT PRIMARY KEY,
     course_id INT NOT NULL,
     lecturer_id INT NOT NULL,
     lecture_date DATE NOT NULL,
     start_time TIME NOT NULL,
     duration_hours DECIMAL(4, 2) NOT NULL,
     session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
     CONSTRAINT fk_lecture_course FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT fk_lecture_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturer (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);