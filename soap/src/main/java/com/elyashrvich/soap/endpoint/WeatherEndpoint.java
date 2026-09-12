package com.elyashrvich.soap.endpoint;

import com.elyashrvich.soap.domain.WeatherDeletionResult;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.service.WeatherService;
import com.elyashrvich.soap.wsdl.DeleteWeatherByCityRequest;
import com.elyashrvich.soap.wsdl.DeleteWeatherByCityResponse;
import com.elyashrvich.soap.wsdl.GetWeatherByCityRequest;
import com.elyashrvich.soap.wsdl.GetWeatherByCityResponse;
import com.elyashrvich.soap.wsdl.Weather;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
@RequiredArgsConstructor
public class WeatherEndpoint {

    private static final String NAMESPACE_URI = "http://elyashrvich.com/soap/weather";

    private final WeatherService weatherService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWeatherByCityRequest")
    @ResponsePayload
    public GetWeatherByCityResponse getWeatherByCity(@RequestPayload GetWeatherByCityRequest request) {
        log.info("Received getWeatherByCity request for city '{}'", request.getCity());
        WeatherRecord weather = weatherService.getWeatherByCity(request.getCity());
        GetWeatherByCityResponse response = new GetWeatherByCityResponse();
        response.setWeather(toXml(weather));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteWeatherByCityRequest")
    @ResponsePayload
    public DeleteWeatherByCityResponse deleteWeatherByCity(@RequestPayload DeleteWeatherByCityRequest request) {
        log.info("Received deleteWeatherByCity request for city '{}'", request.getCity());
        WeatherDeletionResult result = weatherService.deleteWeatherByCity(request.getCity());
        DeleteWeatherByCityResponse response = new DeleteWeatherByCityResponse();
        response.setCity(result.city());
        response.setStatus(result.status().name());
        response.setMessage(result.message());
        return response;
    }

    private Weather toXml(WeatherRecord record) {
        Weather weather = new Weather();
        weather.setCity(record.city());
        weather.setTemperatureCelsius(record.temperatureCelsius());
        weather.setHumidityPercent(record.humidityPercent());
        weather.setWindSpeedKph(record.windSpeedKph());
        weather.setDescription(record.condition().getDescription());
        weather.setUpdatedAt(record.updatedAt().toString());
        return weather;
    }
}
