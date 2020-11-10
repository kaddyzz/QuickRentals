package com.example.quickrentals.ModelClasses;

import java.io.Serializable;

public class Cars implements Serializable
{
    //Model class for data population
    public String carID;
    public String carMake;
    public String carModel;
    public String carImage;
    public String carLogo;
    public String carCylinder;
    public String carFuelEconomy;
    public String carHorsePower;
    public String carSeats;
    public String carSpeed;
    public String carType;
    public String carPrice;

    public Cars()
    {}

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getCarID() {
        return carID;
    }


    public String getCarMake() {
        return carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarLogo() {
        return carLogo;
    }

    public String getCarImage() {
        return carImage;
    }

    public String getCarCylinder() {
        return carCylinder;
    }

    public String getCarFuelEconomy() {
        return carFuelEconomy;
    }

    public String getCarHorsePower() {
        return carHorsePower;
    }

    public String getCarSeats() {
        return carSeats;
    }

    public String getCarSpeed() {
        return carSpeed;
    }

    public String getCarType() {
        return carType;
    }

    public String getCarPrice() {
        return carPrice;
    }
}
