package Model; // ระบุว่าคลาสนี้อยู่ในแพ็คเกจ Model

import java.time.LocalDate; // นำเข้าคลาส LocalDate สำหรับจัดการข้อมูลวันที่
import java.time.Period; // นำเข้าคลาส Period สำหรับคำนวณระยะเวลาระหว่างวันที่

public class Student { // ประกาศคลาส Student สำหรับเก็บข้อมูลของนักเรียน
    private final String studentId; // ประกาศตัวแปรสำหรับเก็บรหัสนักเรียน (แก้ไขไม่ได้)
    private final String firstName; // ประกาศตัวแปรสำหรับเก็บชื่อจริง (แก้ไขไม่ได้)
    private final String lastName; // ประกาศตัวแปรสำหรับเก็บนามสกุล (แก้ไขไม่ได้)
    private final String idCard; // ประกาศตัวแปรสำหรับเก็บเลขบัตรประชาชน (แก้ไขไม่ได้)
    private final LocalDate dateOfBirth; // ประกาศตัวแปรสำหรับเก็บวันเกิด (แก้ไขไม่ได้)
    private final String address; // ประกาศตัวแปรสำหรับเก็บที่อยู่ (แก้ไขไม่ได้)
    private final String phoneNumber; // ประกาศตัวแปรสำหรับเก็บเบอร์โทรศัพท์ (แก้ไขไม่ได้)

    // Constructor ของคลาส Student ใช้สำหรับสร้าง object นักเรียนใหม่พร้อมกำหนดค่าเริ่มต้น
    public Student(String studentId, String firstName, String lastName, String idCard, LocalDate dateOfBirth, String address, String phoneNumber) {
        this.studentId = studentId; // กำหนดค่ารหัสนักเรียนที่รับเข้ามาให้กับตัวแปรของคลาส
        this.firstName = firstName; // กำหนดค่าชื่อจริงที่รับเข้ามาให้กับตัวแปรของคลาส
        this.lastName = lastName; // กำหนดค่านามสกุลที่รับเข้ามาให้กับตัวแปรของคลาส
        this.idCard = idCard; // กำหนดค่าเลขบัตรประชาชนที่รับเข้ามาให้กับตัวแปรของคลาส
        this.dateOfBirth = dateOfBirth; // กำหนดค่าวันเกิดที่รับเข้ามาให้กับตัวแปรของคลาส
        this.address = address; // กำหนดค่าที่อยู่ที่รับเข้ามาให้กับตัวแปรของคลาส
        this.phoneNumber = phoneNumber; // กำหนดค่าเบอร์โทรศัพท์ที่รับเข้ามาให้กับตัวแปรของคลาส
    }

    public String getStudentId() { return studentId; } // เมธอดสำหรับดึงค่ารหัสนักเรียน
    public String getFirstName() { return firstName; } // เมธอดสำหรับดึงค่าชื่อจริง
    public String getLastName() { return lastName; } // เมธอดสำหรับดึงค่านามสกุล
    public String getFullName() { return firstName + " " + lastName; } // เมธอดสำหรับดึงชื่อ-นามสกุลเต็ม
    public String getIdCard() { return idCard; } // เมธอดสำหรับดึงค่าเลขบัตรประชาชน
    public LocalDate getDateOfBirth() { return dateOfBirth; } // เมธอดสำหรับดึงค่าวันเกิด
    public String getAddress() { return address; } // เมธอดสำหรับดึงค่าที่อยู่
    public String getPhoneNumber() { return phoneNumber; } // เมธอดสำหรับดึงค่าเบอร์โทรศัพท์

    public int getAge() { // เมธอดสำหรับคำนวณอายุ
        return Period.between(dateOfBirth, LocalDate.now()).getYears(); // คำนวณระยะห่างระหว่างวันเกิดกับวันปัจจุบัน แล้วคืนค่าเป็นปี
    }

    @Override // ระบุว่าเป็นการเขียนทับ (override) เมธอดจากคลาสแม่
    public String toString() { // เมธอดที่จะถูกเรียกเมื่อ object ถูกแปลงเป็น String
        return getFullName(); // คืนค่าเป็นชื่อ-นามสกุลเต็มของนักเรียน
    }
}