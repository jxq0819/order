package com.jacob.order.service;


import com.jacob.order.config.GoogleMapConfig;
import com.jacob.order.entity.Order;
import com.jacob.order.entity.OrderVO;
import com.jacob.order.mapper.OrderMapper;
import com.jacob.order.utils.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Autowired
    private GoogleMapConfig googleMapConfig;

    @Override
    @Transactional
    public Long createOrder(BigDecimal originLatitude, BigDecimal originLongitude, BigDecimal destinationLatitude, BigDecimal destinationLongitude) {
        int distance = distanceCalculator.getDistance(originLatitude, originLongitude, destinationLatitude, destinationLongitude, googleMapConfig.getApiKey());
        if (distance < 0) {
            throw new RuntimeException("Failed to get distance from Google Map API");
        }

        Order order = new Order();
        order.setOriginLatitude(originLatitude);
        order.setOriginLongitude(originLongitude);
        order.setDestinationLatitude(destinationLatitude);
        order.setDestinationLongitude(destinationLongitude);
        order.setStatus("UNASSIGNED");
        order.setDistance(distance);
        int count = orderMapper.createOrder(order);
        if (count <= 0) {
            throw new RuntimeException("Failed to create order");
        }
        return order.getId();
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int takeOrder(long id, String status) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            return 0;
        }
        if (order.getStatus() == null || "TAKEN".equals(order.getStatus())) {
            return 0;
        }
        return orderMapper.updateOrderStatus(id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderVO> listOrders(long page, long limit) {
        List<Order> orderList = orderMapper.listOrders((page - 1) * limit, limit);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            orderVOList.add(new OrderVO(order.getId(), order.getDistance(), order.getStatus()));
        }
        return orderVOList;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderVO getOrderById(long id) {
        Order order = orderMapper.getOrderById(id);
        return new OrderVO(order.getId(), order.getDistance(), order.getStatus());
    }
}
