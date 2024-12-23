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
                    case 1 -> addContact(statement, scanner); // Add contact to both Friend and Family
                    case 2 -> viewContacts(statement);
                    case 3 -> editContact(statement, scanner);
                    case 4 -> deleteContact(statement, scanner);
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

    // Add a new contact to both Friend and Family address books
    private static void addContact(Statement statement, Scanner scanner) {
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

        // Add both "Friend" and "Family" address books if they don't exist
        int friendAddressBookId = getOrCreateAddressBook(statement, "Friend");
        int familyAddressBookId = getOrCreateAddressBook(statement, "Family");

        // Insert the contact into both Friend and Family address books
        String insertContactSQL = """
                INSERT INTO contacts (first_name, last_name, address, city, state, zip, phone_number, email, address_book_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement ps = statement.getConnection().prepareStatement(insertContactSQL)) {
            // Add to "Friend" address book
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, address);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, zip);
            ps.setString(7, phoneNumber);
            ps.setString(8, email);
            ps.setInt(9, friendAddressBookId);
            ps.executeUpdate();

            // Add to "Family" address book
            ps.setInt(9, familyAddressBookId);
            ps.executeUpdate();

            System.out.println("Contact added to both Friend and Family address books successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }

    // Get or create address book and return its ID
    private static int getOrCreateAddressBook(Statement statement, String type) {
        String checkAddressBookSQL = "SELECT id FROM address_books WHERE name = ? AND type = ?;";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(checkAddressBookSQL)) {
            ps.setString(1, type);
            ps.setString(2, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking address book: " + e.getMessage());
        }

        // If not found, create new address book
        String insertAddressBookSQL = "INSERT INTO address_books (name, type) VALUES (?, ?);";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(insertAddressBookSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setString(2, type);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating address book: " + e.getMessage());
        }

        return -1; // Error case
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
        System.out.print("Enter contact ID to edit: ");
        int contactId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter new Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter new Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter new City: ");
        String city = scanner.nextLine();
        System.out.print("Enter new State: ");
        String state = scanner.nextLine();
        System.out.print("Enter new Zip: ");
        String zip = scanner.nextLine();
        System.out.print("Enter new Phone Number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter new Email: ");
        String email = scanner.nextLine();

        String updateSQL = """
                UPDATE contacts
                SET first_name = ?, last_name = ?, address = ?, city = ?, state = ?, zip = ?, phone_number = ?, email = ?
                WHERE id = ?;
                """;

        try (PreparedStatement ps = statement.getConnection().prepareStatement(updateSQL)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, address);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, zip);
            ps.setString(7, phoneNumber);
            ps.setString(8, email);
            ps.setInt(9, contactId);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Contact updated successfully!");
            } else {
                System.out.println("Contact not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
        }
    }

    // Delete a contact
    private static void deleteContact(Statement statement, Scanner scanner) {
        System.out.print("Enter contact ID to delete: ");
        int contactId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String deleteSQL = "DELETE FROM contacts WHERE id = ?;";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(deleteSQL)) {
            ps.setInt(1, contactId);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("Contact not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting contact: " + e.getMessage());
        }
    }

    // Retrieve contacts by City or State
    private static void retrieveContactsByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State: ");
        String location = scanner.nextLine();

        String selectSQL = "SELECT * FROM contacts WHERE city = ? OR state = ?;";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(selectSQL)) {
            ps.setString(1, location);
            ps.setString(2, location);

            try (ResultSet resultSet = ps.executeQuery()) {
                System.out.println("\nContacts in " + location + ":");
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
        } catch (SQLException e) {
            System.err.println("Error retrieving contacts: " + e.getMessage());
        }
    }

    // Get contact count by City or State
    private static void getContactCountByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State: ");
        String location = scanner.nextLine();

        String selectSQL = "SELECT COUNT(*) FROM contacts WHERE city = ? OR state = ?;";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(selectSQL)) {
            ps.setString(1, location);
            ps.setString(2, location);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    System.out.println("Number of contacts in " + location + ": " + count);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting contacts: " + e.getMessage());
        }
    }

    // Retrieve contacts sorted alphabetically by name in a given city
    private static void retrieveSortedContactsByCity(Statement statement, Scanner scanner) {
        System.out.print("Enter City: ");
        String city = scanner.nextLine();

        String selectSQL = "SELECT * FROM contacts WHERE city = ? ORDER BY first_name, last_name;";
        try (PreparedStatement ps = statement.getConnection().prepareStatement(selectSQL)) {
            ps.setString(1, city);

            try (ResultSet resultSet = ps.executeQuery()) {
                System.out.println("\nContacts in " + city + " sorted alphabetically:");
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
        } catch (SQLException e) {
            System.err.println("Error retrieving contacts: " + e.getMessage());
        }
    }
}
