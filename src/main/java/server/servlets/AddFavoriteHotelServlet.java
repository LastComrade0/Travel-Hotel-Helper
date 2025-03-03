package server.servlets;

import database.DatabaseHandler;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
/*
Servlet Class to add favorite hotel only be called by AJAX
 */
public class AddFavoriteHotelServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userID = request.getParameter("userID");
        String hotelID = request.getParameter("hotelID");

        response.setContentType("text/html");
        response.getWriter().println("<h1>POST request received!</h1>");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        Map<String, String> favoriteHotels = dbHandler.getFavoriteHotels(Integer.parseInt(userID));
        System.out.println(favoriteHotels);
        if(!favoriteHotels.containsKey(hotelID)) {
            dbHandler.addFavoriteHotel(userID, hotelID);
        }
        else{
            System.out.println("Hotel: " + favoriteHotels.get(hotelID) +" Already in favorite hotel");
            response.getWriter().println("Duplicate");
            return;
        }

        response.getWriter().println("Success");
        response.getWriter().println("<p>Username: " + userID + "</p>");
        response.getWriter().println("<p>Password: " + hotelID + "</p>");
    }
}
