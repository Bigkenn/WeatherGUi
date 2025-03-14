
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class WeatherAppGUI extends JFrame {

    private JSONObject weatherData;

    private String resourcePath;

    public WeatherAppGUI() {
        // set up our gui and add a title

        super("Weather App");

        // configure gui to end the programs's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of our gui (in pixels)
        setSize(450, 650);

        // load our gui at the center of the sceen
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resize of our gui
        setResizable(false);

        addGuiComponents();

    }

    private void addGuiComponents() {
        // search field
        JTextField searchTextField = new JTextField();

        // set the location and size of our components
        searchTextField.setBounds(15, 15, 351, 45);

        // change font size and style
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/Assets/weather_10603913.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperture text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/Assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%<html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("src/Assets/anemometer.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        // windspeed text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 15km/h<html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        // search button
        JButton searchButton = new JButton(loadImage("src/Assets/search.png"));

        // change the cursor to a hand cursor when horvering over thid button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                // validate input - remove whitespace to ensure non-empty text
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // check id user enters an invalid country, city or location
                // retrieve weather data
                weatherData = Weatherapp.getWeatherData(userInput);

                // update gui
                // update weaher image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // depending on the condition we can update the weather image that corresponds with the condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/Assets/weather_10603913.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/Assets/windy_10076603 copy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/Assets/night_16179267.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/Assets/winter_10991031 (1).png"));
                        break;

                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });

        add(searchButton);

    }

    // used to create images in our gui component
    private ImageIcon loadImage(String resourcePath) {
        try {
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // returns on image icon so that our component can render it
            return new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("could not find resource");
        return null;

    }
}
