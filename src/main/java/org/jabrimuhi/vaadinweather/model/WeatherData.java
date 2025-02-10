package org.jabrimuhi.vaadinweather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {
    private Double temperature;
    private Double feelsLikeTemperature;
    private String name;
    private String description;
    private String iconUrl;

}
