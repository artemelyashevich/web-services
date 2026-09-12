package com.elyashrvich.soap.service;

import com.elyashrvich.soap.domain.WeatherDeletionResult;
import com.elyashrvich.soap.domain.WeatherRecord;
import com.elyashrvich.soap.exception.WeatherNotFoundException;

public interface WeatherService {

    /**
     * @throws WeatherNotFoundException if no weather data is stored for the given city
     */
    WeatherRecord getWeatherByCity(String city);

    /**
     * @throws WeatherNotFoundException if no weather data is stored for the given city
     */
    WeatherDeletionResult deleteWeatherByCity(String city);
}
