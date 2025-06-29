package com.claude.customapi.Repository;

import com.claude.customapi.Model.WeatherForecast;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherForecastRepository extends MongoRepository<WeatherForecast, String> {
    WeatherForecast findByCityName(String cityName);
    boolean existsByCityName(String cityName);
}
