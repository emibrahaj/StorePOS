package Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple file-based logging utility.
 * Logs messages to both console and persistent log files.
 * Works without external logging dependencies.
 */
public class Logger {
    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public enum LogLevel {
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR");
        
        private final String label;
        
        LogLevel(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }

    /**
     * Logs a message at DEBUG level.
     * 
     * @param tag the tag/class name
     * @param message the message to log
     */
    public static void debug(String tag, String message) {
        log(LogLevel.DEBUG, tag, message, null);
    }

    /**
     * Logs a message at INFO level.
     * 
     * @param tag the tag/class name
     * @param message the message to log
     */
    public static void info(String tag, String message) {
        log(LogLevel.INFO, tag, message, null);
    }

    /**
     * Logs a message at WARN level.
     * 
     * @param tag the tag/class name
     * @param message the message to log
     */
    public static void warn(String tag, String message) {
        log(LogLevel.WARN, tag, message, null);
    }

    /**
     * Logs a message at ERROR level.
     * 
     * @param tag the tag/class name
     * @param message the message to log
     */
    public static void error(String tag, String message) {
        log(LogLevel.ERROR, tag, message, null);
    }

    /**
     * Logs a message with an exception at ERROR level.
     * 
     * @param tag the tag/class name
     * @param message the message to log
     * @param throwable the exception to log
     */
    public static void error(String tag, String message, Throwable throwable) {
        log(LogLevel.ERROR, tag, message, throwable);
    }

    /**
     * Main logging method that writes to both console and file.
     * 
     * @param level the log level
     * @param tag the source tag/class
     * @param message the log message
     * @param throwable optional exception
     */
    private static void log(LogLevel level, String tag, String message, Throwable throwable) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DATE_FORMAT);
        String logMessage = String.format("[%s] %s - %s: %s", timestamp, level.getLabel(), tag, message);
        
        // Print to console
        if (level == LogLevel.ERROR) {
            System.err.println(logMessage);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        } else {
            System.out.println(logMessage);
        }
        
        // Write to file
        writeToFile(now, logMessage, throwable);
    }

    /**
     * Writes log message to file.
     * Creates log directory and files as needed.
     * 
     * @param timestamp the timestamp for file naming
     * @param logMessage the formatted log message
     * @param throwable optional exception stack trace
     */
    private static void writeToFile(LocalDateTime timestamp, String logMessage, Throwable throwable) {
        try {
            Path logDir = Path.of(LOG_DIR);
            Files.createDirectories(logDir);
            
            String fileName = "app_" + timestamp.format(FILE_DATE_FORMAT) + ".log";
            Path logFile = logDir.resolve(fileName);
            
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(logFile.toFile(), true))) {  // append mode
                writer.write(logMessage);
                writer.newLine();
                
                if (throwable != null) {
                    writer.write("Exception: " + throwable.getClass().getName());
                    writer.newLine();
                    writer.write("Message: " + throwable.getMessage());
                    writer.newLine();
                    writer.write("Stack trace:");
                    writer.newLine();
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        writer.write("  at " + element.toString());
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
