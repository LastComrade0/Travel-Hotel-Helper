package hoteltools.reviewparse;

public class HotelReview implements Comparable<HotelReview>{
    private String hotelID;
    private String reviewId;
    private double averageRating;
    private String title;
    private String reviewText;
    private String userNickname;
    private String submissionDate;
    private int wordCount;
    private int likes;

    public HotelReview(String hotelID, String reviewId, Double averageRating, String title, String reviewText, String userNickname, String submissionDate){
        this.hotelID = hotelID;
        this.reviewId = reviewId;
        this.averageRating = averageRating;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.submissionDate = submissionDate;
    }

    public HotelReview(String hotelID, String reviewId, Double averageRating, String title, String reviewText, String userNickname, String submissionDate, int likes){
        this.hotelID = hotelID;
        this.reviewId = reviewId;
        this.averageRating = averageRating;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.submissionDate = submissionDate;
        this.likes = likes;
    }


    /**
     * Getters
     */
    public String getReviewText(){
        return reviewText;
    }

    public String getReviewID(){
        return reviewId;
    }

    public String getSubmissionDate(){
        return submissionDate;
    }

    public String getHotelID(){
        return hotelID;
    }

    public String getTitle(){
        return title;
    }

    public String getUserNickname(){
        return userNickname;
    }

    public void updateReviewText(String newReviewText){
        this.reviewText = newReviewText;
    }

    public void updateAverageRating(double newAverageRating){
        this.averageRating = newAverageRating;
    }

    public double getAverageRating(){
        return averageRating;
    }

    /**
     * Method to return hotel review object as string
     */
    @Override
    public String toString() {
        if(userNickname.equals("")){
            userNickname = "Anonymous";
        }
        return
                "Review by " + userNickname + " on " + submissionDate + System.lineSeparator() +
                        "Rating: " + (int)averageRating + System.lineSeparator() +
                        "ReviewId: " + reviewId + System.lineSeparator() +
                        title + System.lineSeparator() +
                        reviewText + System.lineSeparator();

    }

    /**
     * Method to do compare in order to put hotel review objects in ordered tree set
     */
    @Override
    public int compareTo(HotelReview otherHotelObj) {
        //Essential for tree set
        if(this.submissionDate.compareTo(otherHotelObj.submissionDate) == 0){
            return this.reviewId.compareTo(otherHotelObj.reviewId);
        }

        if(this.submissionDate.compareTo(otherHotelObj.submissionDate) > 0){
            return -1;
        }

        if(this.submissionDate.compareTo(otherHotelObj.submissionDate) < 0){
            return 1;
        }

        return 0;

    }
}
