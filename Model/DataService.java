package Model; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ Model

import java.io.*; // นำเข้าคลาสทั้งหมดที่เกี่ยวกับการจัดการ Input/Output
import java.nio.charset.StandardCharsets; // นำเข้าคลาสสำหรับกำหนดชุดอักขระเป็น UTF-8
import java.time.LocalDate; // นำเข้าคลาสสำหรับจัดการข้อมูลวันที่
import java.util.ArrayList; // นำเข้าคลาส ArrayList สำหรับสร้างลิสต์ที่ปรับขนาดได้
import java.util.HashMap; // นำเข้าคลาส HashMap สำหรับเก็บข้อมูลแบบ key-value
import java.util.List; // นำเข้า Interface List สำหรับการทำงานกับข้อมูลแบบรายการ
import java.util.Map; // นำเข้า Interface Map สำหรับการทำงานกับข้อมูลแบบ key-value
import java.util.stream.Collectors; // นำเข้าคลาสสำหรับรวบรวมข้อมูลจาก Stream

public class DataService { // ประกาศคลาส DataService สำหรับจัดการข้อมูลทั้งหมดของโปรแกรม
    private static DataService instance; // ประกาศตัวแปร instance แบบ static เพื่อใช้ในรูปแบบ Singleton
    private final Map<String, Student> students = new HashMap<>(); // สร้าง Map เพื่อเก็บข้อมูลนักเรียน โดยใช้รหัสนักเรียนเป็น key
    private final Map<String, Subject> subjects = new HashMap<>(); // สร้าง Map เพื่อเก็บข้อมูลวิชา โดยใช้รหัสวิชาเป็น key
    private final Map<String, Map<String, String>> registrations = new HashMap<>(); // สร้าง Map ซ้อน Map เพื่อเก็บข้อมูลการลงทะเบียนและเกรด

    private static final String STUDENTS_CSV = "student.csv"; // กำหนดชื่อไฟล์ CSV สำหรับข้อมูลนักเรียน
    private static final String SUBJECTS_CSV = "subject.csv"; // กำหนดชื่อไฟล์ CSV สำหรับข้อมูลวิชา
    private static final String REGISTRATIONS_CSV = "dataregisteration.csv"; // กำหนดชื่อไฟล์ CSV สำหรับข้อมูลการลงทะเบียน
    public static final String BOM = "\uFEFF"; // กำหนดค่า Byte Order Mark (BOM) สำหรับไฟล์ UTF-8

    private DataService() { // Constructor ของคลาส (private เพื่อป้องกันการสร้าง object จากภายนอก)
        System.out.println("DataService instance is being created."); // แสดงข้อความในคอนโซลเมื่อมีการสร้าง instance
        loadStudentsFromCsv(); // เรียกเมธอดเพื่อโหลดข้อมูลนักเรียนจากไฟล์ CSV
        loadSubjectsFromCsv(); // เรียกเมธอดเพื่อโหลดข้อมูลวิชาจากไฟล์ CSV
        loadRegistrationsFromCsv(); // เรียกเมธอดเพื่อโหลดข้อมูลการลงทะเบียนจากไฟล์ CSV
        updateEnrollmentCounts(); // เรียกเมธอดเพื่ออัปเดตจำนวนผู้ลงทะเบียนในแต่ละวิชา
    }
    
    public static synchronized DataService getInstance() { // เมธอดสำหรับเรียกใช้ instance เดียวของ DataService (Singleton Pattern)
        if (instance == null) { // ตรวจสอบว่ายังไม่มี instance ถูกสร้างขึ้นหรือไม่
            instance = new DataService(); // ถ้ายังไม่มี ให้สร้าง instance ใหม่
        }
        return instance; // คืนค่า instance ที่มีอยู่ (หรือที่เพิ่งสร้าง) กลับไป
    }

