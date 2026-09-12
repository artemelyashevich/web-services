package com.elyashrvich.soap.domain;

public record WeatherDeletionResult(String city, OperationStatus status, String message) {
}
