package Controller;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import Model.DataService;
import Model.Student;
import Model.Subject;
import View.AdminFrame;
import View.LoginFrame;
import View.MainFrame;
import java.util.List;

public class AppController {
    private final DataService model;
    private Student currentStudent;

    public AppController(DataService model) {
        this.model = model;
    }

    public void handleLogin(String username, LoginFrame loginFrame) {
        if ("admin".equalsIgnoreCase(username)) {
            System.out.println("Login success as ADMIN. Attempting to create AdminFrame...");
            loginFrame.dispose();
            new AdminFrame(this);
            System.out.println("AdminFrame object should have been created.");
            return;
        }

        Student student = model.findStudentById(username);
        if (student != null) {
            this.currentStudent = student;
            loginFrame.dispose();
            new MainFrame(currentStudent, this); 
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Invalid Student ID.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleLogout(Component currentWindow) {
        if (currentWindow instanceof JFrame) {
            ((JFrame) currentWindow).dispose();
        }
        this.currentStudent = null;
        new LoginFrame(this);
    }

    public void handleRegistration(String subjectId, MainFrame mainFrame) {
        if (currentStudent == null) return;
        String result = model.registerStudentForSubject(currentStudent.getStudentId(), subjectId);
        JOptionPane.showMessageDialog(mainFrame, result, "Registration Status", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.refreshTables();
    }
    
    public void updateGrade(String studentId, String subjectId, String grade, Component parent) {
        model.updateGrade(studentId, subjectId, grade);
        JOptionPane.showMessageDialog(parent, "Grade updated successfully for student " + studentId, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public Student getCurrentStudent() { return currentStudent; }
    public String getGradeForSubject(String subjectId) {
        if (currentStudent == null) return "N/A";
        return model.getGrade(currentStudent.getStudentId(), subjectId);
    }
    
    // Methods for Student View
    public List<Subject> getRegisteredSubjectsFor(Student student) { return model.getRegisteredSubjectsFor(student); }
    public List<Subject> getAvailableSubjects() { return model.getAvailableSubjectsFor(currentStudent); }
    
    // Methods for Admin View
    public List<Subject> getAllSubjects() { return model.getAllSubjects(); }
    public List<Student> getAllStudents() { return model.getAllStudents(); }
    public String getGradeForStudent(String studentId, String subjectId) { return model.getGrade(studentId, subjectId); }
}