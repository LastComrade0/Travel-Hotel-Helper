package database;

import hoteltools.hotelparse.Hotel;
import hoteltools.reviewparse.HotelReview;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
/*
Class for executing SQL queries
 */
public class DatabaseHandler {
    private static DatabaseHandler databaseHandler = new DatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();


    private DatabaseHandler(String databaseProperty){
        this.config = loadConfig(databaseProperty);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("database") +  "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        System.out.println("URI: " + uri);
    }

    public static DatabaseHandler getInstance(){
        return databaseHandler;
    }

    public Properties loadConfig(String propertyFile){
        Properties config = new Properties();
        try(FileReader fr = new FileReader(propertyFile)){
            config.load(fr);
        }
        catch(IOException e){
            System.out.println("Error loading config file: " + propertyFile);
        }

        return config;
    }

    public void registerUser(String username, String password) throws SQLException {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        System.out.println("Generated salt bytes for new user: " + Arrays.toString(saltBytes));

        validateDupeName(username);

        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash(password, usersalt);
        System.out.println("New Usersalt: " + usersalt);
        System.out.println("New Passhash: " + passhash);

        PreparedStatement registerStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            System.out.println("Can register");
            try{
                registerStatement = dbConnection.prepareStatement(SQLStatements.REGISTER_USER_SQL);
                registerStatement.setString(1, username);
                registerStatement.setString(2, passhash);
                registerStatement.setString(3, usersalt);
                registerStatement.executeUpdate();
                registerStatement.close();
            }
            catch(SQLException ex){
                throw new SQLException(ex.getMessage());
            }

        }
        catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password){
        PreparedStatement authenticateStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            System.out.println("Can authenticate");
            authenticateStatement = dbConnection.prepareStatement(SQLStatements.AUTH_USER_SQL);
            String usersalt = getSalt(dbConnection, username);
            String passhash = getHash(password, usersalt);
            System.out.println("Hash to verify: " + passhash);

            authenticateStatement.setString(1, username);
            authenticateStatement.setString(2, passhash);
            ResultSet result = authenticateStatement.executeQuery();
            boolean flag = result.next();
            return flag;

        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        return false;

    }

    public static String encodeHex(byte[] bytes, int length){
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);
        //System.out.println("Encoded to hex: " + hex);

        assert hex.length() == length;
        return hex;
    }

    public static String getHash(String password, String salt){
        String salted = salt + password;
        String hashed = salted;

        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salted.getBytes());
            hashed = encodeHex(messageDigest.digest(), 64);
        }
        catch(Exception e){
            System.out.println("Error getting hash: " + e);
        }

        return hashed;
    }

    private String getSalt(Connection connection, String user){
        String salt = null;
        try(PreparedStatement saltStatement = connection.prepareStatement(SQLStatements.SALT_SQL)){
            saltStatement.setString(1, user);
            ResultSet resultSet = saltStatement.executeQuery();
            if(resultSet.next()){
                salt = resultSet.getString("user_salt");
                System.out.println("User salt extracted: " + salt);
                return salt;
            }
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return salt;
    }

    public void checkAutoIncrement(){
        Statement statement;
        String currentIncrement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            try(PreparedStatement checkAutoIncrementStatement = dbConnection.prepareStatement(SQLStatements.CHECK_AUTO_INCREMENT)) {
                System.out.println("Checking auto increment");
                ResultSet resultSet = checkAutoIncrementStatement.executeQuery();
                if(resultSet.next()){
                    currentIncrement = resultSet.getString("AUTO_INCREMENT");
                    System.out.println("Current auto increment: " + currentIncrement);
                }
            }
            catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void resetAutoIncrement(){
        Statement statement;
        String currentIncrement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            System.out.println("Resetting auto increment to 1");
            statement = dbConnection.createStatement();
            statement.executeUpdate(SQLStatements.RESET_AUTO_INCREMENT);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public boolean validateDupeName(String username) throws SQLException {
        PreparedStatement validateDupeStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            validateDupeStatement = dbConnection.prepareStatement(SQLStatements.VALIDATE_DUPE_NAME);
            validateDupeStatement.setString(1, username);
            ResultSet validateDupeResult = validateDupeStatement.executeQuery();
            System.out.println(validateDupeResult.toString());
            if(validateDupeResult.next()){
                return true;
                //throw new SQLException("Unable to register: Username already exists");
            }
        }
        catch(SQLException e){
            throw new SQLException(e.getMessage());
        }

        return false;
    }

    public String getUserID(String name) {
        PreparedStatement getUserIDStatement;
        String userID = "";
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            getUserIDStatement = dbConnection.prepareStatement(SQLStatements.QUERY_USERID);
            getUserIDStatement.setString(1, name);
            ResultSet resultSet = getUserIDStatement.executeQuery();
            if(resultSet.next()){
                userID = resultSet.getString("user_id");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return userID;
    }

    public TreeMap<String, Hotel> showAllHotel() throws SQLException{
        PreparedStatement showAllHotelStatement;
        TreeMap<String, Hotel> hotels = new TreeMap<>();
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            showAllHotelStatement = dbConnection.prepareStatement(SQLStatements.SHOW_HOTEL);
            ResultSet hotelResultSet = showAllHotelStatement.executeQuery();
            while(hotelResultSet.next()){
                String hotelID = hotelResultSet.getString("hotel_id");
                String hotelName = hotelResultSet.getString("hotel_name");
                String address = hotelResultSet.getString("address");
                String city = hotelResultSet.getString("city");
                String state = hotelResultSet.getString("state");
                String country = "USA";
                BigDecimal latitude = hotelResultSet.getBigDecimal("latitude");
                BigDecimal longitude = hotelResultSet.getBigDecimal("longitude");

                hotels.put(hotelID, new Hotel(hotelName, hotelID, latitude, longitude, address, city, state, country));
            }
            return hotels;
        }
        catch(SQLException e){
            throw new SQLException(e.getMessage());
        }

    }

    public TreeMap<String, Hotel> showHotelQuery(String hotelQuery) throws SQLException{
        PreparedStatement queryHotelStatement;
        TreeMap<String, Hotel> queriedHotels = new TreeMap<>();
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            queryHotelStatement = dbConnection.prepareStatement(SQLStatements.QUERY_HOTEL);
            String quantifier = "%" + hotelQuery + "%";
            queryHotelStatement.setString(1, quantifier);
            ResultSet hotelResultSet = queryHotelStatement.executeQuery();
            boolean wordFlag = false;

            while(hotelResultSet.next()){
                if(!wordFlag){
                    wordFlag = true;
                }
                String hotelID = hotelResultSet.getString("hotel_id");
                String hotelName = hotelResultSet.getString("hotel_name");
                String address = hotelResultSet.getString("address");
                String city = hotelResultSet.getString("city");
                String state = hotelResultSet.getString("state");
                String country = "USA";
                BigDecimal latitude = hotelResultSet.getBigDecimal("latitude");
                BigDecimal longitude = hotelResultSet.getBigDecimal("longitude");

                queriedHotels.put(hotelID, new Hotel(hotelName, hotelID, latitude, longitude, address, city, state, country));
            }

            if(!wordFlag){
                queryHotelStatement = dbConnection.prepareStatement(SQLStatements.QUERY_HOTELID);
                queryHotelStatement.setString(1, hotelQuery);
                ResultSet hotelIDResultSet = queryHotelStatement.executeQuery();
                while(hotelIDResultSet.next()){
                    String hotelID = hotelIDResultSet.getString("hotel_id");
                    String hotelName = hotelIDResultSet.getString("hotel_name");
                    String address = hotelIDResultSet.getString("address");
                    String city = hotelIDResultSet.getString("city");
                    String state = hotelIDResultSet.getString("state");
                    String country = "USA";
                    BigDecimal latitude = hotelIDResultSet.getBigDecimal("latitude");
                    BigDecimal longitude = hotelIDResultSet.getBigDecimal("longitude");

                    queriedHotels.put(hotelID, new Hotel(hotelName, hotelID, latitude, longitude, address, city, state, country));
                }
            }

            return queriedHotels;
        }
        catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }

    public Hotel getSingleHotel(String hotelIDQuery){
        PreparedStatement getSingleHotelStatement;
        Hotel hotel = null;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getSingleHotelStatement = dbConnection.prepareStatement(SQLStatements.QUERY_SINGLE_HOTEL);
            getSingleHotelStatement.setString(1, hotelIDQuery);
            ResultSet hotelResultSet = getSingleHotelStatement.executeQuery();
            while(hotelResultSet.next()){
                String hotelID = hotelResultSet.getString("hotel_id");
                String hotelName = hotelResultSet.getString("hotel_name");
                String address = hotelResultSet.getString("address");
                String city = hotelResultSet.getString("city");
                String state = hotelResultSet.getString("state");
                String country = "USA";
                BigDecimal latitude = hotelResultSet.getBigDecimal("latitude");
                BigDecimal longitude = hotelResultSet.getBigDecimal("longitude");

                hotel = new Hotel(hotelName, hotelID, latitude, longitude, address, city, state, country);
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return hotel;
    }

    public TreeSet<HotelReview> showReviewQuery(String hotelQuery) {
        PreparedStatement queryHotelStatement;
        TreeSet<HotelReview> hotelReviewSet = new TreeSet<>();
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            queryHotelStatement = dbConnection.prepareStatement(SQLStatements.QUERY_REVIEWS);
            queryHotelStatement.setString(1, hotelQuery);
            ResultSet hotelResultSet = queryHotelStatement.executeQuery();
            while (hotelResultSet.next()) {
                String hotelID = hotelResultSet.getString("hotel_id");
                String reviewID = hotelResultSet.getString("review_id");
                Double rating = hotelResultSet.getDouble("rating");
                String title = hotelResultSet.getString("title");
                String userNickname = hotelResultSet.getString("user_nickname");
                String reviewText = hotelResultSet.getString("review_text");
                String submissionDate = hotelResultSet.getString("submission_date");
                int likes = hotelResultSet.getInt("likes");
                HotelReview newHotelReview = new HotelReview(hotelID, reviewID, rating, title, reviewText, userNickname, submissionDate, likes);
                hotelReviewSet.add(newHotelReview);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return hotelReviewSet;
    }

    public TreeSet<HotelReview> showOffsetReviewQuery(String hotelQuery, String page) {
        int offSet = ((Integer.parseInt(page)-1)*5);
        if(page.equals("1")){
            offSet = 0;
        }
        System.out.println("Page: " + page);
        System.out.println("OffSet: " + offSet);

        PreparedStatement queryOffsetHotelStatement;
        TreeSet<HotelReview> hotelReviewOffset = new TreeSet<>();
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            queryOffsetHotelStatement = dbConnection.prepareStatement(SQLStatements.QUERY_OFFSET_REVIEWS);
            queryOffsetHotelStatement.setString(1, hotelQuery);
            queryOffsetHotelStatement.setInt(2, offSet);
            System.out.println(queryOffsetHotelStatement);
            ResultSet hotelResultSet = queryOffsetHotelStatement.executeQuery();
            while (hotelResultSet.next()) {
                String hotelID = hotelResultSet.getString("hotel_id");
                String reviewID = hotelResultSet.getString("review_id");
                Double rating = hotelResultSet.getDouble("rating");
                String title = hotelResultSet.getString("title");
                String userNickname = hotelResultSet.getString("user_nickname");
                String reviewText = hotelResultSet.getString("review_text");
                String submissionDate = hotelResultSet.getString("submission_date");
                int likes = hotelResultSet.getInt("likes");
                HotelReview newHotelReview = new HotelReview(hotelID, reviewID, rating, title, reviewText, userNickname, submissionDate, likes);
                hotelReviewOffset.add(newHotelReview);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return hotelReviewOffset;
    }

    public HotelReview getSingleReview(String reviewIDQuery) {
        PreparedStatement getSingleReviewStatement;
        HotelReview hotelReview = null;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getSingleReviewStatement = dbConnection.prepareStatement(SQLStatements.QUERY_SINGLE_REVIEW);
            getSingleReviewStatement.setString(1, reviewIDQuery);
            ResultSet reviewResultSet = getSingleReviewStatement.executeQuery();
            while(reviewResultSet.next()){
                String hotelID = reviewResultSet.getString("hotel_id");
                String reviewID = reviewResultSet.getString("review_id");
                Double rating = reviewResultSet.getDouble("rating");
                String title = reviewResultSet.getString("title");
                String userNickname = reviewResultSet.getString("user_nickname");
                String reviewText = reviewResultSet.getString("review_text");
                String submissionDate = reviewResultSet.getString("submission_date");
                int likes =reviewResultSet.getInt("likes");
                hotelReview = new HotelReview(hotelID, reviewID, rating, title, reviewText, userNickname, submissionDate, likes);
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return hotelReview;
    }

    public void updateReview(String reviewID, String rating, String reviewText){
        PreparedStatement editReviewStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            editReviewStatement = dbConnection.prepareStatement(SQLStatements.UPDATE_REVIEW);
            editReviewStatement.setString(1, rating);
            editReviewStatement.setString(2, reviewText);
            editReviewStatement.setString(3, reviewID);
            editReviewStatement.executeUpdate();
            editReviewStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addReview(String hotelID, String reviewID, String rating, String title, String reviewText, String userNickname, String submissionDate){
        PreparedStatement addReviewStatement;

        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            addReviewStatement = dbConnection.prepareStatement(SQLStatements.ADD_REVIEW);
            addReviewStatement.setString(1, reviewID);
            addReviewStatement.setString(2, hotelID);
            addReviewStatement.setString(3, rating);
            addReviewStatement.setString(4, title);
            addReviewStatement.setString(5, reviewText);
            addReviewStatement.setString(6, userNickname);
            addReviewStatement.setString(7, submissionDate);
            addReviewStatement.setString(8, "0");

            addReviewStatement.executeUpdate();
            addReviewStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteReview(String reviewIDQuery){
        PreparedStatement deleteReviewStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            deleteReviewStatement = dbConnection.prepareStatement(SQLStatements.DELETE_REVIEW);
            deleteReviewStatement.setString(1, reviewIDQuery);
            deleteReviewStatement.executeUpdate();
            deleteReviewStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public int getReviewCount(String hotelIDQuery){
        PreparedStatement getReviewCountStatement;
        int count = 0;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getReviewCountStatement = dbConnection.prepareStatement(SQLStatements.GET_REVIEW_COUNT);
            getReviewCountStatement.setString(1, hotelIDQuery);
            ResultSet reviewCountResultSet = getReviewCountStatement.executeQuery();
            if(reviewCountResultSet.next()){
                count = reviewCountResultSet.getInt("Count(*)");
            }
            return count;
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return count;
    }

    public double getAverageRating(String hotelIDQuery){
        PreparedStatement getAverageRatingStatement;
        double averageRating = 0;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));) {
            getAverageRatingStatement = dbConnection.prepareStatement(SQLStatements.GET_AVG_REVIEW_RATING);
            getAverageRatingStatement.setString(1, hotelIDQuery);
            ResultSet reviewRatingResultSet = getAverageRatingStatement.executeQuery();
            if(reviewRatingResultSet.next()){
                averageRating = reviewRatingResultSet.getDouble("avg(rating)");
            }
            return averageRating;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return averageRating;

    }

    public Map<String, String> getFavoriteHotels(int userID){
        PreparedStatement getFavoriteHotelStatement;
        Map<String, String> favoriteHotels = new HashMap<>();
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getFavoriteHotelStatement = dbConnection.prepareStatement(SQLStatements.GET_FAVORITE_HOTEL);
            getFavoriteHotelStatement.setInt(1, userID);
            ResultSet favoriteHotelResultSet = getFavoriteHotelStatement.executeQuery();
            while(favoriteHotelResultSet.next()){
                favoriteHotels.put(favoriteHotelResultSet.getString("hotel_id"),favoriteHotelResultSet.getString("hotel_name"));
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return favoriteHotels;
    }

    public void addFavoriteHotel(String userID, String hotelID){
        PreparedStatement addFavoriteHotelStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            addFavoriteHotelStatement = dbConnection.prepareStatement(SQLStatements.ADD_FAVORITE_HOTEL);
            addFavoriteHotelStatement.setString(1, userID);
            addFavoriteHotelStatement.setString(2, hotelID);
            addFavoriteHotelStatement.executeUpdate();
            addFavoriteHotelStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public List<String> getExpediaHistory(String userID){
        PreparedStatement getExpediaHistoryStatement;
        List<String> expediaHistory = new ArrayList<>();
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getExpediaHistoryStatement = dbConnection.prepareStatement(SQLStatements.SHOW_EXPEDIA_HISTORY);
            getExpediaHistoryStatement.setString(1, userID);
            ResultSet getExpediaHistoryResultSet = getExpediaHistoryStatement.executeQuery();
            while(getExpediaHistoryResultSet.next()){
                expediaHistory.add(getExpediaHistoryResultSet.getString("url"));
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return expediaHistory;

    }

    public String validateURL(String userID, String url){
        PreparedStatement getURLStatement;
        String returnURL = "";
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getURLStatement = dbConnection.prepareStatement(SQLStatements.CHECK_EXIST_EXPEDIA_HISTORY);
            getURLStatement.setString(1, userID);
            getURLStatement.setString(2, url);
            ResultSet getURLResultSet = getURLStatement.executeQuery();

            if(getURLResultSet.next()){
                returnURL = getURLResultSet.getString("url");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return returnURL;
    }

    public void addExpediaHistory(String userID, String url){
        PreparedStatement addExpediaURLStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            addExpediaURLStatement = dbConnection.prepareStatement(SQLStatements.ADD_EXPEDIA_HISTORY);
            addExpediaURLStatement.setString(1, userID);
            addExpediaURLStatement.setString(2, url);
            addExpediaURLStatement.executeUpdate();
            addExpediaURLStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteExpediaHistory(String userID){
        PreparedStatement deleteExpediaHistoryStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            deleteExpediaHistoryStatement= dbConnection.prepareStatement(SQLStatements.DELETE_EXPEDIA_HISTORY);
            deleteExpediaHistoryStatement.setString(1, userID);
            deleteExpediaHistoryStatement.executeUpdate();
            deleteExpediaHistoryStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public String getLoginHistory(String userID){
        PreparedStatement getLoginHistoryStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            getLoginHistoryStatement = dbConnection.prepareStatement(SQLStatements.GET_LOGIN_HISTORY);
            getLoginHistoryStatement.setString(1, userID);
            System.out.println("Querying login history");
            ResultSet getLoginHistoryResultSet = getLoginHistoryStatement.executeQuery();
            if(getLoginHistoryResultSet.next()){
                String returnTimestamp = getLoginHistoryResultSet.getString("time_stamp");
                System.out.println(returnTimestamp);
                return returnTimestamp;
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

        System.out.println("Not logged in before reached");
        return "You have not logged in before";

    }

    public void newLoginHistory(String userID, String timestamp){
        PreparedStatement newLoginHistoryStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            newLoginHistoryStatement = dbConnection.prepareStatement(SQLStatements.NEW_LOGIN_HISTORY);
            newLoginHistoryStatement.setString(1, userID);
            newLoginHistoryStatement.setString(2, timestamp);
            newLoginHistoryStatement.executeUpdate();
            newLoginHistoryStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateLoginHistory(String userID, String timestamp){
        PreparedStatement updateLoginHistoryStatement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));){
            updateLoginHistoryStatement = dbConnection.prepareStatement(SQLStatements.UPDATE_LOGIN_HISTORY);
            updateLoginHistoryStatement.setString(1, timestamp);
            updateLoginHistoryStatement.setString(2, userID);
            updateLoginHistoryStatement.executeUpdate();
            updateLoginHistoryStatement.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void createKeywordTable(String keyword){
        Statement statement;
        try(Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))){
            System.out.println("dbConnection successful, adding new keyword table.");
            statement = dbConnection.createStatement();
            statement.executeUpdate(SQLStatements.CREATE_KEYWORD_TABLE(keyword));
        }
        catch(SQLException ex){
            System.out.println(ex);
        }
    }
}
