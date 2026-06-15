package View;

import Controller.LoginController;
import Util.AppNavigator;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField emailField;
    private TextField phoneField;
    private Button loginButton;
    private Label statusLabel;
    private ComboBox<String> roleComboBox; // ComboBox for user roles

    private LoginController controller;
    private AppNavigator navigator;
    private Stage primaryStage;

    public void start(Stage primaryStage) {
    
        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-pref-width: 200px; -fx-padding: 8px; -fx-font-size: 14px;");

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 200px; -fx-padding: 8px; -fx-font-size: 14px;");
        
        //Email Field
        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-pref-width: 200px; -fx-padding: 8px; -fx-font-size: 14px;");
        
        //phonenUmber field
        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle("-fx-pref-width: 200px; -fx-padding: 8px; -fx-font-size: 14px;");
        
        // ComboBox for user roles
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Manager", "Cashier");
        roleComboBox.setPromptText("Select Role");
        roleComboBox.setStyle("-fx-pref-width: 200px; -fx-padding: 8px;");

        // Login button
        loginButton = new Button("Login");
        loginButton.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;"
        );

        // Status label
        statusLabel = new Label("Please enter your credentials.");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-padding:5px;");

        // Layout setup
        VBox layout = new VBox(15);
        layout.setStyle(
            "-fx-padding: 20px; -fx-alignment: center; -fx-background-color: #0066ff; -fx-border-color: #ccc; -fx-border-width: 2px;"
        );
        layout.getChildren().addAll(statusLabel, roleComboBox,  usernameField, passwordField,
        		emailField, phoneField,loginButton );

        // Controller setup
        if (navigator != null) {
            controller = new LoginController(this, navigator);
        } else {
            controller = new LoginController(this);
        }
        loginButton.setOnAction(e -> controller.loginAction());

        // Set up the scene and stage
        Scene scene = new Scene(layout, 550, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public void setNavigator(AppNavigator navigator) {
        this.navigator = navigator;
        if (controller != null) {
            controller.setNavigator(navigator);
        }
    }

    public void display(Stage primaryStage) {
        this.primaryStage = primaryStage;
        start(primaryStage);
    }

    // Getters for username, password, and selected role
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
    public String getEmail() {
        return emailField.getText();
    }
    
    public String getPhoneNumber() {
        return phoneField.getText();
    }

    public String getSelectedRole() {
        return roleComboBox.getValue();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public Button getLoginButton() {
        return loginButton;
    }

}
