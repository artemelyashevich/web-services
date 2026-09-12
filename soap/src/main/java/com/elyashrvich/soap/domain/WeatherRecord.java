package com.elyashrvich.soap.domain;

import java.time.LocalDateTime;

public record WeatherRecord(
        String city,
        double temperatureCelsius,
        int humidityPercent,
        double windSpeedKph,
        WeatherCondition condition,
        LocalDateTime updatedAt) {
}
