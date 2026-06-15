package View;

import Controller.ManagerController;
import Model.Item;
import Model.Supplier;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ManagerDashboardView {
    private Stage stage;
    private ManagerController controller;

    private TextField itemNameField, categoryField, supplierField, supplierContactField, purchasePriceField, sellingPriceField, stockField, searchField;
    private Button addItemButton, addStockButton, removeItemButton, updateItemButton, logoutButton, viewStatisticsButton;
    private TableView<Item> inventoryTable;
    private TableView<Supplier> supplierTable;

    public ManagerDashboardView(Stage stage, ManagerController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void start(Stage stage) {
        checkOutOfStockItems();
        setupInputFields();
        setupButtons();
        setupTables();

        VBox mainLayout = createMainLayout();
        stage.setScene(new Scene(mainLayout, 900, 700));
        stage.setTitle("Manager Dashboard");
        stage.show();
    }

    private void setupInputFields() {
        itemNameField = createStyledTextField("Item Name");
        categoryField = createStyledTextField("Category");
        supplierField = createStyledTextField("Supplier");
        supplierContactField = createStyledTextField("Supplier Contact");
        purchasePriceField = createStyledTextField("Purchase Price");
        sellingPriceField = createStyledTextField("Selling Price");
        stockField = createStyledTextField("Stock Quantity");
        searchField = createStyledTextField("Search Item");
        searchField.setOnKeyReleased(e -> filterInventory(searchField.getText()));
    }
    
    private void setupInventoryTable() {
        TableColumn<Item, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(cellData -> cellData.getValue().itemNameProperty());

        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Item, Double> purchasePriceCol = new TableColumn<>("Purchase Price");
        purchasePriceCol.setCellValueFactory(cellData -> cellData.getValue().purchasePriceProperty().asObject());

        TableColumn<Item, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());

        inventoryTable.getColumns().addAll(itemNameCol, categoryCol, purchasePriceCol, stockCol);
        inventoryTable.setItems(FXCollections.observableArrayList(controller.getManager().getInventory().getItems()));
    }

    private void setupSupplierTable() {
        TableColumn<Supplier, String> supplierNameCol = new TableColumn<>("Supplier");
        supplierNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplierName()));

        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact Details");
        contactCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactDetails()));

        TableColumn<Supplier, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue();
            return new SimpleStringProperty(
                    supplier.getSuppliedItems().isEmpty() ? "No Products" : supplier.getSuppliedItems().get(0).getItemName()
            );
        });

        supplierTable.getColumns().addAll(supplierNameCol, contactCol, productCol);
        supplierTable.setItems(FXCollections.observableArrayList(controller.getAllSuppliers()));
    }


    private void setupButtons() {
        addItemButton = createStyledButton("Add Item", e -> addItemToInventory());
        addStockButton = createStyledButton("Add Stock", e -> showAddStockDialog());
        removeItemButton = createStyledButton("Remove Item", e -> removeItemFromInventory());
        updateItemButton = createStyledButton("Update Item", e -> updateItemInInventory());
        viewStatisticsButton = createStyledButton("View Statistics", e -> openStatisticsView());
        logoutButton = createStyledButton("Logout", e -> logout());
    }

    private void setupTables() {
        inventoryTable = new TableView<>();
        setupInventoryTable();

        supplierTable = new TableView<>();
        setupSupplierTable();
    }

    private VBox createMainLayout() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #E3F2FD; -fx-border-radius: 10;");
        layout.getChildren().addAll(
            createStyledLabel("Manage Inventory"),
            searchField,
            createInputGrid(),
            createButtonBox(),
            inventoryTable,
            createStyledLabel("Suppliers List"),
            supplierTable
        );
        return layout;
    }

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        
        addGridRow(grid, 0, "Item Name:", itemNameField);
        addGridRow(grid, 1, "Category:", categoryField);
        addGridRow(grid, 2, "Supplier:", supplierField);
        addGridRow(grid, 3, "Contact Details:", supplierContactField);
        addGridRow(grid, 4, "Purchase Price:", purchasePriceField);
        addGridRow(grid, 5, "Selling Price:", sellingPriceField);
        addGridRow(grid, 6, "Stock Quantity:", stockField);

        return grid;
    }

    private void addGridRow(GridPane grid, int row, String label, Control field) {
        grid.addRow(row, createStyledLabel(label), field);
    }

    private HBox createButtonBox() {
        return new HBox(15, addItemButton, removeItemButton, updateItemButton, addStockButton, viewStatisticsButton, logoutButton);
    }

    private void openStatisticsView() {
        new StatisticsView(stage, controller).show();
    }

    private void logout() {
        stage.close();
        Stage loginStage = new Stage();
        new LoginView().start(loginStage);
    }

    private void showAddStockDialog() {
        Item selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select an item to add stock.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Stock");
        dialog.setHeaderText("Updating Stock for: " + selectedItem.getItemName());
        dialog.setContentText("Enter quantity to add:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int addedStock = Integer.parseInt(input);
                if (addedStock < 0) {
                    showAlert("Error", "Stock quantity must be a positive number.");
                    return;
                }

                controller.updateItem(selectedItem.getItemName(), selectedItem.getPurchasePrice(), selectedItem.getSellingPrice(), selectedItem.getStock() + addedStock);
                refreshInventoryTable();
                showAlert("Success", "Stock updated successfully!");
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number.");
            }
        });
    }

    private void checkOutOfStockItems() {
        List<Item> outOfStockItems = controller.getManager().getInventory().getItems().stream()
                .filter(item -> item.getStock() <= 0)
                .toList();

        if (!outOfStockItems.isEmpty()) {
            showAlert("Stock Alert", "The following items are out of stock:\n" + outOfStockItems.stream()
                    .map(Item::getItemName)
                    .reduce("", (a, b) -> a + "- " + b + "\n"));
        }
    }

    private void addItemToInventory() {
        if (!validateInput()) return;

        Item item = new Item(itemNameField.getText(), categoryField.getText(), supplierField.getText(),
                Double.parseDouble(purchasePriceField.getText()), Double.parseDouble(sellingPriceField.getText()),
                Integer.parseInt(stockField.getText()));

        controller.addItemToInventory(item);
        controller.addSupplierIfNotExists(supplierField.getText(), supplierContactField.getText(), itemNameField.getText());

        refreshInventoryTable();
        refreshSupplierTable();
        showAlert("Success", "Item and Supplier added successfully!");
    }

    private void removeItemFromInventory() {
        Item selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select an item to remove.");
            return;
        }

        controller.removeItem(selectedItem.getItemName());
        refreshInventoryTable();
        showAlert("Success", "Item removed successfully!");
    }

    private void updateItemInInventory() {
        addItemToInventory();
    }

    private void refreshInventoryTable() {
        inventoryTable.setItems(FXCollections.observableArrayList(controller.getManager().getInventory().getItems()));
    }

    private void refreshSupplierTable() {
        supplierTable.setItems(FXCollections.observableArrayList(controller.getAllSuppliers()));
    }

    private void filterInventory(String query) {
        inventoryTable.setItems(FXCollections.observableArrayList(
                controller.getManager().getInventory().getItems().stream()
                        .filter(item -> item.getItemName().toLowerCase().contains(query.toLowerCase()))
                        .toList()));
    }

    private TextField createStyledTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setMaxWidth(300);
        return textField;
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
        button.setOnAction(handler);
        return button;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateInput() {
        return !itemNameField.getText().isEmpty();
    }
}
