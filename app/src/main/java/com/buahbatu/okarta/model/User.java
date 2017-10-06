package com.buahbatu.okarta.model;

/**
 * model for users
 */

public class User {
    public String username;
    public String fullName;
    public String carType;
    public String carPlat;
    public String sensorId;

    public User() {
    }

    public User(String username, String fullName, String carType, String carPlat, String sensorId) {
        this.username = username;
        this.fullName = fullName;
        this.carType = carType;
        this.carPlat = carPlat;
        this.sensorId = sensorId;
    }
}
