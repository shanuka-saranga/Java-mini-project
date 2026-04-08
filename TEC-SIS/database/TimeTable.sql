

CREATE TABLE timetable_sessions (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    course_id INT NOT NULL,
                                    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                                    session_day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY') NOT NULL,
                                    start_time TIME NOT NULL,
                                    end_time TIME NOT NULL,
                                    venue VARCHAR(100) NOT NULL,
                                    lecturer_id INT DEFAULT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_timetable_sessions_course
                                        FOREIGN KEY (course_id) REFERENCES courses(id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,

                                    CONSTRAINT fk_timetable_sessions_lecturer
                                        FOREIGN KEY (lecturer_id) REFERENCES users(id)
                                            ON DELETE SET NULL
                                            ON UPDATE CASCADE
);

CREATE TABLE sessions (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          timetable_session_id INT NOT NULL,
                          session_no TINYINT NOT NULL,
                          session_date DATE NOT NULL,
                          actual_start_time TIME DEFAULT NULL,
                          actual_end_time TIME DEFAULT NULL,
                          status ENUM('COMPLETED', 'CANCELLED', 'POSTPONED') NOT NULL DEFAULT 'COMPLETED',
                          remarks VARCHAR(255) DEFAULT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT uq_sessions_timetable_session_no
                              UNIQUE (timetable_session_id, session_no),

                          CONSTRAINT chk_sessions_session_no
                              CHECK (session_no BETWEEN 1 AND 15),

                          CONSTRAINT fk_sessions_timetable_session
                              FOREIGN KEY (timetable_session_id) REFERENCES timetable_sessions(id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE
);


INSERT INTO timetable_sessions
(id, course_id, session_type, session_day, start_time, end_time, venue, lecturer_id)
VALUES
    (1, 1, 'THEORY',    'MONDAY',    '08:00:00', '10:00:00', 'ICT Lab 11', 3),
    (2, 2, 'THEORY',    'TUESDAY',   '09:00:00', '11:00:00', 'ICT Lab 12', 4),
    (3, 3, 'PRACTICAL', 'THURSDAY',  '14:00:00', '16:00:00', 'ICT Lab 12', 5),
    (4, 4, 'THEORY',    'THURSDAY',  '09:00:00', '11:00:00', 'ICT Lab 12', 5),
    (5, 5, 'THEORY',    'MONDAY',    '10:00:00', '12:00:00', 'ICT Lab 13', 6),
    (6, 5, 'PRACTICAL', 'WEDNESDAY', '10:00:00', '12:00:00', 'ICT Lab 13', 6),
    (7, 6, 'THEORY',    'WEDNESDAY', '08:00:00', '10:00:00', 'Lecture Hall 01', NULL),
    (8, 7, 'THEORY',    'FRIDAY',    '11:00:00', '13:00:00', 'ICT Workshop Lab', 3),
    (9, 8, 'THEORY',    'FRIDAY',    '08:00:00', '10:00:00', 'Lecture Hall 02', 4);



INSERT INTO sessions
(timetable_session_id, session_no, session_date, actual_start_time, actual_end_time, status, remarks)
VALUES
    (1, 1, '2026-01-05', '08:00:00', '10:00:00', 'COMPLETED', NULL),
    (1, 2, '2026-01-12', '08:00:00', '10:00:00', 'COMPLETED', NULL),
    (1, 3, '2026-01-19', '08:00:00', '10:00:00', 'COMPLETED', NULL),

    (2, 1, '2026-01-06', '09:00:00', '11:00:00', 'COMPLETED', NULL),
    (2, 2, '2026-01-13', '09:00:00', '11:00:00', 'COMPLETED', NULL),

    (3, 1, '2026-01-08', '14:00:00', '16:00:00', 'COMPLETED', NULL),
    (3, 2, '2026-01-15', '14:00:00', '16:00:00', 'COMPLETED', NULL),

    (5, 1, '2026-01-05', '10:00:00', '12:00:00', 'COMPLETED', NULL),
    (5, 2, '2026-01-12', '10:00:00', '12:00:00', 'COMPLETED', NULL),

    (6, 1, '2026-01-07', '10:00:00', '12:00:00', 'COMPLETED', NULL),
    (6, 2, '2026-01-14', '10:00:00', '12:00:00', 'COMPLETED', NULL),

    (8, 1, '2026-01-09', '11:00:00', '13:00:00', 'COMPLETED', NULL),
    (9, 1, '2026-01-09', '08:00:00', '10:00:00', 'COMPLETED', NULL);
