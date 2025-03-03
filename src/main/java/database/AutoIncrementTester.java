package database;

public class AutoIncrementTester {
    public static void main(String[] args) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.checkAutoIncrement();
    }
}
