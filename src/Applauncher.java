
import javax.swing.SwingUtilities;

public class Applauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // display our weather app gui
                new WeatherAppGUI().setVisible(true);

                //              System.out.println(Weatherapp.getLocationData("Tokyo"));
                //              System.out.println(Weatherapp.getCurrentTime());
            }
        });
    }
}
