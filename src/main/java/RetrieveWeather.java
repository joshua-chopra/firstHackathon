import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * This class is used to Retrieve Weather info for a specified zipcode given by the weatherText.user. It makes
 * use of the openweathermap API, which allows us to retrieve weatherText.weather for a location.
 * An API key is needed to query the API.
 * @author Josh Chopra
 */
public class RetrieveWeather {

    // This is set to an API key from the Runner class based on user input to console.
    private static final String apiKey = "8365433bc5b8ac876c7f82b0af27a5e5";
    private static final String weatherAPIURL = "http://api.openweathermap.org/data/2.5/weather?zip=";

    /***
     * Helper method used to create URL to pass to API with given API key, and zipcode.
     * @param zipCode - users zip code
     * @return - URL in properly encoded format to query API.
     */
    private static URL createURL(String zipCode) {
        URL url = null;
        try {
            url = new URL(weatherAPIURL + zipCode + ",us" + "&appid=" + apiKey + "&units=imperial");
        } catch (MalformedURLException e) {
            System.out.println("Invalid zipcode, or apiKey provided. Please check and try again.");
        }
        return url;
    }

    /***
     * Helper method used from com.google.gson.Gson library that parses JSON objects and returns
     * them as HashMap
     * @param jsonAsString JSON object as string
     * @return JSON object to hashmap
     */
    private static HashMap<String, Object> jsonToHashMap(String jsonAsString) {
        HashMap<String, Object> weatherMap = new Gson().fromJson(jsonAsString,
                new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );
        return weatherMap;
    }

    /***
     * This method takes in the apiKey to be used as well as the weatherText.user's zipcode and queries the
     * openweather.org website for weatherText.weather data for that weatherText.user's zipcode, using the apikey.
     //     * @param apiKey
     * @param zipCode
     * @return Hashmap with all relevant weatherText.weather data for a weatherText.user's zipcode.
     * @throws IOException
     */
    public HashMap<String, String> getWeatherAttributes(String zipCode) throws IOException {
        URLConnection conn = createURL(zipCode).openConnection();
        StringBuilder jsonData = new StringBuilder();
        HashMap<String, String> weatherInfo = new HashMap<String, String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            HashMap<String, Object> responseMap = jsonToHashMap(jsonData.toString());
            // value corresponding to "weatherText.weather" key is stored in java as an ArrayList of LinkedTreeMaps
            ArrayList<Map> weather = (ArrayList<Map>) responseMap.get("weather");
            String weatherDescription = weather.get(0).get("description").toString();

            // value corresponding to key for "main" is a hashmap with string, double pairs
            Map<String, Double> temps = (Map<String, Double>) responseMap.get("main");
            String currentTemp = temps.get("temp").toString();
            String minTemp = temps.get("temp_min").toString();
            String maxTemp = temps.get("temp_max").toString();
            String tempFeelsLike = temps.get("feels_like").toString();

            // add attributes to hashmap, which we'll output & format nicely for sending text message.
            weatherInfo.put("The weather description is: ", weatherDescription);
            weatherInfo.put("The current temperature:", currentTemp);
            weatherInfo.put("The low temperature of the day is:", minTemp);
            weatherInfo.put("The high temperature is:", maxTemp);
            weatherInfo.put("The temperature currently feels like:", tempFeelsLike);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return weatherInfo;
    }

    /***
     * Formats the HashMap returned by getWeatherAttributes, to prepare the text message sent to the user.
     * @param weatherInfo HashMap returned by getWeatherAttributes method
     * @return String that is nicely formatted
     */
    public String formatWeatherDetails(HashMap<String, String> weatherInfo) {
        StringBuilder messageToUser = new StringBuilder('\n' + "Here are the details about weather at your location:" +
                '\n' + '\n');
        for (String weatherAttribute : weatherInfo.keySet()) {
            messageToUser.append(weatherAttribute + " " + weatherInfo.get(weatherAttribute) + '\n' + '\n');
        }
        return messageToUser.toString();
    }


}