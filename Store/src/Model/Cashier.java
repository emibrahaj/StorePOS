  package Model;
    import DAO.AppPaths;
    import java.io.*;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.stream.Collectors;
    import View.CashierDashboardView;
import javafx.scene.control.Alert;

    public class Cashier extends User implements Serializable {
        private static final long serialVersionUID = 1L;
        private String sector;
        private List<Bill> bills;
        private Bill currentBill;
        private static final Path BILL_FILE_PATH = AppPaths.daoFile("bills.dat");

        private Inventory inventory;  // Add inventory field

        public Cashier(String username, String password, String name, String email, String phoneNumber, String sector, Inventory inventory) {
            super(username, password, name, email, phoneNumber);
            this.sector = sector;
            this.inventory = inventory;
            this.bills = new ArrayList<>();
            loadBillsFromFile();
        }

        // Getter for sector
        public String getSector() {
            return sector;
        }


        public Bill createNewBill() {
            // Check if there's already an active bill
            if (currentBill == null) {
                currentBill = new Bill(generateBillNumber(), new Date()); // Create new bill if none exists
                bills.add(currentBill);
                return currentBill;
            }
            return currentBill; // Return the existing bill if one is already open
        }
        
        public void addItemToCurrentBill(Item item, int quantity) {
            if (currentBill == null) {
                System.out.println("Error: No active bill.");
                return;
            }
            if (item.getStock() >= quantity) {
                item.setQuantity(quantity); // Ensure quantity is set properly
                currentBill.addItem(item);
                System.out.println("Item added to current bill: " + item.getItemName() + " x" + quantity);
            } else {
                System.out.println("Error: Not enough stock for item " + item.getItemName());
            }
        }


        public void finalizeCurrentBill() {
            if (currentBill != null) {
                for (Item item : currentBill.getItems()) {
                    int quantity = item.getQuantity();
                    item.setStock(item.getStock() - quantity);
                }

                saveBillsToFile();
                saveInventoryToFile();
                saveBillToPrintableFile(currentBill);  // Save the finalized bill in a printable format

                currentBill = null;
            }
        }

        private void saveBillToPrintableFile(Bill bill) {
            Path folderPath = AppPaths.printableBillsFolder();
            File directory = folderPath.toFile();

            // Ensure the directory exists
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate filename using bill number and date
            Path billPath = folderPath.resolve("Bill_" + bill.getBillNumber() + "_"
                              + new SimpleDateFormat("yyyyMMdd").format(bill.getDate()) + ".txt");

            try (BufferedWriter writer = Files.newBufferedWriter(billPath)) {
                writer.write("Bill Number: " + bill.getBillNumber() + "\n");
                writer.write("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bill.getDate()) + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write(String.format("%-20s %-10s %-10s\n", "Item", "Qty", "Price"));
                writer.write("----------------------------------------------------\n");

                for (Item item : bill.getItems()) {
                    writer.write(String.format("%-20s %-10d $%-10.2f\n",
                            item.getItemName(), item.getQuantity(), item.getSellingPrice() * item.getQuantity()));
                }

                writer.write("----------------------------------------------------\n");
                writer.write("Total Amount: $" + String.format("%.2f", bill.getTotalAmount()) + "\n");
                writer.write("----------------------------------------------------\n");

                // Show success alert when bill is saved
                showAlert("Success", "Bill saved successfully to:\n" + billPath);

            } catch (IOException e) {
                showAlert("Error", "Failed to save bill: " + e.getMessage());
            }
        }

        // Method to show an alert message
        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        public void saveInventoryToFile() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/DAO/inventory.dat"))) {
                oos.writeObject(inventory);  // Save the updated inventory
                System.out.println("Inventory saved successfully.");
            } catch (IOException e) {
                System.out.println("Error saving inventory: " + e.getMessage());
            }
        }

        
        public Bill getCurrentBill() {
            return currentBill;
        }
        
        public List<Bill> getTodaysBills() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            return bills.stream()
                .filter(bill -> sdf.format(bill.getDate()).equals(today))
                .collect(Collectors.toList());
        }

        public void printBills() {
            List<Bill> todaysBills = getTodaysBills();
            for (Bill bill : todaysBills) {
                System.out.println("Bill Number: " + bill.getBillNumber());
                System.out.println("Date: " + bill.getDate());
                for (Item item : bill.getItems()) {
                    System.out.println(item.getItemName() + " - Quantity: " + item.getQuantity() + ", Price: $" + item.getSellingPrice());
                }
                System.out.println("Total Amount: $" + bill.getTotalAmount());
                System.out.println("-----------------------------");
            }
        }


        private String generateBillNumber() {
            return "B" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }

        private void saveBillsToFile() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILL_FILE_PATH.toFile()))) {
                oos.writeObject(bills);
            } catch (IOException e) {
                System.out.println("Error saving bills: " + e.getMessage());
            }
        }


        private void loadBillsFromFile() {
            File file = BILL_FILE_PATH.toFile();
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    bills = (List<Bill>) ois.readObject();
                    System.out.println("Bills loaded successfully.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error loading bills: " + e.getMessage());
                }
            }
        }



        public List<Bill> getAllBills() {
            return bills;
        }
    }
