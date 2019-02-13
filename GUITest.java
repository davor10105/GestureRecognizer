import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class GUITest {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(()->{
                JFrame window=new GUI();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
