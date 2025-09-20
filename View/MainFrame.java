package View;

import Controller.AppController;
import Model.Student;
import Model.Subject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final Student student;
    private final AppController controller;
    private JTable registeredTable, availableTable;
    private DefaultTableModel registeredModel, availableModel;

    public MainFrame(Student student, AppController controller) {
        this.student = student;
        this.controller = controller;
        initComponents();
        refreshTables();
    }

    private void initComponents() {
        setTitle("Student Dashboard - " + student.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel for welcome message and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getFullName() + " (ID: " + student.getStudentId() + ")");
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        JButton logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Main content using JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Registered subjects panel
        JPanel registeredPanel = new JPanel(new BorderLayout());
        registeredPanel.setBorder(BorderFactory.createTitledBorder("Registered Subjects"));
        String[] registeredCols = {"ID", "Subject Name", "Grade"};
        registeredModel = new DefaultTableModel(registeredCols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        registeredTable = new JTable(registeredModel);
        registeredPanel.add(new JScrollPane(registeredTable), BorderLayout.CENTER);
        
        // Available subjects panel
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Subjects for Registration"));
        String[] availableCols = {"ID", "Subject Name", "Enrolled", "Capacity"};
        availableModel = new DefaultTableModel(availableCols, 0) {
             public boolean isCellEditable(int row, int column) { return false; }
        };
        availableTable = new JTable(availableModel);
        availablePanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        
        JButton registerButton = new JButton("Register for Selected Subject");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);
        availablePanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(registeredPanel);
        splitPane.setBottomComponent(availablePanel);
        
        add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        logoutButton.addActionListener(e -> controller.handleLogout(this));
        registerButton.addActionListener(e -> handleRegister());
        
        setVisible(true);
    }
    
    private void handleRegister() {
        int selectedRow = availableTable.getSelectedRow();
        if (selectedRow != -1) {
            String subjectId = (String) availableModel.getValueAt(selectedRow, 0);
            controller.handleRegistration(subjectId, this);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to register.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshTables() {
        // Refresh registered table
        registeredModel.setRowCount(0);
        List<Subject> registeredSubjects = controller.getRegisteredSubjectsFor(student);
        for (Subject subject : registeredSubjects) {
            String grade = controller.getGradeForSubject(subject.getSubjectId());
            registeredModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), grade});
        }

        // Refresh available table
        availableModel.setRowCount(0);
        List<Subject> availableSubjects = controller.getAvailableSubjects();
        for (Subject subject : availableSubjects) {
            availableModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), subject.getCurrentEnrolled(), subject.getCapacity()});
        }
    }
}