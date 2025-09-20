package Model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {
    private static DataService instance;
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Subject> subjects = new HashMap<>();
    private final Map<String, Map<String, String>> registrations = new HashMap<>();

    private static final String STUDENTS_CSV = "student.csv";
    private static final String SUBJECTS_CSV = "subject.csv";
    private static final String REGISTRATIONS_CSV = "dataregisteration.csv";
    public static final String BOM = "\uFEFF";

    private DataService() {
        System.out.println("DataService instance is being created.");
        loadStudentsFromCsv();
        loadSubjectsFromCsv();
        loadRegistrationsFromCsv();
        updateEnrollmentCounts();
    }
    
    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    private void updateEnrollmentCounts() {
        subjects.values().forEach(Subject::resetEnrollment);
        for (Map<String, String> subjectMap : registrations.values()) {
            for (String subjectId : subjectMap.keySet()) {
                Subject subject = findSubjectById(subjectId);
                if (subject != null) subject.enroll();
            }
        }
    }

    public synchronized String registerStudentForSubject(String studentId, String subjectId) {
        Student student = findStudentById(studentId);
        Subject subject = findSubjectById(subjectId);
        if (student == null || subject == null) return "Error: Student or Subject not found.";
        if (student.getAge() < 15) return "Registration Failed: Student must be at least 15 years old.";
        if (subject.isFull()) return "Registration Failed: Subject '" + subject.getSubjectName() + "' is full.";
        if (subject.getPrerequisiteId() != null && !subject.getPrerequisiteId().isEmpty()) {
            Map<String, String> studentRegistrations = registrations.getOrDefault(studentId, new HashMap<>());
            if (!studentRegistrations.containsKey(subject.getPrerequisiteId())) {
                Subject prereq = findSubjectById(subject.getPrerequisiteId());
                return "Registration Failed: Prerequisite '" + (prereq != null ? prereq.getSubjectName() : "N/A") + "' must be completed first.";
            }
        }
        registrations.computeIfAbsent(studentId, k -> new HashMap<>()).put(subjectId, "N/A");
        subject.enroll();
        saveRegistrationsToCsv();
        return "Successfully registered for '" + subject.getSubjectName() + "'!";
    }

    public void updateGrade(String studentId, String subjectId, String grade) {
        if (registrations.containsKey(studentId)) {
            registrations.get(studentId).put(subjectId, grade);
            saveRegistrationsToCsv();
        }
    }

    public List<Student> getStudentsForSubject(String subjectId) {
        return registrations.entrySet().stream()
            .filter(entry -> entry.getValue().containsKey(subjectId))
            .map(entry -> findStudentById(entry.getKey()))
            .filter(student -> student != null)
            .collect(Collectors.toList());
    }

    public String getGrade(String studentId, String subjectId) {
        return registrations.getOrDefault(studentId, new HashMap<>()).getOrDefault(subjectId, "N/A");
    }

    public List<Subject> getAllSubjects() { return new ArrayList<>(subjects.values()); }
    public List<Student> getAllStudents() { return new ArrayList<>(students.values()); }
    public Student findStudentById(String id) { return students.get(id); }
    public Subject findSubjectById(String id) { return subjects.get(id); }

    public List<Subject> getRegisteredSubjectsFor(Student student) {
    System.out.println("\n--- Debugging getRegisteredSubjectsFor ---");
    if (student == null) {
        System.err.println("ERROR: The student object passed in is null!");
        return new ArrayList<>();
    }

    String studentId = student.getStudentId();
    System.out.println("1. Checking for Student ID: '" + studentId + "' - " + student.getFullName());
    
    // ตรวจสอบว่ามีข้อมูลนักเรียนคนนี้ใน Map การลงทะเบียนหรือไม่
    if (!registrations.containsKey(studentId)) {
        System.err.println("2. CRITICAL: Student ID '" + studentId + "' not found in the registrations map!");
        System.err.println("   - This means either the student has no registrations in dataregisteration.csv,");
        System.err.println("   - or the student ID in student.csv and dataregisteration.csv do not match (check for typos or extra spaces).");
        System.out.println("-------------------------------------\n");
        return new ArrayList<>();
    }

    // ดึง Map ของวิชาที่นักเรียนคนนี้ลงทะเบียน
    Map<String, String> subjectMap = registrations.get(studentId);
    System.out.println("2. Student found in registrations map. They have " + subjectMap.size() + " registered subject(s).");

    // ดึงเฉพาะรหัสวิชาออกมา (keySet)
    System.out.println("3. The registered subject IDs are: " + subjectMap.keySet());

    List<Subject> subjectList = subjectMap.keySet().stream()
        .map(subjectId -> {
            System.out.println("   - Attempting to find Subject object for ID: '" + subjectId + "'");
            Subject subject = findSubjectById(subjectId); // เมธอดนี้จะไปหาข้อมูลวิชาจาก Map 'subjects'
            if (subject == null) {
                System.err.println("   - !!! CRITICAL ERROR: Subject ID '" + subjectId + "' was found in dataregisteration.csv, but not in subject.csv!");
                System.err.println("     Please check that this subject ID exists in your subject.csv file.");
            } else {
                System.out.println("   - Successfully found Subject: " + subject.getSubjectName());
            }
            return subject;
        })
        .filter(subject -> subject != null) // กรองเอาเฉพาะวิชาที่หาเจอจริงๆ (ไม่เป็น null)
        .collect(Collectors.toList());

    System.out.println("4. Final number of Subject objects collected: " + subjectList.size());
    System.out.println("-------------------------------------\n");
    return subjectList;
}

    public List<Subject> getAvailableSubjectsFor(Student student) {
        if (student == null) return new ArrayList<>();
        List<String> registeredIds = new ArrayList<>(registrations.getOrDefault(student.getStudentId(), new HashMap<>()).keySet());
        return subjects.values().stream().filter(s -> !registeredIds.contains(s.getSubjectId())).collect(Collectors.toList());
    }
    
    // --- CSV Loading and Saving Methods ---
    
    private void loadStudentsFromCsv() {
        File file = new File(STUDENTS_CSV);
        if (!file.exists()) { System.err.println("Error: " + STUDENTS_CSV + " not found!"); return; }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); }
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    students.put(parts[0], new Student(parts[0], parts[1], parts[2], parts[3], LocalDate.parse(parts[4]), parts[5], parts[6]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void loadSubjectsFromCsv() {
        File file = new File(SUBJECTS_CSV);
        if (!file.exists()) { System.err.println("Error: " + SUBJECTS_CSV + " not found!"); return; }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); }
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    subjects.put(parts[0], new Subject(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4].isEmpty() ? null : parts[4], Integer.parseInt(parts[5])));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void loadRegistrationsFromCsv() {
        File file = new File(REGISTRATIONS_CSV);
        if (!file.exists()) { return; }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); }
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String studentId = parts[0];
                    String subjectId = parts[1];
                    String grade = (parts.length > 2 && !parts[2].isEmpty()) ? parts[2] : "N/A";
                    registrations.computeIfAbsent(studentId, k -> new HashMap<>()).put(subjectId, grade);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void saveRegistrationsToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(REGISTRATIONS_CSV), StandardCharsets.UTF_8))) {
            writer.write("studentId,subjectId,grade");
            writer.newLine();
            for (Map.Entry<String, Map<String, String>> entry : registrations.entrySet()) {
                String studentId = entry.getKey();
                for (Map.Entry<String, String> regEntry : entry.getValue().entrySet()) {
                    writer.write(studentId + "," + regEntry.getKey() + "," + regEntry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}