package Model;

public class Subject {
    private final String subjectId;
    private final String subjectName;
    private final int capacity;
    private final String description;
    private final String prerequisiteId;
    private int currentEnrolled;

    public Subject(String subjectId, String subjectName, int capacity, String description, String prerequisiteId, int currentEnrolled) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.capacity = capacity;
        this.description = description;
        this.prerequisiteId = prerequisiteId;
        this.currentEnrolled = currentEnrolled;
    }

    public String getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    
    public int getCapacity() { return capacity; }

    public String getDescription() { return description; }
    public String getPrerequisiteId() { return prerequisiteId; }
    public int getCurrentEnrolled() { return currentEnrolled; }

    public boolean isFull() {
        return currentEnrolled >= capacity;
    }

    public void enroll() {
        if (!isFull()) {
            this.currentEnrolled++;
        }
    }

    public void resetEnrollment() {
        this.currentEnrolled = 0;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d/%d)", subjectId, subjectName, currentEnrolled, capacity);
    }
}