package server.servlets;

import database.DatabaseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
import java.util.Objects;

/*
Servlet Class to handle login page
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String authenticate = request.getParameter("authenticate");
        username = StringEscapeUtils.escapeHtml4(username);

        if(session.getAttribute("username") != null) {
            response.sendRedirect("/mainpage");
        }

        if(request.getParameter("username") == null){

            //out.println("Login for Hotel Travel Service");
            System.out.println("Login reached");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            StringWriter stringWriter = new StringWriter();
            VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
            Template template = ve.getTemplate("templates/loginTemplate.html");

            VelocityContext context = new VelocityContext();

            if(Objects.equals(authenticate, "error")) {
                context.put("error", true);
            }
            else{
                context.put("error", false);
            }

            template.merge(context, stringWriter);

            out.println(stringWriter);

        }
        else{
            if(session.getAttribute("username") != null && session.getAttribute("username").equals(username)){
                out.println("Login success: " + username);
                response.sendRedirect("/mainpage");
            }
            else{
                response.sendRedirect("/login");
            }

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        DatabaseHandler dbHandler;

        PrintWriter out = response.getWriter();

        System.out.println(user);

        dbHandler = DatabaseHandler.getInstance();
        boolean authenticate = dbHandler.authenticateUser(user, pass);

        if(request.getParameter("register") != null){
            System.out.println("Go to register;");
            response.sendRedirect("/register");
            return;
        }

        if(authenticate){
            HttpSession session = request.getSession();
            session.setAttribute("username", user);
            response.sendRedirect("/login?username=" + user);
        }
        else{
            response.sendRedirect("/login?authenticate=error");
        }

        out.close();
    }


}
