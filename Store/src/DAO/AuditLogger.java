package DAO;

import Model.AuditLogEntry;
import Util.Logger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple audit logger that records user actions to a text file.
 * This is separate from the runtime Logger, and designed for compliance.
 */
public class AuditLogger {
    private static final String TAG = "AuditLogger";
    private static final Path AUDIT_FILE_PATH = AppPaths.daoFile("audit.log");

    public static void logAction(String userId, String action, String entityType, String entityId, String details) {
        try {
            Files.createDirectories(AUDIT_FILE_PATH.getParent());
            AuditLogEntry entry = new AuditLogEntry(userId, action, entityType, entityId, details);
            String line = formatEntry(entry);
            appendLine(line);
            Logger.info(TAG, "Audit entry recorded: " + line);
        } catch (IOException e) {
            Logger.error(TAG, "Failed to write audit entry", e);
        }
    }

    private static String formatEntry(AuditLogEntry entry) {
        return String.format("%s | user=%s | action=%s | entity=%s | id=%s | details=%s",
                entry.getTimestamp(), entry.getUserId(), entry.getAction(),
                entry.getEntityType(), entry.getEntityId(), entry.getDetails());
    }

    private static void appendLine(String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUDIT_FILE_PATH.toFile(), true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    public static List<AuditLogEntry> loadAuditEntries() {
        if (!Files.exists(AUDIT_FILE_PATH)) {
            return new ArrayList<>();
        }
        List<AuditLogEntry> entries = new ArrayList<>();
        try {
            Files.lines(AUDIT_FILE_PATH).forEach(l -> Logger.debug(TAG, "Audit line: " + l));
        } catch (IOException e) {
            Logger.error(TAG, "Failed to read audit log", e);
        }
        return entries;
    }
}
