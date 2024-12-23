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

            // Create Address Book and Contacts tables if not exist
            createTables(statement);

            // Menu loop
            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> addAddressBook(statement, scanner);
                    case 2 -> addContact(statement, scanner);
                    case 3 -> viewContacts(statement);
                    case 4 -> editContact(statement, scanner);
                    case 5 -> deleteContact(statement, scanner);
                    case 6 -> retrieveContactsByLocation(statement, scanner);
                    case 7 -> getContactCountByLocation(statement, scanner);
                    case 8 -> retrieveSortedContactsByCity(statement, scanner);
                    case 9 -> {
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

    // Create Address Books and Contacts tables
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
                    book_id INT,
                    FOREIGN KEY (book_id) REFERENCES address_books(id)
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
        System.out.println("1. Add Address Book");
        System.out.println("2. Add Contact");
        System.out.println("3. View Contacts");
        System.out.println("4. Edit Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Retrieve Contacts by City or State");
        System.out.println("7. Get Contact Count by City or State");
        System.out.println("8. Retrieve Sorted Contacts by City");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }

    // Add a new address book
    private static void addAddressBook(Statement statement, Scanner scanner) {
        System.out.print("Enter Address Book Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Address Book Type (e.g., Family, Friends, Profession): ");
        String type = scanner.nextLine();

        String insertSQL = String.format("""
                INSERT INTO address_books (name, type)
                VALUES ('%s', '%s');
                """, name, type);

        try {
            statement.executeUpdate(insertSQL);
            System.out.println("Address Book added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding address book: " + e.getMessage());
        }
    }

    // Add a new contact to a specific address book
    private static void addContact(Statement statement, Scanner scanner) {
        System.out.print("Enter Address Book Name to Add Contact: ");
        String addressBookName = scanner.nextLine();

        // Retrieve the book_id based on the address book name
        String selectBookSQL = String.format("SELECT id FROM address_books WHERE name = '%s';", addressBookName);
        int bookId = -1;
        try (ResultSet resultSet = statement.executeQuery(selectBookSQL)) {
            if (resultSet.next()) {
                bookId = resultSet.getInt("id");
            } else {
                System.out.println("Address book not found!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving address book: " + e.getMessage());
            return;
        }

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
                INSERT INTO contacts (first_name, last_name, address, city, state, zip, phone_number, email, book_id)
                VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d);
                """, firstName, lastName, address, city, state, zip, phoneNumber, email, bookId);

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

    // Edit an existing contact in a specific address book
    private static void editContact(Statement statement, Scanner scanner) {
        // Get the address book name where the contact belongs
        System.out.print("Enter Address Book Name to Edit Contact: ");
        String addressBookName = scanner.nextLine();

        // Retrieve the book_id based on the address book name
        String selectBookSQL = String.format("SELECT id FROM address_books WHERE name = '%s';", addressBookName);
        int bookId = -1;
        try (ResultSet resultSet = statement.executeQuery(selectBookSQL)) {
            if (resultSet.next()) {
                bookId = resultSet.getInt("id");
            } else {
                System.out.println("Address book not found!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving address book: " + e.getMessage());
            return;
        }

        // Get the contact details
        System.out.print("Enter First Name of the Contact to Edit: ");
        String firstName = scanner.nextLine();

        // Retrieve the contact details based on the name and address book
        String selectContactSQL = String.format("""
                SELECT * FROM contacts WHERE first_name = '%s' AND book_id = %d;
                """, firstName, bookId);
        try (ResultSet resultSet = statement.executeQuery(selectContactSQL)) {
            if (resultSet.next()) {
                System.out.print("Enter New Last Name: ");
                String lastName = scanner.nextLine();
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

                // Update the contact details
                String updateSQL = String.format("""
                        UPDATE contacts
                        SET last_name = '%s', address = '%s', city = '%s', state = '%s', zip = '%s',
                            phone_number = '%s', email = '%s'
                        WHERE first_name = '%s' AND book_id = %d;
                        """, lastName, address, city, state, zip, phoneNumber, email, firstName, bookId);

                int rowsAffected = statement.executeUpdate(updateSQL);
                if (rowsAffected > 0) {
                    System.out.println("Contact updated successfully!");
                } else {
                    System.out.println("No contact found with the given name in this address book.");
                }
            } else {
                System.out.println("Contact not found in the specified address book.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
        }
    }

    // Delete a contact
    private static void deleteContact(Statement statement, Scanner scanner) {
        System.out.print("Enter Address Book Name to Delete Contact: ");
        String addressBookName = scanner.nextLine();

        // Retrieve the book_id based on the address book name
        String selectBookSQL = String.format("SELECT id FROM address_books WHERE name = '%s';", addressBookName);
        int bookId = -1;
        try (ResultSet resultSet = statement.executeQuery(selectBookSQL)) {
            if (resultSet.next()) {
                bookId = resultSet.getInt("id");
            } else {
                System.out.println("Address book not found!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving address book: " + e.getMessage());
            return;
        }

        // Get the contact details
        System.out.print("Enter First Name of the Contact to Delete: ");
        String firstName = scanner.nextLine();

        String deleteSQL = String.format("DELETE FROM contacts WHERE first_name = '%s' AND book_id = %d;", firstName, bookId);
        try {
            int rowsAffected = statement.executeUpdate(deleteSQL);
            if (rowsAffected > 0) {
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("No contact found with the given name in this address book.");
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

        String retrieveSortedSQL = String.format("""
                SELECT * FROM contacts WHERE city = '%s' ORDER BY first_name;
                """, city);
        try (ResultSet resultSet = statement.executeQuery(retrieveSortedSQL)) {
            System.out.println("\nSorted Contacts in " + city + ":");
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
            System.err.println("Error retrieving sorted contacts: " + e.getMessage());
        }
    }
}
