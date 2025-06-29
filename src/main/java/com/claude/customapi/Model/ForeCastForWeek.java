package com.claude.customapi.Model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForeCastForWeek implements Serializable {
    private String currentWeather;
    private String weatherDescription;
    private double temperature;
    private double tempMin;
    private double tempMax;
    private double humidity;
    private double celsius;
    private double fahrenheit;
    private double windSpeed;
    private String date;
    private String time;
    private String icon;
}
