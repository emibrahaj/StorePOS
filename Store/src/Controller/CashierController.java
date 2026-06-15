package Controller;

import Model.Cashier;
import Model.Item;
import Model.Inventory;
import Model.Bill;
import java.util.List;

public class CashierController {
    private Cashier cashier;
    private Inventory inventory;

    public CashierController(Cashier cashier, Inventory inventory) {
        this.cashier = cashier;
        this.inventory = inventory;
    }


    public void addItemToBill(String itemName, int quantity) {
        // Create a new bill if there is no active bill
        if (cashier.getCurrentBill() == null) {
            cashier.createNewBill(); // Create a new bill if none exists
        }

        Item item = inventory.findItemByName(itemName);
        if (item != null && item.getStock() >= quantity) {
            item.setQuantity(quantity);  // Set the quantity for the current item in the bill
            cashier.addItemToCurrentBill(item, quantity);  // Add item to the current bill
        } else {
            System.out.println("Error: Item is out of stock or does not exist.");
        }
    }


    public List<Bill> getTodaysBills() {
        return cashier.getTodaysBills();
    }

    public double getTotalOfTodaysBills() {
        return cashier.getTodaysBills().stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    public void finalizeBill() {
        cashier.finalizeCurrentBill();
    }

    public List<Item> getInventoryItems() {
        return inventory.getItems();  // Always get updated inventory
    }

    public void printBills() {
        cashier.printBills();
    }

    public boolean returnItemFromBill(String billNumber, String itemName, int quantity) {
        return cashier.processReturn(billNumber, itemName, quantity);
    }

    public double getTotalAmount() {
        return cashier.getCurrentBill() != null ? cashier.getCurrentBill().getTotalAmount() : 0;
    }
}
