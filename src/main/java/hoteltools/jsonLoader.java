package hoteltools;

import database.DatabaseHandler;
import database.HotelDatabaseLoader;
import hoteltools.hotelparse.Hotel;
import hoteltools.hotelparse.HotelParser;
import hoteltools.reviewparse.HotelReview;
import hoteltools.reviewparse.ReviewCollectionLoader;

import java.sql.SQLException;
import java.util.*;

public class jsonLoader {
    public static void main(String[] args) throws SQLException {
        HotelCollection hotelCollection = new HotelCollection();
        HotelParser hotelParser = new HotelParser();
        ReviewCollectionsThreadSafe reviewCollectionsThreadSafe = new ReviewCollectionsThreadSafe();
        ReviewCollectionLoader reviewCollectionLoader = null;
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        DataLoader.loadData(args, hotelCollection, hotelParser, reviewCollectionsThreadSafe, reviewCollectionLoader);

        HotelDatabaseLoader hotelDatabaseLoader = HotelDatabaseLoader.getInstance();

        TreeMap<String, Hotel> hotelMap = hotelCollection.getHotelmap();

        Map<String, TreeSet<HotelReview>> reviewMap = reviewCollectionsThreadSafe.getReviewMap();

        List<String> recordedName = new ArrayList<>();

        for(Map.Entry<String, Hotel> hotel : hotelMap.entrySet()) {
            Hotel currentHotel = hotel.getValue();
            //System.out.println("Hotel: " + currentHotel);
            hotelDatabaseLoader.addHotel(currentHotel.getHotelID(), currentHotel.getHotelName(), currentHotel.getStreetAddress(),
                    currentHotel.getCity(), currentHotel.getState(), currentHotel.getLatitude(), currentHotel.getLongitude());
        }

        for(Map.Entry<String, TreeSet<HotelReview>> hotelIDEntry: reviewMap.entrySet()){
            //System.out.println(hotelIDEntry);
            for(HotelReview hotelReview: hotelIDEntry.getValue()){
                hotelDatabaseLoader.addReview(hotelReview.getReviewID(), hotelReview.getHotelID(), hotelReview.getAverageRating()
                , hotelReview.getTitle(), hotelReview.getUserNickname(), hotelReview.getReviewText(), hotelReview.getSubmissionDate());

                if(!recordedName.contains(hotelReview.getUserNickname().toLowerCase().trim())){
                    String newName = hotelReview.getUserNickname().toLowerCase().trim();
                    recordedName.add(newName);
                    databaseHandler.registerUser(newName, "Chen2001517!!");
                }


            }
        }
    }
}
