package com.jacob.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceImplTest {

    private ValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl();
    }

    @Test
    void testValidateCoordinatesSuccess() {
        List<String> origin = List.of("40.7128", "-74.0060");
        List<String> destination = List.of("34.0522", "-118.2437");

        List<BigDecimal> result = validationService.validateCoordinates(origin, destination);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(new BigDecimal("40.7128"), result.get(0));
        assertEquals(new BigDecimal("-74.0060"), result.get(1));
        assertEquals(new BigDecimal("34.0522"), result.get(2));
        assertEquals(new BigDecimal("-118.2437"), result.get(3));
    }

    @Test
    void testValidateCoordinatesFailNullOrigin() {
        List<String> destination = List.of("34.0522", "-118.2437");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(null, destination));

        assertEquals("Coordinates are not in valid size", exception.getMessage());
    }

    @Test
    void testValidateCoordinatesFailNullDestination() {
        List<String> origin = List.of("40.7128", "-74.0060");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(origin, null));

        assertEquals("Coordinates are not in valid size", exception.getMessage());
    }

    @Test
    void testValidateCoordinatesFailInvalidSizeOrigin() {
        List<String> origin = List.of("40.7128");
        List<String> destination = List.of("34.0522", "-118.2437");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(origin, destination));

        assertEquals("Coordinates are not in valid size", exception.getMessage());
    }

    @Test
    void testValidateCoordinatesFailInvalidSizeDestination() {
        List<String> origin = List.of("40.7128", "-74.0060");
        List<String> destination = List.of("34.0522");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(origin, destination));

        assertEquals("Coordinates are not in valid size", exception.getMessage());
    }

    @Test
    void testValidateCoordinatesFailNonNumeric() {
        List<String> origin = List.of("40.7128", "invalid");
        List<String> destination = List.of("34.0522", "-118.2437");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(origin, destination));

        assertEquals("Coordinates must be valid numeric values", exception.getMessage());
    }

    @Test
    void testValidateCoordinatesFailOutOfRange() {
        List<String> origin = List.of("100.0000", "-74.0060");
        List<String> destination = List.of("34.0522", "-118.2437");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateCoordinates(origin, destination));

        assertEquals("Coordinates are out of valid range", exception.getMessage());
    }
}