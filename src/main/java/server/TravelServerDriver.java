package server;

import hoteltools.DataLoader;
import hoteltools.HotelCollection;
import hoteltools.ReviewCollectionsThreadSafe;
import hoteltools.hotelparse.HotelParser;
import hoteltools.reviewparse.ReviewCollectionLoader;
/*
Main class for server driver
 */
public class TravelServerDriver {
    public static final int PORT = 8080;

    public static void main(String[] args)  {
        // FILL IN CODE, and add more classes as needed
        HotelCollection hotelCollection = new HotelCollection();
        HotelParser hotelParser = new HotelParser();
        ReviewCollectionsThreadSafe reviewCollectionsThreadSafe = new ReviewCollectionsThreadSafe();
        ReviewCollectionLoader reviewCollectionLoader = null;

        DataLoader.loadData(args, hotelCollection, hotelParser, reviewCollectionsThreadSafe, reviewCollectionLoader);

        TravelServer travelServer = new TravelServer(hotelCollection, reviewCollectionsThreadSafe, PORT);
        try{
            travelServer.start();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
