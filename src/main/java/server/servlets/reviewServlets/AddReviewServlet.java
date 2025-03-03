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
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddReviewServlet extends HttpServlet {
    private HotelCollection hotelCollection;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;
    private TreeMap<String, Hotel> hotelMap;
    private Map<String, TreeSet<HotelReview>> hotelReviewMap;

    public AddReviewServlet(HotelCollection hotelCollection, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe) {
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
        System.out.println("hotelID add: " + hotelID);

        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
        }

        out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/addReviewTemplate.html");

        VelocityContext context = new VelocityContext();

        context.put("username", username);
        context.put("hotelID", hotelID);

        template.merge(context, stringWriter);

        out.println(stringWriter);


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String hotelID = request.getParameter("hotelID");
        String title = request.getParameter("title");
        String rating = request.getParameter("rating");
        String reviewText = request.getParameter("reviewText");
        System.out.println("reviewText post: " + reviewText);
        System.out.println("hotelID post: " + hotelID);

        if(request.getParameter("cancel") != null) {
            response.sendRedirect("/hotel?hotelID=" + hotelID);
            return;
        }
        else if(reviewText.isEmpty() || rating.isEmpty() || title.isEmpty()) {
            response.sendRedirect("/hotel/add?hotelID=" + hotelID);
        }

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Random random = new Random();
        byte[] reviewBytes = new byte[16];
        random.nextBytes(reviewBytes);

        String newReviewID = encodeHex(reviewBytes, 32);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        databaseHandler.addReview(hotelID, newReviewID, rating, title, reviewText, username, currentDate);

        response.sendRedirect("/hotel?hotelID=" + hotelID);
    }

    public static String encodeHex(byte[] bytes, int length){
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);
        //System.out.println("Encoded to hex: " + hex);

        assert hex.length() == length;
        return hex;
    }
}
