CREATE TABLE marks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       student_reg_no VARCHAR(20) NOT NULL,
                       course_code VARCHAR(10) NOT NULL,
                       semester_no TINYINT NOT NULL,
                       attempt_no TINYINT NOT NULL DEFAULT 1,
                       assessment_1 DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT uq_marks_student_course_semester_attempt
                           UNIQUE (student_reg_no, course_code, semester_no, attempt_no),

                       CONSTRAINT chk_marks_semester_no
                           CHECK (semester_no BETWEEN 1 AND 8),

                       CONSTRAINT chk_marks_attempt_no
                           CHECK (attempt_no >= 1),

                       CONSTRAINT chk_marks_assessment_1
                           CHECK (assessment_1 BETWEEN 0 AND 100),

                       CONSTRAINT fk_marks_student
                           FOREIGN KEY (student_reg_no) REFERENCES student(registration_no)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_marks_course
                           FOREIGN KEY (course_code) REFERENCES courses(course_code)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE
);

CREATE TABLE quizzes (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         mark_id INT NOT NULL,
                         quiz_no TINYINT NOT NULL,
                         quiz_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,

                         CONSTRAINT uq_quizzes_mark_quiz_no
                             UNIQUE (mark_id, quiz_no),

                         CONSTRAINT chk_quizzes_quiz_no
                             CHECK (quiz_no BETWEEN 1 AND 3),

                         CONSTRAINT chk_quizzes_quiz_mark
                             CHECK (quiz_mark BETWEEN 0 AND 100),

                         CONSTRAINT fk_quizzes_mark
                             FOREIGN KEY (mark_id) REFERENCES marks(id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
);

CREATE TABLE mid_exams (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           mark_id INT NOT NULL,
                           exam_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                           mid_exam_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT uq_mid_exams_mark_exam_type
                               UNIQUE (mark_id, exam_type),

                           CONSTRAINT chk_mid_exam_mark
                               CHECK (mid_exam_mark BETWEEN 0 AND 100),

                           CONSTRAINT fk_mid_exams_mark
                               FOREIGN KEY (mark_id) REFERENCES marks(id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
);

CREATE TABLE end_exams (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           mark_id INT NOT NULL,
                           exam_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                           end_exam_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT uq_end_exams_mark_exam_type
                               UNIQUE (mark_id, exam_type),

                           CONSTRAINT chk_end_exam_mark
                               CHECK (end_exam_mark BETWEEN 0 AND 100),

                           CONSTRAINT fk_end_exams_mark
                               FOREIGN KEY (mark_id) REFERENCES marks(id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
);