    private void updateEnrollmentCounts() { // เมธอดสำหรับอัปเดตจำนวนนักเรียนที่ลงทะเบียนในแต่ละวิชา
        subjects.values().forEach(Subject::resetEnrollment); // รีเซ็ตจำนวนผู้ลงทะเบียนของทุกวิชาให้เป็น 0
        for (Map<String, String> subjectMap : registrations.values()) { // วนลูปผ่านข้อมูลการลงทะเบียนของนักเรียนแต่ละคน
            for (String subjectId : subjectMap.keySet()) { // วนลูปผ่านรหัสวิชาที่นักเรียนคนนั้นลงทะเบียน
                Subject subject = findSubjectById(subjectId); // ค้นหาวิชาจากรหัสวิชา
                if (subject != null) subject.enroll(); // ถ้าพบวิชา ให้เพิ่มจำนวนผู้ลงทะเบียน 1 คน
            }
        }
    }

    public synchronized String registerStudentForSubject(String studentId, String subjectId) { // เมธอดสำหรับลงทะเบียนวิชาให้นักเรียน
        Student student = findStudentById(studentId); // ค้นหานักเรียนจากรหัสนักเรียน
        Subject subject = findSubjectById(subjectId); // ค้นหาวิชาจากรหัสวิชา
        if (student == null || subject == null) return "Error: Student or Subject not found."; // ตรวจสอบว่าพบนักเรียนและวิชาหรือไม่
        if (student.getAge() < 15) return "Registration Failed: Student must be at least 15 years old."; // ตรวจสอบว่านักเรียนอายุถึง 15 ปีหรือไม่
        if (subject.isFull()) return "Registration Failed: Subject '" + subject.getSubjectName() + "' is full."; // ตรวจสอบว่าวิชานั้นเต็มแล้วหรือไม่
        if (subject.getPrerequisiteId() != null && !subject.getPrerequisiteId().isEmpty()) { // ตรวจสอบว่าวิชานี้มีวิชาบังคับก่อนหรือไม่
            Map<String, String> studentRegistrations = registrations.getOrDefault(studentId, new HashMap<>()); // ดึงข้อมูลการลงทะเบียนของนักเรียนคนนี้
            if (!studentRegistrations.containsKey(subject.getPrerequisiteId())) { // ตรวจสอบว่านักเรียนได้ลงทะเบียนวิชาบังคับก่อนหรือยัง
                Subject prereq = findSubjectById(subject.getPrerequisiteId()); // ค้นหาวิชาบังคับก่อน
                return "Registration Failed: Prerequisite '" + (prereq != null ? prereq.getSubjectName() : "N/A") + "' must be completed first."; // แจ้งเตือนว่าต้องผ่านวิชาบังคับก่อน
            }
        }
        registrations.computeIfAbsent(studentId, k -> new HashMap<>()).put(subjectId, "N/A"); // เพิ่มข้อมูลการลงทะเบียนวิชานี้ (เกรดเริ่มต้นเป็น "N/A")
        subject.enroll(); // เพิ่มจำนวนผู้ลงทะเบียนในวิชานั้น
        saveRegistrationsToCsv(); // บันทึกข้อมูลการลงทะเบียนลงไฟล์ CSV
        return "Successfully registered for '" + subject.getSubjectName() + "'!"; // คืนค่าข้อความว่าลงทะเบียนสำเร็จ
    }

    public void updateGrade(String studentId, String subjectId, String grade) { // เมธอดสำหรับอัปเดตเกรด
        if (registrations.containsKey(studentId)) { // ตรวจสอบว่ามีข้อมูลการลงทะเบียนของนักเรียนคนนี้หรือไม่
            registrations.get(studentId).put(subjectId, grade); // อัปเดตเกรดสำหรับวิชาที่ระบุ
            saveRegistrationsToCsv(); // บันทึกข้อมูลลงไฟล์ CSV
        }
    }

