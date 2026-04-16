# CSC109 Module 12 Project

This project is a Java skeleton for a CSC109 Module 12 group assignment. The goal of the program is to guide or automate the setup of a local project as a Git repository, connect it to a new GitHub repository, push the initial commit, and provide the user with the final repository URL.

The current codebase is intentionally only a skeleton. The project is split across multiple `.java` files so each group member can work in a separate area with fewer merge conflicts.

## Objective

The finished program is intended to support these 8 project tasks:

- Turn a project on a user's computer into a Git repo
- Add a `.gitignore` file with common files or patterns to ignore
- Add a `README.md` file containing the project name as a Markdown header
- Create an initial commit in the Git repo
- Create a GitHub repo that mirrors the local Git repo
- Set the local Git repo's remote to the GitHub repo as `origin`
- Push the initial commit to GitHub
- Give the user the URL to the new GitHub repo

## Current Scope

The project currently provides method stubs only. No project logic has been implemented yet.

The work is organized so the 8 required Git and GitHub tasks are split across 3 backend developers, while the 4th teammate focuses on the GUI:

- Kang: initialize the Git repo, create the `.gitignore`, and create the `README.md`
- Jordan: create the initial commit, create the GitHub repo, and set the `origin` remote
- Kenneth: push the initial commit and provide the final repo URL
- Thomas: build the GUI, collect user input, show status messages, and display the final URL

## Project Files

- `Challenge2.java` - Small coordinator file for connecting the finished classes together
- `LocalRepoSetup.java` - Kang's backend file for local Git project setup
- `GitHubRepoSetup.java` - Jordan's backend file for commit, GitHub repo creation, and remote setup
- `RepoPublishManager.java` - Kenneth's backend file for pushing and returning the final URL
- `ProjectSetupGUI.java` - Thomas's frontend file for the GUI
- `UserProjectSettings.java` - Shared data class for user-provided project settings
- `GitHubRepoInfo.java` - Shared data class for GitHub repo URLs
- `Documents/Challenge2.pdf` - Assignment instructions or supporting reference
- `Documents/SwingPractice.java` - Separate practice/example file included in the repo
- `Documents/Adding_External_Libraries_to_a_Java_Project.pdf` - Java reference material
- `Documents/Branch_Protection_Rules(2).pdf` - Additional reference material

## Requirements

- A Java Development Kit (JDK) installed
- A terminal or IDE capable of compiling and running Java programs

## How to Run

Compile the project:

```bash
javac Challenge2.java LocalRepoSetup.java GitHubRepoSetup.java RepoPublishManager.java ProjectSetupGUI.java UserProjectSettings.java GitHubRepoInfo.java
```

Run the program:

```bash
java Challenge2
```

## Current Status

The project compiles, but `main`, the backend helper methods, and the GUI methods are still placeholders. The group will fill in the implementation later.
