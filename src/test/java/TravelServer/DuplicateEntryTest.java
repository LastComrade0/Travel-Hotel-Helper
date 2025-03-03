package TravelServer;

import database.DatabaseHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class DuplicateEntryTest {
    @Test
    void testDuplicateInsert(){
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        assertThrows(SQLException.class, () ->
            dbHandler.registerUser("user1", "chen2001517"), "Unable to register"
        );

        assertThrows(SQLException.class, () ->
            dbHandler.registerUser("user2", "ch2001517"), "Unable to register"
        );


    }
}
