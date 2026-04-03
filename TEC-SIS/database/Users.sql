CREATE TABLE users (
   id INT AUTO_INCREMENT PRIMARY KEY,
   first_name VARCHAR(50) NOT NULL,
   last_name VARCHAR(50) NOT NULL,
   role ENUM('Admin', 'Dean', 'Lecturer', 'TO', 'Student') NOT NULL,
   dob DATE NOT NULL,
   email VARCHAR(100) UNIQUE NOT NULL,
   phone VARCHAR(15) UNIQUE NOT NULL,
   address VARCHAR(100),
   department_id INT NOT NULL,
   password_hash VARCHAR(255) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   status ENUM('Active', 'Inactive') DEFAULT 'Active',
    FOREIGN KEY (department_id) REFERENCES department (department_id) ON DELETE CASCADE ON UPDATE CASCADE
);