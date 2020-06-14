package com.example.rideeinhands.models;

public class Request {

    String From;
    Integer Passengers;
    String Status;
    String Time;
    String To;
    String TripID;

    public Request() {
    }

    public Request(String from, Integer passengers, String status, String time, String to, String tripID) {
        From = from;
        Passengers = passengers;
        Status = status;
        Time = time;
        To = to;
        TripID = tripID;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public Integer getPassengers() {
        return Passengers;
    }

    public void setPassengers(Integer passengers) {
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
}
