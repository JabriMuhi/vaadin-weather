package org.jabrimuhi.vaadinweather.service;

import org.jabrimuhi.vaadinweather.entity.WeatherHistory;
import org.jabrimuhi.vaadinweather.model.WeatherData;
import org.jabrimuhi.vaadinweather.repository.WeatherRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class WeatherServiceTest {
    @Mock
    private WeatherRequestRepository weatherRequestRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWeatherHistory_ShouldReturnWeatherHistoryList() {
        WeatherHistory weatherHistory = new WeatherHistory(55.75, 37.61, "Ясно");
        when(weatherRequestRepository.findAll()).thenReturn(Collections.singletonList(weatherHistory));

        List<WeatherHistory> result = weatherService.getWeatherHistory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ясно", result.get(0).getDescription());
    }

    @Test
    void getWeather_ShouldReturnWeatherData() {
        double lat = 55.75;
        double lon = 37.61;
        String apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=55.75&lon=37.61&appid=c6cf8dd56e704fca7b7d314615c6aaf0&lang=ru";

        // Mock
        Map<String, Object> mockResponse = Map.of(
                "main", Map.of("temp", 280.0, "feels_like", 278.0),
                "weather", List.of(Map.of("main", "Cloudy", "description", "облачно", "icon", "10d"))
        );

        when(restTemplate.getForEntity(apiUrl, Map.class)).thenReturn(ResponseEntity.ok(mockResponse));

        WeatherData result = weatherService.getWeather(lat, lon);

        System.out.println("Ожидаемая температура: " + (280.0 - 273.15));
        System.out.println("Фактическая температура: " + result.getTemperature());

        assertNotNull(result);
        assertEquals(-2.63, result.getTemperature(), 0.01);
        assertEquals(-2.63, result.getFeelsLikeTemperature(), 0.01);
        assertEquals("Clear", result.getName());
        assertEquals("ясно", result.getDescription());
        assertEquals("https://openweathermap.org/img/wn/01d@2x.png", result.getIconUrl());
    }

}