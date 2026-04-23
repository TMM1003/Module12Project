import java.util.Arrays;
import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;


public class GitHubRepoSetup {

    private GitHubApiClient github;

    // Constructor
    public GitHubRepoSetup(String token) {
        this.github = new GitHubApiClient(token);
    }

    // Create initial commit
    public void createInitialCommit(String projectPath){
        GitSubprocessClient git = new GitSubprocessClient(projectPath);

        git.runCommand("git add .");
        git.runCommand("git commit -m \"Initial commit\"");
    }

    // Create GitHub repo
    public GitHubRepoInfo createGitHubRepoMirror(String repoName, String description, boolean isPrivate) {

        try {
            if (repoName == null || repoName.trim().isEmpty()) {
                System.out.println("Repo name cannot be empty.");
                return null;
            }

            repoName = repoName.trim().replaceAll(" ", "-");
            
            String repoUrl = github.createRepo(repoName, description, isPrivate);

            System.out.println("GitHub repo created: " + repoUrl);

            GitHubRepoInfo info = new GitHubRepoInfo("", "");
            info.setRemoteUrl(repoUrl);

            String browserUrl = repoUrl.replace(".git", "");
            info.setBrowserUrl(browserUrl);

            return info;

        } catch (Exception e) {
            System.out.println("Something went wrong creating the GitHub repo.");
            return null;
        }
    }

    // Set remote origin
    public void setOriginRemote(String projectPath, String remoteUrl) {

        try {
            GitSubprocessClient git = new GitSubprocessClient(projectPath);
            git.runCommand("git remote add origin " + remoteUrl);

        } catch (Exception e) {
            System.out.println("Origin may already exist. Trying to reset it...");

            try {
                GitSubprocessClient git = new GitSubprocessClient(projectPath);
                git.runCommand("git remote remove origin");
                git.runCommand("git remote add origin " + remoteUrl);

            } catch (Exception ex) {
                System.out.println("Something went wrong setting the remote.");
            }
        }
    }
}
