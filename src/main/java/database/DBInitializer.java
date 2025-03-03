package database;

import java.sql.SQLException;

public class DBInitializer {

    public static void initialize() throws SQLException {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.registerUser("user1", "chen2001517");
        dbHandler.registerUser("user2", "ch2001517");
        dbHandler.registerUser("user4", "testpass");

    }

    public static void main(String[] args) throws SQLException {
        initialize();
    }
}
