package View; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ View

import Controller.AppController; // นำเข้าคลาส AppController เพื่อใช้ควบคุมการทำงาน
import Model.Student; // นำเข้าคลาส Student เพื่อใช้ข้อมูลนักเรียน
import javax.swing.*; // นำเข้าคลาสทั้งหมดใน javax.swing สำหรับสร้าง GUI
import java.awt.*; // นำเข้าคลาสทั้งหมดใน java.awt สำหรับส่วนประกอบกราฟิกและเลย์เอาต์
import java.awt.event.MouseAdapter; // นำเข้าคลาส MouseAdapter สำหรับจัดการอีเวนต์ของเมาส์
import java.awt.event.MouseEvent; // นำเข้าคลาส MouseEvent สำหรับข้อมูลอีเวนต์ของเมาส์
import java.util.List; // นำเข้าคลาส List สำหรับการจัดการชุดข้อมูลแบบรายการ

public class AdminFrame extends JFrame { // ประกาศคลาส AdminFrame ซึ่งเป็นหน้าต่างโปรแกรม (สืบทอดจาก JFrame)
    private final AppController controller; // ประกาศตัวแปรสำหรับอ้างอิงถึง AppController
    private JList<Student> studentList; // ประกาศ JList สำหรับแสดงรายชื่อนักเรียน
    private DefaultListModel<Student> listModel; // ประกาศโมเดลข้อมูลสำหรับ JList เพื่อจัดการข้อมูลนักเรียน

    public AdminFrame(AppController controller) { // Constructor ของคลาส รับ AppController เข้ามา
        this.controller = controller; // กำหนดค่า controller ที่รับเข้ามาให้กับตัวแปรของคลาส
        initComponents(); // เรียกเมธอดเพื่อสร้างและตั้งค่าส่วนประกอบ GUI ทั้งหมด
        loadStudents(); // เรียกเมธอดเพื่อโหลดรายชื่อนักเรียนมาแสดง
    }

    private void initComponents() { // เมธอดสำหรับตั้งค่าและจัดวางส่วนประกอบ GUI
        setTitle("Admin Dashboard - Student Management"); // ตั้งชื่อไตเติ้ลของหน้าต่าง
        setSize(600, 400); // กำหนดขนาดของหน้าต่าง (กว้าง 600, สูง 400)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมปิดเมื่อกดปุ่ม X
        setLocationRelativeTo(null); // กำหนดให้หน้าต่างแสดงผลกลางจอ
        setLayout(new BorderLayout(10, 10)); // ตั้งค่าเลย์เอาต์หลักของหน้าต่างเป็น BorderLayout พร้อมระยะห่าง

        listModel = new DefaultListModel<>(); // สร้างอ็อบเจกต์โมเดลสำหรับ JList
        studentList = new JList<>(listModel); // สร้าง JList โดยใช้โมเดลที่เพิ่งสร้าง
        
        studentList.setCellRenderer(new StudentListCellRenderer()); // กำหนดวิธีแสดงผลแต่ละรายการใน JList ด้วยคลาสที่สร้างขึ้นเอง
        
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // กำหนดให้เลือกนักเรียนได้ทีละคนเท่านั้น
        add(new JScrollPane(studentList), BorderLayout.CENTER); // เพิ่ม JList (ที่อยู่ใน JScrollPane) เข้าไปในส่วนกลางของหน้าต่าง

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // สร้าง Panel สำหรับวางปุ่ม และจัดเรียงชิดขวา
        JButton manageButton = new JButton("Manage Grades for Selected Student"); // สร้างปุ่ม "จัดการเกรด"
        JButton logoutButton = new JButton("Logout"); // สร้างปุ่ม "ออกจากระบบ"

        buttonPanel.add(logoutButton); // เพิ่มปุ่ม "ออกจากระบบ" ลงใน panel
        buttonPanel.add(manageButton); // เพิ่มปุ่ม "จัดการเกรด" ลงใน panel
        add(buttonPanel, BorderLayout.SOUTH); // เพิ่ม panel ที่มีปุ่มต่างๆ เข้าไปในส่วนล่างของหน้าต่าง

        logoutButton.addActionListener(e -> controller.handleLogout(this)); // เพิ่ม action listener ให้ปุ่ม Logout เพื่อเรียกเมธอด handleLogout จาก controller

        manageButton.addActionListener(e -> openGradeManagementDialog()); // เพิ่ม action listener ให้ปุ่มจัดการเกรด เพื่อเรียกเมธอดเปิดหน้าต่างจัดการเกรด
        
        studentList.addMouseListener(new MouseAdapter() { // เพิ่ม mouse listener ให้กับ JList
            public void mouseClicked(MouseEvent evt) { // เมธอดที่จะทำงานเมื่อมีการคลิกเมาส์
                if (evt.getClickCount() == 2) { // ตรวจสอบว่าเป็นการดับเบิลคลิกหรือไม่
                    openGradeManagementDialog(); // ถ้าใช่ ให้เปิดหน้าต่างจัดการเกรด
                }
            }
        });

        setVisible(true); // ทำให้หน้าต่างนี้แสดงผลขึ้นมา
    }
    
    private void loadStudents() { // เมธอดสำหรับโหลดข้อมูลนักเรียนทั้งหมดมาใส่ใน JList
        listModel.clear(); // ล้างข้อมูลเก่าในโมเดลออกทั้งหมด
        List<Student> students = controller.getAllStudents(); // ดึงรายชื่อนักเรียนทั้งหมดจาก controller
        for (Student student : students) { // วนลูปนักเรียนแต่ละคน
            listModel.addElement(student); // เพิ่มนักเรียนคนนั้นเข้าไปในโมเดลของ JList
        }
    }

    private void openGradeManagementDialog() { // เมธอดสำหรับเปิดหน้าต่างจัดการเกรด
        Student selectedStudent = studentList.getSelectedValue(); // ดึงข้อมูลนักเรียนที่ถูกเลือกใน JList
        if (selectedStudent != null) { // ตรวจสอบว่ามีการเลือกนักเรียนหรือไม่
            new StudentGradeManagementDialog(this, controller, selectedStudent); // ถ้ามี ให้สร้างและเปิดหน้าต่างจัดการเกรดสำหรับนักเรียนคนนั้น
        } else { // ถ้าไม่มีการเลือกนักเรียน
            JOptionPane.showMessageDialog(this, "Please select a student to manage.", "No Student Selected", JOptionPane.WARNING_MESSAGE); // แสดงกล่องข้อความเตือนให้เลือกนักเรียนก่อน
        }
    }

    // คลาสภายใน (nested class) สำหรับกำหนดการแสดงผลของแต่ละเซลล์ใน JList
    private static class StudentListCellRenderer extends DefaultListCellRenderer {
        @Override // ระบุว่าเป็นการเขียนทับ (override) เมธอด
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); // เรียกเมธอดของคลาสแม่เพื่อจัดการการแสดงผลพื้นฐาน (เช่น การไฮไลท์เมื่อถูกเลือก)
            if (value instanceof Student) { // ตรวจสอบว่าข้อมูลที่ได้รับมาเป็นอ็อบเจกต์ของ Student หรือไม่
                Student student = (Student) value; // แปลงชนิดข้อมูลเป็น Student
                setText(String.format("%s - %s", student.getStudentId(), student.getFullName())); // ตั้งค่าข้อความที่จะแสดงในเซลล์เป็น "รหัสนักเรียน - ชื่อเต็ม"
            }
            return this; // คืนค่า component ที่ตั้งค่าแล้วกลับไปเพื่อแสดงผล
        }
    }
}