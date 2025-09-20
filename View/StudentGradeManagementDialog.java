package View; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ View

import Controller.AppController; // นำเข้าคลาส AppController เพื่อใช้ควบคุมการทำงาน
import Model.Student; // นำเข้าคลาส Student เพื่อใช้ข้อมูลนักเรียน
import Model.Subject; // นำเข้าคลาส Subject เพื่อใช้ข้อมูลวิชา
import javax.swing.*; // นำเข้าคลาสทั้งหมดใน javax.swing สำหรับสร้าง GUI
import javax.swing.table.DefaultTableModel; // นำเข้าคลาส DefaultTableModel สำหรับจัดการข้อมูลในตาราง
import javax.swing.table.TableCellEditor; // นำเข้าคลาส TableCellEditor สำหรับจัดการการแก้ไขข้อมูลในเซลล์ของตาราง
import javax.swing.table.TableColumn; // นำเข้าคลาส TableColumn สำหรับจัดการคอลัมน์ของตาราง
import java.awt.*; // นำเข้าคลาสทั้งหมดใน java.awt สำหรับส่วนประกอบกราฟิกและเลย์เอาต์
import java.util.List; // นำเข้าคลาส List สำหรับการจัดการชุดข้อมูลแบบรายการ

public class StudentGradeManagementDialog extends JDialog { // ประกาศคลาส StudentGradeManagementDialog ซึ่งเป็นหน้าต่างโต้ตอบ (สืบทอดจาก JDialog)
    private final AppController controller; // ประกาศตัวแปรสำหรับอ้างอิงถึง AppController
    private final Student student; // ประกาศตัวแปรสำหรับเก็บข้อมูลนักเรียนที่กำลังจะถูกจัดการเกรด
    private JTable subjectsTable; // ประกาศ JTable สำหรับแสดงรายวิชาที่นักเรียนลงทะเบียน
    private DefaultTableModel tableModel; // ประกาศโมเดลข้อมูลสำหรับจัดการข้อมูลในตาราง

    // Constructor ของคลาส รับหน้าต่างแม่, controller, และข้อมูลนักเรียนเข้ามา
    public StudentGradeManagementDialog(Frame parent, AppController controller, Student student) {
        super(parent, "Manage Grades for: " + student.getFullName(), true); // เรียก Constructor ของ JDialog เพื่อสร้างหน้าต่างแบบ modal พร้อมตั้งชื่อ
        this.controller = controller; // กำหนดค่า controller ที่รับเข้ามาให้กับตัวแปรของคลาส
        this.student = student; // กำหนดค่า student ที่รับเข้ามาให้กับตัวแปรของคลาส
        initComponents(); // เรียกเมธอดเพื่อสร้างและตั้งค่าส่วนประกอบ GUI
        loadSubjectData(); // เรียกเมธอดเพื่อโหลดข้อมูลวิชาลงในตาราง
        setVisible(true); // ทำให้หน้าต่างนี้แสดงผลขึ้นมา
    }

