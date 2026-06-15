package DAO;

import Model.Supplier;
import Util.Logger;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private static final String TAG = "SupplierDAO";
    private static final Path SUPPLIER_FILE_PATH = AppPaths.daoFile("suppliers.dat");

    public static List<Supplier> loadSuppliers() {
        Logger.info(TAG, "Loading suppliers from " + SUPPLIER_FILE_PATH);
        File file = SUPPLIER_FILE_PATH.toFile();
        
        if (!file.exists()) {
            Logger.warn(TAG, "Supplier file does not exist");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Supplier> suppliers = (List<Supplier>) ois.readObject();
            Logger.info(TAG, "Successfully loaded " + suppliers.size() + " suppliers");
            return suppliers;
        } catch (IOException | ClassNotFoundException e) {
            Logger.error(TAG, "Error loading suppliers", e);
            throw new DataPersistenceException("load", "suppliers", e);
        }
    }

    public static void saveSuppliers(List<Supplier> suppliers) {
        Logger.info(TAG, "Saving " + suppliers.size() + " suppliers to " + SUPPLIER_FILE_PATH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SUPPLIER_FILE_PATH.toFile()))) {
            oos.writeObject(suppliers);
            Logger.info(TAG, "Suppliers saved successfully");
        } catch (IOException e) {
            Logger.error(TAG, "Error saving suppliers", e);
            throw new DataPersistenceException("save", "suppliers", e);
        }
    }
}
