package com.elyashrvich.soap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elyashrvich.soap.domain.OperationStatus;
import com.elyashrvich.soap.domain.WeatherCondition;
import com.elyashrvich.soap.domain.WeatherDeletionResult;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.exception.WeatherNotFoundException;
import com.elyashrvich.soap.repository.WeatherRepository;
import com.elyashrvich.soap.test_data.WeatherRecordFixtures;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @ParameterizedTest(name = "getWeatherByCity(\"{0}\") returns the repository record for condition {1}")
    @MethodSource("com.elyashrvich.soap.test_data.WeatherRecordFixtures#cityAndConditionPairs")
    void getWeatherByCityRepositoryHasRecord_ReturnsSameRecord(String city, WeatherCondition condition) {
        WeatherRecord expected = WeatherRecordFixtures.of(city, condition);
        when(weatherRepository.findByCity(city)).thenReturn(Optional.of(expected));

        WeatherRecord actual = weatherService.getWeatherByCity(city);

        assertAll(
                () -> assertEquals(expected, actual),
                () -> verify(weatherRepository, times(1)).findByCity(eq(city))
        );
    }

    @ParameterizedTest(name = "getWeatherByCity(\"{0}\") throws when repository has nothing")
    @ValueSource(strings = {"Atlantis", "Neverland"})
    void getWeatherByCityRepositoryHasNoRecord_ThrowsWeatherNotFoundException(String city) {
        when(weatherRepository.findByCity(city)).thenReturn(Optional.empty());

        WeatherNotFoundException exception =
                assertThrows(WeatherNotFoundException.class, () -> weatherService.getWeatherByCity(city));

        assertAll(
                () -> assertThat(exception.getMessage()).contains(city),
                () -> verify(weatherRepository, times(1)).findByCity(eq(city))
        );
    }

    @ParameterizedTest(name = "getWeatherByCity({0}) delegates the raw value to the repository")
    @NullAndEmptySource
    void getWeatherByCityNullOrEmptyCity_DelegatesToRepositoryAndThrows(String city) {
        when(weatherRepository.findByCity(city)).thenReturn(Optional.empty());

        assertThrows(WeatherNotFoundException.class, () -> weatherService.getWeatherByCity(city));
    }

    @ParameterizedTest(name = "deleteWeatherByCity(\"{0}\") returns a SUCCESS result")
    @ValueSource(strings = {"Minsk", "Moscow", "London"})
    void deleteWeatherByCityRepositoryRemovesRecord_ReturnsSuccessResult(String city) {
        when(weatherRepository.deleteByCity(city)).thenReturn(true);

        WeatherDeletionResult result = weatherService.deleteWeatherByCity(city);

        assertAll(
                () -> assertEquals(city, result.city()),
                () -> assertEquals(OperationStatus.SUCCESS, result.status()),
                () -> assertThat(result.message()).contains(city),
                () -> verify(weatherRepository, times(1)).deleteByCity(eq(city))
        );
    }

    @ParameterizedTest(name = "deleteWeatherByCity(\"{0}\") throws when nothing was removed")
    @ValueSource(strings = {"Atlantis", "Neverland"})
    void deleteWeatherByCityRepositoryRemovesNothing_ThrowsWeatherNotFoundException(String city) {
        when(weatherRepository.deleteByCity(city)).thenReturn(false);

        WeatherNotFoundException exception =
                assertThrows(WeatherNotFoundException.class, () -> weatherService.deleteWeatherByCity(city));

        assertAll(
                () -> assertThat(exception.getMessage()).contains(city),
                () -> verify(weatherRepository, times(1)).deleteByCity(eq(city)),
                () -> verify(weatherRepository, never()).findByCity(eq(city))
        );
    }
}
