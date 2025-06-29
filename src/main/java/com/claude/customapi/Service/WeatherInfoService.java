package com.claude.customapi.Service;

import com.claude.customapi.Interface.WeatherInfoInterface;
import com.claude.customapi.Model.ForeCastForWeek;
import com.claude.customapi.Model.WeatherForecast;
import com.claude.customapi.Model.WeatherInfo;
import com.claude.customapi.Repository.WeatherForecastRepository;
import com.claude.customapi.Repository.WeatherInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WeatherInfoService implements WeatherInfoInterface {

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;
    @Autowired
    private WeatherForecastRepository weatherForecastRepository;
    @Value("${openweather.api.key}")
    private String apiKey;
    @Value("${openweather.api.weather.uri}")
    private String weatherURL;
    @Value("${openweather.api.forecast.uri}")
    private String forecastURL;
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Override
    @Tool(description = "Get current weather for given city")
    public WeatherInfo getCurrentWeather(String cityName) throws JSONException {
        WeatherInfo weatherInfo = null;
        String capitalize = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);
        if (weatherInfoRepository.existsById(capitalize)) {
            log.info("Weather is available");
            weatherInfo = weatherInfoRepository.findById(capitalize).get();
            Duration duration = Duration.between(weatherInfo.getLastUpdated(), LocalDateTime.now());
            weatherInfo = (duration.toMinutes() > 10) ? weatherInfoRepository.saveAndFlush(this.fetchWeatherFromAPI(cityName)) :
                    weatherInfoRepository.findById(cityName).get();
        } else {
            log.info("Weather information is not available");
            weatherInfo = weatherInfoRepository.saveAndFlush(this.fetchWeatherFromAPI(cityName));
        }
        return weatherInfo;
    }

    @Override
    @Tool(description = "Get week forecast for given city")
    public WeatherForecast weekForecast(String cityName) throws JSONException {
        WeatherForecast weatherForecast = null;
        String capitalize = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);
        if (weatherForecastRepository.existsByCityName(capitalize)) {
            log.info("Weather forecast is available");
            weatherForecast = weatherForecastRepository.findByCityName(capitalize);
            Duration duration = Duration.between(weatherForecast.getLastUpdated(), LocalDateTime.now());
            weatherForecast = (duration.toMinutes() > 60) ? weatherForecastRepository.save(this.fetchWeatherForecast(cityName)) :
                    weatherForecastRepository.findByCityName(cityName);
        } else {
            log.info("Weather forecast information is not available");
            weatherForecast = weatherForecastRepository.save(this.fetchWeatherForecast(cityName));
        }
        return weatherForecast;
    }

    private WeatherInfo fetchWeatherFromAPI(String cityName) throws JSONException {
        log.info("Fetch weather information from External API call");
        String openWeatherURI = weatherURL.concat(cityName).concat("&appid=").concat(apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(openWeatherURI, String.class);
        JSONArray weatherResponse = new JSONObject(jsonResponse).getJSONArray("weather");
        JSONObject mainResponse = new JSONObject(jsonResponse).getJSONObject("main");
        JSONObject windResponse = new JSONObject(jsonResponse).getJSONObject("wind");
        WeatherInfo weatherInfo = WeatherInfo.builder()
                .cityName(cityName)
                .currentWeather(weatherResponse.getJSONObject(0).get("main").toString())
                .weatherDescription(weatherResponse.getJSONObject(0).get("description").toString())
                .temperature(mainResponse.getDouble("temp"))
                .tempMin(mainResponse.getDouble("temp_min"))
                .tempMax(mainResponse.getDouble("temp_max"))
                .celsius(Double.valueOf(decimalFormat.format(mainResponse.getDouble("temp") - 273.15)))
                .fahrenheit(Double.valueOf(decimalFormat.format((((mainResponse.getDouble("temp") - 273.15) * 9) / 5) + 32)))
                .windSpeed(windResponse.getDouble("speed"))
                .lastUpdated(LocalDateTime.now())
                .icon(weatherResponse.getJSONObject(0).get("icon").toString())
                .build();
        return weatherInfo;
    }

    private WeatherForecast fetchWeatherForecast(String cityName) throws JSONException {
        log.info("Fetch weather forecast information from External API call");
        String openWeatherForecastURL = forecastURL.concat(cityName).concat("&appid=").concat(apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(openWeatherForecastURL, String.class);
        //Need to iterate for 40 times to fetch all values from JSON response.
        JSONArray jsonArray = new JSONObject(jsonResponse).getJSONArray("list");
        List<ForeCastForWeek> foreCastForWeekList = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            JSONArray weatherResponse = jsonObject.getJSONArray("weather");
            String dateAndTime = jsonObject.get("dt_txt").toString();
            LocalDate date = LocalDate.parse(dateAndTime.split(" ")[0]);
            LocalTime time = LocalTime.parse(dateAndTime.split(" ")[1]);
            ForeCastForWeek foreCastForWeek = ForeCastForWeek.builder()
                    .currentWeather(weatherResponse.getJSONObject(0).get("main").toString())
                    .weatherDescription(weatherResponse.getJSONObject(0).get("description").toString())
                    .temperature(jsonObject.getJSONObject("main").getDouble("temp"))
                    .tempMin(Double.valueOf(decimalFormat.format(jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15)))
                    .tempMax(Double.valueOf(decimalFormat.format(jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15)))
                    .humidity(jsonObject.getJSONObject("main").getDouble("humidity"))
                    .celsius(Double.valueOf(decimalFormat.format(jsonObject.getJSONObject("main").getDouble("temp") - 273.15)))
                    .fahrenheit(Double.valueOf(decimalFormat.format((((jsonObject.getJSONObject("main").getDouble("temp") - 273.15) * 9) / 5) + 32)))
                    .windSpeed(jsonObject.getJSONObject("wind").getDouble("speed"))
                    .date(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                    .time(time.format(DateTimeFormatter.ofPattern("hh:mm a")))
                    .icon(weatherResponse.getJSONObject(0).getString("icon"))
                    .build();
            foreCastForWeekList.add(foreCastForWeek);
        }
        WeatherForecast weatherForecast = WeatherForecast.builder()
                ._id(UUID.randomUUID().toString())
                .cityName(cityName)
                .foreCastForWeekList(foreCastForWeekList)
                .lastUpdated(LocalDateTime.now())
                .build();
        return weatherForecast;
    }
}
