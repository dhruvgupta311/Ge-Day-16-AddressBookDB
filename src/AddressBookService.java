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

            // Create Address Book table if not exists
            AddressBookDatabase.createTables(statement);

            // Menu loop
            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> AddressBookDatabase.addContact(statement, scanner); // Add contact to both Friend and Family
                    case 2 -> AddressBookDatabase.viewContacts(statement);
                    case 3 -> AddressBookDatabase.editContact(statement, scanner);
                    case 4 -> AddressBookDatabase.deleteContact(statement, scanner);
                    case 5 -> AddressBookDatabase.retrieveContactsByLocation(statement, scanner);
                    case 6 -> AddressBookDatabase.getContactCountByLocation(statement, scanner);
                    case 7 -> AddressBookDatabase.retrieveSortedContactsByCity(statement, scanner);
                    case 8 -> {
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

    // Display menu options
    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add Contact");
        System.out.println("2. View Contacts");
        System.out.println("3. Edit Contact");
        System.out.println("4. Delete Contact");
        System.out.println("5. Retrieve Contacts by City or State");
        System.out.println("6. Get Contact Count by City or State");
        System.out.println("7. Retrieve Sorted Contacts by City");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }
}
