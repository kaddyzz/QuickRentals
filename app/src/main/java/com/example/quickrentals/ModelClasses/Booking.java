package com.example.quickrentals.ModelClasses;

import java.io.Serializable;

public class Booking implements Serializable {

    public String userID;
    public String userName;

    public String carImage;
    public String carMake;
    public String carModel;
    public String carPricePerDay;

    public String addOns;
    public String addOnsPrice;

    public String selectedLocation;
    public String paymentStatus;
    public String pickUpDate;
    public String returnDate;
    public String noOfDays;

    public String finalPrice;

    public String bookingStatus;

    //Empty constructor
    public Booking() {
    }

    //Getters
    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public String getUserID() {
        return userID;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public String getCarImage() {
        return carImage;
    }

    public String getCarMake() {
        return carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarPricePerDay() {
        return carPricePerDay;
    }

    public String getAddOns() {
        return addOns;
    }

    public String getAddOnsPrice() {
        return addOnsPrice;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getUserName() {
        return userName;
    }

    //Setters
    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public void setCarPricePerDay(String carPricePerDay) {
        this.carPricePerDay = carPricePerDay;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }

    public void setAddOnsPrice(String addOnsPrice) {
        this.addOnsPrice = addOnsPrice;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}


