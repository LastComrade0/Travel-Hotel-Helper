package hoteltools.hotelparse;

import java.math.BigDecimal;

public class Hotel {

    private String hotelName;
    private String hotelID;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String streetAddress;
    private String city;
    private String state;
    private String country;

    /**
     *
     * Constructor of hotel
     */
    public Hotel(String hotelName, String hotelID, BigDecimal latitude, BigDecimal longitude, String streetAddress, String city, String state, String country) {
        this.hotelName = hotelName;
        this.hotelID = hotelID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.country = country;

    }

    /**
     * Getters
     */
    public String getHotelName(){
        return hotelName;
    }

    public String getHotelID(){
        return hotelID;
    }

    public BigDecimal getLatitude(){
        return latitude;
    }

    public BigDecimal getLongitude(){
        return longitude;
    }

    public String getStreetAddress(){
        return streetAddress;
    }

    public String getCity(){
        return city;
    }

    public String getState(){
        return state;
    }

    /**
     * Method to return as string
     */
    @Override
    public String toString() {
        return  hotelName + ": " + hotelID + System.lineSeparator() +
                streetAddress + System.lineSeparator() +
                city + ", " + state + System.lineSeparator() ;

    }
}
