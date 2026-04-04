
CREATE TABLE users (
   id INT AUTO_INCREMENT PRIMARY KEY,
   first_name VARCHAR(50) NOT NULL,
   last_name VARCHAR(50) NOT NULL,
   email VARCHAR(100) UNIQUE NOT NULL,
   password_hash VARCHAR(255) NOT NULL,
   phone VARCHAR(15) UNIQUE NOT NULL,
   address VARCHAR(150),
   dob DATE NOT NULL,
   role ENUM('ADMIN', 'DEAN', 'LECTURER', 'TO', 'STUDENT') NOT NULL,
   status ENUM('ACTIVE', 'BLOCKED', 'SUSPENDED') DEFAULT 'ACTIVE',
   department_id INT NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

CREATE TABLE student (
     user_id INT PRIMARY KEY,
     registration_no VARCHAR(20) UNIQUE NOT NULL,
     registration_year YEAR NOT NULL,
     student_type ENUM('PROPER', 'REPEAT') NOT NULL,
     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE staff (
   user_id INT PRIMARY KEY,
   staff_code VARCHAR(20) UNIQUE NOT NULL,
   designation VARCHAR(50),
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);