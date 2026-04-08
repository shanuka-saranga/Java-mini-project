CREATE TABLE course_materials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NULL,
    uploaded_by INT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_course_materials_course
        FOREIGN KEY (course_id) REFERENCES courses(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_course_materials_uploaded_by
        FOREIGN KEY (uploaded_by) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO course_materials
(course_id, title, description, file_path, file_type, uploaded_by, status)
VALUES
(1, 'Week 01 Introduction', 'Introduction slides for the first lecture.', 'storage/course-materials/ict2142/week01-intro.pdf', 'PDF', 2, 'ACTIVE'),
(1, 'Week 02 OOP Basics', 'Lecture notes for object-oriented basics.', 'storage/course-materials/ict2142/week02-oop-basics.pdf', 'PDF', 2, 'ACTIVE'),
(2, 'Business Economics Reading', 'Reference reading material for the current unit.', 'storage/course-materials/tcs2112/business-economics-reading.pdf', 'PDF', 2, 'ACTIVE');
