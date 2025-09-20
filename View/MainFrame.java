package View; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ View

import Controller.AppController; // นำเข้าคลาส AppController เพื่อใช้ควบคุมการทำงาน
import Model.Student; // นำเข้าคลาส Student เพื่อใช้ข้อมูลนักเรียน
import Model.Subject; // นำเข้าคลาส Subject เพื่อใช้ข้อมูลวิชา
import javax.swing.*; // นำเข้าคลาสทั้งหมดใน javax.swing สำหรับสร้าง GUI
import javax.swing.table.DefaultTableModel; // นำเข้าคลาส DefaultTableModel สำหรับจัดการข้อมูลในตาราง
import java.awt.*; // นำเข้าคลาสทั้งหมดใน java.awt สำหรับส่วนประกอบกราฟิกและเลย์เอาต์
import java.util.List; // นำเข้าคลาส List สำหรับการจัดการชุดข้อมูลแบบรายการ

public class MainFrame extends JFrame { // ประกาศคลาส MainFrame ซึ่งเป็นหน้าต่างโปรแกรมหลักของนักเรียน (สืบทอดจาก JFrame)
    private final Student student; // ประกาศตัวแปรสำหรับเก็บข้อมูลนักเรียนที่ล็อกอินอยู่
    private final AppController controller; // ประกาศตัวแปรสำหรับอ้างอิงถึง AppController
    private JTable registeredTable, availableTable; // ประกาศตารางสำหรับแสดงวิชาที่ลงทะเบียนแล้ว และวิชาที่เปิดให้ลงทะเบียน
    private DefaultTableModel registeredModel, availableModel; // ประกาศโมเดลข้อมูลสำหรับจัดการข้อมูลในตารางทั้งสอง

    public MainFrame(Student student, AppController controller) { // Constructor ของคลาส รับข้อมูลนักเรียนและ controller เข้ามา
        this.student = student; // กำหนดค่า student ที่รับเข้ามาให้กับตัวแปรของคลาส
        this.controller = controller; // กำหนดค่า controller ที่รับเข้ามาให้กับตัวแปรของคลาส
        initComponents(); // เรียกเมธอดเพื่อสร้างและตั้งค่าส่วนประกอบ GUI
        refreshTables(); // เรียกเมธอดเพื่อโหลดข้อมูลลงในตาราง
    }

