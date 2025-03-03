package server.servlets.expediaServlets;

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
import java.util.List;
import java.util.Objects;
/*
Servlet Class to get expedia history page
 */
public class ExpediaHistoryServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String userID = request.getParameter("userID");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        System.out.println("User favorite: " + userID);
        String clear = request.getParameter("clear");


        if(username == null) {
            System.out.println("username not logged in");
            response.sendRedirect("/login");
        }

        if(Objects.equals(clear, "yes")){
            dbHandler.deleteExpediaHistory(userID);
            response.sendRedirect("/expediaHistory?userID=" + userID);
            return;
        }

        PrintWriter out = response.getWriter();

        StringWriter stringWriter = new StringWriter();

        List<String> urlHistory = dbHandler.getExpediaHistory(userID);

        VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("templates/expediaHistoryTemplate.html");

        VelocityContext context = new VelocityContext();
        System.out.println(urlHistory);

        context.put("userID", userID);
        context.put("urlHistory", urlHistory);
        context.put("username", username);

        template.merge(context, stringWriter);
        out.println(stringWriter);

    }


}
