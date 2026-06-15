package Controller;

import DAO.AppPaths;
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
    private List<String[]> credentials;  // Store credentials in a list of arrays
    private boolean isLoggedIn;

    public LoginController(LoginView view) {
        this.view = view;
        this.credentials = new ArrayList<>();
        this.isLoggedIn = false;
        loadCredentials();
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

        // Check if any of the fields are empty
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || selectedRole == null) {
            showAlert("Missing Information", "Please fill in all fields.");
            return;
        }

        boolean validLogin = false;
        Manager manager = null;
        Cashier cashier = null;

        // Check credentials based on the selected role
        for (String[] user : credentials) {
            if (selectedRole.equals(user[4]) && username.equals(user[0]) && password.equals(user[1])
                    && email.equals(user[2]) && phoneNumber.equals(user[3])) {
                validLogin = true;
                if (selectedRole.equals("Manager")) {
                    manager = new Manager(username, password, user[2], user[3], phoneNumber);
                } else if (selectedRole.equals("Cashier")) {
                    // Initialize inventory for the cashier using the manager's inventory
                    if (manager == null) {
                        manager = new Manager("defaultUser", "defaultPass", "Default Manager", "default@example.com", "123456789");
                        System.out.println("Manager was null, initialized with default values.");
                    }
                    Inventory sharedInventory = manager.getInventory(); // Use the shared manager inventory
                    cashier = new Cashier(username, password, user[2], user[3], phoneNumber, "Electronics", sharedInventory);
                }
                break;
            }
        }

        if (validLogin) {
            isLoggedIn = true;
            updateViewStatus(selectedRole + " " + username + " logged in.");

            // Open appropriate dashboard based on role
            if (selectedRole.equals("Admin")) {
                openAdminDashboard();
            } else if (selectedRole.equals("Manager") && manager != null) {
                ManagerController managerController = new ManagerController(manager);
                openManagerDashboard(managerController);
            } else if (selectedRole.equals("Cashier") && cashier != null) {
                if (manager == null) {
                    manager = new Manager("defaultUser", "defaultPass", "Default Manager", "default@example.com", "123456789");
                    System.out.println("Manager was null, initialized with default values.");
                }
                Inventory sharedInventory = manager.getInventory(); // Use the shared manager inventory
                CashierController cashierController = new CashierController(cashier, sharedInventory);
                openCashierDashboard(cashierController);
            }


        } else {
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

