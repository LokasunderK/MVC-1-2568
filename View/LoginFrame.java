package View; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ View

import Controller.AppController; // นำเข้าคลาส AppController เพื่อใช้ควบคุมการทำงาน
import javax.swing.*; // นำเข้าคลาสทั้งหมดใน javax.swing สำหรับสร้าง GUI
import java.awt.*; // นำเข้าคลาสทั้งหมดใน java.awt สำหรับส่วนประกอบกราฟิกและเลย์เอาต์

public class LoginFrame extends JFrame { // ประกาศคลาส LoginFrame ซึ่งเป็นหน้าต่างโปรแกรม (สืบทอดจาก JFrame)
    private final AppController controller; // ประกาศตัวแปรสำหรับอ้างอิงถึง AppController
    private JTextField usernameField; // ประกาศช่องข้อความสำหรับกรอกชื่อผู้ใช้
    private JButton loginButton; // ประกาศปุ่มสำหรับกดเข้าสู่ระบบ

    public LoginFrame(AppController controller) { // Constructor ของคลาส รับ AppController เข้ามา
        this.controller = controller; // กำหนดค่า controller ที่รับเข้ามาให้กับตัวแปรของคลาส
        initComponents(); // เรียกเมธอดเพื่อสร้างและตั้งค่าส่วนประกอบ GUI
    }

    private void initComponents() { // เมธอดสำหรับตั้งค่าและจัดวางส่วนประกอบ GUI
        setTitle("Login"); // ตั้งชื่อไตเติ้ลของหน้าต่าง
        setSize(350, 150); // กำหนดขนาดของหน้าต่าง (กว้าง 350, สูง 150)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมปิดเมื่อกดปุ่ม X
        setLocationRelativeTo(null); // กำหนดให้หน้าต่างแสดงผลกลางจอ

        JPanel panel = new JPanel(new GridBagLayout()); // สร้าง Panel ใหม่โดยใช้ GridBagLayout เพื่อการจัดวางที่ยืดหยุ่น
        GridBagConstraints gbc = new GridBagConstraints(); // สร้างอ็อบเจกต์สำหรับกำหนดคุณสมบัติของแต่ละส่วนประกอบใน GridBagLayout
        gbc.insets = new Insets(5, 5, 5, 5); // กำหนดระยะห่างรอบๆ ส่วนประกอบ (บน, ซ้าย, ล่าง, ขวา)

        gbc.gridx = 0; // กำหนดตำแหน่งในแนวนอน (คอลัมน์) เป็น 0
        gbc.gridy = 0; // กำหนดตำแหน่งในแนวตั้ง (แถว) เป็น 0
        panel.add(new JLabel("Student ID or 'admin':"), gbc); // เพิ่มป้ายข้อความ "Student ID or 'admin':" ลงใน panel ตามตำแหน่ง

        gbc.gridx = 1; // เปลี่ยนตำแหน่งในแนวนอนเป็น 1
        gbc.gridy = 0; // กำหนดตำแหน่งในแนวตั้งเป็น 0 (ยังอยู่แถวเดิม)
        gbc.fill = GridBagConstraints.HORIZONTAL; // กำหนดให้ส่วนประกอบขยายเต็มความกว้างของคอลัมน์
        usernameField = new JTextField(15); // สร้างช่องข้อความให้มีความกว้างประมาณ 15 ตัวอักษร
        panel.add(usernameField, gbc); // เพิ่มช่องข้อความลงใน panel ตามตำแหน่ง

        gbc.gridx = 1; // กำหนดตำแหน่งในแนวนอนเป็น 1
        gbc.gridy = 1; // เปลี่ยนตำแหน่งในแนวตั้งเป็น 1 (แถวถัดไป)
        gbc.fill = GridBagConstraints.NONE; // กำหนดให้ส่วนประกอบไม่ต้องขยายขนาด
        gbc.anchor = GridBagConstraints.EAST; // กำหนดให้ส่วนประกอบจัดชิดขวาของพื้นที่
        loginButton = new JButton("Login"); // สร้างปุ่ม "Login"
        panel.add(loginButton, gbc); // เพิ่มปุ่มลงใน panel ตามตำแหน่ง

        add(panel); // เพิ่ม panel ที่จัดวางส่วนประกอบเรียบร้อยแล้วลงในหน้าต่าง (JFrame)

        loginButton.addActionListener(e -> { // เพิ่ม action listener ให้กับปุ่ม Login (ใช้ lambda expression)
            String username = usernameField.getText().trim(); // ดึงข้อความจากช่อง username และตัดช่องว่างหน้า-หลังออก
            controller.handleLogin(username, this); // เรียกเมธอด handleLogin จาก controller พร้อมส่งชื่อผู้ใช้และหน้าต่างนี้ไป
        });

        getRootPane().setDefaultButton(loginButton); // กำหนดให้ปุ่ม Login เป็นปุ่มเริ่มต้น (เมื่อกด Enter จะทำงาน)
        
        setVisible(true); // ทำให้หน้าต่างนี้แสดงผลขึ้นมา
    }
}