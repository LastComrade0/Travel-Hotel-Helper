package database;

public class AutoIncrementResetter {
    public static void main(String[] args) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.resetAutoIncrement();
    }
}
