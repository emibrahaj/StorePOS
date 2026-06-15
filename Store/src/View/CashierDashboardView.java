package View;

import Controller.CashierController;
import Model.Item;
import Model.Bill;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class CashierDashboardView {
    private Stage stage;
    private CashierController controller;
    private TextField searchField;
    private TextField quantityField;
    private Button addItemButton, finalizeBillButton, refreshBillsButton;
    private Label totalLabel;
    private TableView<Item> inventoryTable;
    private TableView<Bill> billTable;
    private ObservableList<Item> inventoryItems;

    public CashierDashboardView(Stage stage, CashierController controller) {
        this.stage = stage;
        this.controller = controller;
        inventoryItems = FXCollections.observableArrayList(controller.getInventoryItems());
    }

    public void start() {
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search item...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterInventory(newValue));

        // Quantity field
        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        // Buttons
        addItemButton = createStyledButton("Add Item");
        addItemButton.setOnAction(e -> addItemToBill());

        finalizeBillButton = createStyledButton("Finalize Bill");
        finalizeBillButton.setOnAction(e -> finalizeBill());

        refreshBillsButton = createStyledButton("Refresh Today's Bills");
        refreshBillsButton.setOnAction(e -> loadTodaysBills());

        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Setup inventory table and load data
        setupInventoryTable();
        loadInventoryData();

        // Setup bill table and load data
        setupBillTable();
        loadTodaysBills();

        VBox layout = new VBox(15,
            createStyledLabel("Cashier Dashboard"),
            searchField,
            inventoryTable,
            quantityField,
            addItemButton,
            finalizeBillButton,
            totalLabel,
            refreshBillsButton,
            billTable
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #1E88E5; -fx-border-radius: 10;");

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Cashier Dashboard");
        stage.show();
    }

    private void setupInventoryTable() {
        inventoryTable = new TableView<>();
        inventoryTable.setItems(inventoryItems);

        TableColumn<Item, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));

        TableColumn<Item, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getStock())));

        TableColumn<Item, String> priceCol = new TableColumn<>("Selling Price");
        priceCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSellingPrice())));

        inventoryTable.getColumns().addAll(itemNameCol, stockCol, priceCol);
    }

    private void loadInventoryData() {
        inventoryItems.setAll(controller.getInventoryItems());
    }

    private void setupBillTable() {
        billTable = new TableView<>();

        TableColumn<Bill, String> billNumberCol = new TableColumn<>("Bill Number");
        billNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBillNumber()));

        TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));

        TableColumn<Bill, Double> totalAmountCol = new TableColumn<>("Total Amount");
        totalAmountCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalAmount()).asObject());

        billTable.getColumns().addAll(billNumberCol, dateCol, totalAmountCol);
    }

    private void loadTodaysBills() {
        ObservableList<Bill> todaysBills = FXCollections.observableArrayList(controller.getTodaysBills());
        billTable.setItems(todaysBills);

        double totalSales = controller.getTotalOfTodaysBills();
        totalLabel.setText("Today's Total Sales: $" + totalSales);
    }

    // Search function for filtering inventory table
    private void filterInventory(String query) {
        ObservableList<Item> filteredList = FXCollections.observableArrayList();
        for (Item item : controller.getInventoryItems()) {
            if (item.getItemName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        inventoryTable.setItems(filteredList);
    }

    private void addItemToBill() {
        Item selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select an item.");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity > selectedItem.getStock()) {
                showAlert("Error", "Not enough stock available.");
                return;
            }

            controller.addItemToBill(selectedItem.getItemName(), quantity);
            totalLabel.setText("Total: $" + controller.getTotalAmount());

            loadInventoryData(); // Reload inventory to reflect updated stock
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid quantity.");
        }
    }

    private void finalizeBill() {
        controller.finalizeBill();
        totalLabel.setText("Total: $0.00");
        loadInventoryData();  // Reload inventory to reflect stock update
        loadTodaysBills();  // Reload bills to show new finalized bill
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        return button;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        return label;
    }
}
