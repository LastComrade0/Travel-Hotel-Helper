package server.servlets;

import database.DatabaseHandler;
import hoteltools.HotelCollection;
import hoteltools.ReviewCollectionsThreadSafe;
import hoteltools.hotelparse.Hotel;
import hoteltools.reviewparse.HotelReview;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

/*
Servlet Class to show hotel page
 */
public class HotelServlet extends HttpServlet {
    private HotelCollection hotelCollection;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;
    private TreeMap<String, Hotel> hotelMap;
    private Map<String, TreeSet<HotelReview>> hotelReviewMap;
    private Hotel currentHotel;



    public HotelServlet(HotelCollection hotelCollection, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe) {
        this.hotelCollection = hotelCollection;
        this.reviewCollectionsThreadSafe = reviewCollectionsThreadSafe;
        this.hotelMap = hotelCollection.getHotelmap();
        this.hotelReviewMap = reviewCollectionsThreadSafe.getReviewMap();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = null;
        StringWriter stringWriter = new StringWriter();

        String hotelID = request.getParameter("hotelID");
        hotelID = StringEscapeUtils.escapeHtml4(hotelID);
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        String userID = dbHandler.getUserID(username);
        currentHotel = dbHandler.getSingleHotel(hotelID);
        System.out.println("User ID: " + userID);

        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
        }

        System.out.println("Accessing " + currentHotel.getHotelName()
                        + " as: " + username);

        out = response.getWriter();


        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/hotelTemplate.html");

        VelocityContext context = new VelocityContext();

        String expediaURL = getExpediaURL(hotelID);

        System.out.println(expediaURL);

        context.put("userID", userID);
        context.put("hotelName", currentHotel.getHotelName());
        context.put("hotelID", hotelID);
        context.put("address", currentHotel.getStreetAddress());
        context.put("expediaURL", expediaURL);

        List<String> listSize = new ArrayList<>();

        int reviewSize = (int)Math.ceil((double)(dbHandler.getReviewCount(hotelID))/5);

        double avgRating = getAvgRating(hotelID, reviewSize);
        DecimalFormat df = new DecimalFormat("#.##");

        System.out.println("Review size" + reviewSize);
        for(int i = 1; i <= reviewSize; i++) {
            listSize.add(String.valueOf(i));
        }
        System.out.println("Review size: " + reviewSize);
        System.out.println(listSize);



        context.put("reviewSize", listSize);
        context.put("sessionUser", username);
        context.put("avgRating", df.format(avgRating));


        template.merge(context, stringWriter);

        out.println(stringWriter);


    }


    private double getAvgRating(String hotelID, int reviewCount){

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        double avgRating = 0;
        avgRating = dbHandler.getAverageRating(hotelID);

        return avgRating;
    }

    private static String getExpediaURL(String hotelID){
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.expedia.com/h");
        sb.append(hotelID);
        sb.append(".Hotel-Information");

        return sb.toString();
    }


}
