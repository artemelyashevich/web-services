package com.elyashrvich.soap.service;

import com.elyashrvich.soap.domain.OperationStatus;
import com.elyashrvich.soap.domain.WeatherDeletionResult;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.exception.WeatherNotFoundException;
import com.elyashrvich.soap.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherRepository weatherRepository;

    @Override
    public WeatherRecord getWeatherByCity(String city) {
        log.debug("Looking up weather for city '{}'", city);
        return weatherRepository.findByCity(city)
                .map(weather -> {
                    log.info("Weather found for city '{}': condition={}", city, weather.condition());
                    return weather;
                })
                .orElseThrow(() -> {
                    log.warn("Weather data not found for city '{}'", city);
                    return new WeatherNotFoundException("Weather data not found for city: " + city);
                });
    }

    @Override
    public WeatherDeletionResult deleteWeatherByCity(String city) {
        log.debug("Attempting to delete weather data for city '{}'", city);
        boolean removed = weatherRepository.deleteByCity(city);
        if (!removed) {
            log.warn("Cannot delete: weather data not found for city '{}'", city);
            throw new WeatherNotFoundException("Cannot delete: weather data not found for city: " + city);
        }

        log.info("Weather data deleted for city '{}'", city);
        return new WeatherDeletionResult(
                city,
                OperationStatus.SUCCESS,
                "Weather data for '" + city + "' has been deleted.");
    }
}
