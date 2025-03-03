package hoteltools.hotelparse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hoteltools.HotelCollection;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

public class HotelParser {

    //private TreeMap<String, Hotel> hotelMap;


    /**
     * Parse hotel information from json file then put them in to list
     */
    public void parseHotelJson(String filePath, HotelCollection hotelCollection) {

        String hotelName;
        String hotelID;
        BigDecimal latitude;
        BigDecimal longitude;
        String streetAddress;
        String city;
        String state;
        String country;

        //hotelMap = new TreeMap<>();


        try (FileReader fr = new FileReader(filePath)) {

            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(fr);
            JsonArray hotelQuery = object.getAsJsonArray("sr");
            for (int i = 0; i < hotelQuery.size(); i += 1) {
                JsonObject currentHotel = hotelQuery.get(i).getAsJsonObject();

                hotelName = currentHotel.get("f").getAsString();
                hotelID = currentHotel.get("id").getAsString();
                streetAddress = currentHotel.get("ad").getAsString();
                city = currentHotel.get("ci").getAsString();
                state = currentHotel.get("pr").getAsString();
                country = currentHotel.get("c").getAsString();

                JsonObject globePosition = currentHotel.getAsJsonObject("ll");
                latitude = globePosition.get("lat").getAsBigDecimal();
                longitude = globePosition.get("lng").getAsBigDecimal();

                Hotel newHotel = new Hotel(hotelName, hotelID, latitude, longitude, streetAddress, city, state, country);
                hotelCollection.addHotel(newHotel.getHotelID(), newHotel);

                //hotelMap.put(newHotel.getHotelID(), newHotel);

            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
