package hoteltools;

import hoteltools.reviewparse.HotelReview;
import hoteltools.reviewparse.ReviewParser;
import hoteltools.reviewparse.ReviewWithFrequency;
import hoteltools.searchReview.ReviewSearcher;

import java.util.*;

/**
 * Class to store review collections
 */
public class ReviewCollections {
    private final Map<String, TreeSet<HotelReview>> hotelIdMap = new HashMap<>(); //<Hotel Id, reviews(Tree set)>
    private final Map<String, TreeSet<ReviewWithFrequency>> keyWordHashMap = new HashMap<>(); //<Keyword, reviews(Tree set)>


    /**
     * Method to iterate through parsed list of reviews and add each of them to review hashmap
     * Calls private method addReviewWithFrequency to add that review to inverted index
     */
    public void addReview(List<HotelReview> reviewList) {
        for(HotelReview review : reviewList) {
            String currentHotelId = review.getHotelID();
            if(!hotelIdMap.containsKey(currentHotelId)){

                hotelIdMap.put(currentHotelId, new TreeSet<>());

            }

            hotelIdMap.get(currentHotelId).add(review);

            Map<String, Integer> wordCountMap = ReviewParser.parseReviewWords(review);
            addReviewWithFrequency(review, wordCountMap);
        }

    }

    public void addReview(HotelReview review) {
        String currentHotelId = review.getHotelID();
        if(!hotelIdMap.containsKey(currentHotelId)){

            hotelIdMap.put(currentHotelId, new TreeSet<>());

        }

        hotelIdMap.get(currentHotelId).add(review);

        Map<String, Integer> wordCountMap = ReviewParser.parseReviewWords(review);
        addReviewWithFrequency(review, wordCountMap);
    }

    /**
     * Method to add inverted word index for reviews given review and its wordCountMap
     */
    private void addReviewWithFrequency(HotelReview newReview, Map<String, Integer> wordCountMap) {
        for(String keyWord: wordCountMap.keySet()){

            if(!keyWordHashMap.containsKey(keyWord)){
                keyWordHashMap.put(keyWord, new TreeSet<>());
            }

            ReviewWithFrequency newWordReview = new ReviewWithFrequency(newReview, keyWord, wordCountMap.get(keyWord));

            keyWordHashMap.get(keyWord).add(newWordReview);

        }
    }

    /**
     * Helper function to call from hotel review service to get desired query without passing entire hotelID hash map
     */
    public List<HotelReview> searchReviewByHotelID(String hotelID){
        ReviewSearcher returnList = new ReviewSearcher();
        return returnList.searchReviewById(hotelID, hotelIdMap);

    }

    /**
     * Helper function to call from hotel review service to get desired query without passing entire keyWordHashMap
     */
    public List<HotelReview> searchReviewByKeyWord(String keyword){
        ReviewSearcher returnString = new ReviewSearcher();
        return returnString.searchReviewByWord(keyword, keyWordHashMap);

    }

    /**
     * Method to return hotel id key set for reviews
     */
    public ArrayList<String> getReviewHotelIdList(){
        return new ArrayList<>(hotelIdMap.keySet());
    }

    public Map<String, TreeSet<HotelReview>> getReviewMap(){
        return hotelIdMap;
    }


}


