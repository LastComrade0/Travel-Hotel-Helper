package server.servlets.expediaServlets;

import database.DatabaseHandler;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;
/*
Servlet Class to add Expedia History upon visiting url
 */
public class AddExpediaHistoryServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userID = request.getParameter("userID");
        String url = request.getParameter("url");
        System.out.println("Adding: " + url);

        response.setContentType("text/html");
        response.getWriter().println("<h1>POST request received!</h1>");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        String checkDupeURL = dbHandler.validateURL(userID, url);

        System.out.println(checkDupeURL);
        if(!Objects.equals(checkDupeURL, url)) {
            dbHandler.addExpediaHistory(userID, url);
        }
        else{
            System.out.println("Entry: " + url + " already exists");
            return;
        }

        response.getWriter().println("Success");
        response.getWriter().println("<p>Username: " + userID + "</p>");
        response.getWriter().println("<p>Password: " + url + "</p>");
    }
}
