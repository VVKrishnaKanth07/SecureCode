package com.example.rbac.demo;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.Base64;

@SpringBootApplication
public class HospitalSystem {
    private static final Map<String, Admin> admins = new HashMap<>();
    private static final Map<String, SecretKey> adminKeys = new HashMap<>();
    private static final Map<String, List<String>> patientRecords = new HashMap<>();
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
        adminKeys.put(username, generateSecretKey());
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
            loginAttempts.put(username, 0);
            System.out.println("Login successful!");
            managePatients(scanner, username);
            return true;
        } else {
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
            System.out.println("Invalid passcode. Attempts left: " + (MAX_ATTEMPTS - loginAttempts.get(username)));
            return false;
        }
    }

    public static void managePatients(Scanner scanner, String adminUsername) {
        while (true) {
            System.out.println("\nPatient Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    addPatient(scanner, adminUsername);
                    break;
                case 2:
                    viewPatients(adminUsername);
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void addPatient(Scanner scanner, String adminUsername) {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        String age = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine();
        System.out.print("Enter history of illness: ");
        String history = scanner.nextLine();

        SecretKey key = adminKeys.get(adminUsername);
        String encryptedData = encrypt(name + "," + age + "," + email + "," + ssn + "," + history, key);

        patientRecords.computeIfAbsent(adminUsername, k -> new ArrayList<>()).add(encryptedData);
        System.out.println("Patient data added securely.");
    }

    public static void viewPatients(String adminUsername) {
        SecretKey key = adminKeys.get(adminUsername);
        List<String> records = patientRecords.getOrDefault(adminUsername, new ArrayList<>());
        if (records.isEmpty()) {
            System.out.println("No patient records found.");
        } else {
            System.out.println("Decrypted Patient Records:");
            for (String record : records) {
                System.out.println(decrypt(record, key));
            }
        }
    }

    public static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating encryption key", e);
        }
    }

    public static String encrypt(String data, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedData, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
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
                    loginAdmin(scanner);
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
