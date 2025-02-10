package org.jabrimuhi.vaadinweather.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeatherHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDateTime timestamp;

    public WeatherHistory(Double latitude, Double longitude, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}
