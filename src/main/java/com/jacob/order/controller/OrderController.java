package com.jacob.order.controller;

import com.jacob.order.entity.OrderCreationVO;
import com.jacob.order.entity.OrderPlaceVO;
import com.jacob.order.entity.OrderStatusVO;
import com.jacob.order.entity.OrderVO;
import com.jacob.order.exception.ErrorResponse;
import com.jacob.order.service.OrderService;
import com.jacob.order.service.ValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ValidationService validationService;

    @PostMapping("")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid OrderCreationVO orderCreationVO) {
        try {
            List<String> origin = orderCreationVO.getOrigin();
            List<String> destination = orderCreationVO.getDestination();
            List<BigDecimal> coordinates = validationService.validateCoordinates(origin, destination);
            long createdOrderId = orderService.createOrder(
                    coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3)
            );

            if (createdOrderId > 0) {
                OrderVO createdOrder = orderService.getOrderById(createdOrderId);
                return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new ErrorResponse("Order creation failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ErrorResponse("Order coordinates are not valid: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse("An unexpected error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> takeOrder(@PathVariable long id, @RequestBody @Valid OrderPlaceVO orderPlaceVO) {
        try {
            if (orderService.takeOrder(id, orderPlaceVO.getStatus()) > 0) {
                return new ResponseEntity<>(
                        new OrderStatusVO("SUCCESS"),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        new ErrorResponse("This order has been taken or does not exist."),
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse("An unexpected error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("")
    public ResponseEntity<?> listOrders(@RequestParam @Min(value = 1L) long page, @RequestParam @Min(value = 1L) long limit) {
        try {
            List<OrderVO> orderList = orderService.listOrders(page, limit);
            return new ResponseEntity<>(orderList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse("An unexpected error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}