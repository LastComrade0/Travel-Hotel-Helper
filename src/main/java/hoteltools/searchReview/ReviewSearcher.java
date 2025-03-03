package hoteltools.searchReview;

import hoteltools.reviewparse.HotelReview;
import hoteltools.reviewparse.ReviewWithFrequency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * This class is to query hotel reviews either by hotel id or key word in review text
 */
public class ReviewSearcher {
    /**
     * Method to query reviews by hotel id given the whole hash map from ReviewParser
     * Returns desired query only
     */
    public List<HotelReview> searchReviewById(String hotelId, Map<String, TreeSet<HotelReview>> hotelIdMap){
        List<HotelReview> hotelReviewsList = new ArrayList<HotelReview>();

        if(hotelIdMap.get(hotelId) == null){
            return null;
        }

        for(HotelReview hotelReview : hotelIdMap.get(hotelId)){
            hotelReviewsList.add(hotelReview);
        }

        if(hotelReviewsList.isEmpty()){
            throw new IllegalArgumentException("Hotel ID not found");
        }

        return hotelReviewsList;
    }

    /**
     * Method to query reviews by key word given the whole hash map from ReviewParser
     * Returns desired query only
     */
    public List<HotelReview> searchReviewByWord(String keyWord, Map<String, TreeSet<ReviewWithFrequency>> hotelWordCountMap){
        List<HotelReview> keyWordReviewList = new ArrayList<HotelReview>();
        for(ReviewWithFrequency reviewWithFrequency : hotelWordCountMap.get(keyWord)){
            keyWordReviewList.add(reviewWithFrequency.getHotelReview());
        }


        return keyWordReviewList;
    }
}
