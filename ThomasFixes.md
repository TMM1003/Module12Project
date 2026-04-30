# Fixes

This is a summary of how I fixed the project, step by step, from when I first checked the code to when I got it compiling again.

## What I (Thomas) Fixed (4/28/26)

1. I started by compiling the project so I could see the real errors instead of guessing. That showed me the project had missing dependency issues, API mismatches, and broken wiring between classes.

2. I fixed the dependency problem by using the two required JAR files on the classpath. Once I did that, I could separate missing-library errors from actual code problems.

3. I updated `GitHubRepoSetup.java` so it matched the real APIs inside the JAR files. I removed the bad import, changed the `GitHubApiClient` constructor to use both a username and token, changed repo creation to use `RequestParams` and `CreateRepoResponse`, and returned the correct GitHub URLs.

4. I fixed the mismatch between `GitHubRepoSetup.java` and `GitHubRepoInfo.java`. The original code tried to call setter methods that did not exist, so I changed the logic to use the real fields that the class actually had.

5. I fixed the Git command logic. The original code called methods on `GitSubprocessClient` that were not part of the real JAR API, so I replaced that with working Git calls using the correct methods and the existing local command runner.

6. I fixed the initial commit step so it would not break on reruns. Before creating a commit, the code now checks whether there are any changes waiting to be committed.

7. I fixed the remote setup step. Instead of blindly adding `origin`, the program now safely removes an existing `origin` if needed and then adds the correct GitHub remote URL.

8. I fixed `RepoPublishManager.java`, which had several issues. It used an uninitialized helper object, tried to run multiple Git commands as if they were one command, used the wrong parameter type, and hardcoded the branch name as `main`. I changed it so it detects the current branch and correctly pushes with `git push -u origin <branch>`.

9. I fixed `Challenge2.java` so the frontend and backend were properly connected. It now loads `GITHUB_USERNAME` and `GITHUB_TOKEN` from either `.env` or environment variables, creates the GitHub setup object with real credentials, re-enables the push step, and shows the actual error message instead of only showing a generic failure message.

10. I cleaned up smaller problems, including removing an unused import and updating the README (I know its not neccesary I just feel projects are incomplete spiritually without documentation) so the instructions matched the actual project setup.

11. After making the fixes, I recompiled the full project with the JAR files on the classpath and confirmed that the project builds successfully.

## Final Result

After these fixes, the project compiles correctly and the backend is wired to the frontend. 

## Todo

I still need valid GitHub credentials and working GitHub authentication on the machine for the full live repo creation and push flow to work end to end.
