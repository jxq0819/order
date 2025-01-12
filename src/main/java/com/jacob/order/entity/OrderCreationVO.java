package com.jacob.order.entity;

import java.util.List;

public class OrderCreationVO {

    private List<String> origin;
    private List<String> destination;

    public OrderCreationVO(List<String> origin, List<String> destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public OrderCreationVO() {
    }

    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }
}
