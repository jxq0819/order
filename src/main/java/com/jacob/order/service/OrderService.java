package com.jacob.order.service;


import com.jacob.order.entity.OrderVO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Long createOrder(BigDecimal originLatitude, BigDecimal originLongitude, BigDecimal destinationLatitude, BigDecimal destinationLongitude);

    int takeOrder(long id, String status);

    List<OrderVO> listOrders(long page, long limit);

    OrderVO getOrderById(long id);
}
