CREATE TABLE notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    audience ENUM('ALL', 'STUDENT', 'LECTURER', 'TO') NOT NULL DEFAULT 'ALL',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    published_date DATE NOT NULL,
    expiry_date DATE DEFAULT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO notices (title, content, audience, priority, status, published_date, expiry_date, created_by) VALUES
('Semester Registration Notice', 'All students must complete semester registration before April 15.', 'STUDENT', 'HIGH', 'ACTIVE', '2026-04-01', '2026-04-15', 1),
('Lecture Hall Maintenance', 'Lecture Hall A will be unavailable on April 10 due to maintenance work.', 'ALL', 'MEDIUM', 'ACTIVE', '2026-04-05', '2026-04-10', 1),
('Staff Meeting', 'All lecturers and technical officers are requested to attend the monthly staff meeting.', 'LECTURER', 'MEDIUM', 'ACTIVE', '2026-04-06', '2026-04-20', 1),
('Lab Equipment Check', 'Technical officers should complete the lab equipment inspection before Friday.', 'TO', 'HIGH', 'ACTIVE', '2026-04-07', '2026-04-12', 1),
('New Library Resources', 'New programming and networking reference books are now available in the library.', 'ALL', 'LOW', 'ACTIVE', '2026-03-28', '2026-04-30', 1),
('Past Notice Sample', 'This is a past notice record for testing inactive and expired data handling.', 'STUDENT', 'LOW', 'INACTIVE', '2026-03-01', '2026-03-15', 1);
