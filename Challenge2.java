public class Challenge2 {

    public static void main(String[] args) {
        ProjectSetupGUI projectSetupGui = new ProjectSetupGUI();
        LocalRepoSetup localRepoSetup = new LocalRepoSetup();
        GitHubRepoSetup gitHubRepoSetup = new GitHubRepoSetup();
        RepoPublishManager repoPublishManager = new RepoPublishManager();

        projectSetupGui.launchGui();
        projectSetupGui.showStatusMessage("Enter your project details and click Start Setup to begin.");

        UserProjectSettings settings = projectSetupGui.collectUserInputFromGui();
        if (settings == null) {
            projectSetupGui.showStatusMessage("Something went wrong.");
            return;
        }

        try {
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
            repoPublishManager.giveUserRepoUrl(repoInfo.repoUrl);
            projectSetupGui.showRepoUrlInGui(repoInfo.repoUrl);
            projectSetupGui.showStatusMessage("Setup complete.");
        } catch (Exception exception) {
            projectSetupGui.showStatusMessage("Something went wrong while completing the setup.");
        }
    }
}
