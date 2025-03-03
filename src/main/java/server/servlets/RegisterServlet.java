package server.servlets;

import database.DatabaseHandler;
import jakarta.servlet.ServletException;
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
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Servlet Class to handle register and register page
 */
public class RegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        String username = request.getParameter("username");
        username = StringEscapeUtils.escapeHtml4(username);

        String error = request.getParameter("error");

        if(request.getParameter("username") == null){
            System.out.println("Login reached");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            StringWriter stringWriter = new StringWriter();
            VelocityEngine ve = (VelocityEngine) session.getServletContext().getAttribute("templateEngine");
            Template template = ve.getTemplate("templates/registerTemplate.html");

            VelocityContext context = new VelocityContext();

            if(!Objects.equals(error, null)){
                context.put("error", error);
            }

            template.merge(context, stringWriter);

            out.println(stringWriter);
        }
        else{

            response.sendRedirect("/register");

        }


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String passcheck = request.getParameter("password check");
        DatabaseHandler dbHandler;
        Boolean dupeFlag = true;

        PrintWriter out = response.getWriter();

        System.out.println(user);

        if(request.getParameter("back") != null){
            System.out.println("Back to login;");
            response.sendRedirect("/login");
            return;
        }

        dbHandler = DatabaseHandler.getInstance();

        try{
            dupeFlag = dbHandler.validateDupeName(user);
            System.out.println(dupeFlag);

        } catch (SQLException e) {
            response.sendRedirect("/register");
        }

        if(dupeFlag){
            response.sendRedirect("/register?error=dupename");
        }

        if(!validatePassInput(pass)){
            response.sendRedirect("/register?error=passworderror");
            return;
        }

        if(pass.equals(passcheck) && !dupeFlag){
            try {
                dbHandler.registerUser(user, pass);
                System.out.println("User " + user + " successfully registered.");
                response.sendRedirect("/login");
            }
            catch (SQLException e) {

                response.sendRedirect("/register");
                return;
            }

        }
        else{
            if(!dupeFlag){
                response.sendRedirect("/register?error=passwordmatch");
                return;
            }
        }



        //response.sendRedirect("/register");
        //out.close();
    }

    private static boolean validatePassInput(String password){
        boolean validate = false;
        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&\\-])[A-Za-z\\d@$!%*#?&\\-]{8,32}$");
        Matcher m = p.matcher(password);
        if(m.find()){
            validate = true;
        }
        return validate;
    }


//    private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        PrintWriter out = response.getWriter();
//
//        out.printf("<form method = 'Post' action '%s' > %n", request.getServletPath());
//        out.printf("Enter username : <br><input type= 'text' name = 'username' maxlength='32'><br>");
//        out.printf("Enter password : <br><input type= 'text' name = 'password' maxlength='32'><br>");
//        out.printf("Enter password again : <br><input type= 'text' name = 'password check' maxlength='32'><br>");
//        out.printf("<p><input type = 'submit' value = 'Enter'></p>%n");
//        out.printf("<p><input type = 'submit' name = 'back' value = 'Back'></p>%n");
//        out.printf("</form>%n");
//
//    }
}
