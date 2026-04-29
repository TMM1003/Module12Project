import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Challenge2 {

    public static void main(String[] args) {
        ProjectSetupGUI projectSetupGui = new ProjectSetupGUI();
        LocalRepoSetup localRepoSetup = new LocalRepoSetup();
        RepoPublishManager repoPublishManager = new RepoPublishManager();

        projectSetupGui.launchGui();
        projectSetupGui.showStatusMessage("Enter your project details and click Start Setup to begin.");

        UserProjectSettings settings = projectSetupGui.collectUserInputFromGui();
        if (settings == null) {
            projectSetupGui.showStatusMessage("Something went wrong.");
            return;
        }

        try {
            GitHubCredentials credentials = loadGitHubCredentials();
            GitHubRepoSetup gitHubRepoSetup = new GitHubRepoSetup(credentials.username, credentials.token);

            projectSetupGui.showStatusMessage("Turning the selected folder into a Git repository...");
            localRepoSetup.turnProjectIntoGitRepo(settings.projectPath);

            projectSetupGui.showStatusMessage("Adding the .gitignore file...");
            localRepoSetup.addGitIgnoreFile(settings.projectPath);

            projectSetupGui.showStatusMessage("Adding the README.md file...");
            localRepoSetup.addReadMeFile(settings.projectPath, settings.projectName);

            projectSetupGui.showStatusMessage("Creating the initial commit...");
            gitHubRepoSetup.createInitialCommit(settings.projectPath);

            projectSetupGui.showStatusMessage("Creating the matching GitHub repository...");
            GitHubRepoInfo repoInfo = gitHubRepoSetup.createGitHubRepoMirror(
                    settings.repoName,
                    settings.description,
                    settings.isPrivate);

            if (repoInfo == null || repoInfo.remoteUrl == null || repoInfo.remoteUrl.isBlank()
                    || repoInfo.repoUrl == null || repoInfo.repoUrl.isBlank()) {
                throw new IllegalStateException("GitHub repo details were not returned.");
            }

            projectSetupGui.showStatusMessage("Setting the origin remote...");
            gitHubRepoSetup.setOriginRemote(settings.projectPath, repoInfo.remoteUrl);

            projectSetupGui.showStatusMessage("Pushing the initial commit to GitHub...");
            repoPublishManager.pushInitialCommit(settings.projectPath);

            projectSetupGui.showStatusMessage("Displaying the final repository URL...");
            String finalRepoUrl = repoPublishManager.giveUserRepoUrl(repoInfo.repoUrl);
            projectSetupGui.showRepoUrlInGui(finalRepoUrl);
            projectSetupGui.showStatusMessage("Setup complete.");
        } catch (Exception exception) {
            projectSetupGui.showStatusMessage("Setup failed: " + getRootMessage(exception));
        }
    }

    private static GitHubCredentials loadGitHubCredentials() {
        Map<String, String> dotEnvValues = loadDotEnvValues(Path.of(".env"));
        String username = firstNonBlank(
                System.getenv("GITHUB_USERNAME"),
                System.getenv("GITHUB_USER"),
                dotEnvValues.get("GITHUB_USERNAME"),
                dotEnvValues.get("GITHUB_USER"));
        String token = firstNonBlank(
                System.getenv("GITHUB_TOKEN"),
                dotEnvValues.get("GITHUB_TOKEN"));

        if (username == null || token == null) {
            throw new IllegalStateException(
                    "Missing GitHub credentials. Add GITHUB_USERNAME and GITHUB_TOKEN to .env or environment variables.");
        }

        return new GitHubCredentials(username, token);
    }

    private static Map<String, String> loadDotEnvValues(Path envPath) {
        Map<String, String> values = new HashMap<>();
        if (!Files.exists(envPath)) {
            return values;
        }

        try {
            List<String> lines = Files.readAllLines(envPath);
            for (String line : lines) {
                if (line == null) {
                    continue;
                }

                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int equalsIndex = trimmedLine.indexOf('=');
                if (equalsIndex <= 0) {
                    continue;
                }

                String key = trimmedLine.substring(0, equalsIndex).trim();
                String value = trimmedLine.substring(equalsIndex + 1).trim();
                values.put(key, stripOptionalQuotes(value));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read .env file.", exception);
        }

        return values;
    }

    private static String stripOptionalQuotes(String value) {
        if (value.length() >= 2) {
            boolean wrappedInDoubleQuotes = value.startsWith("\"") && value.endsWith("\"");
            boolean wrappedInSingleQuotes = value.startsWith("'") && value.endsWith("'");
            if (wrappedInDoubleQuotes || wrappedInSingleQuotes) {
                return value.substring(1, value.length() - 1).trim();
            }
        }

        return value;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private static String getRootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }

        String message = current.getMessage();
        if (message == null || message.isBlank()) {
            return current.getClass().getSimpleName();
        }

        return message.trim();
    }

    private static final class GitHubCredentials {
        private final String username;
        private final String token;

        private GitHubCredentials(String username, String token) {
            this.username = username;
            this.token = token;
        }
    }
}
