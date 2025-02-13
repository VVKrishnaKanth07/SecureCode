package com.example.rbac.demo;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class HospitalSystem {
    private static final Map<String, Admin> admins = new HashMap<>();
    private static final List<String> patients = new ArrayList<>();
    private static final Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_ATTEMPTS = 3;

    public static void registerAdmin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (admins.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter passcode: ");
        String password = scanner.nextLine();
        String salt = BCrypt.gensalt();
        admins.put(username, new Admin(username, password, salt));
        System.out.println("Admin registered successfully!");
    }

    public static boolean loginAdmin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        if (!admins.containsKey(username)) {
            System.out.println("Invalid username.");
            return false;
        }

        if (loginAttempts.getOrDefault(username, 0) >= MAX_ATTEMPTS) {
            System.out.println("Too many failed attempts. Account locked.");
            return false;
        }

        System.out.print("Enter passcode: ");
        String password = scanner.nextLine();
        Admin admin = admins.get(username);

        if (admin.verifyPassword(password)) {
            loginAttempts.put(username, 0); // Reset attempts
            System.out.println("Login successful!");
            return true;
        } else {
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
            System.out.println("Invalid passcode. Attempts left: " + (MAX_ATTEMPTS - loginAttempts.get(username)));
            return false;
        }
    }

    public static void managePatients(Scanner scanner) {
        while (true) {
            System.out.println("\nPatient Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    System.out.print("Enter patient name: ");
                    patients.add(scanner.nextLine());
                    System.out.println("Patient added successfully.");
                    break;
                case 2:
                    System.out.println("Patient List: " + patients);
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(HospitalSystem.class, args);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nHospital Admin System");
            System.out.println("1. Register Admin");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    registerAdmin(scanner);
                    break;
                case 2:
                    if (loginAdmin(scanner)) {
                        managePatients(scanner);
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

class Admin {
    private String username;
    private String passwordHash;
    private String salt;

    public Admin(String username, String password, String salt) {
        this.username = username;
        this.salt = salt;
        this.passwordHash = BCrypt.hashpw(password, salt);
    }

    public String getUsername() {
        return username;
    }

    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, passwordHash);
    }
}