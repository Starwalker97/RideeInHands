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
    Integer NumberOfPassengers;

    public TripModel() {
    }

    public TripModel(String name, String detail, String start, String destination, String startLocation, String destinationLocation, String date, String time, String route, String rideHolder, String tripID, Integer numberOfPassengers) {
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

    public String getTripId() {
        return TripID;
    }

    public void setTripId(String tripID) {
        TripID = tripID;
    }

    public Integer getNumberOfPassengers() {
        return NumberOfPassengers;
    }

    public void setNumberOfPassengers(Integer numberOfPassengers) {
        NumberOfPassengers = numberOfPassengers;
    }
}