    private void initComponents() { // เมธอดสำหรับตั้งค่าและจัดวางส่วนประกอบ GUI
        setSize(700, 500); // กำหนดขนาดของหน้าต่าง (กว้าง 700, สูง 500)
        setLocationRelativeTo(getParent()); // กำหนดให้หน้าต่างแสดงผลกลางหน้าต่างแม่
        setLayout(new BorderLayout(10, 10)); // ตั้งค่าเลย์เอาต์หลักของหน้าต่างเป็น BorderLayout พร้อมระยะห่าง

        JLabel infoLabel = new JLabel("Showing registered subjects for " + student.getStudentId() + " - " + student.getFullName()); // สร้างป้ายข้อความเพื่อแสดงข้อมูลของนักเรียน
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // กำหนดขอบว่างเพื่อเพิ่มระยะห่างรอบๆ ป้ายข้อความ
        add(infoLabel, BorderLayout.NORTH); // เพิ่มป้ายข้อความไปทางด้านบนของหน้าต่าง

        String[] columnNames = {"Subject ID", "Subject Name", "Current Grade", "Assign New Grade"}; // กำหนดชื่อคอลัมน์สำหรับตาราง
        tableModel = new DefaultTableModel(columnNames, 0) { // สร้างโมเดลสำหรับตาราง
            @Override // ระบุว่าเป็นการเขียนทับ (override) เมธอด
            public boolean isCellEditable(int row, int column) { // เมธอดสำหรับตรวจสอบว่าเซลล์ไหนสามารถแก้ไขได้
                return column == 3; // กำหนดให้แก้ไขได้เฉพาะคอลัมน์ที่ 4 (index 3) เท่านั้น
            }
        };
        subjectsTable = new JTable(tableModel); // สร้างตารางโดยใช้โมเดลที่เพิ่งสร้าง

        TableColumn gradeColumn = subjectsTable.getColumnModel().getColumn(3); // ดึงคอลัมน์ที่ 4 (index 3) สำหรับกำหนดเกรดใหม่
        String[] grades = {"N/A", "A", "B+", "B", "C+", "C", "D+", "D", "F"}; // สร้างอาร์เรย์ของเกรดที่เป็นไปได้
        gradeColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(grades))); // กำหนดให้ตัวแก้ไขเซลล์ของคอลัมน์นี้เป็น JComboBox (กล่องดรอปดาวน์) ที่มีรายการเกรด

        add(new JScrollPane(subjectsTable), BorderLayout.CENTER); // เพิ่มตาราง (ที่อยู่ใน JScrollPane) ไปยังส่วนกลางของหน้าต่าง

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // สร้าง Panel สำหรับวางปุ่ม และจัดเรียงชิดขวา
        JButton saveButton = new JButton("Save Selected Grade"); // สร้างปุ่ม "Save Selected Grade"
        JButton closeButton = new JButton("Close"); // สร้างปุ่ม "Close"
        buttonPanel.add(saveButton); // เพิ่มปุ่ม Save ลงใน panel
        buttonPanel.add(closeButton); // เพิ่มปุ่ม Close ลงใน panel
        add(buttonPanel, BorderLayout.SOUTH); // เพิ่ม panel ที่มีปุ่มไปทางด้านล่างของหน้าต่าง

        saveButton.addActionListener(e -> saveSelectedGrade()); // เพิ่ม action listener ให้ปุ่ม Save เพื่อเรียกเมธอด saveSelectedGrade
        closeButton.addActionListener(e -> dispose()); // เพิ่ม action listener ให้ปุ่ม Close เพื่อปิดหน้าต่างนี้
    }

    private void loadSubjectData() { // เมธอดสำหรับโหลดข้อมูลวิชาของนักเรียนลงในตาราง
        List<Subject> subjects = controller.getRegisteredSubjectsFor(student); // ดึงรายชื่อวิชาที่นักเรียนคนนี้ลงทะเบียนแล้วจาก controller
        tableModel.setRowCount(0); // ล้างข้อมูลเก่าในตารางทั้งหมด
        for (Subject subject : subjects) { // วนลูปผ่านวิชาแต่ละตัว
            String currentGrade = controller.getGradeForStudent(student.getStudentId(), subject.getSubjectId()); // ดึงเกรดปัจจุบันของนักเรียนในวิชานี้
            tableModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), currentGrade, currentGrade}); // เพิ่มแถวใหม่ลงในตารางพร้อมข้อมูล รหัส, ชื่อ, เกรดปัจจุบัน, และเกรดที่จะกำหนด (ค่าเริ่มต้นเหมือนกัน)
        }
    }

    private void saveSelectedGrade() { // เมธอดสำหรับบันทึกเกรดที่ถูกเลือกในตาราง
        int selectedRow = subjectsTable.getSelectedRow(); // ดึงหมายเลขแถวที่ถูกเลือกในตาราง
        if (selectedRow == -1) { // ตรวจสอบว่ามีการเลือกแถวหรือไม่
            JOptionPane.showMessageDialog(this, "Please select a subject to save the grade for.", "No Selection", JOptionPane.WARNING_MESSAGE); // ถ้าไม่ ให้แสดงกล่องข้อความเตือน
            return; // ออกจากเมธอด
        }

        TableCellEditor editor = subjectsTable.getCellEditor(); // ดึงตัวแก้ไขเซลล์ (cell editor) ที่กำลังทำงานอยู่
        if (editor != null) { // ตรวจสอบว่ามีเซลล์ที่กำลังถูกแก้ไขหรือไม่
            editor.stopCellEditing(); // ถ้ามี ให้หยุดการแก้ไขเพื่อบันทึกค่าที่แก้ไขลงในโมเดล
        }

        String subjectId = (String) tableModel.getValueAt(selectedRow, 0); // ดึงรหัสวิชาจากคอลัมน์แรกของแถวที่เลือก
        String newGrade = (String) tableModel.getValueAt(selectedRow, 3); // ดึงเกรดใหม่จากคอลัมน์ที่สี่ของแถวที่เลือก
        
        controller.updateGrade(student.getStudentId(), subjectId, newGrade, this); // เรียก controller ให้อัปเดตเกรดในระบบ
        
        loadSubjectData(); // โหลดข้อมูลในตารางใหม่เพื่อแสดงเกรดที่อัปเดตแล้ว
    }
}