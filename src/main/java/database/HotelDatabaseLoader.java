package database;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
/*
Class for loading all reviews and hotels in database. In addition, user info will also be created
*/
public class HotelDatabaseLoader {
    private static HotelDatabaseLoader hotelDatabaseLoader = new HotelDatabaseLoader("database.properties");
    private Properties config;
    private String uri = null;

    private HotelDatabaseLoader(String databaseProperty){
        this.config = loadConfig(databaseProperty);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("database") +  "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        System.out.println("URI: " + uri);
    }

    public static HotelDatabaseLoader getInstance(){
        return hotelDatabaseLoader;
    }

    public Properties loadConfig(String proprtyFile){
        Properties config = new Properties();
        try(FileReader fr = new FileReader(proprtyFile)){
            config.load(fr);
        }
        catch(IOException e){
            System.out.println("Error loading config file: " + proprtyFile);
        }

        return config;
    }

    public void addHotel(String hotelID, String hotelName, String address, String city, String state, BigDecimal latitude, BigDecimal longitude){
        PreparedStatement registerStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            try{
                registerStatement = dbConnection.prepareStatement(SQLStatements.INSERT_HOTEL);
                registerStatement.setString(1, hotelID);
                registerStatement.setString(2, hotelName);
                registerStatement.setString(3, address);
                registerStatement.setString(4, city);
                registerStatement.setString(5, state);
                registerStatement.setString(6, latitude.toString());
                registerStatement.setString(7, longitude.toString());
                registerStatement.executeUpdate();
                registerStatement.close();
            }
            catch(SQLException ex){
                throw new SQLException(ex.getMessage());
            }
        }
        catch(SQLException e){
            System.out.println("Error adding hotel: " + hotelID);
        }
    }

    public void addReview(String reviewID, String hotelID, double rating, String title, String userNickname, String reviewText, String submissionDate){
        PreparedStatement registerStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){

            try{
                registerStatement = dbConnection.prepareStatement(SQLStatements.INSERT_REVIEW);
                registerStatement.setString(1, reviewID);
                registerStatement.setString(2, hotelID);
                registerStatement.setString(3, String.valueOf(rating));
                registerStatement.setString(4, title);
                registerStatement.setString(5, userNickname);
                registerStatement.setString(6, reviewText);
                registerStatement.setString(7, submissionDate);
                registerStatement.executeUpdate();
                registerStatement.close();
            }
            catch(SQLException ex){
                throw new SQLException(ex.getMessage());
            }
        }
        catch(SQLException e){
            System.out.println("Error adding review: " + reviewID);
        }
    }


}
