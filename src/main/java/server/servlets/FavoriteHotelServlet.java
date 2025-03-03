package server.servlets;

import database.DatabaseHandler;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteHotelServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String userID = request.getParameter("userID");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        System.out.println("User favorite: " + userID);

        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
        }

        PrintWriter out = response.getWriter();

        //Favorite Hotels can be join tables
        Map<String, String> favoriteHotels = dbHandler.getFavoriteHotels(Integer.parseInt(userID));
        List<String> keyset = new ArrayList<>(favoriteHotels.keySet());

        StringWriter stringWriter = new StringWriter();

        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/favoriteHotelTemplate.html");

        VelocityContext context = new VelocityContext();
        System.out.println(keyset);

        context.put("username", username);
        context.put("userID", userID);
        context.put("hotelMap", favoriteHotels);
        context.put("keyset", keyset);

        template.merge(context, stringWriter);
        out.println(stringWriter);

    }
}
