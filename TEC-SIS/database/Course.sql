CREATE TABLE courses (
     id INT AUTO_INCREMENT PRIMARY KEY,
     course_code VARCHAR(10) UNIQUE NOT NULL,
     course_name VARCHAR(100) NOT NULL,
     credits INT NOT NULL,
     total_hours INT NOT NULL,
     session_type ENUM('THEORY', 'PRACTICAL', 'BOTH') NOT NULL DEFAULT 'THEORY',
     department_id INT NOT NULL,
     lecturer_in_charge_id INT,
     FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (lecturer_in_charge_id) REFERENCES user(id) ON DELETE SET NULL ON UPDATE CASCADE
);