package hoteltools;

import hoteltools.reviewparse.HotelReview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to extend review collections and use read and write lock
 */
public class ReviewCollectionsThreadSafe extends ReviewCollections{
    private ReentrantReadWriteLock lock;

    /**
     * Constructor
     */
    public ReviewCollectionsThreadSafe(){
        super();
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Method to iterate through parsed list of reviews and add each of them to review hashmap
     * Write lock before the method finishes
     */
    @Override
    public void addReview(List<HotelReview> reviewList) {
        try {

            lock.writeLock().lock();

            super.addReview(reviewList);

        }

        finally{
            lock.writeLock().unlock();
        }

    }

    /**
     * Overload method when add only one review
     */
    @Override
    public void addReview(HotelReview review) {
        try {

            lock.writeLock().lock();

            super.addReview(review);

        }

        finally{
            lock.writeLock().unlock();
        }
    }


    /**
     * Helper function to call from hotel review service to get desired query without passing entire hotelID hash map
     */
    @Override
    public List<HotelReview> searchReviewByHotelID(String hotelID){
        try {
            lock.readLock().lock();
            return super.searchReviewByHotelID(hotelID);
        }
        finally {
            lock.readLock().unlock();
        }

    }

    /**
     * Helper function to call from hotel review service to get desired query without passing entire keyWordHashMap
     */
    @Override
    public List<HotelReview> searchReviewByKeyWord(String keyword){
        try {
            lock.readLock().lock();
            return super.searchReviewByKeyWord(keyword);
        }
        finally {
            lock.readLock().unlock();
        }

    }

    /**
     * Method to return hotel id key set for reviews
     */
    @Override
    public ArrayList<String> getReviewHotelIdList(){
        return super.getReviewHotelIdList();
    }

    @Override
    public Map<String, TreeSet<HotelReview>> getReviewMap(){
        return super.getReviewMap();
    }


}
