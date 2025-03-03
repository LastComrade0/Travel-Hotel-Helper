package server;

import hoteltools.HotelCollection;
import hoteltools.ReviewCollectionsThreadSafe;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import server.servlets.*;
import server.servlets.expediaServlets.AddExpediaHistoryServlet;
import server.servlets.expediaServlets.ExpediaHistoryServlet;
import server.servlets.reviewServlets.AddReviewServlet;
import server.servlets.reviewServlets.DeleteReviewServlet;
import server.servlets.reviewServlets.EditReviewServlet;
import server.servlets.reviewServlets.ReviewServlet;

/*
Main class to initiate server instance
 */
public class TravelServer {
    private static int PORT;
    private Server server;
    private ServletContextHandler handler;
    private HotelCollection hotelCollection;
    private ReviewCollectionsThreadSafe reviewCollectionsThreadSafe;

    public TravelServer(HotelCollection hotelCollection, ReviewCollectionsThreadSafe reviewCollectionsThreadSafe, int PORT) {
        server = new Server(PORT);
        this.hotelCollection = hotelCollection;
        this.reviewCollectionsThreadSafe = reviewCollectionsThreadSafe;
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        addServlet(handler);
    }

    public void addServlet(ServletContextHandler handler) {
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(RegisterServlet.class, "/register");
        handler.addServlet(new ServletHolder(new TravelMainServlet(hotelCollection, reviewCollectionsThreadSafe)), "/mainpage");
        handler.addServlet(new ServletHolder(new HotelServlet(hotelCollection, reviewCollectionsThreadSafe)), "/hotel");
        handler.addServlet(new ServletHolder(new EditReviewServlet(hotelCollection, reviewCollectionsThreadSafe)), "/hotel/edit");
        handler.addServlet(new ServletHolder(new DeleteReviewServlet(hotelCollection, reviewCollectionsThreadSafe)), "/hotel/delete");
        handler.addServlet(new ServletHolder(new AddReviewServlet(hotelCollection, reviewCollectionsThreadSafe)), "/hotel/add");
        handler.addServlet(ReviewServlet.class, "/reviews");
        handler.addServlet(FavoriteHotelServlet.class, "/favoriteHotel");
        handler.addServlet(AddFavoriteHotelServlet.class, "/favoriteHotel/add");
        handler.addServlet(ExpediaHistoryServlet.class, "/expediaHistory");
        handler.addServlet(AddExpediaHistoryServlet.class, "/expediaHistory/add");
    }

    public void start() throws Exception {
        VelocityEngine velocity = new VelocityEngine();
        System.out.println("Travel Velocity Server Started");
        velocity.init();

        handler.setAttribute("templateEngine", velocity);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("templates");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resourceHandler, handler});
        server.setHandler(handlers);

        try{
            server.start();
            server.join();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
