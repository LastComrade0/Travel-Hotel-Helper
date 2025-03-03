package server.servlets;

import database.DatabaseHandler;
import hoteltools.HotelCollection;
import hoteltools.ReviewCollectionsThreadSafe;
import hoteltools.hotelparse.Hotel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
/*
Servlet to handle main page of travel service
 */
public class TravelMainServlet extends HttpServlet {
    private HotelCollection hotelCollection;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;
    private TreeMap<String, Hotel> hotelMap;


    public TravelMainServlet(HotelCollection hotelCollection, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe) {
        this.hotelCollection = hotelCollection;
        this.reviewCollectionsThreadSafe = reviewCollectionsThreadSafe;
        hotelMap = hotelCollection.getHotelmap();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = null;
        StringWriter stringWriter = new StringWriter();
        HttpSession session = request.getSession();
        String hotelQuery = request.getParameter("hotelQuery");
        String username = (String) session.getAttribute("username");
        boolean queryFlag = false;
        System.out.println("Hotel query: " + hotelQuery);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        String userID = dbHandler.getUserID(username);
        String now = getCurrentTime();
        System.out.println(now);
        String lastLogin = dbHandler.getLoginHistory(userID);
        boolean hasLoginCookie = false;

        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
            return;
        }

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("lastLogin")) {

                System.out.println("Cookie value: " + cookie.getValue());
                hasLoginCookie = true;

            }

        }

        if(!hasLoginCookie) {
            if (request.getIntHeader("DNT") != 1) { // ok to track
                System.out.println("new cookie");
                response.addCookie(new Cookie("lastLogin", now));
            }
        }

        System.out.println("Logged in: " + username);

        out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/mainpageTemplate.html");

        VelocityContext context = new VelocityContext();
        context.put("username", username);


        context.put("userID", userID);
        context.put("lastLogin", lastLogin);
        System.out.println("userID: " + userID);

        if(hotelQuery == null) {
            try {
                TreeMap<String, Hotel> allHotelList = dbHandler.showAllHotel();
                context.put("hotels", allHotelList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            TreeMap<String, Hotel> queriedHotelList;
            try {
                queriedHotelList = dbHandler.showHotelQuery(hotelQuery);
                context.put("hotels", queriedHotelList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(queriedHotelList.isEmpty()) {
                response.sendRedirect("/mainpage");
            }

            queryFlag = true;
            context.put("queryFlag", queryFlag);
            context.put("queryIndex", hotelQuery);
            context.put("hotels", queriedHotelList);
        }

        template.merge(context, stringWriter);
        //System.out.println(stringWriter);

        out.println(stringWriter);




    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String hotelQuery = request.getParameter("hotelQuery");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        String userID = dbHandler.getUserID(username);

        if(request.getParameter("logout") != null){
            Cookie[] cookies = request.getCookies();


            for (Cookie cookie: cookies) {
                if (cookie.getName().equals("lastLogin")) {
                    String lastLogin = dbHandler.getLoginHistory(userID);
                    System.out.println("Last login logout: " + lastLogin);
                    System.out.println("Cookie time: " + cookie.getValue());

                    String cookieTime = cookie.getValue();
                    String parsedTime = parseTime(cookieTime);

                    if(lastLogin.contains("not")) {
                        System.out.println("New last login");
                        dbHandler.newLoginHistory(userID, parsedTime);

                    }
                    else{
                        System.out.println("Updating last login");
                        dbHandler.updateLoginHistory(userID, parsedTime);
                    }

                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    System.out.println("Cleared cookie" +  cookie.getValue());
                    response.addCookie(cookie);

                    break;
            }

        }


            System.out.println("Logging out " + username);
            session.invalidate();
            response.sendRedirect("/login");
            return;
        }

        System.out.println("Send hotel query");

        response.sendRedirect("/mainpage?hotelQuery=" + hotelQuery);

        out.close();


    }

    private String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        return now.format(formatter);
    }

    private String parseTime(String cookieTime){
        StringBuilder parsedTime = new StringBuilder();
        parsedTime.append(cookieTime, 0, 10);
        System.out.println(cookieTime.substring(0,10));
        String time = cookieTime.substring(10);
        String parsedString = " " + time.substring(1, 3) + ":" + time.substring(4, 6) +
                ":" + time.substring(7);
        System.out.println(parsedString);

        parsedTime.append(parsedString);
        return parsedTime.toString();
    }


}
