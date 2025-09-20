package View;

import Controller.AppController;
import Model.Student;
import Model.Subject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class StudentGradeManagementDialog extends JDialog {
    private final AppController controller;
    private final Student student;
    private JTable subjectsTable;
    private DefaultTableModel tableModel;

    public StudentGradeManagementDialog(Frame parent, AppController controller, Student student) {
        super(parent, "Manage Grades for: " + student.getFullName(), true);
        this.controller = controller;
        this.student = student;
        
        // 1. สร้าง Component UI ก่อน
        initComponents();
        
        // 2. โหลดข้อมูลใส่ใน TableModel
        loadSubjectData();
        
        // 3. สั่งให้หน้าต่างปรากฏเป็นขั้นตอนสุดท้าย
        setVisible(true);
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JLabel infoLabel = new JLabel("Showing registered subjects for " + student.getStudentId() + " - " + student.getFullName());
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Subject ID", "Subject Name", "Current Grade", "Assign New Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        subjectsTable = new JTable(tableModel);

        TableColumn gradeColumn = subjectsTable.getColumnModel().getColumn(3);
        String[] grades = {"N/A", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        gradeColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(grades)));

        add(new JScrollPane(subjectsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Selected Grade");
        JButton closeButton = new JButton("Close");
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveSelectedGrade());
        closeButton.addActionListener(e -> dispose());
        
        // --- บรรทัด setVisible(true); ถูกย้ายออกไปจากตรงนี้ ---
    }

    private void loadSubjectData() {
        List<Subject> subjects = controller.getRegisteredSubjectsFor(student);
        tableModel.setRowCount(0);
        for (Subject subject : subjects) {
            String currentGrade = controller.getGradeForStudent(student.getStudentId(), subject.getSubjectId());
            tableModel.addRow(new Object[]{subject.getSubjectId(), subject.getSubjectName(), currentGrade, currentGrade});
        }
    }

    private void saveSelectedGrade() {
        int selectedRow = subjectsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to save the grade for.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TableCellEditor editor = subjectsTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        String subjectId = (String) tableModel.getValueAt(selectedRow, 0);
        String newGrade = (String) tableModel.getValueAt(selectedRow, 3);
        
        controller.updateGrade(student.getStudentId(), subjectId, newGrade, this);
        
        loadSubjectData();
    }
}