package DAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility class to migrate existing plaintext passwords to hashed passwords.
 * Run this once to upgrade passwords.txt from plaintext to hashed format.
 * 
 * Usage: PasswordMigrationUtil.migratePasswordsToHash()
 */
public class PasswordMigrationUtil {
    
    /**
     * Migrates all passwords in passwords.txt from plaintext to hashed format.
     * Creates a backup and then replaces the original file.
     * 
     * @throws IOException if file operations fail
     * @throws Exception if password hashing fails
     */
    public static void migratePasswordsToHash() throws Exception {
        Path passwordFilePath = AppPaths.daoFile("passwords.txt");
        Path backupFilePath = AppPaths.daoFile("passwords.txt.backup");
        
        System.out.println("Starting password migration...");
        System.out.println("Backup will be created at: " + backupFilePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFilePath.toFile()));
             BufferedWriter backupWriter = new BufferedWriter(new FileWriter(backupFilePath.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFilePath.toFile()))) {
            
            String line;
            int migratedCount = 0;
            
            while ((line = reader.readLine()) != null) {
                // Create backup
                backupWriter.write(line);
                backupWriter.newLine();
                
                // Parse line: username,password,email,phone,role
                String[] parts = line.split(",", 5);
                if (parts.length != 5) {
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }
                
                String username = parts[0];
                String plainPassword = parts[1];
                String email = parts[2];
                String phone = parts[3];
                String role = parts[4];
                
                try {
                    // Hash the password
                    String hashedPassword = PasswordManager.hashPassword(plainPassword);
                    
                    // Write hashed version
                    String hashedLine = String.format("%s,%s,%s,%s,%s", 
                        username, hashedPassword, email, phone, role);
                    writer.write(hashedLine);
                    writer.newLine();
                    
                    migratedCount++;
                    System.out.println("✓ Migrated: " + username);
                    
                } catch (Exception e) {
                    System.err.println("Failed to hash password for " + username + ": " + e.getMessage());
                    // Write original line if hashing fails
                    writer.write(line);
                    writer.newLine();
                }
            }
            
            System.out.println("Migration complete!");
            System.out.println("Successfully migrated " + migratedCount + " passwords");
            System.out.println("Original passwords backed up to: " + backupFilePath);
            
        } catch (IOException e) {
            System.err.println("Migration failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Main method for testing/running migration directly.
     * Uncomment and run if needed.
     */
    /*
    public static void main(String[] args) {
        try {
            PasswordMigrationUtil.migratePasswordsToHash();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */
}
