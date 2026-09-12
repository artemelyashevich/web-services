package com.elyashrvich.soap.test_data;

import com.elyashrvich.soap.domain.WeatherCondition;
import com.elyashrvich.soap.domain.WeatherRecord;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class WeatherRecordFixtures {

    public static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2026, 1, 1, 12, 0, 0);

    private WeatherRecordFixtures() {
    }

    public static WeatherRecord of(String city) {
        return of(city, WeatherCondition.CLEAR_SKY);
    }

    public static WeatherRecord of(String city, WeatherCondition condition) {
        return new WeatherRecord(city, 20.5, 55, 10.0, condition, FIXED_TIMESTAMP);
    }

    public static Stream<Arguments> cityAndConditionPairs() {
        return Stream.of(
                Arguments.of("Minsk", WeatherCondition.PARTLY_CLOUDY),
                Arguments.of("Tokyo", WeatherCondition.HEAVY_RAIN),
                Arguments.of("Cairo", WeatherCondition.FOG),
                Arguments.of("Oslo", WeatherCondition.SNOW),
                Arguments.of("Berlin", WeatherCondition.THUNDERSTORM)
        );
    }

    public static Stream<Arguments> seededCityAndConditionPairs() {
        return Stream.of(
                Arguments.of("Minsk", WeatherCondition.PARTLY_CLOUDY),
                Arguments.of("Moscow", WeatherCondition.OVERCAST),
                Arguments.of("London", WeatherCondition.LIGHT_RAIN),
                Arguments.of("Warsaw", WeatherCondition.CLEAR_SKY)
        );
    }

    public static Stream<String> cityNameCaseAndWhitespaceVariants() {
        return Stream.of("Minsk", "minsk", "MINSK", "MiNsK", " Minsk ", "\tMinsk\n");
    }
}
