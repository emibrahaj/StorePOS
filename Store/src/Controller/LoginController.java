package Controller;

import DAO.AppPaths;
import DAO.PasswordManager;
import Util.InputValidator;
import Util.SharedContext;
import Util.AppNavigator;
import Util.Logger;
import View.LoginView;
import View.AdminDashboardView;
import View.CashierDashboardView;
import View.ManagerDashboardView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Model.Manager;
import Model.Cashier;
import Model.Inventory;

public class LoginController {
    private LoginView view;
    private AppNavigator navigator;
    private List<String[]> credentials;  // Store credentials in a list of arrays
    private boolean isLoggedIn;

    public LoginController(LoginView view) {
        this(view, null);
    }

    public LoginController(LoginView view, AppNavigator navigator) {
        this.view = view;
        this.navigator = navigator;
        this.credentials = new ArrayList<>();
        this.isLoggedIn = false;
        loadCredentials();
    }

    public void setNavigator(AppNavigator navigator) {
        this.navigator = navigator;
    }

    // Load credentials from the passwords.txt file
    private void loadCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader(AppPaths.daoFile("passwords.txt").toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                credentials.add(userData);  // Store each line as an array of strings
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load credentials file: " + e.getMessage());
        }
    }

    // Login action triggered by the user
    public void loginAction() {
        String username = view.getUsername();
        String password = view.getPassword();
        String email = view.getEmail();
        String phoneNumber = view.getPhoneNumber();
        String selectedRole = view.getSelectedRole();

        // Validate all input fields
        if (!InputValidator.isValidUsername(username)) {
            showAlert("Invalid Username", InputValidator.getValidationErrorMessage("Username", "username"));
            return;
        }
        
        if (!InputValidator.isValidPassword(password)) {
            showAlert("Invalid Password", InputValidator.getValidationErrorMessage("Password", "password"));
            return;
        }
        
        if (!InputValidator.isValidEmail(email)) {
            showAlert("Invalid Email", InputValidator.getValidationErrorMessage("Email", "email"));
            return;
        }
        
        if (!InputValidator.isValidPhoneNumber(phoneNumber)) {
            showAlert("Invalid Phone Number", InputValidator.getValidationErrorMessage("Phone Number", "phone"));
            return;
        }
        
        if (selectedRole == null || selectedRole.isBlank()) {
            showAlert("Missing Role", "Please select a role.");
            return;
        }

        boolean validLogin = false;
        String loggedInUsername = null;
        String loggedInRole = null;

        // Check credentials based on the selected role
        for (String[] user : credentials) {
            if (selectedRole.equals(user[4]) && username.equals(user[0]) && email.equals(user[2]) 
                    && phoneNumber.equals(user[3])) {
                // Verify password using PasswordManager (supports both hashed and plaintext for backward compatibility)
                boolean passwordMatches = false;
                try {
                    // Try hashed password verification first
                    passwordMatches = PasswordManager.verifyPassword(password, user[1]);
                } catch (Exception e) {
                    // Fallback to plaintext comparison for backward compatibility
                    passwordMatches = password.equals(user[1]);
                }
                
                if (passwordMatches) {
                    validLogin = true;
                    loggedInUsername = username;
                    loggedInRole = selectedRole;
                    break;
                }
            }
        }

        if (validLogin) {
            isLoggedIn = true;
            
            // Initialize shared context with current user
            SharedContext context = SharedContext.getInstance();
            context.setCurrentUsername(loggedInUsername);
            context.setCurrentUserRole(loggedInRole);
            
            Logger.info("LoginController", loggedInRole + " " + loggedInUsername + " logged in successfully");
            updateViewStatus(loggedInRole + " " + loggedInUsername + " logged in.");

            // Use navigator for screen transitions (delegate to AppNavigator)
            if (navigator != null) {
                if (loggedInRole.equals("Admin")) {
                    navigator.navigateToAdminDashboard();
                } else if (loggedInRole.equals("Manager")) {
                    navigator.navigateToManagerDashboard(loggedInUsername, password, email, phoneNumber);
                } else if (loggedInRole.equals("Cashier")) {
                    navigator.navigateToCashierDashboard(loggedInUsername, password, email, phoneNumber);
                }
            } else {
                // Fallback if navigator not set (for backward compatibility)
                if (loggedInRole.equals("Admin")) {
                    openAdminDashboard();
                } else if (loggedInRole.equals("Manager")) {
                    Manager manager = new Manager(loggedInUsername, password, email, phoneNumber, phoneNumber);
                    ManagerController managerController = new ManagerController(manager);
                    openManagerDashboard(managerController);
                } else if (loggedInRole.equals("Cashier")) {
                    Inventory sharedInventory = context.getInventory();
                    Manager manager = new Manager("system", "internal", "System Manager", "system@store.local", "0000000000");
                    Cashier cashier = new Cashier(loggedInUsername, password, email, phoneNumber, "Electronics", sharedInventory);
                    CashierController cashierController = new CashierController(cashier, sharedInventory);
                    openCashierDashboard(cashierController);
                }
            }


        } else {
            Logger.warn("LoginController", "Login failed for user: " + username);
            showAlert("Login Failed", "Invalid username, password, or other credentials.");
        }
    }



    // Update the login status on the view
    private void updateViewStatus(String status) {
        Platform.runLater(() -> view.setStatus(status));
    }

    // Open the Admin Dashboard
    private void openAdminDashboard() {
        AdminDashboardView adminDashboardView = new AdminDashboardView();
        Stage stage = (Stage) view.getLoginButton().getScene().getWindow();
        adminDashboardView.show(stage);
    }

    // Open the Manager Dashboard
    private void openManagerDashboard(ManagerController managerController) {
        Stage stage = (Stage) view.getLoginButton().getScene().getWindow();
        ManagerDashboardView managerDashboardView = new ManagerDashboardView(stage, managerController);
        managerDashboardView.start(stage);
    }

    // Open the Cashier Dashboard
    private void openCashierDashboard(CashierController cashierController) {
        Stage stage = (Stage) view.getLoginButton().getScene().getWindow();
        CashierDashboardView cashierDashboardView = new CashierDashboardView(stage, cashierController);
        cashierDashboardView.start();
    }

    // Logout action triggered by the user
    public void logoutAction() {
        if (isLoggedIn) {
            isLoggedIn = false;
            updateViewStatus("User logged out.");
        } else {
            updateViewStatus("No user is logged in.");
        }
    }
    
    

    // Show alert method for displaying messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

