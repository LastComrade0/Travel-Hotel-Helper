package server.servlets.reviewServlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.DatabaseHandler;
import hoteltools.reviewparse.HotelReview;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

public class ReviewServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String query = request.getParameter("hotelID");
        String page = request.getParameter("page");
        System.out.println("query: " + query);
        System.out.println("page: " + page);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        TreeSet<HotelReview> hotelReviewSet = dbHandler.showOffsetReviewQuery(query, page);

        JsonArray reviewArray = new JsonArray();
        for(HotelReview hotelReview : hotelReviewSet) {
            JsonObject reviewObject = serializeReview(hotelReview);
            reviewArray.add(reviewObject);
        }


        PrintWriter out = response.getWriter();
        out.println(reviewArray);
    }
    // String title, String reviewText, String userNickname, String submissionDate)
    private static JsonObject serializeReview(HotelReview hotelReview) {
        JsonObject reviewObject = new JsonObject();
        reviewObject.addProperty("reviewID", hotelReview.getReviewID());
        reviewObject.addProperty("hotelID", hotelReview.getHotelID());
        reviewObject.addProperty("rating", hotelReview.getAverageRating());
        reviewObject.addProperty("title", hotelReview.getTitle());
        reviewObject.addProperty("reviewText", hotelReview.getReviewText());
        reviewObject.addProperty("nickname", hotelReview.getUserNickname());
        reviewObject.addProperty("submissionDate", hotelReview.getSubmissionDate());

        return reviewObject;
    }
}
