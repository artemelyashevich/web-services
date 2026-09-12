package com.elyashrvich.soap.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elyashrvich.soap.domain.WeatherCondition;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.test_data.WeatherRecordFixtures;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class WeatherRepositoryTest {

    private static final String KNOWN_CITY = "Minsk";
    private static final String UNKNOWN_CITY = "Atlantis";

    private WeatherRepository weatherRepository;

    @BeforeEach
    void setUp() {
        weatherRepository = new WeatherRepository();
        weatherRepository.seed();
    }

    @ParameterizedTest(name = "findByCity(\"{0}\") returns the seeded record")
    @MethodSource("com.elyashrvich.soap.test_data.WeatherRecordFixtures#cityNameCaseAndWhitespaceVariants")
    void findByCityCaseAndWhitespaceVariantOfKnownCity_ReturnsWeatherRecord(String rawCityInput) {
        Optional<WeatherRecord> found = weatherRepository.findByCity(rawCityInput);

        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(KNOWN_CITY, found.get().city())
        );
    }

    @ParameterizedTest(name = "findByCity(\"{0}\") returns empty")
    @ValueSource(strings = {UNKNOWN_CITY, "", " "})
    void findByCityUnknownOrBlankCity_ReturnsEmptyOptional(String city) {
        assertTrue(weatherRepository.findByCity(city).isEmpty());
    }

    @ParameterizedTest(name = "findByCity(null) returns empty")
    @NullAndEmptySource
    void findByCityNullOrEmptyCity_ReturnsEmptyOptional(String city) {
        assertTrue(weatherRepository.findByCity(city).isEmpty());
    }

    @Test
    void deleteByCityExistingCity_ReturnsTrueAndRemovesRecord() {
        boolean removed = weatherRepository.deleteByCity(KNOWN_CITY);

        assertAll(
                () -> assertTrue(removed),
                () -> assertTrue(weatherRepository.findByCity(KNOWN_CITY).isEmpty())
        );
    }

    @ParameterizedTest(name = "deleteByCity(\"{0}\") returns false")
    @ValueSource(strings = {UNKNOWN_CITY, "", " "})
    void deleteByCityUnknownOrBlankCity_ReturnsFalse(String city) {
        assertFalse(weatherRepository.deleteByCity(city));
    }

    @ParameterizedTest(name = "deleteByCity(null) returns false")
    @NullAndEmptySource
    void deleteByCityNullOrEmptyCity_ReturnsFalse(String city) {
        assertFalse(weatherRepository.deleteByCity(city));
    }

    @Test
    void deleteByCityAlreadyDeletedCity_ReturnsFalseOnSecondCall() {
        weatherRepository.deleteByCity(KNOWN_CITY);

        assertFalse(weatherRepository.deleteByCity(KNOWN_CITY));
    }

    @ParameterizedTest(name = "seeded city \"{0}\" has condition {1}")
    @MethodSource("com.elyashrvich.soap.test_data.WeatherRecordFixtures#seededCityAndConditionPairs")
    void findByCitySeededCity_ReturnsRecordWithExpectedCondition(String city, WeatherCondition expectedCondition) {
        Optional<WeatherRecord> found = weatherRepository.findByCity(city);

        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(expectedCondition, found.map(WeatherRecord::condition).orElse(null))
        );
    }

    @Test
    void seedCalledTwice_IsIdempotentAndOverwritesExistingRecords() {
        long before = countSeededCities();
        weatherRepository.seed();
        long after = countSeededCities();

        assertEquals(before, after);
    }

    private long countSeededCities() {
        return WeatherRecordFixtures.seededCityAndConditionPairs()
                .filter(arguments -> weatherRepository.findByCity((String) arguments.get()[0]).isPresent())
                .count();
    }
}
