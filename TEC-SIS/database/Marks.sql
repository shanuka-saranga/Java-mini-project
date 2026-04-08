CREATE TABLE marks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       student_reg_no VARCHAR(20) NOT NULL,
                       course_id INT NOT NULL,
                       semester_year YEAR NOT NULL,
                       attempt_no TINYINT NOT NULL DEFAULT 1,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT uq_marks_student_course_year_attempt
                           UNIQUE (student_reg_no, course_id, semester_year, attempt_no),

                       CONSTRAINT chk_marks_attempt_no
                           CHECK (attempt_no >= 1),

                       CONSTRAINT fk_marks_student
                           FOREIGN KEY (student_reg_no) REFERENCES student(registration_no)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_marks_course
                           FOREIGN KEY (course_id) REFERENCES courses(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE
);

CREATE TABLE quizzes (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         mark_id INT NOT NULL,
                         quiz_no TINYINT NOT NULL,
                         quiz_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                         status ENUM('PENDING', 'PRESENT', 'ABSENT', 'MEDICAL') NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

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

CREATE TABLE assignments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             mark_id INT NOT NULL,
                             assignment_no TINYINT NOT NULL,
                             assignment_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                             status ENUM('PENDING', 'SUBMITTED', 'NOT_SUBMITTED', 'MEDICAL') NOT NULL DEFAULT 'PENDING',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT uq_assignments_mark_assignment_no
                                 UNIQUE (mark_id, assignment_no),

                             CONSTRAINT chk_assignments_assignment_no
                                 CHECK (assignment_no >= 1),

                             CONSTRAINT chk_assignments_assignment_mark
                                 CHECK (assignment_mark BETWEEN 0 AND 100),

                             CONSTRAINT fk_assignments_mark
                                 FOREIGN KEY (mark_id) REFERENCES marks(id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
);

CREATE TABLE mid_exams (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           mark_id INT NOT NULL,
                           exam_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
                           mid_exam_mark DECIMAL(5,2) NOT NULL DEFAULT 0.00,
                           status ENUM('PENDING', 'PRESENT', 'ABSENT', 'MEDICAL') NOT NULL DEFAULT 'PENDING',
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
                           status ENUM('PENDING', 'PRESENT', 'ABSENT', 'MEDICAL') NOT NULL DEFAULT 'PENDING',
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


/* -------------------------------------------------------
   1. marks
   PROPER       -> attempt_no = 1
   REPEAT       -> attempt_no = 2
   BATCH_MISSED -> attempt_no = 3
------------------------------------------------------- */
INSERT INTO marks (
    student_reg_no,
    course_id,
    semester_year,
    attempt_no
)
SELECT
    s.registration_no,
    c.id,
    YEAR(CURDATE()) AS semester_year,
    CASE
    WHEN s.student_type = 'PROPER' THEN 1
    WHEN s.student_type = 'REPEAT' THEN 2
    WHEN s.student_type = 'BATCH_MISSED' THEN 3
END AS attempt_no
FROM student s
CROSS JOIN courses c
LEFT JOIN marks m
    ON m.student_reg_no = s.registration_no
   AND m.course_id = c.id
   AND m.semester_year = YEAR(CURDATE())
   AND m.attempt_no = CASE
        WHEN s.student_type = 'PROPER' THEN 1
        WHEN s.student_type = 'REPEAT' THEN 2
        WHEN s.student_type = 'BATCH_MISSED' THEN 3
END
WHERE m.id IS NULL;


/* -------------------------------------------------------
   2. quizzes
------------------------------------------------------- */
INSERT INTO quizzes (
    mark_id,
    quiz_no,
    quiz_mark,
    status
)
SELECT
    m.id,
    q.quiz_no,
    ROUND(
            CASE q.quiz_no
                WHEN 1 THEN 55 + ((m.id * 3 + c.id * 2) % 31)
            WHEN 2 THEN 50 + ((m.id * 5 + c.id * 3) % 36)
            ELSE 52 + ((m.id * 7 + c.id * 4) % 34)
        END,
        2
    ) AS quiz_mark,
    'PRESENT' AS status
FROM marks m
         INNER JOIN courses c
                    ON c.id = m.course_id
         CROSS JOIN (
    SELECT 1 AS quiz_no
    UNION ALL SELECT 2
    UNION ALL SELECT 3
) q
         LEFT JOIN quizzes qu
                   ON qu.mark_id = m.id
                       AND qu.quiz_no = q.quiz_no
WHERE m.semester_year = YEAR(CURDATE())
  AND q.quiz_no <= c.no_of_quizzes
  AND qu.id IS NULL;


/* -------------------------------------------------------
   3. assignments
------------------------------------------------------- */
INSERT INTO assignments (
    mark_id,
    assignment_no,
    assignment_mark,
    status
)
SELECT
    m.id,
    a.assignment_no,
    ROUND(48 + ((m.id * 6 + a.assignment_no * 5) % 41), 2) AS assignment_mark,
    'SUBMITTED' AS status
FROM marks m
         INNER JOIN courses c
                    ON c.id = m.course_id
         CROSS JOIN (
    SELECT 1 AS assignment_no
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
) a
         LEFT JOIN assignments ass
                   ON ass.mark_id = m.id
                       AND ass.assignment_no = a.assignment_no
WHERE m.semester_year = YEAR(CURDATE())
  AND a.assignment_no <= c.no_of_assignments
  AND ass.id IS NULL;


/* -------------------------------------------------------
   4. mid_exams
------------------------------------------------------- */
INSERT INTO mid_exams (
    mark_id,
    exam_type,
    mid_exam_mark,
    status
)
SELECT
    exam_data.mark_id,
    exam_data.exam_type,
    exam_data.mid_exam_mark,
    'PRESENT' AS status
FROM (
         SELECT
             m.id AS mark_id,
             'THEORY' AS exam_type,
             ROUND(45 + ((m.id * 7 + c.id * 5) % 41), 2) AS mid_exam_mark
         FROM marks m
                  INNER JOIN courses c
                             ON c.id = m.course_id
         WHERE m.semester_year = YEAR(CURDATE())
    AND c.session_type IN ('THEORY', 'BOTH')

UNION ALL

SELECT
    m.id AS mark_id,
    'PRACTICAL' AS exam_type,
    ROUND(50 + ((m.id * 6 + c.id * 4) % 36), 2) AS mid_exam_mark
FROM marks m
         INNER JOIN courses c
                    ON c.id = m.course_id
WHERE m.semester_year = YEAR(CURDATE())
  AND c.session_type IN ('PRACTICAL', 'BOTH')
    ) AS exam_data
    LEFT JOIN mid_exams me
ON me.mark_id = exam_data.mark_id
    AND me.exam_type = exam_data.exam_type
WHERE me.id IS NULL;


/* -------------------------------------------------------
   5. end_exams
------------------------------------------------------- */
INSERT INTO end_exams (
    mark_id,
    exam_type,
    end_exam_mark,
    status
)
SELECT
    exam_data.mark_id,
    exam_data.exam_type,
    exam_data.end_exam_mark,
    'PRESENT' AS status
FROM (
         SELECT
             m.id AS mark_id,
             'THEORY' AS exam_type,
             ROUND(48 + ((m.id * 8 + c.id * 3) % 39), 2) AS end_exam_mark
         FROM marks m
                  INNER JOIN courses c
                             ON c.id = m.course_id
         WHERE m.semester_year = YEAR(CURDATE())
    AND c.session_type IN ('THEORY', 'BOTH')

UNION ALL

SELECT
    m.id AS mark_id,
    'PRACTICAL' AS exam_type,
    ROUND(52 + ((m.id * 5 + c.id * 7) % 34), 2) AS end_exam_mark
FROM marks m
         INNER JOIN courses c
                    ON c.id = m.course_id
WHERE m.semester_year = YEAR(CURDATE())
  AND c.session_type IN ('PRACTICAL', 'BOTH')
    ) AS exam_data
    LEFT JOIN end_exams ee
ON ee.mark_id = exam_data.mark_id
    AND ee.exam_type = exam_data.exam_type
WHERE ee.id IS NULL;
