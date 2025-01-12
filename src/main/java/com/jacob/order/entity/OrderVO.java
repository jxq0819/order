package com.jacob.order.entity;

public class OrderVO {

    private long id;


    private int distance;


    private String status;

    public OrderVO(long id, int distance, String status) {
        this.id = id;
        this.distance = distance;
        this.status = status;
    }

    public OrderVO() {
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
