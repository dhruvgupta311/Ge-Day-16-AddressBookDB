import java.sql.*;
import java.util.Scanner;

public class AddressBookService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/address_book";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Dhruv@2101041cs";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the database successfully!");

            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS contacts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(100),"
                    + "phone VARCHAR(15));";
            statement.execute(createTableSQL);

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. View Contacts");
                System.out.println("2. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> {
                        String selectSQL = "SELECT * FROM contacts;";
                        ResultSet resultSet = statement.executeQuery(selectSQL);
                        System.out.println("\nContacts:");
                        while (resultSet.next()) {
                            System.out.printf("ID: %d, Name: %s, Email: %s, Phone: %s%n",
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("email"),
                                    resultSet.getString("phone"));
                        }
                    }
                    case 2 -> {
                        System.out.println("Exiting... Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
