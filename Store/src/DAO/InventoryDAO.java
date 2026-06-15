package DAO;

import Model.Inventory;
import java.io.*;
import java.nio.file.Path;

public class InventoryDAO {
    private static final Path INVENTORY_FILE_PATH = AppPaths.daoFile("inventory.dat");

    public static Inventory loadInventory() {
        File file = INVENTORY_FILE_PATH.toFile();
        if (!file.exists()) {
            System.out.println("Inventory file does not exist, returning empty inventory.");
            return new Inventory();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Inventory) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
            return new Inventory();
        }
    }

    public static void saveInventory(Inventory inventory) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE_PATH.toFile()))) {
            oos.writeObject(inventory);
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }
}
