package Controller;

import DAO.AppPaths;
import DAO.PasswordManager;
import Util.InputValidator;
import Model.Employee;
import Model.Role;
import Model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminController {
    private static final Path PASSWORD_FILE = AppPaths.daoFile("passwords.txt");
    private final List<User> employees;

    public AdminController() {
        employees = new ArrayList<>();
        loadEmployees();
    }

    public List<User> getEmployees() {
        return Collections.unmodifiableList(employees);
    }

    public void addEmployee(String username, String password, String name, String email,
                            String phoneNumber, Role role, String accessLevel) {
        validateEmployee(username, password, email, phoneNumber, role);

        if (findEmployee(username) != null) {
            throw new IllegalArgumentException("An employee with this username already exists.");
        }

        // Hash password before storing
        String hashedPassword;
        try {
            hashedPassword = PasswordManager.hashPassword(password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password: " + e.getMessage(), e);
        }

        employees.add(new Employee(username, hashedPassword, name, email, phoneNumber, role, accessLevel));
        saveEmployees();
    }

    public void updateEmployee(String originalUsername, String username, String password, String name,
                               String email, String phoneNumber, Role role, String accessLevel) {
        validateEmployee(username, password, email, phoneNumber, role);

        User existing = findEmployee(originalUsername);
        if (existing == null) {
            throw new IllegalArgumentException("Please select an employee to update.");
        }

        User duplicate = findEmployee(username);
        if (duplicate != null && !duplicate.getUsername().equalsIgnoreCase(originalUsername)) {
            throw new IllegalArgumentException("Another employee already uses this username.");
        }

        existing.setUsername(username);
        try {
            existing.setPassword(PasswordManager.hashPassword(password));
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password: " + e.getMessage(), e);
        }
        existing.setName(name);
        existing.setEmail(email);
        existing.setPhoneNumber(phoneNumber);
        existing.setRole(role);
        existing.setAccessLevel(accessLevel);
        saveEmployees();
    }

    public void deleteEmployee(User employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Please select an employee to delete.");
        }

        employees.removeIf(user -> user.getUsername().equalsIgnoreCase(employee.getUsername()));
        saveEmployees();
    }

    public User findEmployee(String username) {
        if (username == null) {
            return null;
        }

        return employees.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public int getEmployeeCountByRole(Role role) {
        return (int) employees.stream()
                .filter(user -> role.equals(user.getRole()))
                .count();
    }

    private void loadEmployees() {
        employees.clear();

        if (!Files.exists(PASSWORD_FILE)) {
            addDefaultEmployees();
            saveEmployees();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(PASSWORD_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 5) {
                    Role role = parseRole(fields[4]);
                    employees.add(new Employee(fields[0], fields[1], fields[0], fields[2],
                            fields[3], role, defaultAccessLevel(role)));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load employees: " + e.getMessage(), e);
        }

        if (employees.isEmpty()) {
            addDefaultEmployees();
            saveEmployees();
        }
    }

    private void saveEmployees() {
        try {
            Files.createDirectories(PASSWORD_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(PASSWORD_FILE)) {
                for (User employee : employees) {
                    writer.write(String.join(",",
                            employee.getUsername(),
                            employee.getPassword(),
                            employee.getEmail(),
                            employee.getPhoneNumber(),
                            roleToFileValue(employee.getRole())));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not save employees: " + e.getMessage(), e);
        }
    }

    private void addDefaultEmployees() {
        try {
            employees.add(new Employee("adminUser", PasswordManager.hashPassword("adminPass"), "Admin", "klajdi@gmail.com", "0684801184", Role.ADMIN, "Full Access"));
            employees.add(new Employee("managerUser", PasswordManager.hashPassword("managerPass"), "Manager", "manager@example.com", "4466778899", Role.MANAGER, "Inventory and reports"));
            employees.add(new Employee("cashierUser", PasswordManager.hashPassword("cashierPass"), "Cashier", "cashier@example.com", "123456789", Role.CASHIER, "Sales"));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create default employees: " + e.getMessage(), e);
        }
    }

    private void validateEmployee(String username, String password, String email, String phoneNumber, Role role) {
        if (!InputValidator.isValidUsername(username)) {
            throw new IllegalArgumentException(InputValidator.getValidationErrorMessage("Username", "username"));
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException(InputValidator.getValidationErrorMessage("Password", "password"));
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException(InputValidator.getValidationErrorMessage("Email", "email"));
        }
        if (!InputValidator.isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(InputValidator.getValidationErrorMessage("Phone Number", "phone"));
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }
    }

    private Role parseRole(String role) {
        return Role.valueOf(role.trim().toUpperCase());
    }

    private String roleToFileValue(Role role) {
        String value = role.name().toLowerCase();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private String defaultAccessLevel(Role role) {
        switch (role) {
            case ADMIN:
                return "Full Access";
            case MANAGER:
                return "Inventory and reports";
            case CASHIER:
                return "Sales";
            default:
                return "Standard";
        }
    }

}
