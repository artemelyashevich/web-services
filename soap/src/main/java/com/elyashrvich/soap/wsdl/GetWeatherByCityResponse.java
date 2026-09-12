package com.elyashrvich.soap.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "getWeatherByCityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetWeatherByCityResponse {

    private Weather weather;
}
