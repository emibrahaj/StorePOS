package DAO;

/**
 * Custom exception for data persistence errors.
 * Provides context about what operation failed and on which resource.
 */
public class DataPersistenceException extends RuntimeException {
    private String operation;
    private String resource;

    public DataPersistenceException(String operation, String resource, Throwable cause) {
        super(String.format("Failed to %s for resource: %s", operation, resource), cause);
        this.operation = operation;
        this.resource = resource;
    }

    public DataPersistenceException(String operation, String resource, String message) {
        super(String.format("Failed to %s for resource: %s - %s", operation, resource, message));
        this.operation = operation;
        this.resource = resource;
    }

    public String getOperation() {
        return operation;
    }

    public String getResource() {
        return resource;
    }
}
