package Model;

import DAO.BillDAO;
import DAO.InventoryDAO;
import DAO.SupplierDAO;

import java.time.LocalDate;
import java.util.List;

public class Manager extends User {
    private String sector;
    private Inventory inventory;
    private List<Supplier> suppliers;
    private List<Bill> bills;

    public Manager(String username, String password, String name, String email, String phoneNumber) {
        super(username, password, name, email, phoneNumber);
        this.inventory = InventoryDAO.loadInventory();
        this.suppliers = SupplierDAO.loadSuppliers();
        this.bills = BillDAO.loadBills();
    }

    // Inventory Management Methods
    public void addItemToInventory(Item item, String contactDetails) {
        inventory.addItem(item);
        addSupplierIfNotExists(item.getSupplier(), contactDetails, item.getItemName());
        saveAllData();
    }

    public void removeItemFromInventory(String itemName) {
        inventory.removeItem(itemName);
        saveAllData();
    }

    public void updateItemInInventory(String itemName, double purchasePrice, double sellingPrice, int stock) {
        inventory.updateItem(itemName, purchasePrice, sellingPrice, stock);
        saveAllData();
    }

    public Inventory getInventory() {
        return inventory;
    }

    // Supplier Management Methods
    public void addSupplierIfNotExists(String supplierName, String contactDetails, String productName) {
        boolean supplierExists = suppliers.stream()
                .anyMatch(supplier -> supplier.getSupplierName().equalsIgnoreCase(supplierName));

        if (!supplierExists) {
            Supplier newSupplier = new Supplier(supplierName, contactDetails);
            newSupplier.addItem(new Item(productName, "", supplierName, 0, 0, 0));
            suppliers.add(newSupplier);
        } else {
            for (Supplier supplier : suppliers) {
                if (supplier.getSupplierName().equalsIgnoreCase(supplierName)) {
                    if (supplier.getContactDetails().isEmpty()) {
                        supplier.setContactDetails(contactDetails);
                    }
                    supplier.addItem(new Item(productName, "", supplierName, 0, 0, 0));
                    break;
                }
            }
        }
        saveAllData();
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    // Statistics Methods
    public int getTotalBillsByDate(LocalDate date) {
        return (int) bills.stream()
                .filter(bill -> bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(date))
                .count();
    }

    public int getTotalItemsSoldByDate(LocalDate date) {
        return bills.stream()
                .filter(bill -> bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(date))
                .mapToInt(Bill::getTotalItemsSold)
                .sum();
    }

    public double getTotalRevenueByDate(LocalDate date) {
        return bills.stream()
                .filter(bill -> bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(date))
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    public int getTotalBillsForPeriod(LocalDate startDate, LocalDate endDate) {
        return (int) bills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    return !billDate.isBefore(startDate) && !billDate.isAfter(endDate);
                })
                .count();
    }

    public int getTotalItemsSoldForPeriod(LocalDate startDate, LocalDate endDate) {
        return bills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    return !billDate.isBefore(startDate) && !billDate.isAfter(endDate);
                })
                .mapToInt(Bill::getTotalItemsSold)
                .sum();
    }

    public double getTotalRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        return bills.stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    return !billDate.isBefore(startDate) && !billDate.isAfter(endDate);
                })
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    public List<Bill> getAllBills() {
        return bills;
    }

    // Save all data using DAOs
    private void saveAllData() {
        InventoryDAO.saveInventory(inventory);
        SupplierDAO.saveSuppliers(suppliers);
        BillDAO.saveBills(bills);
    }

    // Debugging Method
    public void printBillDetails() {
        bills.forEach(bill -> System.out.println("Bill: " + bill.getBillNumber() + " Total: " + bill.getTotalAmount()));
    }
}
