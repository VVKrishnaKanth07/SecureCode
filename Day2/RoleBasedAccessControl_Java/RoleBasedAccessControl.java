package com.example.rbac.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {
    private static final Map<String, List<String>> roles = new HashMap<>();

    // User-role mapping
    private static final Map<String, String> users = new HashMap<>();

    static {
        roles.put("Radiologist", Arrays.asList("create", "read", "update", "delete (restricted)"));
        roles.put("Physician", Arrays.asList("read", "update (comments only)"));
        roles.put("Lab Technician", Arrays.asList("read (limited)", "update (metadata only)"));
        roles.put("Administrator", Arrays.asList("create", "read", "update", "delete"));
        roles.put("Patient", Arrays.asList("read (own reports only)"));
        roles.put("Billing Staff", Arrays.asList("read (billing info only)"));

        users.put("User1", "Radiologist");
        users.put("User2", "Physician");
        users.put("User3", "Lab Technician");
        users.put("User4", "Administrator");
        users.put("User5", "Patient");
        users.put("User6", "Billing Staff");
    }

    // Function to check if a user has a specific permission
    public static boolean hasPermission(String user, String action) {
        String role = users.get(user); // Get user's role

        if (role != null && roles.containsKey(role)) { // Check if role exists
            return roles.get(role).contains(action); // Check if role has permission
        }

        return false; // User not found or no permission
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
        System.out.println("Test 1: Radiologist can create → " + (DemoApplication.hasPermission("User1", "create") == true));
        System.out.println("Test 2: Radiologist can delete (restricted) → " + (DemoApplication.hasPermission("User1", "delete (restricted)") == true));
        System.out.println("Test 3: Physician can read → " + (DemoApplication.hasPermission("User2", "read") == true));
        System.out.println("Test 4: Physician cannot create → " + (DemoApplication.hasPermission("User2", "create") == false));
        System.out.println("Test 5: Lab Technician can update metadata → " + (DemoApplication.hasPermission("User3", "update (metadata only)") == true));
        System.out.println("Test 6: Lab Technician cannot create → " + (DemoApplication.hasPermission("User3", "create") == false));
        System.out.println("Test 7: Administrator can delete → " + (DemoApplication.hasPermission("User4", "delete") == true));
        System.out.println("Test 8: Patient can read own reports → " + (DemoApplication.hasPermission("User5", "read (own reports only)") == true));
        System.out.println("Test 9: Patient cannot update → " + (DemoApplication.hasPermission("User5", "update") == false));
        System.out.println("Test 10: Billing Staff can read billing info → " + (DemoApplication.hasPermission("User6", "read (billing info only)") == true));
        System.out.println("Test 11: Billing Staff cannot delete → " + (DemoApplication.hasPermission("User6", "delete") == false));
        System.out.println("Test 12: Non-existent user cannot read → " + (DemoApplication.hasPermission("User7", "read") == false));


    }





}
