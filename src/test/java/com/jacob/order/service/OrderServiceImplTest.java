package com.jacob.order.service;

import com.jacob.order.config.GoogleMapConfig;
import com.jacob.order.entity.Order;
import com.jacob.order.entity.OrderVO;
import com.jacob.order.mapper.OrderMapper;
import com.jacob.order.utils.DistanceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private GoogleMapConfig googleMapConfig;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderSuccess() {
        when(distanceCalculator.getDistance(any(), any(), any(), any(), any())).thenReturn(100);
        when(orderMapper.createOrder(any(Order.class))).thenReturn(1);
        when(googleMapConfig.getApiKey()).thenReturn("dummyApiKey");

        BigDecimal originLatitude = new BigDecimal("40.7128");
        BigDecimal originLongitude = new BigDecimal("-74.0060");
        BigDecimal destinationLatitude = new BigDecimal("34.0522");
        BigDecimal destinationLongitude = new BigDecimal("-118.2437");

        Long orderId = orderService.createOrder(originLatitude, originLongitude, destinationLatitude, destinationLongitude);

        assertNotNull(orderId);
        verify(orderMapper, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrderFailDistance() {
        when(distanceCalculator.getDistance(any(), any(), any(), any(), any())).thenReturn(-1);
        when(googleMapConfig.getApiKey()).thenReturn("dummyApiKey");

        BigDecimal originLatitude = new BigDecimal("40.7128");
        BigDecimal originLongitude = new BigDecimal("-74.0060");
        BigDecimal destinationLatitude = new BigDecimal("34.0522");
        BigDecimal destinationLongitude = new BigDecimal("-118.2437");

        assertThrows(RuntimeException.class, () -> orderService.createOrder(originLatitude, originLongitude, destinationLatitude, destinationLongitude));
    }

    @Test
    void testCreateOrderFailCreate() {
        when(distanceCalculator.getDistance(any(), any(), any(), any(), any())).thenReturn(100);
        when(orderMapper.createOrder(any(Order.class))).thenReturn(0);
        when(googleMapConfig.getApiKey()).thenReturn("dummyApiKey");

        BigDecimal originLatitude = new BigDecimal("40.7128");
        BigDecimal originLongitude = new BigDecimal("-74.0060");
        BigDecimal destinationLatitude = new BigDecimal("34.0522");
        BigDecimal destinationLongitude = new BigDecimal("-118.2437");

        assertThrows(RuntimeException.class, () -> orderService.createOrder(originLatitude, originLongitude, destinationLatitude, destinationLongitude));
    }

    @Test
    void testTakeOrderSuccess() {
        Order order = new Order();
        order.setStatus("UNASSIGNED");
        when(orderMapper.getOrderById(anyLong())).thenReturn(order);
        when(orderMapper.updateOrderStatus(anyLong(), anyString())).thenReturn(1);

        int result = orderService.takeOrder(1L, "TAKEN");

        assertEquals(1, result);
        verify(orderMapper, times(1)).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testTakeOrderFailNotFound() {
        when(orderMapper.getOrderById(anyLong())).thenReturn(null);

        int result = orderService.takeOrder(1L, "TAKEN");

        assertEquals(0, result);
        verify(orderMapper, never()).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testTakeOrderFailAlreadyTaken() {
        Order order = new Order();
        order.setStatus("TAKEN");
        when(orderMapper.getOrderById(anyLong())).thenReturn(order);

        int result = orderService.takeOrder(1L, "TAKEN");

        assertEquals(0, result);
        verify(orderMapper, never()).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void testListOrders() {
        Order order = new Order();
        order.setId(1L);
        order.setDistance(100);
        order.setStatus("UNASSIGNED");
        when(orderMapper.listOrders(anyLong(), anyLong())).thenReturn(List.of(order));

        List<OrderVO> orders = orderService.listOrders(1, 10);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderMapper, times(1)).listOrders(anyLong(), anyLong());
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        order.setId(1L);
        order.setDistance(100);
        order.setStatus("UNASSIGNED");
        when(orderMapper.getOrderById(anyLong())).thenReturn(order);

        OrderVO orderVO = orderService.getOrderById(1L);

        assertNotNull(orderVO);
        assertEquals(1L, orderVO.getId());
        verify(orderMapper, times(1)).getOrderById(anyLong());
    }
}