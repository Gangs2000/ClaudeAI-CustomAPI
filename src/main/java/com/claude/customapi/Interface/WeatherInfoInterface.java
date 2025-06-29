package com.claude.customapi.Interface;


import com.claude.customapi.Model.WeatherForecast;
import com.claude.customapi.Model.WeatherInfo;
import org.json.JSONException;

public interface WeatherInfoInterface {
    WeatherInfo getCurrentWeather(String cityName) throws JSONException;
    WeatherForecast weekForecast(String cityName) throws JSONException;
}
