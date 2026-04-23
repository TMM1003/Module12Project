import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocalRepoSetup {

    private static final List<String> DEFAULT_GITIGNORE_LINES = List.of(
            ".DS_Store",
            "*.class",
            "*.log",
            ".idea/",
            ".vscode/",
            "*.iml",
            "out/",
            "build/",
            "target/");

    // Kang

    public void turnProjectIntoGitRepo(String projectPath) {
        Path projectDirectory = validateProjectDirectory(projectPath);
        Path gitDirectory = projectDirectory.resolve(".git");

        if (Files.exists(gitDirectory)) {
            if (!Files.isDirectory(gitDirectory)) {
                throw new IllegalStateException(".git exists but is not a directory: " + gitDirectory);
            }
            return;
        }

        runCommand(projectDirectory, "git", "init");
    }

    public void addGitIgnoreFile(String projectPath) {
        Path projectDirectory = validateProjectDirectory(projectPath);
        Path gitIgnorePath = projectDirectory.resolve(".gitignore");

        try {
            Set<String> mergedLines = new LinkedHashSet<>();
            if (Files.exists(gitIgnorePath)) {
                mergedLines.addAll(Files.readAllLines(gitIgnorePath, StandardCharsets.UTF_8));
            }
            mergedLines.addAll(DEFAULT_GITIGNORE_LINES);

            String content = String.join(System.lineSeparator(), mergedLines);
            if (!content.isEmpty()) {
                content += System.lineSeparator();
            }

            Files.writeString(
                    gitIgnorePath,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create .gitignore in " + projectDirectory, e);
        }
    }

    public void addReadMeFile(String projectPath, String projectName) {
        Path projectDirectory = validateProjectDirectory(projectPath);
        String normalizedProjectName = validateProjectName(projectName);
        Path readmePath = projectDirectory.resolve("README.md");
        String header = "# " + normalizedProjectName;

        try {
            String content = header + System.lineSeparator();
            if (Files.exists(readmePath)) {
                List<String> lines = Files.readAllLines(readmePath, StandardCharsets.UTF_8);
                if (lines.isEmpty()) {
                    content = header + System.lineSeparator();
                } else {
                    List<String> updatedLines = new ArrayList<>(lines);
                    updatedLines.set(0, header);
                    content = String.join(System.lineSeparator(), updatedLines);
                    if (!content.endsWith(System.lineSeparator())) {
                        content += System.lineSeparator();
                    }
                }
            }

            Files.writeString(
                    readmePath,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create README.md in " + projectDirectory, e);
        }
    }

    private Path validateProjectDirectory(String projectPath) {
        if (projectPath == null || projectPath.isBlank()) {
            throw new IllegalArgumentException("Project path is required.");
        }

        Path projectDirectory = Path.of(projectPath).toAbsolutePath().normalize();
        if (!Files.exists(projectDirectory)) {
            throw new IllegalArgumentException("Project directory does not exist: " + projectDirectory);
        }
        if (!Files.isDirectory(projectDirectory)) {
            throw new IllegalArgumentException("Project path is not a directory: " + projectDirectory);
        }

        return projectDirectory;
    }

    private String validateProjectName(String projectName) {
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("Project name is required.");
        }

        return projectName.trim();
    }

    public void runCommand(Path workingDirectory, String... commandParts) {
        ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IllegalStateException("Command failed (" + String.join(" ", Arrays.asList(commandParts))
                        + "): " + output);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to run command: " + String.join(" ", Arrays.asList(commandParts)),
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Command interrupted: " + String.join(" ", Arrays.asList(commandParts)),
                    e);
        }
    }
}
