package com.example.rideeinhands.models;

public class TripModel {

    String Name;
    String Detail;
    String Start;
    String Destination;
    String StartLocation;
    String DestinationLocation;
    String Date;
    String Time;
    String Route;
    String userId;
    String tripId;

    public TripModel() {
    }

    public TripModel(String name, String detail, String start, String destination, String startLocation, String destinationLocation, String date, String time, String route) {
        Name = name;
        Detail = detail;
        Start = start;
        Destination = destination;
        StartLocation = startLocation;
        DestinationLocation = destinationLocation;
        Date = date;
        Time = time;
        Route = route;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public void setStartLocation(String startLocation) {
        StartLocation = startLocation;
    }

    public String getDestinationLocation() {
        return DestinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        DestinationLocation = destinationLocation;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }
}
