package com.example.quickrentals;

public class Cars
{
    //Model class for data population
    private String carName;
    private String carPrice;
    private String carImage;

    public Cars(String carName, String carPrice, String carImage) {
        this.carName = carName;
        this.carPrice = carPrice;
        this.carImage = carImage;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(String carPrice) {
        this.carPrice = carPrice;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }
}
