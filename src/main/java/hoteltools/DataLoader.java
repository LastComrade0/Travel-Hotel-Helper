package hoteltools;

import hoteltools.hotelparse.HotelParser;
import hoteltools.reviewparse.ReviewCollectionLoader;

import java.nio.file.Paths;

/**
 Class to parse argument and load Json Data
 **/
public class DataLoader {
    /**
     * Method to parse argument and load Json Data to hotelCollection and reviewCollectionsThreadSafe
     */
    public static void loadData(String[] args, HotelCollection hotelCollection , HotelParser hotelParser, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe, ReviewCollectionLoader reviewCollectionLoader){
        boolean hotelFlag = false;
        boolean reviewFlag = false;
        int threadCount = 1;

        for (int argIndex = 0; argIndex < args.length; argIndex+=2) {
            if(args[argIndex].equals("-threads")) {
                threadCount = Integer.parseInt(args[argIndex+1]);
            }
        }

        for (int argIndex = 0; argIndex < args.length; argIndex+=2) {

            //System.out.println(args[argIndex]);

            switch (args[argIndex]) {
                //Single thread
                case ("-hotels"):
                    hotelFlag = true;
                    hotelParser.parseHotelJson(args[argIndex+1], hotelCollection);
                    continue;

                    //Multi thread
                case ("-reviews"):
                    //Call review thread safe then get data from review parser
                    reviewFlag = true;
                    reviewCollectionLoader = new ReviewCollectionLoader(reviewCollectionsThreadSafe, threadCount);
                    reviewCollectionLoader.traverseDirectory(Paths.get(args[argIndex+1]));
                    reviewCollectionLoader.waitToFinish();
                    continue;

                case ("-threads"):

                    continue;


                default:
                    throw new RuntimeException("Unknown argument: " + args[argIndex]);

            }
        }
    }
}
