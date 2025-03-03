package hoteltools.searchhotelbyID;

import java.util.TreeMap;

public class HotelSearcher {
    /**
     * Method to query hotel id
     */
    public String searchHotelbyID(String hotelID, TreeMap hotelMap) {
        //System.out.println("Searching " + hotelID);
        try {
            String finalResult = hotelMap.get(hotelID).toString();
            return finalResult;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Hotel ID not found");
        }

    }
}
