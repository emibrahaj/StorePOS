package Controller;

import Model.Bill;
import Model.Cashier;
import Model.Item;
import Model.Manager;
import Model.Supplier;

import java.time.LocalDate;
import java.util.List;

public class ManagerController {
    private Manager manager;

    public ManagerController(Manager manager) {
        this.manager = manager;
    }

    public void addItemToInventory(Item item) {
        manager.addItemToInventory(item, "Unknown Contact");
    }

    public void addItemToInventory(Item item, String contactDetails) {
        manager.addItemToInventory(item, contactDetails);
    }

    public void removeItem(String itemName) {
        manager.removeItemFromInventory(itemName);
    }

    public void updateItem(String itemName, double purchasePrice, double sellingPrice, int stock) {
        manager.updateItemInInventory(itemName, purchasePrice, sellingPrice, stock);
    }

    public List<Item> getAllItems() {
        return manager.getInventory().getItems();
    }

    public void addSupplierIfNotExists(String supplierName, String contactDetails, String productName) {
        manager.addSupplierIfNotExists(supplierName, contactDetails, productName);
    }

    public List<Supplier> getAllSuppliers() {
        return manager.getSuppliers();
    }

    public Manager getManager() {
        return manager;
    }

    public List<Bill> getAllBills() {
        return manager.getAllBills();
    }

public int getTotalBillsByDate(LocalDate date) {
    return manager.getTotalBillsByDate(date);
}

public int getTotalItemsSoldByDate(LocalDate date) {
    return manager.getTotalItemsSoldByDate(date);
}

public double getTotalRevenueByDate(LocalDate date) {
    return manager.getTotalRevenueByDate(date);
}

public int getTotalBillsForPeriod(LocalDate startDate, LocalDate endDate) {
    return manager.getTotalBillsForPeriod(startDate, endDate);
}

public double getTotalRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
    return manager.getTotalRevenueForPeriod(startDate, endDate);
}

public int getTotalItemsSoldForPeriod(LocalDate startDate, LocalDate endDate) {
    return manager.getTotalItemsSoldForPeriod(startDate, endDate);
}
}


