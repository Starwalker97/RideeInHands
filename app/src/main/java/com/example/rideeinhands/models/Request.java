package com.example.rideeinhands.models;

public class Request {

    String userId;
    String status;
    String dateTime;
    String pickupPoint;

    public Request(String userId, String status, String dateTime, String pickupPoint) {
        this.userId = userId;
        this.status = status;
        this.dateTime = dateTime;
        this.pickupPoint = pickupPoint;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
