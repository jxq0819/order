package com.jacob.order.entity;

public class OrderPlaceVO {

    private String status;

    public OrderPlaceVO(String status) {
        this.status = status;
    }

    public OrderPlaceVO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
