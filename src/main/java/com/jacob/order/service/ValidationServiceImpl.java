package com.jacob.order.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {
    public List<BigDecimal> validateCoordinates(List<String> origin, List<String> destination) {

        if (origin == null || origin.size() != 2
                || destination == null || destination.size() != 2) {
            throw new IllegalArgumentException("Coordinates are not in valid size");
        }

        try {
            BigDecimal originLatitude = new BigDecimal(origin.get(0));
            BigDecimal originLongitude = new BigDecimal(origin.get(1));
            BigDecimal destinationLatitude = new BigDecimal(destination.get(0));
            BigDecimal destinationLongitude = new BigDecimal(destination.get(1));

            boolean isValid = originLatitude.compareTo(new BigDecimal(-90)) >= 0
                    && originLatitude.compareTo(new BigDecimal(90)) <= 0
                    && originLongitude.compareTo(new BigDecimal(-180)) >= 0
                    && originLongitude.compareTo(new BigDecimal(180)) <= 0
                    && destinationLatitude.compareTo(new BigDecimal(-90)) >= 0
                    && destinationLatitude.compareTo(new BigDecimal(90)) <= 0
                    && destinationLongitude.compareTo(new BigDecimal(-180)) >= 0
                    && destinationLongitude.compareTo(new BigDecimal(180)) <= 0;

            if (!isValid) {
                throw new IllegalArgumentException("Coordinates are out of valid range");
            }

            return List.of(originLatitude, originLongitude, destinationLatitude, destinationLongitude);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid numeric values");
        }
    }
}