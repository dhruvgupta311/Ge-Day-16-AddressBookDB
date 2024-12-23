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
            createTables(statement);

            // Menu loop
            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> addContact(statement, scanner);
                    case 2 -> viewContacts(statement);
                    case 3 -> editContact(statement, scanner);  // Fixed method call
                    case 4 -> deleteContact(statement, scanner); // Fixed method call
                    case 5 -> retrieveContactsByLocation(statement, scanner);
                    case 6 -> getContactCountByLocation(statement, scanner);
                    case 7 -> retrieveSortedContactsByCity(statement, scanner);
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

    // Create tables if they don't exist
    private static void createTables(Statement statement) {
        String createAddressBookTableSQL = """
                CREATE TABLE IF NOT EXISTS address_books (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    type VARCHAR(50) NOT NULL
                );
                """;

        String createContactsTableSQL = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    address VARCHAR(100),
                    city VARCHAR(50),
                    state VARCHAR(50),
                    zip VARCHAR(10),
                    phone_number VARCHAR(15),
                    email VARCHAR(100),
                    address_book_id INT,
                    FOREIGN KEY (address_book_id) REFERENCES address_books(id)
                );
                """;

        try {
            statement.execute(createAddressBookTableSQL);
            statement.execute(createContactsTableSQL);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
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

    // Add a new contact
    private static void addContact(Statement statement, Scanner scanner) {
        System.out.print("Enter Address Book ID: ");
        int addressBookId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

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

        String insertSQL = String.format("""
                INSERT INTO contacts (first_name, last_name, address, city, state, zip, phone_number, email, address_book_id)
                VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d);
                """, firstName, lastName, address, city, state, zip, phoneNumber, email, addressBookId);

        try {
            statement.executeUpdate(insertSQL);
            System.out.println("Contact added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }

    // View all contacts
    private static void viewContacts(Statement statement) {
        String selectSQL = "SELECT * FROM contacts;";
        try (ResultSet resultSet = statement.executeQuery(selectSQL)) {
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
        } catch (SQLException e) {
            System.err.println("Error retrieving contacts: " + e.getMessage());
        }
    }

    // Edit a contact
    private static void editContact(Statement statement, Scanner scanner) {
        System.out.print("Enter Contact ID to Edit: ");
        int contactId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter New Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter New City: ");
        String city = scanner.nextLine();
        System.out.print("Enter New State: ");
        String state = scanner.nextLine();
        System.out.print("Enter New Zip: ");
        String zip = scanner.nextLine();
        System.out.print("Enter New Phone Number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter New Email: ");
        String email = scanner.nextLine();

        String updateSQL = String.format("""
                UPDATE contacts SET address = '%s', city = '%s', state = '%s', zip = '%s', phone_number = '%s', email = '%s'
                WHERE id = %d;
                """, address, city, state, zip, phoneNumber, email, contactId);

        try {
            int rowsAffected = statement.executeUpdate(updateSQL);
            if (rowsAffected > 0) {
                System.out.println("Contact updated successfully!");
            } else {
                System.out.println("No contact found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
        }
    }

    // Delete a contact
    private static void deleteContact(Statement statement, Scanner scanner) {
        System.out.print("Enter Contact ID to Delete: ");
        int contactId = scanner.nextInt();

        String deleteSQL = String.format("DELETE FROM contacts WHERE id = %d;", contactId);
        try {
            int rowsAffected = statement.executeUpdate(deleteSQL);
            if (rowsAffected > 0) {
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("No contact found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting contact: " + e.getMessage());
        }
    }

    // Retrieve contacts by City or State
    private static void retrieveContactsByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State to Retrieve Contacts: ");
        String location = scanner.nextLine();

        String retrieveSQL = String.format("""
                SELECT * FROM contacts WHERE city = '%s' OR state = '%s';
                """, location, location);
        try (ResultSet resultSet = statement.executeQuery(retrieveSQL)) {
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
        } catch (SQLException e) {
            System.err.println("Error retrieving contacts by location: " + e.getMessage());
        }
    }

    // Get contact count by City or State
    private static void getContactCountByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State to Get Contact Count: ");
        String location = scanner.nextLine();

        String countSQL = String.format("""
                SELECT COUNT(*) FROM contacts WHERE city = '%s' OR state = '%s';
                """, location, location);
        try (ResultSet resultSet = statement.executeQuery(countSQL)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("Number of contacts in " + location + ": " + count);
            }
        } catch (SQLException e) {
            System.err.println("Error getting contact count: " + e.getMessage());
        }
    }

    // Retrieve contacts sorted alphabetically by name in a given city
    private static void retrieveSortedContactsByCity(Statement statement, Scanner scanner) {
        System.out.print("Enter City to Retrieve Sorted Contacts: ");
        String city = scanner.nextLine();

        String retrieveSQL = String.format("""
                SELECT * FROM contacts WHERE city = '%s' ORDER BY first_name, last_name;
                """, city);
        try (ResultSet resultSet = statement.executeQuery(retrieveSQL)) {
            System.out.println("\nContacts in " + city + " sorted alphabetically:");
            while (resultSet.next()) {
                System.out.printf("ID: %d, First Name: %s, Last Name: %s, Address: %s, State: %s, Zip: %s, Phone: %s, Email: %s%n",
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("address"),
                        resultSet.getString("state"),
                        resultSet.getString("zip"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving sorted contacts: " + e.getMessage());
        }
    }
}
