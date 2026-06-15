package View;

import Controller.AdminController;
import Model.Role;
import Model.User;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class AdminDashboardView {
    private AdminController controller;
    private UserView userView;
    private VBox contentArea;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private ComboBox<Role> roleComboBox;
    private TextField accessField;

    public void show(Stage primaryStage) {
        controller = new AdminController();
        userView = new UserView();
        userView.setUsers(controller.getEmployees());

        final BorderPane mainLayout = new BorderPane();
        
    //set style for admin dashboard display
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #1E90FF; -fx-padding: 15;");
        Label welcomeLabel = new Label("Welcome, Admin");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        welcomeLabel.setTextFill(Color.WHITE);
        header.getChildren().add(welcomeLabel);

        VBox sidebar = new VBox(20);
        sidebar.setStyle("-fx-background-color: #4682B4;");
        sidebar.setPadding(new javafx.geometry.Insets(20));
        //create buttons on the left side 
        Button manageEmployeesButton = createSidebarButton("Manage Employees");
        Button viewReportsButton = createSidebarButton("View Reports");
        Button accessLevelButton = createSidebarButton("View Access");
        Button logoutButton = createSidebarButton("Logout");

        manageEmployeesButton.setOnAction(e -> showEmployeeManagement());
        viewReportsButton.setOnAction(e -> showReports());
        accessLevelButton.setOnAction(e -> showAccessLevels());
        logoutButton.setOnAction(e -> {
            primaryStage.close();
            new LoginView().start(new Stage());
        });

        sidebar.getChildren().addAll(manageEmployeesButton, viewReportsButton, accessLevelButton, logoutButton);

        contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: #F0F8FF;");
        contentArea.setPadding(new Insets(20));

        showEmployeeManagement();

        HBox quickActions = new HBox(20);
        quickActions.setStyle("-fx-background-color: #87CEFA;");
        quickActions.setPadding(new Insets(10));
        //create action buttons on the bottom
        Button addEmployeeButton = createActionButton("Add Employee");
        Button deleteEmployeeButton = createActionButton("Delete Employee");
        Button updateDetailsButton = createActionButton("Update Details");
        Button clearButton = createActionButton("Clear Form");

        addEmployeeButton.setOnAction(e -> addEmployee());
        deleteEmployeeButton.setOnAction(e -> deleteSelectedEmployee());
        updateDetailsButton.setOnAction(e -> updateSelectedEmployee());
        clearButton.setOnAction(e -> clearForm());

        quickActions.getChildren().addAll(addEmployeeButton, deleteEmployeeButton, updateDetailsButton, clearButton);

        mainLayout.setTop(header);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);
        mainLayout.setBottom(quickActions);

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        button.setPrefWidth(250);
        
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #5F9EA0; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white;"));

        return button;
    }

    private void showEmployeeManagement() {
        contentArea.getChildren().clear();

        Label contentTitle = new Label("Employee Management");
        contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane form = createEmployeeForm();
        userView.setUsers(controller.getEmployees());
        userView.getTableView().setOnMouseClicked(e -> populateForm(userView.getSelectedUser()));

        contentArea.getChildren().addAll(contentTitle, form, userView.getTableView());
    }

    private GridPane createEmployeeForm() {
        usernameField = createTextField("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        nameField = createTextField("Name");
        emailField = createTextField("Email");
        phoneField = createTextField("Phone Number");
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(Role.ADMIN, Role.MANAGER, Role.CASHIER);
        roleComboBox.setPromptText("Role");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        accessField = createTextField("Access Level");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Username:"), usernameField, new Label("Password:"), passwordField);
        form.addRow(1, new Label("Name:"), nameField, new Label("Email:"), emailField);
        form.addRow(2, new Label("Phone:"), phoneField, new Label("Role:"), roleComboBox);
        form.addRow(3, new Label("Access:"), accessField);
        return form;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private void addEmployee() {
        try {
            controller.addEmployee(usernameField.getText(), passwordField.getText(), nameField.getText(),
                    emailField.getText(), phoneField.getText(), roleComboBox.getValue(), accessField.getText());
            refreshEmployeeTable();
            clearForm();
            showAlert("Success", "Employee added successfully.");
        } catch (RuntimeException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void updateSelectedEmployee() {
        User selectedUser = userView.getSelectedUser();
        if (selectedUser == null) {
            showAlert("Error", "Please select an employee to update.");
            return;
        }

        try {
            controller.updateEmployee(selectedUser.getUsername(), usernameField.getText(), passwordField.getText(),
                    nameField.getText(), emailField.getText(), phoneField.getText(), roleComboBox.getValue(),
                    accessField.getText());
            refreshEmployeeTable();
            showAlert("Success", "Employee updated successfully.");
        } catch (RuntimeException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void deleteSelectedEmployee() {
        User selectedUser = userView.getSelectedUser();
        if (selectedUser == null) {
            showAlert("Error", "Please select an employee to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete " + selectedUser.getUsername() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.deleteEmployee(selectedUser);
            refreshEmployeeTable();
            clearForm();
            showAlert("Success", "Employee deleted successfully.");
        }
    }

    private void populateForm(User user) {
        if (user == null) {
            return;
        }

        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        roleComboBox.setValue(user.getRole());
        accessField.setText(user.getAccessLevel());
    }

    private void refreshEmployeeTable() {
        userView.setUsers(controller.getEmployees());
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.setValue(null);
        accessField.clear();
        userView.getTableView().getSelectionModel().clearSelection();
    }

    private void showReports() {
        contentArea.getChildren().clear();

        Label title = new Label("Admin Reports");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label total = new Label("Total Employees: " + controller.getEmployees().size());
        Label admins = new Label("Admins: " + controller.getEmployeeCountByRole(Role.ADMIN));
        Label managers = new Label("Managers: " + controller.getEmployeeCountByRole(Role.MANAGER));
        Label cashiers = new Label("Cashiers: " + controller.getEmployeeCountByRole(Role.CASHIER));

        contentArea.getChildren().addAll(title, total, admins, managers, cashiers);
    }

    private void showAccessLevels() {
        contentArea.getChildren().clear();

        Label title = new Label("Access Levels");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label admin = new Label("Admin: manage employees and access levels.");
        Label manager = new Label("Manager: manage inventory, suppliers, stock, and statistics.");
        Label cashier = new Label("Cashier: create bills, finalize sales, and view daily sales.");

        contentArea.getChildren().addAll(title, admin, manager, cashier);
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #87CEFA; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        button.setPrefWidth(200);
      

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #ADD8E6; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #87CEFA; -fx-text-fill: white;"));

        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
