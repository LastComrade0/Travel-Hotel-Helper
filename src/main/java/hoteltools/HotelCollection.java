package hoteltools;

import hoteltools.hotelparse.Hotel;

import java.util.TreeMap;

/**
 * Class to store hotelMap
 */
public class HotelCollection {
    private final TreeMap<String, Hotel> hotelMap = new TreeMap<>();

    /**
     * Method to add hotel
     */
    public void addHotel(String hotelID, Hotel hotel){
        hotelMap.put(hotelID, hotel);
    }

    /**
     * Method to search hotel by id
     */
    public Hotel searchHotel(String hotelID){
        if(hotelMap.containsKey(hotelID)){
            return hotelMap.get(hotelID);
        }
        return null;
    }

    public TreeMap<String, Hotel> getHotelmap(){
        System.out.println("Hotel count: " + hotelMap.size());

        return hotelMap;
    }


}
