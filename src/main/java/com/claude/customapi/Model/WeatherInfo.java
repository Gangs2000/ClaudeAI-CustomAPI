package com.claude.customapi.Model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Component
@Entity
public class WeatherInfo implements Serializable {
    @Id
    private String cityName;
    private String currentWeather;
    private String weatherDescription;
    private double temperature;
    private double tempMin;
    private double tempMax;
    private double celsius;
    private double fahrenheit;
    private double windSpeed;
    private LocalDateTime lastUpdated;
    private String icon;
}
