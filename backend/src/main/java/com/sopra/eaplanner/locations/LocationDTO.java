package com.sopra.eaplanner.locations;

public class LocationDTO {

    private Long id;

    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String country;

    public LocationDTO() {
    }

    public LocationDTO(Long id, String city, String postalCode, String street, String houseNumber, String country) {
        this.id = id;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.houseNumber = houseNumber;
        this.country = country;
    }

    public LocationDTO(Location location) {
        this.id = location.getId();
        this.city = location.getCity();
        this.postalCode = location.getPostalCode();
        this.street = location.getStreet();
        this.houseNumber = location.getHouseNumber();
        this.country = location.getCountry();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
