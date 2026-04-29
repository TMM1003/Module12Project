import java.nio.file.Path;
import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;
import github.tools.client.RequestParams;
import github.tools.responseObjects.CreateRepoResponse;

public class GitHubRepoSetup {

    private final GitHubApiClient github;
    private final LocalRepoSetup localRepoSetup = new LocalRepoSetup();

    public GitHubRepoSetup(String username, String token) {
        String normalizedUsername = requireValue(username, "GitHub username");
        String normalizedToken = requireValue(token, "GitHub token");
        this.github = new GitHubApiClient(normalizedUsername, normalizedToken);
    }

    public void createInitialCommit(String projectPath) {
        String normalizedProjectPath = requireValue(projectPath, "Project path");
        GitSubprocessClient git = new GitSubprocessClient(normalizedProjectPath);
        String pendingChanges = git.runGitCommand("status --porcelain").trim();

        if (pendingChanges.isBlank()) {
            return;
        }

        Path projectDirectory = Path.of(normalizedProjectPath).toAbsolutePath().normalize();
        localRepoSetup.runCommand(projectDirectory, "git", "add", ".");
        localRepoSetup.runCommand(projectDirectory, "git", "commit", "-m", "Initial commit");
    }

    public GitHubRepoInfo createGitHubRepoMirror(String repoName, String description, boolean isPrivate) {
        String normalizedRepoName = requireValue(repoName, "Repository name").replaceAll("\\s+", "-");
        String normalizedDescription = description == null ? "" : description.trim();

        RequestParams requestParams = new RequestParams();
        requestParams.addParam("name", normalizedRepoName);
        requestParams.addParam("description", normalizedDescription);
        requestParams.addParam("private", isPrivate);

        CreateRepoResponse response = github.createRepo(requestParams);
        String repoFullName = requireValue(response.getRepoFullName(), "GitHub repository full name");
        String browserUrl = requireValue(response.getUrl(), "GitHub repository URL");
        String remoteUrl = "https://github.com/" + repoFullName + ".git";

        return new GitHubRepoInfo(remoteUrl, browserUrl);
    }

    public void setOriginRemote(String projectPath, String remoteUrl) {
        String normalizedProjectPath = requireValue(projectPath, "Project path");
        String normalizedRemoteUrl = requireValue(remoteUrl, "Remote URL");
        Path projectDirectory = Path.of(normalizedProjectPath).toAbsolutePath().normalize();

        try {
            localRepoSetup.runCommand(projectDirectory, "git", "remote", "remove", "origin");
        } catch (IllegalStateException ignored) {
            // Removing a missing origin is safe to ignore before adding the new one.
        }

        localRepoSetup.runCommand(projectDirectory, "git", "remote", "add", "origin", normalizedRemoteUrl);
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value.trim();
    }
}
