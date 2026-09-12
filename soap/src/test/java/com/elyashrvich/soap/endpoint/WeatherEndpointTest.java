package com.elyashrvich.soap.endpoint;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elyashrvich.soap.domain.OperationStatus;
import com.elyashrvich.soap.domain.WeatherCondition;
import com.elyashrvich.soap.domain.WeatherDeletionResult;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.exception.WeatherNotFoundException;
import com.elyashrvich.soap.service.WeatherService;
import com.elyashrvich.soap.test_data.WeatherRecordFixtures;
import com.elyashrvich.soap.wsdl.DeleteWeatherByCityRequest;
import com.elyashrvich.soap.wsdl.DeleteWeatherByCityResponse;
import com.elyashrvich.soap.wsdl.GetWeatherByCityRequest;
import com.elyashrvich.soap.wsdl.GetWeatherByCityResponse;
import com.elyashrvich.soap.wsdl.Weather;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherEndpointTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherEndpoint weatherEndpoint;

    @ParameterizedTest(name = "getWeatherByCity maps a {1} WeatherRecord onto the XML Weather DTO")
    @MethodSource("com.elyashrvich.soap.test_data.WeatherRecordFixtures#cityAndConditionPairs")
    void getWeatherByCityServiceReturnsRecord_MapsAllFieldsOntoResponse(String city, WeatherCondition condition) {
        WeatherRecord record = WeatherRecordFixtures.of(city, condition);
        when(weatherService.getWeatherByCity(city)).thenReturn(record);

        GetWeatherByCityRequest request = new GetWeatherByCityRequest();
        request.setCity(city);

        GetWeatherByCityResponse response = weatherEndpoint.getWeatherByCity(request);
        Weather weather = response.getWeather();

        assertAll(
                () -> assertEquals(record.city(), weather.getCity()),
                () -> assertEquals(record.temperatureCelsius(), weather.getTemperatureCelsius()),
                () -> assertEquals(record.humidityPercent(), weather.getHumidityPercent()),
                () -> assertEquals(record.windSpeedKph(), weather.getWindSpeedKph()),
                () -> assertEquals(condition.getDescription(), weather.getDescription()),
                () -> assertEquals(record.updatedAt().toString(), weather.getUpdatedAt()),
                () -> verify(weatherService, times(1)).getWeatherByCity(eq(city))
        );
    }

    @ParameterizedTest(name = "getWeatherByCity(\"{0}\") propagates WeatherNotFoundException from the service")
    @ValueSource(strings = {"Atlantis", "Neverland"})
    void getWeatherByCityServiceThrowsNotFound_PropagatesException(String city) {
        when(weatherService.getWeatherByCity(city)).thenThrow(new WeatherNotFoundException("not found: " + city));

        GetWeatherByCityRequest request = new GetWeatherByCityRequest();
        request.setCity(city);

        assertThrows(WeatherNotFoundException.class, () -> weatherEndpoint.getWeatherByCity(request));
    }

    @ParameterizedTest(name = "deleteWeatherByCity maps a {0} result onto the XML response")
    @EnumSource(OperationStatus.class)
    void deleteWeatherByCityServiceReturnsResult_MapsAllFieldsOntoResponse(OperationStatus status) {
        String city = "Minsk";
        WeatherDeletionResult result = new WeatherDeletionResult(city, status, "message for " + city);
        when(weatherService.deleteWeatherByCity(city)).thenReturn(result);

        DeleteWeatherByCityRequest request = new DeleteWeatherByCityRequest();
        request.setCity(city);

        DeleteWeatherByCityResponse response = weatherEndpoint.deleteWeatherByCity(request);

        assertAll(
                () -> assertEquals(result.city(), response.getCity()),
                () -> assertEquals(status.name(), response.getStatus()),
                () -> assertEquals(result.message(), response.getMessage()),
                () -> verify(weatherService, times(1)).deleteWeatherByCity(eq(city))
        );
    }

    @ParameterizedTest(name = "deleteWeatherByCity(\"{0}\") propagates WeatherNotFoundException from the service")
    @ValueSource(strings = {"Atlantis", "Neverland"})
    void deleteWeatherByCityServiceThrowsNotFound_PropagatesException(String city) {
        when(weatherService.deleteWeatherByCity(city))
                .thenThrow(new WeatherNotFoundException("Cannot delete: not found: " + city));

        DeleteWeatherByCityRequest request = new DeleteWeatherByCityRequest();
        request.setCity(city);

        assertThrows(WeatherNotFoundException.class, () -> weatherEndpoint.deleteWeatherByCity(request));
    }
}
