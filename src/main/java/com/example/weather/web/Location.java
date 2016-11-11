package com.example.weather.web;


public class Location {
    private final String country;
    private final String city;

    public Location(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
