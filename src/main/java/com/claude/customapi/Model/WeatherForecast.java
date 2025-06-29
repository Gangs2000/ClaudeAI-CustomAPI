package com.claude.customapi.Model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
@Builder
@Document
public class WeatherForecast implements Serializable {
    @Id
    private String _id;
    private String cityName;
    private List<ForeCastForWeek> foreCastForWeekList;
    private LocalDateTime lastUpdated;
}
