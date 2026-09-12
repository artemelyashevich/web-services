package com.elyashrvich.soap.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "deleteWeatherByCityRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteWeatherByCityRequest {

    private String city;
}
