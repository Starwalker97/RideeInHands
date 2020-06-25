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
    String RideHolder;
    String TripID;
    String Status;
    Long NumberOfPassengers;

    public TripModel() {
    }

    public TripModel(String name, String detail, String start, String destination, String startLocation, String destinationLocation, String date, String time, String route, String rideHolder, String tripID, String status, Long numberOfPassengers) {
        Name = name;
        Detail = detail;
        Start = start;
        Destination = destination;
        StartLocation = startLocation;
        DestinationLocation = destinationLocation;
        Date = date;
        Time = time;
        Route = route;
        RideHolder = rideHolder;
        TripID = tripID;
        Status = status;
        NumberOfPassengers = numberOfPassengers;
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

    public String getRideHolder() {
        return RideHolder;
    }

    public void setRideHolder(String rideHolder) {
        RideHolder = rideHolder;
    }

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Long getNumberOfPassengers() {
        return NumberOfPassengers;
    }

    public void setNumberOfPassengers(Long numberOfPassengers) {
        NumberOfPassengers = numberOfPassengers;
    }
}
