import java.nio.file.Path;

public class UserProjectSettings {
    // Shared data class used by backend and frontend.
    // Keep this file small to avoid merge conflicts.

    public String projectPath;
    public String projectName;
    public String repoName;
    public String description;
    public boolean isPrivate;

    public UserProjectSettings(String projectPath, String projectName, String repoName, String description, boolean isPrivate) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.repoName = repoName;
        this.description = description;
        this.isPrivate = isPrivate;
    }
}
