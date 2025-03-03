package TravelServer;

import database.DatabaseHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthenticateTest {

    @Test
     void testAuthenticate() {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        Assertions.assertTrue(dbHandler.authenticateUser("user1", "chen2001517"));
        Assertions.assertFalse(dbHandler.authenticateUser("user2", "chen2001517"));
        Assertions.assertTrue(dbHandler.authenticateUser("user2", "ch2001517"));
        Assertions.assertTrue(dbHandler.authenticateUser("user4", "testpass"));
    }


}
