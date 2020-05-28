package com.example.rideeinhands.adminmodels;

public class License {
    String Picture;
    String Status;

    public License() {
    }

    public License(String picture, String status) {
        Picture = picture;
        Status = status;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
