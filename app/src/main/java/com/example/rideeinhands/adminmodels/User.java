package com.example.rideeinhands.adminmodels;

public class User {
    String Name;
    String ProfilePicture;
    String Address;
    String DateOfBirth;
    String EmailAddress;
    String MobileNumber;
    String Role;
    String Disabled;

    public User() {
    }

    public User(String name, String profilePicture, String address, String dateOfBirth, String emailAddress, String mobileNumber, String role, String disabled) {
        Name = name;
        ProfilePicture = profilePicture;
        Address = address;
        DateOfBirth = dateOfBirth;
        EmailAddress = emailAddress;
        MobileNumber = mobileNumber;
        Role = role;
        Disabled = disabled;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getDisabled() {
        return Disabled;
    }

    public void setDisabled(String disabled) {
        Disabled = disabled;
    }
}
