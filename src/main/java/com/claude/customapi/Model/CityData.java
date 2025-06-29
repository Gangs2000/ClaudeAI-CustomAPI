package com.claude.customapi.Model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Component
public class CityData implements Serializable {
    @Id
    private long cityId;
    private String cityName;
    private String country;
    private long latitude;
    private long longitude;
    private long population;
    private long timeZone;
    private long sunRise;
    private long sunSet;
}
