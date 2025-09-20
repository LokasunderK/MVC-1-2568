package Model;

import java.time.LocalDate;
import java.time.Period;

public class Student {
    private final String studentId;
    private final String firstName;
    private final String lastName;
    private final String idCard;
    private final LocalDate dateOfBirth;
    private final String address;
    private final String phoneNumber;

    public Student(String studentId, String firstName, String lastName, String idCard, LocalDate dateOfBirth, String address, String phoneNumber) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCard = idCard;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getIdCard() { return idCard; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}