package program;

import java.util.Scanner;
import java.io.Console;

public class App {

    public static void main(String[] args) {
        Console console = System.console();
        Scanner kb = new Scanner(System.in);

        String username;
        String password;

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");

            int choice = kb.nextInt();
            kb.nextLine();

            System.out.print("Enter Username: ");
            username = kb.nextLine();

            System.out.print("Enter Password: ");
            char[] passChars = console.readPassword();
            password = new String(passChars);

            User user = null;

            switch (choice) {
                case 1:
                    user = new User(username, password, true);
                    if (user != null) {
                        System.out.println("Welcome back, " + username + "!");
                        displayMenu(user);
                        return;
                    }
                case 2:
                    user = new User(username, password, false);
                    if (user != null) {
                        System.out.println("Welcome, " + username + "!");
                        displayMenu(user);
                        return;
                    }
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu(User user) {
        Scanner kb = new Scanner(System.in);
        Console console = System.console();

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add a new service");
            System.out.println("2. Get a service");
            System.out.println("3. Delete a service");
            System.out.println("4. Exit");

            int menuChoice = kb.nextInt();
            kb.nextLine();

            switch (menuChoice) {
                case 1:
                    System.out.print("Enter service name: ");
                    String service = kb.nextLine();
                    System.out.print("Enter password for " + service + ": ");
                    char[] passwordChars = console.readPassword();
                    String pass = new String(passwordChars);
                    user.addService(service, pass);
                    break;
                case 2:
                    System.out.print("Enter service name to get password: ");
                    String getService = kb.nextLine();
                    String retrievedPassword = user.getService(getService);
                    if (retrievedPassword != null) {
                        System.out.println("Password for " + getService + ": " + retrievedPassword);
                    } else {
                        System.out.println("Password not found for " + getService);
                    }
                    break;
                case 3:
                    System.out.print("Enter service name to delete: ");
                    String deleteService = kb.nextLine();
                    user.deleteService(deleteService);
                    break;
                case 4:
                    System.out.println("Exiting. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
