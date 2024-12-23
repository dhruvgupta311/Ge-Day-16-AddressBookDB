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

            // Create Address Book table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS contacts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "first_name VARCHAR(50) NOT NULL,"
                    + "last_name VARCHAR(50) NOT NULL,"
                    + "address VARCHAR(100),"
                    + "city VARCHAR(50),"
                    + "state VARCHAR(50),"
                    + "zip VARCHAR(10),"
                    + "phone_number VARCHAR(15),"
                    + "email VARCHAR(100));";
            statement.execute(createTableSQL);

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Insert Contact");
                System.out.println("2. View Contacts");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter First Name: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Enter Last Name: ");
                        String lastName = scanner.nextLine();
                        System.out.print("Enter Address: ");
                        String address = scanner.nextLine();
                        System.out.print("Enter City: ");
                        String city = scanner.nextLine();
                        System.out.print("Enter State: ");
                        String state = scanner.nextLine();
                        System.out.print("Enter Zip: ");
                        String zip = scanner.nextLine();
                        System.out.print("Enter Phone Number: ");
                        String phoneNumber = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();

                        String insertSQL = String.format("INSERT INTO contacts (first_name, last_name, address, city, state, zip, phone_number, email) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", firstName, lastName, address, city, state, zip, phoneNumber, email);
                        statement.executeUpdate(insertSQL);
                        System.out.println("Contact added successfully!");
                    }
                    case 2 -> {
                        String selectSQL = "SELECT * FROM contacts;";
                        ResultSet resultSet = statement.executeQuery(selectSQL);
                        System.out.println("\nContacts:");
                        while (resultSet.next()) {
                            System.out.printf("ID: %d, First Name: %s, Last Name: %s, Address: %s, City: %s, State: %s, Zip: %s, Phone: %s, Email: %s%n",
                                    resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("state"),
                                    resultSet.getString("zip"),
                                    resultSet.getString("phone_number"),
                                    resultSet.getString("email"));
                        }
                    }
                    case 3 -> {
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
