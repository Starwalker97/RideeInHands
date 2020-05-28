package com.example.rideeinhands.models;

public class Vehicle {
    private int position;
    private String id;
    private String RegisterationNumber;
    private String Company;
    private String Model;
    private String Type;

    public Vehicle() {
    }

    public Vehicle(int position, String id, String registerationNumber, String company, String model, String type) {
        this.position = position;
        this.id = id;
        RegisterationNumber = registerationNumber;
        Company = company;
        Model = model;
        Type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisterationNumber() {
        return RegisterationNumber;
    }

    public void setRegisterationNumber(String registerationNumber) {
        RegisterationNumber = registerationNumber;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
