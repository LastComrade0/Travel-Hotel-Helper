package hoteltools.reviewparse;

import java.util.Objects;

public class ReviewWithFrequency implements Comparable<ReviewWithFrequency>{
    private final HotelReview hotelReview;
    private final String keyWord;
    private final int wordOccurence;

    /**
     * Constructor
     */
    public ReviewWithFrequency(HotelReview review, String keyWord, int wordOccurence) {

        this.hotelReview = review;
        this.keyWord = keyWord;
        this.wordOccurence = wordOccurence;
    }

    /**
     * Getters
     */
    public HotelReview getHotelReview() {
        return hotelReview;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public int getWordOccurrence() {
        return wordOccurence;
    }

    /**
     * Method to return review frequency object as string
     */
    public String toString() {
        return  "hotelReview : " + hotelReview + System.lineSeparator() +
                "keyWord = " + keyWord + System.lineSeparator() +
                "wordOccurance = " + wordOccurence + System.lineSeparator();

    }

    /**
     * Method to do compare in order to put hotel review by key word objects in ordered tree set
     */
    @Override
    public int compareTo(ReviewWithFrequency otherReviewWithFrequency) {
        //Case if wordOccurrence are same
        if(this.wordOccurence == otherReviewWithFrequency.wordOccurence){

            if(Objects.equals(this.hotelReview.getSubmissionDate(), otherReviewWithFrequency.hotelReview.getSubmissionDate())){

                if( (this.hotelReview.getReviewID().compareTo(otherReviewWithFrequency.hotelReview.getReviewID())) > 0 ){
                    return 1;
                }

                if( (this.hotelReview.getReviewID().compareTo(otherReviewWithFrequency.hotelReview.getReviewID())) < 0 ){
                    return -1;
                }

                return 0;
            }

            if((this.hotelReview.getSubmissionDate().compareTo(otherReviewWithFrequency.hotelReview.getSubmissionDate())) > 0){
                return -1;
            }

            if((this.hotelReview.getSubmissionDate().compareTo(otherReviewWithFrequency.hotelReview.getSubmissionDate())) < 0){
                return 1;
            }

            return 0;
        }

        if((this.wordOccurence - otherReviewWithFrequency.wordOccurence) > 0){
            return -1;
        }

        if((this.wordOccurence - otherReviewWithFrequency.wordOccurence) < 0){
            return 1;
        }

        return 0;

    }
}