    public List<Student> getStudentsForSubject(String subjectId) { // เมธอดสำหรับดึงรายชื่อนักเรียนทั้งหมดที่ลงทะเบียนในวิชาใดวิชาหนึ่ง
        return registrations.entrySet().stream() // แปลง Map ของการลงทะเบียนเป็น Stream
            .filter(entry -> entry.getValue().containsKey(subjectId)) // กรองเฉพาะนักเรียนที่ลงทะเบียนวิชานี้
            .map(entry -> findStudentById(entry.getKey())) // แปลงรหัสนักเรียนเป็น object Student
            .filter(student -> student != null) // กรองกรณีที่ไม่พบ object Student
            .collect(Collectors.toList()); // รวบรวมผลลัพธ์เป็น List
    }

    public String getGrade(String studentId, String subjectId) { // เมธอดสำหรับดึงเกรดของนักเรียนในวิชาที่ระบุ
        return registrations.getOrDefault(studentId, new HashMap<>()).getOrDefault(subjectId, "N/A"); // คืนค่าเกรด หรือ "N/A" ถ้าไม่พบ
    }

    public List<Subject> getAllSubjects() { return new ArrayList<>(subjects.values()); } // เมธอดสำหรับดึงรายชื่อวิชาทั้งหมด
    public List<Student> getAllStudents() { return new ArrayList<>(students.values()); } // เมธอดสำหรับดึงรายชื่อนักเรียนทั้งหมด
    public Student findStudentById(String id) { return students.get(id); } // เมธอดสำหรับค้นหานักเรียนจากรหัส
    public Subject findSubjectById(String id) { return subjects.get(id); } // เมธอดสำหรับค้นหาวิชาจากรหัส

    public List<Subject> getRegisteredSubjectsFor(Student student) { // เมธอดสำหรับดึงวิชาที่นักเรียนคนหนึ่งลงทะเบียนไว้
    System.out.println("\n--- Debugging getRegisteredSubjectsFor ---"); // แสดงข้อความเริ่มต้นการดีบัก
    if (student == null) { // ตรวจสอบว่า object student ที่รับมาเป็น null หรือไม่
        System.err.println("ERROR: The student object passed in is null!"); // แสดงข้อผิดพลาดถ้าเป็น null
        return new ArrayList<>(); // คืนค่าเป็นลิสต์ว่าง
    }

    String studentId = student.getStudentId(); // ดึงรหัสนักเรียนออกมา
    System.out.println("1. Checking for Student ID: '" + studentId + "' - " + student.getFullName()); // แสดงข้อมูลนักเรียนที่กำลังตรวจสอบ
    
    if (!registrations.containsKey(studentId)) { // ตรวจสอบว่ามีรหัสนักเรียนนี้ใน Map การลงทะเบียนหรือไม่
        System.err.println("2. CRITICAL: Student ID '" + studentId + "' not found in the registrations map!"); // แสดงข้อผิดพลาดถ้าไม่พบ
        System.err.println("   - This means either the student has no registrations in dataregisteration.csv,"); // อธิบายสาเหตุที่เป็นไปได้
        System.err.println("   - or the student ID in student.csv and dataregisteration.csv do not match (check for typos or extra spaces)."); // อธิบายสาเหตุที่เป็นไปได้
        System.out.println("-------------------------------------\n"); // แสดงเส้นคั่นเพื่อจบการดีบัก
        return new ArrayList<>(); // คืนค่าเป็นลิสต์ว่าง
    }

    Map<String, String> subjectMap = registrations.get(studentId); // ดึง Map ของวิชาที่นักเรียนคนนี้ลงทะเบียน
    System.out.println("2. Student found in registrations map. They have " + subjectMap.size() + " registered subject(s)."); // แสดงจำนวนวิชาที่ลงทะเบียน

    System.out.println("3. The registered subject IDs are: " + subjectMap.keySet()); // แสดงรหัสวิชาทั้งหมดที่ลงทะเบียน

    List<Subject> subjectList = subjectMap.keySet().stream() // แปลงเซ็ตของรหัสวิชาเป็น Stream
        .map(subjectId -> { // สำหรับแต่ละรหัสวิชา
            System.out.println("   - Attempting to find Subject object for ID: '" + subjectId + "'"); // แสดงข้อความว่ากำลังค้นหาวิชา
            Subject subject = findSubjectById(subjectId); // ค้นหา object Subject จากรหัส
            if (subject == null) { // ตรวจสอบว่าค้นหาวิชาไม่พบหรือไม่
                System.err.println("   - !!! CRITICAL ERROR: Subject ID '" + subjectId + "' was found in dataregisteration.csv, but not in subject.csv!"); // แสดงข้อผิดพลาดร้ายแรง
                System.err.println("     Please check that this subject ID exists in your subject.csv file."); // แนะนำให้ตรวจสอบไฟล์ subject.csv
            } else { // ถ้าพบวิชา
                System.out.println("   - Successfully found Subject: " + subject.getSubjectName()); // แสดงว่าค้นหาวิชาสำเร็จ
            }
            return subject; // คืนค่า object Subject กลับไป
        })
        .filter(subject -> subject != null) // กรองเอาเฉพาะ object ที่ไม่เป็น null ออก
        .collect(Collectors.toList()); // รวบรวมผลลัพธ์เป็น List

    System.out.println("4. Final number of Subject objects collected: " + subjectList.size()); // แสดงจำนวนวิชาที่หาเจอทั้งหมด
    System.out.println("-------------------------------------\n"); // แสดงเส้นคั่นเพื่อจบการดีบัก
    return subjectList; // คืนค่าลิสต์ของวิชาที่ลงทะเบียน
}

