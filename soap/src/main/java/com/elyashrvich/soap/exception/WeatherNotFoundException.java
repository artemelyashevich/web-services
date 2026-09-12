package com.elyashrvich.soap.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class WeatherNotFoundException extends RuntimeException {

    public WeatherNotFoundException(String message) {
        super(message);
    }
}
