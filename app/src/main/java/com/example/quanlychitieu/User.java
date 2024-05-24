package com.example.quanlychitieu;

public class User {
    String name;
    String idUser;
    String urlIMG;
    String email;
    String Water_price;
    String Electricity_price;

    public User() {

    }

    public User(String name, String idUser, String urlIMG, String email, String water_price, String electricity_price) {
        this.name = name;
        this.idUser = idUser;
        this.urlIMG = urlIMG;
        this.email = email;
        Water_price = water_price;
        Electricity_price = electricity_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUrlIMG() {
        return urlIMG;
    }

    public void setUrlIMG(String urlIMG) {
        this.urlIMG = urlIMG;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWater_price() {
        return Water_price;
    }

    public void setWater_price(String water_price) {
        Water_price = water_price;
    }

    public String getElectricity_price() {
        return Electricity_price;
    }

    public void setElectricity_price(String electricity_price) {
        Electricity_price = electricity_price;
    }
}
