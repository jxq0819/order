package com.jacob.order.mapper;

import com.jacob.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface OrderMapper {

    int createOrder(Order order);

    int updateOrderStatus(@Param("id") long id, @Param("status") String status);

    List<Order> listOrders(@Param("page") long page, @Param("limit") long limit);

    Order getOrderById(@Param("id") long id);
}

