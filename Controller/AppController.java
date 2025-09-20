package Controller; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ Controller

import java.awt.Component; // นำเข้าคลาส Component สำหรับองค์ประกอบ UI ทั่วไป
import javax.swing.JFrame; // นำเข้าคลาส JFrame สำหรับการสร้างหน้าต่างโปรแกรม
import javax.swing.JOptionPane; // นำเข้าคลาส JOptionPane สำหรับแสดงกล่องโต้ตอบ (dialog boxes)
import Model.DataService; // นำเข้าคลาส DataService จากแพ็คเกจ Model เพื่อจัดการข้อมูล
import Model.Student; // นำเข้าคลาส Student จากแพ็คเกจ Model เพื่อใช้ข้อมูลนักเรียน
import Model.Subject; // นำเข้าคลาส Subject จากแพ็คเกจ Model เพื่อใช้ข้อมูลวิชา
import View.AdminFrame; // นำเข้าคลาส AdminFrame จากแพ็คเกจ View สำหรับหน้าต่างผู้ดูแลระบบ
import View.LoginFrame; // นำเข้าคลาส LoginFrame จากแพ็คเกจ View สำหรับหน้าต่างล็อกอิน
import View.MainFrame; // นำเข้าคลาส MainFrame จากแพ็คเกจ View สำหรับหน้าต่างหลักของนักเรียน
import java.util.List; // นำเข้าคลาส List สำหรับการจัดการชุดข้อมูลแบบรายการ

public class AppController { // ประกาศคลาสหลักชื่อ AppController เพื่อควบคุมการทำงานของแอปพลิเคชัน
    private final DataService model; // ประกาศตัวแปร model สำหรับจัดการข้อมูล (DataService)
    private Student currentStudent; // ประกาศตัวแปร currentStudent เพื่อเก็บข้อมูลนักเรียนที่ล็อกอินอยู่

    public AppController(DataService model) { // Constructor ของคลาส AppController รับ DataService เข้ามา
        this.model = model; // กำหนดค่า model ที่รับเข้ามาให้กับตัวแปรของคลาส
    }

    public void handleLogin(String username, LoginFrame loginFrame) { // เมธอดสำหรับจัดการการล็อกอิน
        if ("admin".equalsIgnoreCase(username)) { // ตรวจสอบว่าชื่อผู้ใช้เป็น "admin" หรือไม่ (ไม่สนตัวพิมพ์เล็ก/ใหญ่)
            System.out.println("Login success as ADMIN. Attempting to create AdminFrame..."); // แสดงข้อความในคอนโซลว่าล็อกอินเป็นแอดมินสำเร็จ
            loginFrame.dispose(); // ปิดหน้าต่างล็อกอิน
            new AdminFrame(this); // สร้างและเปิดหน้าต่างสำหรับผู้ดูแลระบบ (AdminFrame)
            System.out.println("AdminFrame object should have been created."); // แสดงข้อความในคอนโซลว่าได้สร้างหน้าต่างแอดมินแล้ว
            return; // ออกจากเมธอด
        }

        Student student = model.findStudentById(username); // ค้นหานักเรียนจากรหัสนักเรียนที่ป้อนเข้ามา
        if (student != null) { // ตรวจสอบว่าพบนักเรียนหรือไม่
            this.currentStudent = student; // ถ้าพบ ให้กำหนดเป็นนักเรียนที่ล็อกอินอยู่
            loginFrame.dispose(); // ปิดหน้าต่างล็อกอิน
            new MainFrame(currentStudent, this); // สร้างและเปิดหน้าต่างหลักสำหรับนักเรียน
        } else { // ถ้าไม่พบนักเรียน
            JOptionPane.showMessageDialog(loginFrame, "Invalid Student ID.", "Login Failed", JOptionPane.ERROR_MESSAGE); // แสดงกล่องข้อความแจ้งเตือนว่ารหัสนักเรียนไม่ถูกต้อง
        }
    }

    public void handleLogout(Component currentWindow) { // เมธอดสำหรับจัดการการล็อกเอาท์
        if (currentWindow instanceof JFrame) { // ตรวจสอบว่าหน้าต่างปัจจุบันเป็น JFrame หรือไม่
            ((JFrame) currentWindow).dispose(); // ถ้าใช่ ให้ปิดหน้าต่างนั้น
        }
        this.currentStudent = null; // ล้างข้อมูลนักเรียนที่ล็อกอินอยู่ออก
        new LoginFrame(this); // สร้างและเปิดหน้าต่างล็อกอินขึ้นมาใหม่
    }

    public void handleRegistration(String subjectId, MainFrame mainFrame) { // เมธอดสำหรับจัดการการลงทะเบียนวิชา
        if (currentStudent == null) return; // ถ้าไม่มีนักเรียนล็อกอินอยู่ ให้ออกจากเมธอดทันที
        String result = model.registerStudentForSubject(currentStudent.getStudentId(), subjectId); // เรียกใช้เมธอดลงทะเบียนวิชาจาก model
        JOptionPane.showMessageDialog(mainFrame, result, "Registration Status", JOptionPane.INFORMATION_MESSAGE); // แสดงผลลัพธ์การลงทะเบียนในกล่องข้อความ
        mainFrame.refreshTables(); // สั่งให้หน้าต่างหลักทำการรีเฟรชตารางข้อมูล
    }
    
    public void updateGrade(String studentId, String subjectId, String grade, Component parent) { // เมธอดสำหรับอัปเดตเกรด
        model.updateGrade(studentId, subjectId, grade); // เรียกใช้เมธอดอัปเดตเกรดจาก model
        JOptionPane.showMessageDialog(parent, "Grade updated successfully for student " + studentId, "Success", JOptionPane.INFORMATION_MESSAGE); // แสดงข้อความยืนยันการอัปเดตเกรด
    }

    public Student getCurrentStudent() { return currentStudent; } // เมธอดสำหรับดึงข้อมูลนักเรียนที่ล็อกอินอยู่
    public String getGradeForSubject(String subjectId) { // เมธอดสำหรับดึงเกรดของวิชาที่นักเรียนคนปัจจุบันลงทะเบียน
        if (currentStudent == null) return "N/A"; // ถ้าไม่มีใครล็อกอิน ให้คืนค่า "N/A"
        return model.getGrade(currentStudent.getStudentId(), subjectId); // คืนค่าเกรดจาก model
    }
    
    public List<Subject> getRegisteredSubjectsFor(Student student) { return model.getRegisteredSubjectsFor(student); } // เมธอดสำหรับดึงรายชื่อวิชาที่นักเรียนคนหนึ่งลงทะเบียนแล้ว
    public List<Subject> getAvailableSubjects() { return model.getAvailableSubjectsFor(currentStudent); } // เมธอดสำหรับดึงรายชื่อวิชาที่นักเรียนคนปัจจุบันสามารถลงทะเบียนได้
    
    public List<Subject> getAllSubjects() { return model.getAllSubjects(); } // เมธอดสำหรับดึงรายชื่อวิชาทั้งหมดในระบบ
    public List<Student> getAllStudents() { return model.getAllStudents(); } // เมธอดสำหรับดึงรายชื่อนักเรียนทั้งหมดในระบบ
    public String getGradeForStudent(String studentId, String subjectId) { return model.getGrade(studentId, subjectId); } // เมธอดสำหรับดึงเกรดของนักเรียนคนใดคนหนึ่งในวิชาใดวิชาหนึ่ง
}