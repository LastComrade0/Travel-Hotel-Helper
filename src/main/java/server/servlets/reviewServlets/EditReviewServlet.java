package server.servlets.reviewServlets;

import database.DatabaseHandler;
import hoteltools.HotelCollection;
import hoteltools.ReviewCollectionsThreadSafe;
import hoteltools.hotelparse.Hotel;
import hoteltools.reviewparse.HotelReview;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class EditReviewServlet extends HttpServlet {
    private HotelCollection hotelCollection;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;
    private TreeMap<String, Hotel> hotelMap;
    private Map<String, TreeSet<HotelReview>> hotelReviewMap;

    public EditReviewServlet(HotelCollection hotelCollection, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe) {
        this.hotelCollection = hotelCollection;
        this.reviewCollectionsThreadSafe = reviewCollectionsThreadSafe;
        this.hotelMap = hotelCollection.getHotelmap();
        this.hotelReviewMap = reviewCollectionsThreadSafe.getReviewMap();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = null;
        StringWriter stringWriter = new StringWriter();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String hotelID = request.getParameter("hotelID");
        String reviewID = request.getParameter("reviewID");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        HotelReview hotelReview = dbHandler.getSingleReview(reviewID);

        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
        }

        out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/editReviewTemplate.html");

        VelocityContext context = new VelocityContext();

        context.put("hotelID", hotelID);
        context.put("reviewID", reviewID);
        assert hotelReview != null;
        context.put("rating", hotelReview.getAverageRating());
        context.put("reviewText", hotelReview.getReviewText());
        template.merge(context, stringWriter);

        out.println(stringWriter);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelID = request.getParameter("hotelID");
        String reviewID = request.getParameter("reviewID");
        String rating = request.getParameter("rating");
        String reviewText = request.getParameter("reviewText");
        System.out.println("reviewText post: " + reviewText);
        System.out.println("hotelID post: " + hotelID);
        System.out.println("reviewID post: " + reviewID);

        if(request.getParameter("editReview") != null) {
            response.sendRedirect("/hotel?hotelID=" + hotelID);
        }

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        if(!reviewText.isEmpty()) {

            //HotelReview hotelReview = dbHandler.getSingleReview(reviewID);

            dbHandler.updateReview(reviewID, rating, reviewText);

//            for(HotelReview hotelReview : hotelReviewMap.get(hotelID)) {
//                if(hotelReview.getReviewID().equals(reviewID)) {
//                    hotelReview.updateAverageRating(Double.parseDouble(rating));
//                    hotelReview.updateReviewText(reviewText);
//                }
//            }
        }

        response.sendRedirect("/hotel?hotelID=" + hotelID);
    }

}
