import javax.swing.SwingUtilities;
import Controller.AppController;
import Model.DataService;
import View.LoginFrame;

public class Main {
    public static void main(String[] args) {
        DataService model = DataService.getInstance();
        AppController controller = new AppController(model);
        SwingUtilities.invokeLater(() -> new LoginFrame(controller));
    }
}