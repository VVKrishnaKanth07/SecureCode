package com.example.rbac.demo;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.*;
import java.util.Base64;

@SpringBootApplication
public class DigitalSignature {
    private static final Map<String, Admin> admins = new HashMap<>();
    private static final Map<String, SecretKey> adminKeys = new HashMap<>();
    private static final Map<String, List<String>> patientRecords = new HashMap<>();
    private static final Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static KeyPair rsaKeyPair = generateRSAKeyPair();

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
            System.out.println("3. Encrypt Patient Data for Doctor");
            System.out.println("4. Verify Digital Signature");
            System.out.println("5. Logout");
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
                    encryptPatientDataForDoctor(adminUsername);
                    break;
                case 4:
                    verifyDigitalSignature(scanner);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void verifyDigitalSignature(Scanner scanner) {
        try {
            System.out.print("Enter the original medical report: ");
            String report = scanner.nextLine();
            System.out.print("Enter the digital signature: ");
            String signature = scanner.nextLine();

            byte[] reportHash = DigitalSignatureUtil.generateHash(report);
            boolean isVerified = DigitalSignatureUtil.verifySignature(reportHash, signature, rsaKeyPair.getPublic());

            if (isVerified) {
                System.out.println("Signature verification successful. The report is authentic.");
            } else {
                System.out.println("Signature verification failed. The report may be tampered with.");
            }
        } catch (Exception e) {
            System.out.println("Error during signature verification: " + e.getMessage());
        }
    }

    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DigitalSignature.class, args);
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
