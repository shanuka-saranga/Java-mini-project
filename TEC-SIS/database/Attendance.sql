CREATE TABLE attendance (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            student_reg_no VARCHAR(20) NOT NULL,
                            session_id INT NOT NULL,
                            attendance_status ENUM('PRESENT', 'ABSENT', 'MEDICAL') NOT NULL,
                            marked_by INT NOT NULL,
                            marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            remarks VARCHAR(255) DEFAULT NULL,

                            CONSTRAINT uq_attendance_student_session
                                UNIQUE (student_reg_no, session_id),

                            CONSTRAINT fk_attendance_student
                                FOREIGN KEY (student_reg_no) REFERENCES student(registration_no)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,

                            CONSTRAINT fk_attendance_session
                                FOREIGN KEY (session_id) REFERENCES sessions(id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,

                            CONSTRAINT fk_attendance_marked_by
                                FOREIGN KEY (marked_by) REFERENCES users(id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
);


CREATE TABLE medicals (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          student_reg_no VARCHAR(20) NOT NULL,
                          session_id INT NOT NULL,
                          medical_document VARCHAR(255) NOT NULL,
                          submitted_date DATE NOT NULL,
                          approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
                          approved_by INT DEFAULT NULL,
                          approved_at TIMESTAMP NULL DEFAULT NULL,
                          remarks VARCHAR(255) DEFAULT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT uq_medicals_student_session
                              UNIQUE (student_reg_no, session_id),

                          CONSTRAINT fk_medicals_student
                              FOREIGN KEY (student_reg_no) REFERENCES student(registration_no)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                          CONSTRAINT fk_medicals_session
                              FOREIGN KEY (session_id) REFERENCES sessions(id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                          CONSTRAINT fk_medicals_approved_by
                              FOREIGN KEY (approved_by) REFERENCES users(id)
                                  ON DELETE SET NULL
                                  ON UPDATE CASCADE
);

INSERT INTO attendance
(student_reg_no, session_id, attendance_status, marked_by, remarks)
VALUES
    ('TG/2022/001', 1,  'PRESENT', 7, NULL),
    ('TG/2022/001', 2,  'PRESENT', 7, NULL),
    ('TG/2022/001', 3,  'ABSENT',  7, NULL),
    ('TG/2022/001', 4,  'PRESENT', 7, NULL),
    ('TG/2022/001', 5,  'PRESENT', 7, NULL),
    ('TG/2022/001', 6,  'PRESENT', 7, NULL),
    ('TG/2022/001', 7,  'PRESENT', 7, NULL),
    ('TG/2022/001', 8,  'PRESENT', 7, NULL),
    ('TG/2022/001', 9,  'PRESENT', 7, NULL),
    ('TG/2022/001', 10, 'PRESENT', 7, NULL),
    ('TG/2022/001', 11, 'ABSENT',  7, NULL),
    ('TG/2022/001', 12, 'PRESENT', 7, NULL),
    ('TG/2022/001', 13, 'PRESENT', 7, NULL),

    ('TG/2022/002', 1,  'PRESENT', 7, NULL),
    ('TG/2022/002', 2,  'ABSENT',  7, NULL),
    ('TG/2022/002', 3,  'PRESENT', 7, NULL),
    ('TG/2022/002', 4,  'PRESENT', 7, NULL),
    ('TG/2022/002', 5,  'ABSENT',  7, NULL),
    ('TG/2022/002', 6,  'PRESENT', 7, NULL),
    ('TG/2022/002', 7,  'PRESENT', 7, NULL),
    ('TG/2022/002', 8,  'PRESENT', 7, NULL),
    ('TG/2022/002', 9,  'PRESENT', 7, NULL),
    ('TG/2022/002', 10, 'ABSENT',  7, NULL),
    ('TG/2022/002', 11, 'PRESENT', 7, NULL),
    ('TG/2022/002', 12, 'PRESENT', 7, NULL),
    ('TG/2022/002', 13, 'PRESENT', 7, NULL),

    ('TG/2022/003', 1,  'ABSENT',  7, NULL),
    ('TG/2022/003', 2,  'ABSENT',  7, NULL),
    ('TG/2022/003', 3,  'PRESENT', 7, NULL),
    ('TG/2022/003', 4,  'ABSENT',  7, NULL),
    ('TG/2022/003', 5,  'PRESENT', 7, NULL),
    ('TG/2022/003', 6,  'ABSENT',  7, NULL),
    ('TG/2022/003', 7,  'PRESENT', 7, NULL),
    ('TG/2022/003', 8,  'ABSENT',  7, NULL),
    ('TG/2022/003', 9,  'PRESENT', 7, NULL),
    ('TG/2022/003', 10, 'ABSENT',  7, NULL),
    ('TG/2022/003', 11, 'PRESENT', 7, NULL),
    ('TG/2022/003', 12, 'ABSENT',  7, NULL),
    ('TG/2022/003', 13, 'ABSENT',  7, NULL),

    ('TG/2022/004', 1,  'PRESENT', 7, NULL),
    ('TG/2022/004', 2,  'PRESENT', 7, NULL),
    ('TG/2022/004', 3,  'PRESENT', 7, NULL),
    ('TG/2022/004', 4,  'PRESENT', 7, NULL),
    ('TG/2022/004', 5,  'PRESENT', 7, NULL),
    ('TG/2022/004', 6,  'MEDICAL', 7, 'Medical submitted'),
    ('TG/2022/004', 7,  'PRESENT', 7, NULL),
    ('TG/2022/004', 8,  'PRESENT', 7, NULL),
    ('TG/2022/004', 9,  'PRESENT', 7, NULL),
    ('TG/2022/004', 10, 'PRESENT', 7, NULL),
    ('TG/2022/004', 11, 'MEDICAL', 7, 'Medical submitted'),
    ('TG/2022/004', 12, 'PRESENT', 7, NULL),
    ('TG/2022/004', 13, 'PRESENT', 7, NULL),

    ('TG/2022/005', 1,  'PRESENT', 7, NULL),
    ('TG/2022/005', 2,  'ABSENT',  7, NULL),
    ('TG/2022/005', 3,  'ABSENT',  7, NULL),
    ('TG/2022/005', 4,  'PRESENT', 7, NULL),
    ('TG/2022/005', 5,  'MEDICAL', 7, 'Medical submitted'),
    ('TG/2022/005', 6,  'ABSENT',  7, NULL),
    ('TG/2022/005', 7,  'PRESENT', 7, NULL),
    ('TG/2022/005', 8,  'ABSENT',  7, NULL),
    ('TG/2022/005', 9,  'PRESENT', 7, NULL),
    ('TG/2022/005', 10, 'MEDICAL', 7, 'Medical submitted'),
    ('TG/2022/005', 11, 'ABSENT',  7, NULL),
    ('TG/2022/005', 12, 'PRESENT', 7, NULL),
    ('TG/2022/005', 13, 'ABSENT',  7, NULL);


INSERT INTO medicals
(student_reg_no, session_id, medical_document, submitted_date, approval_status, approved_by, approved_at, remarks)
VALUES
    ('TG/2022/004', 6,  'storage/medical-documents/TG-2022-004-s6.pdf',  '2026-01-08', 'APPROVED', 1, '2026-01-09 10:00:00', 'Approved'),
    ('TG/2022/004', 11, 'storage/medical-documents/TG-2022-004-s11.pdf', '2026-01-14', 'APPROVED', 1, '2026-01-15 10:00:00', 'Approved'),
    ('TG/2022/005', 5,  'storage/medical-documents/TG-2022-005-s5.pdf',  '2026-01-13', 'PENDING',  NULL, NULL, 'Pending review'),
    ('TG/2022/005', 10, 'storage/medical-documents/TG-2022-005-s10.pdf', '2026-01-14', 'REJECTED', 1, '2026-01-15 11:00:00', 'Rejected');
