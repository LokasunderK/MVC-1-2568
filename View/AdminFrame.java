package View;

import Controller.AppController;
import Model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminFrame extends JFrame {
    private final AppController controller;
    private JList<Student> studentList;
    private DefaultListModel<Student> listModel;

    public AdminFrame(AppController controller) {
        this.controller = controller;
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setTitle("Admin Dashboard - Student Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        studentList = new JList<>(listModel);
        
        studentList.setCellRenderer(new StudentListCellRenderer());
        
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(studentList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton manageButton = new JButton("Manage Grades for Selected Student");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(logoutButton);
        buttonPanel.add(manageButton);
        add(buttonPanel, BorderLayout.SOUTH);

        logoutButton.addActionListener(e -> controller.handleLogout(this));

        manageButton.addActionListener(e -> openGradeManagementDialog());
        
        studentList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openGradeManagementDialog();
                }
            }
        });

        setVisible(true);
    }
    
    private void loadStudents() {
        listModel.clear();
        List<Student> students = controller.getAllStudents();
        for (Student student : students) {
            listModel.addElement(student);
        }
    }

    private void openGradeManagementDialog() {
        Student selectedStudent = studentList.getSelectedValue();
        if (selectedStudent != null) {
            new StudentGradeManagementDialog(this, controller, selectedStudent);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to manage.", "No Student Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static class StudentListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Student) {
                Student student = (Student) value;
                setText(String.format("%s - %s", student.getStudentId(), student.getFullName()));
            }
            return this;
        }
    }
}