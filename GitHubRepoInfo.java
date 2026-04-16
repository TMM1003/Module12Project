public class GitHubRepoInfo {
    // Shared data class used by backend and frontend.
    // Keep this file small to avoid merge conflicts.

    public String remoteUrl;
    public String repoUrl;

    public GitHubRepoInfo(String remoteUrl, String repoUrl) {
        this.remoteUrl = remoteUrl;
        this.repoUrl = repoUrl;
    }
}
