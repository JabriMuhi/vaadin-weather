package org.jabrimuhi.vaadinweather.service;

import org.jabrimuhi.vaadinweather.entity.WeatherHistory;
import org.jabrimuhi.vaadinweather.model.WeatherData;
import org.jabrimuhi.vaadinweather.repository.WeatherRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private final String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "c6cf8dd56e704fca7b7d314615c6aaf0";

    private final WeatherRequestRepository weatherRequestRepository;

    public WeatherService(final WeatherRequestRepository weatherRequestRepository) {
        this.weatherRequestRepository = weatherRequestRepository;
    }

    public WeatherData getWeather(double lat, double lon) {
        String url = String.format("%s?lat=%s&lon=%s&appid=%s&lang=ru",
                API_URL, lat, lon, API_KEY);

        ResponseEntity<Map> response = new RestTemplate().getForEntity(url, Map.class);
        WeatherData resultData = new WeatherData();

        if (response.getBody() != null) {
            Map<String, Object> main = (Map<String, Object>) response.getBody().get("main");
            if (main != null) {
                double tempKelvin = (double) main.get("temp");
                double feelsLikeKelvin = (double) main.get("feels_like");

                resultData.setTemperature(tempKelvin - 273.15);
                resultData.setFeelsLikeTemperature(feelsLikeKelvin - 273.15);

            } else {
                System.out.println("Данные о температуре не найдены.");
            }

            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.getBody().get("weather");
            if (weatherList != null && !weatherList.isEmpty()) {
                Map<String, Object> weather = weatherList.get(0);

                String mainWeather = (String) weather.get("main");
                String description = (String) weather.get("description");
                String iconUrl = "https://openweathermap.org/img/wn/" + weather.get("icon") + "@2x.png";

                resultData.setName(mainWeather);
                resultData.setDescription(description);
                resultData.setIconUrl(iconUrl);

                WeatherHistory weatherHistory = new WeatherHistory(lat, lon, description);
                weatherRequestRepository.save(weatherHistory);
            } else {
                System.out.println("Данные о погоде не найдены.");
            }

            return resultData;
        }
        return null;
    }

    public List<WeatherHistory> getWeatherHistory() {
        return weatherRequestRepository.findAll();
    }
}
