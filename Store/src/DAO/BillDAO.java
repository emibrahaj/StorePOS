package DAO;

import Model.Bill;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    private static final Path BILL_FILE_PATH = AppPaths.daoFile("bills.dat");

    public static List<Bill> loadBills() {
        File file = BILL_FILE_PATH.toFile();
        if (!file.exists()) {
            System.out.println("Bill file does not exist, returning empty list.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Bill>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading bills: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveBills(List<Bill> bills) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILL_FILE_PATH.toFile()))) {
            oos.writeObject(bills);
            System.out.println("Bills saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving bills: " + e.getMessage());
        }
    }
}
