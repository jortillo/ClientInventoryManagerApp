import gui.*;
import javax.swing.SwingUtilities;
public class Main {

    public static void main(String[] args) {
        System.out.println("This main class works!");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Starter starter = new Starter();
                //starter.setVisible(true);
            }
        });
    }
}
