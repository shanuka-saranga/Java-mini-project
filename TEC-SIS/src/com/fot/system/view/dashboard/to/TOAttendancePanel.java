package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.AttendanceSessionController;
import com.fot.system.model.*;
import com.fot.system.service.*;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.dashboard.lecturer.attendance.AttendanceSessionDialog;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TOAttendancePanel extends JPanel {

    private static final String[] STATUSES = {"PRESENT", "ABSENT"};

    private final User currentUser;
    private final AttendanceService attendanceService = new AttendanceService();
    private final CourseService courseService = new CourseService();
    private final TimetableService timetableService = new TimetableService();
    private final AttendanceSessionController controller = new AttendanceSessionController();

    private DefaultTableModel sessionModel, studentModel;
    private JTable sessionTable, studentTable;
    private JTextField txtSearch;
    private JLabel lblSession;

    private List<Course> courses = new ArrayList<>();
    private List<TimetableSession> timetableSessions = new ArrayList<>();
    private AttendanceSessionRow selectedSession;

    public TOAttendancePanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(header(), BorderLayout.NORTH);
        add(content(), BorderLayout.CENTER);

        loadLookupData();
        loadSessions();
    }

    // ================= HEADER =================
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Attendance Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel sub = new JLabel("Manage sessions and student attendance.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        actions.add(btn("Refresh", FontAwesomeSolid.SYNC_ALT, e -> loadSessions()));
        actions.add(btn("Add", FontAwesomeSolid.PLUS, e -> openDialog()));
        actions.add(btn("Save", FontAwesomeSolid.SAVE, e -> saveAttendance()));

        p.add(left, BorderLayout.WEST);
        p.add(actions, BorderLayout.EAST);

        return p;
    }

    // ================= CONTENT =================
    private JComponent content() {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.getDocument().addDocumentListener(simpleListener(this::filter));

        sessionModel = new DefaultTableModel(new Object[]{
                "ID","Code","Course","Type","No","Date","Day","Time","Venue","Status"},0);

        sessionTable = table(sessionModel, false);
        sessionTable.setRowSorter(new TableRowSorter<>(sessionModel));
        sessionTable.getSelectionModel().addListSelectionListener(e -> openSession());

        studentModel = new DefaultTableModel(
                new Object[]{"Reg No","Student","Status","Medical"},0);

        studentTable = table(studentModel, true);
        studentTable.getColumnModel().getColumn(2)
                .setCellEditor(new DefaultCellEditor(new JComboBox<>(STATUSES)));

        lblSession = new JLabel("Select session");

        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);

        wrap.add(new JLabel("Sessions"));
        wrap.add(txtSearch);
        wrap.add(scroll(sessionTable, 250));
        wrap.add(Box.createVerticalStrut(15));
        wrap.add(lblSession);
        wrap.add(scroll(studentTable, 300));

        p.add(new JScrollPane(wrap), BorderLayout.CENTER);
        return p;
    }

    // ================= HELPERS =================
    private CustomButton btn(String text, FontAwesomeSolid icon, java.awt.event.ActionListener e) {
        CustomButton b = new CustomButton(text, AppTheme.BTN_SAVE_BG, Color.WHITE,
                AppTheme.BTN_SAVE_HOVER, new Dimension(140,40));
        b.setIcon(FontIcon.of(icon, 14));
        b.addActionListener(e);
        return b;
    }

    private JTable table(DefaultTableModel model, boolean editable) {
        JTable t = new JTable(model);
        t.setRowHeight(28);
        if (!editable) t.setDefaultEditor(Object.class, null);
        return t;
    }

    private JScrollPane scroll(JTable t, int h) {
        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(0,h));
        return sp;
    }

    private javax.swing.event.DocumentListener simpleListener(Runnable r) {
        return new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){r.run();}
        };
    }

    private void showError(String msg){
        JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);
    }

    // ================= DATA =================
    private void loadLookupData() {
        new SwingWorker<Void,Void>(){
            protected Void doInBackground(){
                courses = courseService.getAllCourses();
                timetableSessions = timetableService.getAllTimetableSessions();
                return null;
            }
        }.execute();
    }

    private void loadSessions() {
        new SwingWorker<List<AttendanceSessionRow>,Void>(){
            protected List<AttendanceSessionRow> doInBackground(){
                return attendanceService.getAllAttendanceSessions();
            }
            protected void done(){
                try{ renderSessions(get()); }
                catch(Exception e){ showError("Load failed"); }
            }
        }.execute();
    }

    private void renderSessions(List<AttendanceSessionRow> list){
        sessionModel.setRowCount(0);
        list.forEach(r -> sessionModel.addRow(new Object[]{
                r.getSessionId(), r.getCourseCode(), r.getCourseName(),
                r.getSessionType(), r.getSessionNo(), r.getSessionDate(),
                r.getSessionDay(), r.getTimeRange(), r.getVenue(), r.getSessionStatus()
        }));
        studentModel.setRowCount(0);
        lblSession.setText("Select session");
        selectedSession = null;
    }

    private void filter(){
        String k = txtSearch.getText().trim();
        TableRowSorter<?> s = (TableRowSorter<?>) sessionTable.getRowSorter();
        s.setRowFilter(k.isEmpty()? null : RowFilter.regexFilter("(?i)"+k));
    }

    private void openSession(){
        int r = sessionTable.getSelectedRow();
        if(r<0) return;

        int id = Integer.parseInt(sessionModel.getValueAt(
                sessionTable.convertRowIndexToModel(r),0).toString());

        new SwingWorker<AttendanceSessionEditorData,Void>(){
            protected AttendanceSessionEditorData doInBackground(){
                return attendanceService.getSessionEditorData(id);
            }
            protected void done(){
                try{ renderEditor(get()); }
                catch(Exception e){ showError("Load failed"); }
            }
        }.execute();
    }

    private void renderEditor(AttendanceSessionEditorData d){
        selectedSession = d.getSession();
        studentModel.setRowCount(0);

        if(selectedSession==null) return;

        lblSession.setText(selectedSession.getCourseCode()+" | "+selectedSession.getSessionDate());

        d.getStudentRows().forEach(s ->
                studentModel.addRow(new Object[]{
                        s.getRegistrationNo(),
                        s.getStudentName(),
                        s.getAttendanceStatus().isEmpty()?"ABSENT":s.getAttendanceStatus(),
                        s.getMedicalApprovalStatus()
                }));
    }

    private void openDialog(){
        if(courses.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Loading...");
            return;
        }

        AttendanceSessionDialog d = new AttendanceSessionDialog(
                SwingUtilities.getWindowAncestor(this),
                courses, timetableSessions);

        d.setVisible(true);

        AddAttendanceSessionRequest req = d.getRequest();
        if(req==null) return;

        try{
            controller.createSessionForTo(req);
            loadSessions();
        }catch(Exception e){ showError(e.getMessage()); }
    }

    private void saveAttendance(){
        if(selectedSession==null){
            JOptionPane.showMessageDialog(this,"Select session first");
            return;
        }

        List<StudentAttendanceUpdate> list = new ArrayList<>();
        for(int i=0;i<studentModel.getRowCount();i++){
            list.add(new StudentAttendanceUpdate(
                    String.valueOf(studentModel.getValueAt(i,0)),
                    String.valueOf(studentModel.getValueAt(i,2))
            ));
        }

        try{
            controller.saveAttendance(selectedSession.getSessionId(),
                    currentUser.getId(), list);
            JOptionPane.showMessageDialog(this,"Saved");
            openSession();
        }catch(Exception e){ showError(e.getMessage()); }
    }
}