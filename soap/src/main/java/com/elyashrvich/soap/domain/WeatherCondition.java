package com.elyashrvich.soap.domain;

public enum WeatherCondition {

    CLEAR_SKY("Clear sky"),
    PARTLY_CLOUDY("Partly cloudy"),
    OVERCAST("Overcast"),
    LIGHT_RAIN("Light rain"),
    HEAVY_RAIN("Heavy rain"),
    THUNDERSTORM("Thunderstorm"),
    SNOW("Snow"),
    FOG("Fog");

    private final String description;

    WeatherCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
