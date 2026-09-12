package com.elyashrvich.soap.repository;

import com.elyashrvich.soap.domain.WeatherCondition;
import com.elyashrvich.soap.domain.WeatherRecord;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class WeatherRepository {

    private final Map<String, WeatherRecord> weatherByCity = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        save("Minsk", 18.5, 63, 12.0, WeatherCondition.PARTLY_CLOUDY);
        save("Moscow", 12.0, 70, 18.5, WeatherCondition.OVERCAST);
        save("London", 15.2, 80, 22.3, WeatherCondition.LIGHT_RAIN);
        save("Warsaw", 17.8, 65, 10.4, WeatherCondition.CLEAR_SKY);
        log.info("Seeded weather repository with {} cities", weatherByCity.size());
    }

    private void save(String city, double temperature, int humidity, double windSpeed, WeatherCondition condition) {
        WeatherRecord record = new WeatherRecord(city, temperature, humidity, windSpeed, condition, LocalDateTime.now());
        weatherByCity.put(key(city), record);
    }

    public Optional<WeatherRecord> findByCity(String city) {
        Optional<WeatherRecord> result = Optional.ofNullable(weatherByCity.get(key(city)));
        log.debug("findByCity('{}') -> {}", city, result.isPresent() ? "found" : "not found");
        return result;
    }

    public boolean deleteByCity(String city) {
        boolean removed = weatherByCity.remove(key(city)) != null;
        log.debug("deleteByCity('{}') -> {}", city, removed);
        return removed;
    }

    private String key(String city) {
        return city == null ? "" : city.trim().toLowerCase();
    }
}
