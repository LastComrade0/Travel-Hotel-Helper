package database;

/*
Class for macro SQL statement
 */
public class SQLStatements {

    public static final String REGISTER_USER_SQL =
            "INSERT INTO login_credentials (user_name, hashed_password, user_salt)" +
            "VALUES (?, ?, ?)";

    public static final String SALT_SQL =
            "SELECT user_salt FROM login_credentials WHERE user_name = ?;";

    public static final String AUTH_USER_SQL =
            "SELECT user_name FROM login_credentials" +
                    " Where user_name = ? and hashed_password = ?;";

    public static String CREATE_KEYWORD_TABLE(String keyword){
        String statement = "CREATE " + keyword +
                "( review_id VARCHAR(20) primary key, word_occurrence int);";
        return statement;
    }

    public static final String CHECK_AUTO_INCREMENT =
        "SELECT `AUTO_INCREMENT` " +
        "FROM  INFORMATION_SCHEMA.TABLES " +
        "WHERE TABLE_SCHEMA = 'travelserver' " +
        "AND   TABLE_NAME   = 'login_credentials'; ";


    public static final String RESET_AUTO_INCREMENT =
            "ALTER TABLE login_credentials AUTO_INCREMENT = 0;";

    public static final String VALIDATE_DUPE_NAME =
            "SELECT * FROM login_credentials WHERE user_name = ?;";

    public static final String QUERY_USERID =
            "SELECT user_id FROM login_credentials WHERE user_name = ?;";

    public static final String INSERT_HOTEL =
            "INSERT INTO hotels (hotel_id, hotel_name, address, city, state, latitude, longitude)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String INSERT_REVIEW =
            "INSERT INTO reviews (review_id, hotel_id, rating, title, user_nickname, review_text, submission_date)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String SHOW_HOTEL =
            "SELECT * from hotels;";

    public static final String QUERY_HOTEL =
            "SELECT * from hotels where lower(hotel_name) like lower(?);";

    public static final String QUERY_SINGLE_HOTEL =
            "SELECT * from hotels where hotel_id = ?;";

    public static final String QUERY_HOTELID =
            "SELECT * from hotels where hotel_id = ?;";

    public static final String QUERY_REVIEWS =
            "SELECT * from reviews where hotel_id = ?;";

    public static final String QUERY_OFFSET_REVIEWS =
            "SELECT * from reviews where hotel_id = ? order by submission_date desc, review_id limit 5 offset ?;";

    public static final String QUERY_SINGLE_REVIEW =
            "SELECT * from reviews where review_id = ?;";

    public static final String UPDATE_REVIEW =
            "UPDATE reviews SET rating = ?, review_text = ? WHERE review_id = ?;";

    public static final String ADD_REVIEW =
            "INSERT INTO reviews (review_id, hotel_id, rating, title, review_text, user_nickname, submission_date, likes)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE review_id = ?;";

    public static final String GET_REVIEW_COUNT =
            "SELECT COUNT(*) FROM reviews WHERE hotel_id = ?;";

    public static final String GET_AVG_REVIEW_RATING =
            "SELECT avg(rating) FROM reviews WHERE hotel_id = ?;";

    public static final String GET_FAVORITE_HOTEL =
            "SELECT hotel_name, hotels.hotel_id FROM hotels inner join favorite_hotel on"
                    + " favorite_hotel.hotel_id = hotels.hotel_id WHERE user_id = ?;";

    public static final String ADD_FAVORITE_HOTEL =
            "INSERT INTO favorite_hotel (user_id, hotel_id)"
            + " VALUES (?, ?);";

    public static final String SHOW_EXPEDIA_HISTORY =
            "SELECT url from expedia_history WHERE user_id = ?;";

    public static final String CHECK_EXIST_EXPEDIA_HISTORY =
            "Select url from expedia_history where user_id = ? and url = ?;";

    public static final String ADD_EXPEDIA_HISTORY =
            "INSERT INTO expedia_history (user_id, url) values (?, ?)";

    public static final String DELETE_EXPEDIA_HISTORY =
            "DELETE FROM expedia_history WHERE user_id = ?;";

    public static final String GET_LOGIN_HISTORY =
            "SELECT time_stamp FROM login_history WHERE user_id = ?;";

    public static final String UPDATE_LOGIN_HISTORY =
            "UPDATE login_history SET time_stamp = ? WHERE user_id = ?;";

    public static final String NEW_LOGIN_HISTORY =
            "INSERT INTO login_history (user_id, time_stamp) values (?, ?);";
}
