package com.jacob.order.service;

import java.math.BigDecimal;
import java.util.List;

public interface ValidationService {
    List<BigDecimal> validateCoordinates(List<String> origin, List<String> destination);
}
