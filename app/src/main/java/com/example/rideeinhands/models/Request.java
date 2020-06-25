package com.example.rideeinhands.models;

public class Request {

    String From;
    Long Passengers;
    String Status;
    String Time;
    String To;
    String TripID;
    Long Price;

    public Request() {
    }

    public Request(String from, Long passengers, String status, String time, String to, String tripID, Long price) {
        From = from;
        Passengers = passengers;
        Status = status;
        Time = time;
        To = to;
        TripID = tripID;
        Price = price;
    }


    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public Long getPassengers() {
        return Passengers;
    }

    public void setPassengers(Long passengers) {
        Passengers = passengers;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        Price = price;
    }
}
