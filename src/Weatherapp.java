
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Weather Forecast Backend
public class Weatherapp {

    // Get Weather Data
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        if (locationData != null && !locationData.isEmpty()) {
            JSONObject firstLocation = (JSONObject) locationData.get(0);
            double lat = (double) firstLocation.get("latitude");
            double lon = (double) firstLocation.get("longitude");

            // build API request url with location coordinates
            String urlString = "https://api.open-meteo.com/v1/forecast?"
                    + "latitude=" + lat + "&longitude=" + lon
                    + "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m&timezone=America%2FLos_Angeles";

            try {
                // call api and get response
                HttpURLConnection conn = fetchApiResponse(urlString);

                // check for response status
                // 200 - means that connection was a success
                if (conn.getResponseCode() != 200) {
                    System.out.println("Error: Could not connect to API");
                    return null;

                }

                // store resulting json data
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) {
                    // read and store in string builder
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection 
                conn.disconnect();

                // parse through our data
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                // retrieve hourly data
                JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");

                // we want to get the current hours's data
                // so we need to get the index of our current data
                JSONArray time = (JSONArray) hourly.get("time");
                int index = findIndexOfCurrentTime(time);

                // get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(index);

                // get weather code
                JSONArray weathercode = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long) weathercode.get(index));

                // get humidity
                JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (long) relativeHumidity.get(index);

                // get windspeed
                JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
                double windspeed = (double) windspeedData.get(index);

                // build the weather json data object that we are going to access in our frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

                return weatherData;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return fetchWeather(lat, lon);
        }
        return null;
    }

    // Get Location Data
    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + locationName + "&count=1&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());
            return (JSONArray) resultsJsonObj.get("results");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetch Weather Data
    public static JSONObject fetchWeather(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                + "&longitude=" + lon + "&current_weather=true";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not fetch weather data");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(resultJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetch API Response
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // couldn't find location
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist) {
        String currentTime = getCurrentTime();

        // iterate through the time list and see which one matches our current time 
        for (int i = 0; i < timelist.size(); i++) {
            String time = (String) timelist.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // return the index
                return i;
            }
        }

        return 0;

    }

    public static String getCurrentTime() {
        // get current data and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00 (this is how it is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // convert weather code to something more readable
    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            // clear
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)) {
            // rain
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;

    }

    // Main Method (Test)
    public static void main(String[] args) {
        JSONObject weather = getWeatherData("New York");
        System.out.println(weather);
    }
}
