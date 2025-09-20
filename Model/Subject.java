package Model; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ Model

public class Subject { // ประกาศคลาส Subject สำหรับเก็บข้อมูลของรายวิชา
    private final String subjectId; // ประกาศตัวแปรสำหรับเก็บรหัสวิชา (แก้ไขไม่ได้)
    private final String subjectName; // ประกาศตัวแปรสำหรับเก็บชื่อวิชา (แก้ไขไม่ได้)
    private final int capacity; // ประกาศตัวแปรสำหรับเก็บจำนวนนักเรียนที่รับได้สูงสุด (แก้ไขไม่ได้)
    private final String description; // ประกาศตัวแปรสำหรับเก็บคำอธิบายรายวิชา (แก้ไขไม่ได้)
    private final String prerequisiteId; // ประกาศตัวแปรสำหรับเก็บรหัสวิชาบังคับก่อน (แก้ไขไม่ได้)
    private int currentEnrolled; // ประกาศตัวแปรสำหรับเก็บจำนวนนักเรียนที่ลงทะเบียนในปัจจุบัน

    // Constructor ของคลาส Subject ใช้สำหรับสร้าง object วิชาใหม่พร้อมกำหนดค่าเริ่มต้น
    public Subject(String subjectId, String subjectName, int capacity, String description, String prerequisiteId, int currentEnrolled) {
        this.subjectId = subjectId; // กำหนดค่ารหัสวิชาที่รับเข้ามาให้กับตัวแปรของคลาส
        this.subjectName = subjectName; // กำหนดค่าชื่อวิชาที่รับเข้ามาให้กับตัวแปรของคลาส
        this.capacity = capacity; // กำหนดค่าจำนวนที่รับได้ที่รับเข้ามาให้กับตัวแปรของคลาส
        this.description = description; // กำหนดค่าคำอธิบายที่รับเข้ามาให้กับตัวแปรของคลาส
        this.prerequisiteId = prerequisiteId; // กำหนดค่ารหัสวิชาบังคับก่อนที่รับเข้ามาให้กับตัวแปรของคลาส
        this.currentEnrolled = currentEnrolled; // กำหนดค่าจำนวนผู้ลงทะเบียนปัจจุบันที่รับเข้ามาให้กับตัวแปรของคลาส
    }

    public String getSubjectId() { return subjectId; } // เมธอดสำหรับดึงค่ารหัสวิชา
    public String getSubjectName() { return subjectName; } // เมธอดสำหรับดึงค่าชื่อวิชา
    
    public int getCapacity() { return capacity; } // เมธอดสำหรับดึงค่าจำนวนที่รับได้สูงสุด

    public String getDescription() { return description; } // เมธอดสำหรับดึงค่าคำอธิบายรายวิชา
    public String getPrerequisiteId() { return prerequisiteId; } // เมธอดสำหรับดึงค่ารหัสวิชาบังคับก่อน
    public int getCurrentEnrolled() { return currentEnrolled; } // เมธอดสำหรับดึงค่าจำนวนผู้ลงทะเบียนปัจจุบัน

    public boolean isFull() { // เมธอดสำหรับตรวจสอบว่าวิชานี้เต็มแล้วหรือยัง
        return currentEnrolled >= capacity; // คืนค่า true ถ้าจำนวนคนลงทะเบียนมากกว่าหรือเท่ากับจำนวนที่รับได้
    }

    public void enroll() { // เมธอดสำหรับเพิ่มจำนวนผู้ลงทะเบียน
        if (!isFull()) { // ตรวจสอบก่อนว่าวิชายังไม่เต็ม
            this.currentEnrolled++; // เพิ่มจำนวนผู้ลงทะเบียนขึ้น 1 คน
        }
    }

    public void resetEnrollment() { // เมธอดสำหรับรีเซ็ตจำนวนผู้ลงทะเบียน
        this.currentEnrolled = 0; // ตั้งค่าจำนวนผู้ลงทะเบียนปัจจุบันให้เป็น 0
    }

    @Override // ระบุว่าเป็นการเขียนทับ (override) เมธอดจากคลาสแม่
    public String toString() { // เมธอดที่จะถูกเรียกเมื่อ object ถูกแปลงเป็น String
        return String.format("%s - %s (%d/%d)", subjectId, subjectName, currentEnrolled, capacity); // คืนค่าเป็น String ที่จัดรูปแบบตามที่กำหนด (รหัส - ชื่อวิชา (คนลงทะเบียน/เต็ม))
    }
}