    public List<Subject> getAvailableSubjectsFor(Student student) { // เมธอดสำหรับดึงรายชื่อวิชาที่นักเรียนสามารถลงทะเบียนได้
        if (student == null) return new ArrayList<>(); // ถ้าไม่มีข้อมูลนักเรียน ให้คืนค่าลิสต์ว่าง
        List<String> registeredIds = new ArrayList<>(registrations.getOrDefault(student.getStudentId(), new HashMap<>()).keySet()); // ดึงรหัสวิชาที่ลงทะเบียนไปแล้ว
        return subjects.values().stream().filter(s -> !registeredIds.contains(s.getSubjectId())).collect(Collectors.toList()); // กรองวิชาทั้งหมดให้เหลือเฉพาะวิชาที่ยังไม่ได้ลงทะเบียน
    }
        
    private void loadStudentsFromCsv() { // เมธอดสำหรับโหลดข้อมูลนักเรียนจากไฟล์ CSV
        File file = new File(STUDENTS_CSV); // สร้าง object ไฟล์สำหรับ student.csv
        if (!file.exists()) { System.err.println("Error: " + STUDENTS_CSV + " not found!"); return; } // ตรวจสอบว่าไฟล์มีอยู่จริงหรือไม่
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) { // เปิดไฟล์เพื่ออ่านด้วย encoding UTF-8
            String line = reader.readLine(); // อ่านบรรทัดแรก (header)
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); } // ลบ BOM ถ้ามี
            while ((line = reader.readLine()) != null) { // วนลูปอ่านข้อมูลทีละบรรทัดจนหมดไฟล์
                String[] parts = line.split(","); // แยกข้อมูลในบรรทัดด้วยจุลภาค
                if (parts.length >= 7) { // ตรวจสอบว่ามีข้อมูลครบ 7 ส่วนหรือไม่
                    students.put(parts[0], new Student(parts[0], parts[1], parts[2], parts[3], LocalDate.parse(parts[4]), parts[5], parts[6])); // สร้าง object Student แล้วเพิ่มลงใน Map
                }
            }
        } catch (IOException e) { e.printStackTrace(); } // จัดการข้อผิดพลาดที่อาจเกิดขึ้นระหว่างการอ่านไฟล์
    }
    
    private void loadSubjectsFromCsv() { // เมธอดสำหรับโหลดข้อมูลวิชาจากไฟล์ CSV
        File file = new File(SUBJECTS_CSV); // สร้าง object ไฟล์สำหรับ subject.csv
        if (!file.exists()) { System.err.println("Error: " + SUBJECTS_CSV + " not found!"); return; } // ตรวจสอบว่าไฟล์มีอยู่จริงหรือไม่
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) { // เปิดไฟล์เพื่ออ่านด้วย encoding UTF-8
            String line = reader.readLine(); // อ่านบรรทัดแรก (header)
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); } // ลบ BOM ถ้ามี
            while ((line = reader.readLine()) != null) { // วนลูปอ่านข้อมูลทีละบรรทัด
                String[] parts = line.split(",", -1); // แยกข้อมูลในบรรทัด (รวมสตริงว่างด้วย)
                if (parts.length >= 6) { // ตรวจสอบว่ามีข้อมูลครบ 6 ส่วนหรือไม่
                    subjects.put(parts[0], new Subject(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4].isEmpty() ? null : parts[4], Integer.parseInt(parts[5]))); // สร้าง object Subject และเพิ่มลงใน Map
                }
            }
        } catch (IOException e) { e.printStackTrace(); } // จัดการข้อผิดพลาดที่อาจเกิดขึ้น
    }
    
    private void loadRegistrationsFromCsv() { // เมธอดสำหรับโหลดข้อมูลการลงทะเบียนจากไฟล์ CSV
        File file = new File(REGISTRATIONS_CSV); // สร้าง object ไฟล์สำหรับ dataregisteration.csv
        if (!file.exists()) { return; } // ถ้าไฟล์ไม่มี ให้ออกจากเมธอด
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) { // เปิดไฟล์เพื่ออ่าน
            String line = reader.readLine(); // อ่านบรรทัดแรก (header)
            if (line != null && line.startsWith(BOM)) { line = line.substring(1); } // ลบ BOM ถ้ามี
            while ((line = reader.readLine()) != null) { // วนลูปอ่านข้อมูลทีละบรรทัด
                String[] parts = line.split(",", -1); // แยกข้อมูลในบรรทัด (รวมสตริงว่างด้วย)
                if (parts.length >= 2) { // ตรวจสอบว่ามีข้อมูลอย่างน้อย 2 ส่วนหรือไม่
                    String studentId = parts[0]; // ดึงรหัสนักเรียน
                    String subjectId = parts[1]; // ดึงรหัสวิชา
                    String grade = (parts.length > 2 && !parts[2].isEmpty()) ? parts[2] : "N/A"; // ดึงเกรด ถ้ามี, ถ้าไม่มีให้เป็น "N/A"
                    registrations.computeIfAbsent(studentId, k -> new HashMap<>()).put(subjectId, grade); // เพิ่มข้อมูลการลงทะเบียนลงใน Map
                }
            }
        } catch (IOException e) { e.printStackTrace(); } // จัดการข้อผิดพลาด
    }
    
    private void saveRegistrationsToCsv() { // เมธอดสำหรับบันทึกข้อมูลการลงทะเบียนลงไฟล์ CSV
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(REGISTRATIONS_CSV), StandardCharsets.UTF_8))) { // เปิดไฟล์เพื่อเขียนด้วย encoding UTF-8
            writer.write("studentId,subjectId,grade"); // เขียน header ของไฟล์
            writer.newLine(); // ขึ้นบรรทัดใหม่
            for (Map.Entry<String, Map<String, String>> entry : registrations.entrySet()) { // วนลูปผ่านข้อมูลการลงทะเบียนของนักเรียนทุกคน
                String studentId = entry.getKey(); // ดึงรหัสนักเรียน
                for (Map.Entry<String, String> regEntry : entry.getValue().entrySet()) { // วนลูปผ่านวิชาที่นักเรียนคนนั้นลงทะเบียน
                    writer.write(studentId + "," + regEntry.getKey() + "," + regEntry.getValue()); // เขียนข้อมูลลงไฟล์ในรูปแบบ CSV
                    writer.newLine(); // ขึ้นบรรทัดใหม่
                }
            }
        } catch (IOException e) { e.printStackTrace(); } // จัดการข้อผิดพลาดที่อาจเกิดขึ้นระหว่างการเขียนไฟล์
    }
}