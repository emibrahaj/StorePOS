package DAO;

import Model.Inventory;
import Util.Logger;
import java.io.*;
import java.nio.file.Path;

public class InventoryDAO {
    private static final String TAG = "InventoryDAO";
    private static final Path INVENTORY_FILE_PATH = AppPaths.daoFile("inventory.dat");

    public static Inventory loadInventory() {
        Logger.info(TAG, "Loading inventory from " + INVENTORY_FILE_PATH);
        File file = INVENTORY_FILE_PATH.toFile();
        
        if (!file.exists()) {
            Logger.warn(TAG, "Inventory file does not exist, returning empty inventory");
            return new Inventory();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Inventory inventory = (Inventory) ois.readObject();
            Logger.info(TAG, "Successfully loaded inventory");
            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            Logger.error(TAG, "Error loading inventory", e);
            throw new DataPersistenceException("load", "inventory", e);
        }
    }

    public static void saveInventory(Inventory inventory) {
        Logger.info(TAG, "Saving inventory to " + INVENTORY_FILE_PATH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE_PATH.toFile()))) {
            oos.writeObject(inventory);
            Logger.info(TAG, "Inventory saved successfully");
        } catch (IOException e) {
            Logger.error(TAG, "Error saving inventory", e);
            throw new DataPersistenceException("save", "inventory", e);
        }
    }
}
