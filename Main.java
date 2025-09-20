import javax.swing.SwingUtilities; // นำเข้าคลาส SwingUtilities สำหรับจัดการเธรดของ GUI
import Controller.AppController; // นำเข้าคลาส AppController ซึ่งเป็นตัวควบคุมหลักของโปรแกรม
import Model.DataService; // นำเข้าคลาส DataService ซึ่งทำหน้าที่จัดการข้อมูล
import View.LoginFrame; // นำเข้าคลาส LoginFrame ซึ่งเป็นหน้าต่าง GUI แรกสำหรับล็อกอิน

public class Main { // ประกาศคลาส Main ซึ่งเป็นคลาสเริ่มต้นของโปรแกรม
    public static void main(String[] args) { // เมธอด main ซึ่งเป็นจุดเริ่มต้นการทำงานของโปรแกรม Java
        DataService model = DataService.getInstance(); // สร้างหรือเรียกใช้อินสแตนซ์ของ DataService (Model) ผ่าน Singleton Pattern
        AppController controller = new AppController(model); // สร้างอ็อบเจกต์ของ AppController (Controller) โดยส่ง model เข้าไป
        SwingUtilities.invokeLater(() -> new LoginFrame(controller)); // สั่งให้สร้างและแสดง LoginFrame (View) บน Event Dispatch Thread (EDT) ซึ่งเป็นวิธีที่ถูกต้องและปลอดภัยสำหรับแอปพลิเคชัน Swing
    }
}