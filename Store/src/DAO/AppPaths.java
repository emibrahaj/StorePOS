package DAO;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AppPaths {
    private AppPaths() {
    }

    public static Path daoFile(String fileName) {
        return storeRoot().resolve("src").resolve("DAO").resolve(fileName);
    }

    public static Path printableBillsFolder() {
        return storeRoot().resolve("src").resolve("Database").resolve("PrintableBills");
    }

    private static Path storeRoot() {
        Path currentProject = Path.of("");
        if (Files.exists(currentProject.resolve("src").resolve("DAO").resolve("passwords.txt"))) {
            return currentProject;
        }

        Path parentWorkspace = Path.of("Store");
        if (Files.exists(parentWorkspace.resolve("src").resolve("DAO").resolve("passwords.txt"))) {
            return parentWorkspace;
        }

        return currentProject;
    }
}
