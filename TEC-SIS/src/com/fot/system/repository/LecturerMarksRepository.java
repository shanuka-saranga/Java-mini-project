package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.AssessmentCardSummary;
import com.fot.system.model.AssessmentStudentMarkRow;
import com.fot.system.model.CourseSemesterContext;
import com.fot.system.model.StudentMarksOverviewRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerMarksRepository {

    private final Connection conn;

    public LecturerMarksRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public CourseSemesterContext findCurrentSemesterContext(int courseId, int currentYear) {
        String sql = """
                SELECT COUNT(*) AS record_count
                FROM marks
                WHERE course_id = ? AND semester_year = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, currentYear);
            try (ResultSet rs = stmt.executeQuery()) {
                CourseSemesterContext context = new CourseSemesterContext();
                context.setSemesterYear(currentYear);
                rs.next();
                return context;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load semester context: " + e.getMessage(), e);
        }
    }

    public List<AssessmentCardSummary> findQuizCardSummaries(int courseId, int semesterYear, int quizCount) {
        List<AssessmentCardSummary> summaries = new ArrayList<>();
        String sql = """
                SELECT
                    q.quiz_no AS item_no,
                    COALESCE(AVG(CASE WHEN q.status = 'PRESENT' THEN q.quiz_mark END), 0) AS average_mark,
                    SUM(CASE WHEN q.status = 'PRESENT' THEN 1 ELSE 0 END) AS attempt_count,
                    SUM(CASE WHEN q.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN q.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                    SUM(CASE WHEN q.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count
                FROM marks m
                INNER JOIN quizzes q ON q.mark_id = m.id
                WHERE m.course_id = ? AND m.semester_year = ? AND q.quiz_no = ?
                GROUP BY q.quiz_no
                """;

        for (int quizNo = 1; quizNo <= quizCount; quizNo++) {
            AssessmentCardSummary summary = findNumberedSummary(sql, courseId, semesterYear, quizNo, "Quiz " + quizNo);
            summary.setAssessmentType("QUIZ");
            summary.setItemNo(quizNo);
            summaries.add(summary);
        }
        return summaries;
    }

    public List<AssessmentCardSummary> findAssignmentCardSummaries(int courseId, int semesterYear, int assignmentCount) {
        List<AssessmentCardSummary> summaries = new ArrayList<>();
        String sql = """
                SELECT
                    a.assignment_no AS item_no,
                    COALESCE(AVG(CASE WHEN a.status = 'SUBMITTED' THEN a.assignment_mark END), 0) AS average_mark,
                    SUM(CASE WHEN a.status = 'SUBMITTED' THEN 1 ELSE 0 END) AS attempt_count,
                    SUM(CASE WHEN a.status = 'NOT_SUBMITTED' THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN a.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                    SUM(CASE WHEN a.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count
                FROM marks m
                INNER JOIN assignments a ON a.mark_id = m.id
                WHERE m.course_id = ? AND m.semester_year = ? AND a.assignment_no = ?
                GROUP BY a.assignment_no
                """;

        for (int assignmentNo = 1; assignmentNo <= assignmentCount; assignmentNo++) {
            AssessmentCardSummary summary = findNumberedSummary(sql, courseId, semesterYear, assignmentNo, "Assessment " + assignmentNo);
            summary.setAssessmentType("ASSIGNMENT");
            summary.setItemNo(assignmentNo);
            summaries.add(summary);
        }
        return summaries;
    }

    public AssessmentCardSummary findMidExamSummary(int courseId, int semesterYear) {
        String sql = """
                SELECT
                    COALESCE(AVG(CASE WHEN me.status = 'PRESENT' THEN me.mid_exam_mark END), 0) AS average_mark,
                    SUM(CASE WHEN me.status = 'PRESENT' THEN 1 ELSE 0 END) AS attempt_count,
                    SUM(CASE WHEN me.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN me.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                    SUM(CASE WHEN me.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count
                FROM marks m
                INNER JOIN mid_exams me ON me.mark_id = m.id
                WHERE m.course_id = ? AND m.semester_year = ?
                """;
        AssessmentCardSummary summary = findSingleSummary(sql, courseId, semesterYear, "Mid Exam");
        summary.setAssessmentType("MID");
        summary.setItemNo(0);
        return summary;
    }

    public AssessmentCardSummary findEndExamSummary(int courseId, int semesterYear) {
        String sql = """
                SELECT
                    COALESCE(AVG(CASE WHEN ee.status = 'PRESENT' THEN ee.end_exam_mark END), 0) AS average_mark,
                    SUM(CASE WHEN ee.status = 'PRESENT' THEN 1 ELSE 0 END) AS attempt_count,
                    SUM(CASE WHEN ee.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN ee.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                    SUM(CASE WHEN ee.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count
                FROM marks m
                INNER JOIN end_exams ee ON ee.mark_id = m.id
                WHERE m.course_id = ? AND m.semester_year = ?
                """;
        AssessmentCardSummary summary = findSingleSummary(sql, courseId, semesterYear, "End Exam");
        summary.setAssessmentType("END");
        summary.setItemNo(0);
        return summary;
    }

    public List<StudentMarksOverviewRow> findStudentMarksOverviewByCourse(int courseId) {
        return findStudentMarksOverviewByCourse(courseId, 0);
    }

    public List<StudentMarksOverviewRow> findStudentMarksOverviewByCourse(int courseId, int semesterYear) {
        List<StudentMarksOverviewRow> rows = new ArrayList<>();
        boolean filterByYear = semesterYear > 0;
        String sql = """
                SELECT
                    m.student_reg_no,
                    s.student_type,
                    m.attempt_no,
                    COUNT(DISTINCT CASE WHEN q.status = 'PRESENT' THEN q.id END) AS quizzes_completed,
                    COUNT(DISTINCT CASE WHEN a.status = 'SUBMITTED' THEN a.id END) AS assignments_completed,
                    COALESCE(MAX(CASE WHEN me.exam_type = 'THEORY' THEN me.status END), 'PENDING') AS mid_theory_status,
                    COALESCE(MAX(CASE WHEN me.exam_type = 'PRACTICAL' THEN me.status END), 'PENDING') AS mid_practical_status,
                    COALESCE(MAX(CASE WHEN ee.exam_type = 'THEORY' THEN ee.status END), 'PENDING') AS end_theory_status,
                    COALESCE(MAX(CASE WHEN ee.exam_type = 'PRACTICAL' THEN ee.status END), 'PENDING') AS end_practical_status
                FROM marks m
                INNER JOIN student s ON s.registration_no = m.student_reg_no
                LEFT JOIN quizzes q ON q.mark_id = m.id
                LEFT JOIN assignments a ON a.mark_id = m.id
                LEFT JOIN mid_exams me ON me.mark_id = m.id
                LEFT JOIN end_exams ee ON ee.mark_id = m.id
                WHERE m.course_id = ?
                """ + (filterByYear ? " AND m.semester_year = ? " : "") + """
                GROUP BY m.id, m.student_reg_no, s.student_type, m.attempt_no
                ORDER BY m.student_reg_no, m.attempt_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            if (filterByYear) {
                stmt.setInt(2, semesterYear);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentMarksOverviewRow row = new StudentMarksOverviewRow();
                    row.setRegistrationNo(rs.getString("student_reg_no"));
                    row.setStudentType(rs.getString("student_type"));
                    row.setAttemptNo(rs.getInt("attempt_no"));
                    row.setQuizzesCompleted(rs.getInt("quizzes_completed"));
                    row.setAssignmentsCompleted(rs.getInt("assignments_completed"));
                    row.setMidTheoryStatus(rs.getString("mid_theory_status"));
                    row.setMidPracticalStatus(rs.getString("mid_practical_status"));
                    row.setEndTheoryStatus(rs.getString("end_theory_status"));
                    row.setEndPracticalStatus(rs.getString("end_practical_status"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student marks overview: " + e.getMessage(), e);
        }

        return rows;
    }

    public List<AssessmentStudentMarkRow> findAssessmentRows(String assessmentType, int courseId, int semesterYear, int itemNo) {
        return switch (assessmentType) {
            case "QUIZ" -> runAssessmentRowsQuery(
                    """
                    SELECT m.id AS mark_id, m.student_reg_no, m.attempt_no, NULL AS exam_type, q.status, q.quiz_mark AS mark_value
                    FROM marks m
                    LEFT JOIN quizzes q ON q.mark_id = m.id AND q.quiz_no = ?
                    WHERE m.course_id = ? AND m.semester_year = ?
                    ORDER BY m.student_reg_no, m.attempt_no
                    """,
                    courseId, semesterYear, itemNo
            );
            case "ASSIGNMENT" -> runAssessmentRowsQuery(
                    """
                    SELECT m.id AS mark_id, m.student_reg_no, m.attempt_no, NULL AS exam_type, a.status, a.assignment_mark AS mark_value
                    FROM marks m
                    LEFT JOIN assignments a ON a.mark_id = m.id AND a.assignment_no = ?
                    WHERE m.course_id = ? AND m.semester_year = ?
                    ORDER BY m.student_reg_no, m.attempt_no
                    """,
                    courseId, semesterYear, itemNo
            );
            case "MID" -> runAssessmentRowsQuery(
                    """
                    SELECT m.id AS mark_id, m.student_reg_no, m.attempt_no, exam_types.exam_type, me.status, me.mid_exam_mark AS mark_value
                    FROM marks m
                    INNER JOIN courses c ON c.id = m.course_id
                    INNER JOIN (
                        SELECT 'THEORY' AS exam_type
                        UNION ALL
                        SELECT 'PRACTICAL' AS exam_type
                    ) exam_types
                        ON (c.session_type = 'BOTH' OR c.session_type = exam_types.exam_type)
                    LEFT JOIN mid_exams me
                        ON me.mark_id = m.id
                       AND me.exam_type = exam_types.exam_type
                    WHERE m.course_id = ? AND m.semester_year = ?
                    ORDER BY m.student_reg_no, m.attempt_no, exam_types.exam_type
                    """,
                    courseId, semesterYear, 0
            );
            case "END" -> runAssessmentRowsQuery(
                    """
                    SELECT m.id AS mark_id, m.student_reg_no, m.attempt_no, exam_types.exam_type, ee.status, ee.end_exam_mark AS mark_value
                    FROM marks m
                    INNER JOIN courses c ON c.id = m.course_id
                    INNER JOIN (
                        SELECT 'THEORY' AS exam_type
                        UNION ALL
                        SELECT 'PRACTICAL' AS exam_type
                    ) exam_types
                        ON (c.session_type = 'BOTH' OR c.session_type = exam_types.exam_type)
                    LEFT JOIN end_exams ee
                        ON ee.mark_id = m.id
                       AND ee.exam_type = exam_types.exam_type
                    WHERE m.course_id = ? AND m.semester_year = ?
                    ORDER BY m.student_reg_no, m.attempt_no, exam_types.exam_type
                    """,
                    courseId, semesterYear, 0
            );
            default -> new ArrayList<>();
        };
    }

    private AssessmentCardSummary findNumberedSummary(String sql, int courseId, int semesterYear, int itemNo, String title) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterYear);
            stmt.setInt(3, itemNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAssessmentSummary(rs, title);
                }
                return emptySummary(title);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load assessment card summary: " + e.getMessage(), e);
        }
    }

    private AssessmentCardSummary findSingleSummary(String sql, int courseId, int semesterYear, String title) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterYear);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAssessmentSummary(rs, title);
                }
                return emptySummary(title);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load assessment card summary: " + e.getMessage(), e);
        }
    }

    private AssessmentCardSummary mapAssessmentSummary(ResultSet rs, String title) throws SQLException {
        AssessmentCardSummary summary = new AssessmentCardSummary();
        summary.setTitle(title);
        summary.setAverageMark(rs.getDouble("average_mark"));
        summary.setAttemptCount(rs.getInt("attempt_count"));
        summary.setAbsentCount(rs.getInt("absent_count"));
        summary.setMedicalCount(rs.getInt("medical_count"));
        summary.setPendingCount(rs.getInt("pending_count"));
        return summary;
    }

    private AssessmentCardSummary emptySummary(String title) {
        AssessmentCardSummary summary = new AssessmentCardSummary();
        summary.setTitle(title);
        summary.setAverageMark(0);
        summary.setAttemptCount(0);
        summary.setAbsentCount(0);
        summary.setMedicalCount(0);
        summary.setPendingCount(0);
        return summary;
    }

    private List<AssessmentStudentMarkRow> runAssessmentRowsQuery(String sql, int courseId, int semesterYear, int itemNo) {
        List<AssessmentStudentMarkRow> rows = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (itemNo > 0) {
                stmt.setInt(1, itemNo);
                stmt.setInt(2, courseId);
                stmt.setInt(3, semesterYear);
            } else {
                stmt.setInt(1, courseId);
                stmt.setInt(2, semesterYear);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentStudentMarkRow row = new AssessmentStudentMarkRow();
                    row.setMarkId(rs.getInt("mark_id"));
                    row.setRegistrationNo(rs.getString("student_reg_no"));
                    row.setAttemptNo(rs.getInt("attempt_no"));
                    row.setExamType(rs.getString("exam_type"));
                    row.setStatus(rs.getString("status"));
                    String status = rs.getString("status");
                    if ("PRESENT".equalsIgnoreCase(status) || "SUBMITTED".equalsIgnoreCase(status)) {
                        row.setMark(rs.getDouble("mark_value"));
                    } else {
                        row.setMark(null);
                    }
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load assessment marks: " + e.getMessage(), e);
        }
        return rows;
    }

    public void saveAssessmentRows(String assessmentType, int itemNo, List<AssessmentStudentMarkRow> rows) {
        String sql = switch (assessmentType) {
            case "QUIZ" -> """
                    INSERT INTO quizzes (mark_id, quiz_no, quiz_mark, status)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE quiz_mark = VALUES(quiz_mark), status = VALUES(status)
                    """;
            case "ASSIGNMENT" -> """
                    INSERT INTO assignments (mark_id, assignment_no, assignment_mark, status)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE assignment_mark = VALUES(assignment_mark), status = VALUES(status)
                    """;
            case "MID" -> """
                    INSERT INTO mid_exams (mark_id, exam_type, mid_exam_mark, status)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE mid_exam_mark = VALUES(mid_exam_mark), status = VALUES(status)
                    """;
            case "END" -> """
                    INSERT INTO end_exams (mark_id, exam_type, end_exam_mark, status)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE end_exam_mark = VALUES(end_exam_mark), status = VALUES(status)
                    """;
            default -> throw new RuntimeException("Unsupported assessment type.");
        };

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (AssessmentStudentMarkRow row : rows) {
                if ((row.getStatus() == null || row.getStatus().isBlank()) && row.getMark() == null) {
                    continue;
                }

                stmt.setInt(1, row.getMarkId());
                if ("QUIZ".equals(assessmentType) || "ASSIGNMENT".equals(assessmentType)) {
                    stmt.setInt(2, itemNo);
                } else {
                    stmt.setString(2, row.getExamType());
                }
                stmt.setDouble(3, row.getMark() == null ? 0.0 : row.getMark());
                stmt.setString(4, row.getStatus().isBlank() ? "PENDING" : row.getStatus());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save assessment marks: " + e.getMessage(), e);
        }
    }
}
