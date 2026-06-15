package DAO;

import Model.Supplier;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private static final Path SUPPLIER_FILE_PATH = AppPaths.daoFile("suppliers.dat");

    public static List<Supplier> loadSuppliers() {
        File file = SUPPLIER_FILE_PATH.toFile();
        if (!file.exists()) {
            System.out.println("Supplier file does not exist, returning empty list.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Supplier>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading suppliers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveSuppliers(List<Supplier> suppliers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SUPPLIER_FILE_PATH.toFile()))) {
            oos.writeObject(suppliers);
            System.out.println("Suppliers saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving suppliers: " + e.getMessage());
        }
    }
}
