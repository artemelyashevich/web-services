package com.elyashrvich.soap.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "weather", propOrder = {
        "city", "temperatureCelsius", "humidityPercent", "windSpeedKph", "description", "updatedAt"
})
public class Weather {

    private String city;
    private double temperatureCelsius;
    private int humidityPercent;
    private double windSpeedKph;
    private String description;
    private String updatedAt;
}
