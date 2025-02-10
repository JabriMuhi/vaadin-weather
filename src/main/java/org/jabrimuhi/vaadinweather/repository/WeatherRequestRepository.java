package org.jabrimuhi.vaadinweather.repository;

import org.jabrimuhi.vaadinweather.entity.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRequestRepository extends JpaRepository<WeatherHistory, Long> {
}