    private void initComponents() { // เมธอดสำหรับตั้งค่าและจัดวางส่วนประกอบ GUI
        setTitle("Student Dashboard - " + student.getFullName()); // ตั้งชื่อไตเติ้ลของหน้าต่าง พร้อมแสดงชื่อนักเรียน
        setSize(800, 600); // กำหนดขนาดของหน้าต่าง (กว้าง 800, สูง 600)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมปิดเมื่อกดปุ่ม X
        setLocationRelativeTo(null); // กำหนดให้หน้าต่างแสดงผลกลางจอ
        setLayout(new BorderLayout(10, 10)); // ตั้งค่าเลย์เอาต์หลักของหน้าต่างเป็น BorderLayout พร้อมระยะห่าง

        JPanel topPanel = new JPanel(new BorderLayout()); // สร้าง Panel สำหรับส่วนบนสุดของหน้าต่าง
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // กำหนดขอบว่างเพื่อเพิ่มระยะห่างภายใน Panel
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getFullName() + " (ID: " + student.getStudentId() + ")"); // สร้างป้ายข้อความต้อนรับ พร้อมแสดงชื่อและรหัสนักเรียน
        topPanel.add(welcomeLabel, BorderLayout.WEST); // เพิ่มป้ายข้อความต้อนรับไว้ทางซ้ายของ topPanel
        JButton logoutButton = new JButton("Logout"); // สร้างปุ่ม "Logout"
        topPanel.add(logoutButton, BorderLayout.EAST); // เพิ่มปุ่ม Logout ไว้ทางขวาของ topPanel
        add(topPanel, BorderLayout.NORTH); // เพิ่ม topPanel เข้าไปในส่วนบนสุดของหน้าต่าง
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); // สร้าง JSplitPane เพื่อแบ่งหน้าต่างออกเป็นสองส่วนในแนวตั้ง
        splitPane.setResizeWeight(0.5); // กำหนดให้พื้นที่ถูกแบ่งครึ่ง (50/50) และปรับขนาดเท่าๆ กัน

        JPanel registeredPanel = new JPanel(new BorderLayout()); // สร้าง Panel สำหรับแสดงตารางวิชาที่ลงทะเบียนแล้ว
        registeredPanel.setBorder(BorderFactory.createTitledBorder("Registered Subjects")); // กำหนดขอบพร้อมชื่อหัวข้อให้กับ Panel
        String[] registeredCols = {"ID", "Subject Name", "Grade"}; // กำหนดชื่อคอลัมน์สำหรับตารางวิชาที่ลงทะเบียนแล้ว
        registeredModel = new DefaultTableModel(registeredCols, 0) { // สร้างโมเดลสำหรับตารางวิชาที่ลงทะเบียนแล้ว
            public boolean isCellEditable(int row, int column) { return false; } // เขียนทับเมธอดเพื่อไม่ให้แก้ไขข้อมูลในเซลล์ได้
        };
        registeredTable = new JTable(registeredModel); // สร้างตารางโดยใช้โมเดลที่เพิ่งสร้าง
        registeredPanel.add(new JScrollPane(registeredTable), BorderLayout.CENTER); // เพิ่มตาราง (ที่อยู่ใน JScrollPane) เข้าไปใน Panel
        
        JPanel availablePanel = new JPanel(new BorderLayout()); // สร้าง Panel สำหรับแสดงตารางวิชาที่สามารถลงทะเบียนได้
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Subjects for Registration")); // กำหนดขอบพร้อมชื่อหัวข้อให้กับ Panel
        String[] availableCols = {"ID", "Subject Name", "Enrolled", "Capacity"}; // กำหนดชื่อคอลัมน์สำหรับตารางวิชาที่เปิดสอน
        availableModel = new DefaultTableModel(availableCols, 0) { // สร้างโมเดลสำหรับตารางวิชาที่เปิดสอน
             public boolean isCellEditable(int row, int column) { return false; } // เขียนทับเมธอดเพื่อไม่ให้แก้ไขข้อมูลในเซลล์ได้
        };
        availableTable = new JTable(availableModel); // สร้างตารางโดยใช้โมเดลที่เพิ่งสร้าง
        availablePanel.add(new JScrollPane(availableTable), BorderLayout.CENTER); // เพิ่มตาราง (ที่อยู่ใน JScrollPane) เข้าไปใน Panel
        
        JButton registerButton = new JButton("Register for Selected Subject"); // สร้างปุ่ม "Register for Selected Subject"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // สร้าง Panel สำหรับวางปุ่ม และจัดเรียงชิดขวา
        buttonPanel.add(registerButton); // เพิ่มปุ่มลงทะเบียนลงใน panel
        availablePanel.add(buttonPanel, BorderLayout.SOUTH); // เพิ่ม panel ที่มีปุ่มเข้าไปในส่วนล่างของ availablePanel

        splitPane.setTopComponent(registeredPanel); // กำหนดให้ registeredPanel เป็นส่วนประกอบด้านบนของ splitPane
        splitPane.setBottomComponent(availablePanel); // กำหนดให้ availablePanel เป็นส่วนประกอบด้านล่างของ splitPane
        
        add(splitPane, BorderLayout.CENTER); // เพิ่ม splitPane เข้าไปในส่วนกลางของหน้าต่าง

        logoutButton.addActionListener(e -> controller.handleLogout(this)); // เพิ่ม action listener ให้ปุ่ม Logout เพื่อเรียกเมธอด handleLogout จาก controller
        registerButton.addActionListener(e -> handleRegister()); // เพิ่ม action listener ให้ปุ่ม Register เพื่อเรียกเมธอด handleRegister
        
        setVisible(true); // ทำให้หน้าต่างนี้แสดงผลขึ้นมา
    }
    
    private void handleRegister() { // เมธอดสำหรับจัดการเมื่อกดปุ่มลงทะเบียน
        int selectedRow = availableTable.getSelectedRow(); // ดึงหมายเลขแถวที่ถูกเลือกในตารางวิชาที่เปิดสอน
        if (selectedRow != -1) { // ตรวจสอบว่ามีการเลือกแถวหรือไม่ (-1 คือไม่มีการเลือก)
            String subjectId = (String) availableModel.getValueAt(selectedRow, 0); // ดึงรหัสวิชาจากคอลัมน์แรก (index 0) ของแถวที่เลือก
            controller.handleRegistration(subjectId, this); // เรียกเมธอด handleRegistration จาก controller เพื่อทำการลงทะเบียน
        } else { // ถ้าไม่มีการเลือกแถว
            JOptionPane.showMessageDialog(this, "Please select a subject to register.", "No Selection", JOptionPane.WARNING_MESSAGE); // แสดงกล่องข้อความเตือนให้เลือกวิชาก่อน
        }
    }

    public void refreshTables() { // เมธอดสำหรับอัปเดตข้อมูลในตารางทั้งสอง
        registeredModel.setRowCount(0); // ล้างข้อมูลทั้งหมดในตารางวิชาที่ลงทะเบียนแล้ว
        List<Subject> registeredSubjects = controller.getRegisteredSubjectsFor(student); // ดึงรายชื่อวิชาที่นักเรียนลงทะเบียนแล้วจาก controller
        for (Subject subject : registeredSubjects) { // วนลูปผ่านวิชาที่ลงทะเบียนแล้วแต่ละวิชา
            String grade = controller.getGradeForSubject(subject.getSubjectId()); // ดึงเกรดสำหรับวิชานั้นๆ จาก controller
            registeredModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), grade}); // เพิ่มแถวใหม่ลงในตารางพร้อมข้อมูล รหัสวิชา, ชื่อวิชา, และเกรด
        }

        availableModel.setRowCount(0); // ล้างข้อมูลทั้งหมดในตารางวิชาที่เปิดสอน
        List<Subject> availableSubjects = controller.getAvailableSubjects(); // ดึงรายชื่อวิชาที่สามารถลงทะเบียนได้จาก controller
        for (Subject subject : availableSubjects) { // วนลูปผ่านวิชาที่เปิดสอนแต่ละวิชา
            availableModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), subject.getCurrentEnrolled(), subject.getCapacity()}); // เพิ่มแถวใหม่ลงในตารางพร้อมข้อมูล รหัส, ชื่อ, จำนวนคนลงทะเบียน, และจำนวนที่รับได้
        }
    }
}