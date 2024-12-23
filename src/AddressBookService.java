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
                System.out.println("1. Add Contact");
                System.out.println("2. View Contacts");
                System.out.println("3. Edit Contact");
                System.out.println("4. Delete Contact");
                System.out.println("5. Retrieve Contacts by City or State");
                System.out.println("6. Get Contact Count by City or State");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> addContact(statement, scanner);
                    case 2 -> viewContacts(statement);
                    case 3 -> editContact(statement, scanner);
                    case 4 -> deleteContact(statement, scanner);
                    case 5 -> retrieveContactsByLocation(statement, scanner);
                    case 6 -> getContactCountByLocation(statement, scanner);
                    case 7 -> {
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

        String insertSQL = String.format("INSERT INTO contacts (first_name, last_name, address, city, state, zip, phone_number, email) "
                + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", firstName, lastName, address, city, state, zip, phoneNumber, email);
        try {
            statement.executeUpdate(insertSQL);
            System.out.println("Contact added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }

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

    private static void editContact(Statement statement, Scanner scanner) {
        System.out.print("Enter First Name of the Contact to Edit: ");
        String firstName = scanner.nextLine();

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

        String updateSQL = String.format("UPDATE contacts SET address = '%s', city = '%s', state = '%s', zip = '%s', phone_number = '%s', email = '%s' WHERE first_name = '%s';",
                address, city, state, zip, phoneNumber, email, firstName);
        try {
            int rowsAffected = statement.executeUpdate(updateSQL);
            if (rowsAffected > 0) {
                System.out.println("Contact updated successfully!");
            } else {
                System.out.println("No contact found with the given name.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
        }
    }

    private static void deleteContact(Statement statement, Scanner scanner) {
        System.out.print("Enter First Name of the Contact to Delete: ");
        String firstName = scanner.nextLine();

        String deleteSQL = String.format("DELETE FROM contacts WHERE first_name = '%s';", firstName);
        try {
            int rowsAffected = statement.executeUpdate(deleteSQL);
            if (rowsAffected > 0) {
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("No contact found with the given name.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting contact: " + e.getMessage());
        }
    }

    private static void retrieveContactsByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State to Retrieve Contacts: ");
        String location = scanner.nextLine();

        String retrieveSQL = String.format("SELECT * FROM contacts WHERE city = '%s' OR state = '%s';", location, location);
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

    private static void getContactCountByLocation(Statement statement, Scanner scanner) {
        System.out.print("Enter City or State to Get Contact Count: ");
        String location = scanner.nextLine();

        String countSQL = String.format("SELECT COUNT(*) FROM contacts WHERE city = '%s' OR state = '%s';", location, location);
        try (ResultSet resultSet = statement.executeQuery(countSQL)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("Number of contacts in " + location + ": " + count);
            }
        } catch (SQLException e) {
            System.err.println("Error getting contact count: " + e.getMessage());
        }
    }
}
