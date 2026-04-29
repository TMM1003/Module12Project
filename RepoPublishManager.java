import git.tools.client.GitSubprocessClient;
import java.nio.file.Path;

public class RepoPublishManager {
    private final LocalRepoSetup localRepoSetup = new LocalRepoSetup();

    public void pushInitialCommit(String projectPath) {
        if (projectPath == null || projectPath.isBlank()) {
            throw new IllegalArgumentException("Project path is required.");
        }

        Path projectDirectory = Path.of(projectPath).toAbsolutePath().normalize();
        GitSubprocessClient git = new GitSubprocessClient(projectDirectory.toString());
        String branchName = sanitizeBranchName(git.runGitCommand("branch --show-current"));

        if (branchName == null) {
            branchName = sanitizeBranchName(git.getCurrentBranchName());
        }

        if (branchName == null) {
            throw new IllegalStateException("Unable to determine the current Git branch.");
        }

        localRepoSetup.runCommand(projectDirectory, "git", "push", "-u", "origin", branchName);
    }

    public String giveUserRepoUrl(String repoUrl) {
        if (repoUrl == null || repoUrl.isBlank()) {
            throw new IllegalArgumentException("Repository URL is required.");
        }

        return repoUrl.trim();
    }

    private String sanitizeBranchName(String rawBranchName) {
        if (rawBranchName == null) {
            return null;
        }

        String normalizedBranchName = rawBranchName.trim();
        if (normalizedBranchName.isBlank()
                || normalizedBranchName.contains("fatal:")
                || normalizedBranchName.contains("error:")
                || normalizedBranchName.equals("undefined")) {
            return null;
        }

        int lineBreakIndex = normalizedBranchName.indexOf(System.lineSeparator());
        if (lineBreakIndex >= 0) {
            normalizedBranchName = normalizedBranchName.substring(0, lineBreakIndex).trim();
        }

        return normalizedBranchName.isBlank() ? null : normalizedBranchName;
    }
}
