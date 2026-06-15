package DAO;

import Model.Bill;
import Util.Logger;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    private static final String TAG = "BillDAO";
    private static final Path BILL_FILE_PATH = AppPaths.daoFile("bills.dat");

    public static List<Bill> loadBills() {
        Logger.info(TAG, "Loading bills from " + BILL_FILE_PATH);
        File file = BILL_FILE_PATH.toFile();
        
        if (!file.exists()) {
            Logger.warn(TAG, "Bill file does not exist: " + BILL_FILE_PATH);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Bill> bills = (List<Bill>) ois.readObject();
            Logger.info(TAG, "Successfully loaded " + bills.size() + " bills");
            return bills;
        } catch (IOException | ClassNotFoundException e) {
            Logger.error(TAG, "Error loading bills", e);
            throw new DataPersistenceException("load", "bills", e);
        }
    }

    public static void saveBills(List<Bill> bills) {
        Logger.info(TAG, "Saving " + bills.size() + " bills to " + BILL_FILE_PATH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILL_FILE_PATH.toFile()))) {
            oos.writeObject(bills);
            Logger.info(TAG, "Bills saved successfully");
        } catch (IOException e) {
            Logger.error(TAG, "Error saving bills", e);
            throw new DataPersistenceException("save", "bills", e);
        }
    }
}
