package hoteltools.reviewparse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewParser {
    /**
     * Parse hotel reviews from json file
     */
    public static List<HotelReview> parseReviewJson(Path path){
        List<HotelReview> hotelReviews = new ArrayList<>();

        String hotelID;
        String reviewId;
        double averageRating;
        String title;
        String reviewText;
        String userNickname;
        String submissionDate;
        int wordCount;


        try(FileReader fr = new FileReader(String.valueOf(path))){


            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject)parser.parse(fr);
            JsonObject reviewDetails = object.get("reviewDetails").getAsJsonObject();
            JsonObject reviewCollection = reviewDetails.get("reviewCollection").getAsJsonObject();
            JsonArray review = reviewCollection.getAsJsonArray("review");


            for(int i = 0; i < review.size(); i += 1){
                JsonObject currentReview = review.get(i).getAsJsonObject();


                hotelID = currentReview.get("hotelId").getAsString();
                reviewId = currentReview.get("reviewId").getAsString();
                averageRating = currentReview.get("ratingOverall").getAsDouble();
                title = currentReview.get("title").getAsString();
                reviewText = currentReview.get("reviewText").getAsString();
                userNickname = currentReview.get("userNickname").getAsString();

                submissionDate = currentReview.get("reviewSubmissionDate").getAsString();


                HotelReview newReview = new HotelReview(hotelID, reviewId, averageRating, title, reviewText, userNickname, submissionDate);

                hotelReviews.add(newReview);

            }

        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

        return hotelReviews;

    }

    /**
     * Method to create word count hashmap and return it to ReviewCollection Class
     */
    public static Map<String, Integer> parseReviewWords(HotelReview review){

        Map<String, Integer> wordCountMap = new HashMap<>();

        for(String reviewWord: review.getReviewText().split("[ .,;]") ){


            if(!wordCountMap.containsKey(reviewWord)){

                wordCountMap.put(reviewWord, 1);

            }
            else{
                wordCountMap.put(reviewWord, wordCountMap.get(reviewWord) + 1);
            }

        }

        return wordCountMap;


    }
}
