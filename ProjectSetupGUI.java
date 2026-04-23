import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.CountDownLatch;

public class ProjectSetupGUI {

    // Thomas

    private final CountDownLatch submissionLatch = new CountDownLatch(1);

    private JFrame frame;
    private JTextField projectPathField;
    private JTextField projectNameField;
    private JTextField repoNameField;
    private JTextArea descriptionArea;
    private JRadioButton privateRadioButton;
    private JTextArea statusArea;
    private JTextField repoUrlField;
    private JButton copyUrlButton;
    private JButton submitButton;
    private UserProjectSettings collectedSettings;

    public void launchGui() {
        if (frame != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setVisible(true);
                    frame.toFront();
                    frame.requestFocus();
                }
            });
            return;
        }

        runOnEventThreadAndWait(new Runnable() {
            @Override
            public void run() {
                buildGui();
            }
        });
    }

    public UserProjectSettings collectUserInputFromGui() {
        launchGui();

        try {
            submissionLatch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            showStatusMessage("Something went wrong while waiting for project details.");
            return null;
        }

        return collectedSettings;
    }

    public void showStatusMessage(final String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (statusArea == null) {
                    return;
                }

                if (!statusArea.getText().isBlank()) {
                    statusArea.append(System.lineSeparator());
                }

                statusArea.append(message.trim());
                statusArea.setCaretPosition(statusArea.getDocument().getLength());
            }
        });
    }

    public void showRepoUrlInGui(final String repoUrl) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (repoUrlField == null || copyUrlButton == null) {
                    return;
                }

                String safeRepoUrl = repoUrl == null ? "" : repoUrl.trim();
                repoUrlField.setText(safeRepoUrl);
                copyUrlButton.setEnabled(!safeRepoUrl.isEmpty());
            }
        });
    }

    private void buildGui() {
        frame = new JFrame("GitHub Project Setup Prototype");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 720));
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(18, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        mainPanel.setBackground(new Color(244, 247, 250));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showStatusMessage("Enter the project details, then click Start Setup.");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(16, 16));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Git to GitHub Prototype");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(20, 34, 56));

        JLabel subtitleLabel = new JLabel(
                "<html>Convert a local project into a Git repo, mirror it to GitHub, and surface the final URL.</html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(58, 73, 96));

        JLabel disclaimerLabel = new JLabel(
                "<html><b>Prototype disclaimer:</b> This classroom proof of concept is not intended for commercial use.</html>");
        disclaimerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        disclaimerLabel.setForeground(new Color(122, 69, 22));
        disclaimerLabel.setOpaque(true);
        disclaimerLabel.setBackground(new Color(255, 245, 225));
        disclaimerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(236, 194, 122)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JPanel textPanel = new JPanel(new BorderLayout(0, 10));
        textPanel.setOpaque(false);

        JPanel topTextPanel = new JPanel(new BorderLayout(0, 6));
        topTextPanel.setOpaque(false);
        topTextPanel.add(titleLabel, BorderLayout.NORTH);
        topTextPanel.add(subtitleLabel, BorderLayout.CENTER);

        textPanel.add(topTextPanel, BorderLayout.NORTH);
        textPanel.add(disclaimerLabel, BorderLayout.SOUTH);

        headerPanel.add(new LogoPanel(), BorderLayout.WEST);
        headerPanel.add(textPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = 1.0;

        projectPathField = new JTextField(30);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(event -> chooseProjectFolder());

        projectNameField = new JTextField(30);
        repoNameField = new JTextField(30);
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JRadioButton publicRadioButton = new JRadioButton("Public");
        privateRadioButton = new JRadioButton("Private", true);
        publicRadioButton.setOpaque(false);
        privateRadioButton.setOpaque(false);

        ButtonGroup visibilityGroup = new ButtonGroup();
        visibilityGroup.add(publicRadioButton);
        visibilityGroup.add(privateRadioButton);

        JPanel visibilityPanel = new JPanel();
        visibilityPanel.setOpaque(false);
        visibilityPanel.add(publicRadioButton);
        visibilityPanel.add(privateRadioButton);

        addFormRow(formPanel, constraints, 0, "Local Project Folder", projectPathField, browseButton);
        addFormRow(formPanel, constraints, 1, "Project Name", projectNameField, null);
        addFormRow(formPanel, constraints, 2, "GitHub Repo Name", repoNameField, null);
        addFormRow(formPanel, constraints, 3, "Visibility", visibilityPanel, null);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        formPanel.add(new JLabel("Repo Description"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createLineBorder(new Color(190, 200, 214)));
        formPanel.add(descriptionScrollPane, constraints);

        submitButton = new JButton("Start Setup");
        submitButton.addActionListener(event -> handleSubmit());

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.WEST;
        formPanel.add(submitButton, constraints);

        centerPanel.add(formPanel, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.gridx = 0;
        constraints.gridy = 0;

        statusArea = new JTextArea(8, 20);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setBackground(Color.WHITE);
        statusArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Status"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JScrollPane statusScrollPane = new JScrollPane(statusArea);
        bottomPanel.add(statusScrollPane, constraints);

        JPanel repoUrlPanel = new JPanel(new BorderLayout(10, 10));
        repoUrlPanel.setBackground(Color.WHITE);
        repoUrlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Final Repository URL"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        repoUrlField = new JTextField();
        repoUrlField.setEditable(false);

        copyUrlButton = new JButton("Copy URL");
        copyUrlButton.setEnabled(false);
        copyUrlButton.addActionListener(event -> copyRepoUrlToClipboard());

        repoUrlPanel.add(repoUrlField, BorderLayout.CENTER);
        repoUrlPanel.add(copyUrlButton, BorderLayout.EAST);

        constraints.gridy = 1;
        constraints.insets = new Insets(12, 0, 0, 0);
        bottomPanel.add(repoUrlPanel, constraints);

        return bottomPanel;
    }

    private void addFormRow(JPanel formPanel, GridBagConstraints constraints, int rowIndex, String labelText,
            java.awt.Component fieldComponent, JButton sideButton) {
        constraints.gridx = 0;
        constraints.gridy = rowIndex;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel(labelText), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        if (sideButton == null) {
            constraints.gridwidth = 2;
            formPanel.add(fieldComponent, constraints);
        } else {
            constraints.gridwidth = 1;
            formPanel.add(fieldComponent, constraints);

            constraints.gridx = 2;
            constraints.weightx = 0.0;
            formPanel.add(sideButton, constraints);
        }
    }

    private void chooseProjectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a project folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (!projectPathField.getText().isBlank()) {
            fileChooser.setCurrentDirectory(new java.io.File(projectPathField.getText().trim()));
        }

        int selection = fileChooser.showOpenDialog(frame);
        if (selection == JFileChooser.APPROVE_OPTION) {
            projectPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleSubmit() {
        String projectPath = projectPathField.getText().trim();
        String projectName = projectNameField.getText().trim();
        String repoName = repoNameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (projectPath.isEmpty() || projectName.isEmpty() || repoName.isEmpty()) {
            showStatusMessage("Something went wrong: project folder, project name, and repo name are required.");
            return;
        }

        collectedSettings = new UserProjectSettings(projectPath, projectName, repoName, description,
                privateRadioButton.isSelected());
        submitButton.setEnabled(false);
        showRepoUrlInGui("");
        showStatusMessage("Project settings collected. Running setup...");
        submissionLatch.countDown();
    }

    private void copyRepoUrlToClipboard() {
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.isEmpty()) {
            showStatusMessage("Something went wrong: there is no repository URL to copy yet.");
            return;
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(repoUrl), null);
        showStatusMessage("Repository URL copied to the clipboard.");
    }

    private void runOnEventThreadAndWait(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }

        try {
            SwingUtilities.invokeAndWait(action);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to launch the GUI.", exception);
        }
    }

    private static final class LogoPanel extends JPanel {
        private static final Color QU_NAVY = new Color(12, 34, 64);
        private static final Color QU_GOLD = new Color(232, 173, 47);
        private static final Color MICROSOFT_RED = new Color(242, 80, 34);
        private static final Color MICROSOFT_GREEN = new Color(127, 186, 0);
        private static final Color MICROSOFT_BLUE = new Color(0, 164, 239);
        private static final Color MICROSOFT_YELLOW = new Color(255, 185, 0);

        LogoPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(180, 110));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D graphics2d = (Graphics2D) graphics.create();
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int shieldX = 12;
            int shieldY = 12;
            int shieldWidth = 62;
            int shieldHeight = 78;

            int[] xPoints = { shieldX + (shieldWidth / 2), shieldX + shieldWidth, shieldX + shieldWidth - 6,
                    shieldX + (shieldWidth / 2), shieldX + 6, shieldX };
            int[] yPoints = { shieldY, shieldY + 10, shieldY + 52, shieldY + shieldHeight, shieldY + 52,
                    shieldY + 10 };

            graphics2d.setColor(QU_NAVY);
            graphics2d.fillPolygon(xPoints, yPoints, xPoints.length);
            graphics2d.setColor(QU_GOLD);
            graphics2d.setStroke(new BasicStroke(3f));
            graphics2d.drawPolygon(xPoints, yPoints, xPoints.length);

            graphics2d.setFont(new Font("Segoe UI", Font.BOLD, 30));
            graphics2d.drawString("Q", shieldX + 18, shieldY + 46);

            graphics2d.setColor(new Color(123, 138, 160));
            graphics2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics2d.drawLine(88, 50, 104, 50);

            int squareSize = 22;
            int microsoftX = 112;
            int microsoftY = 28;
            int squareGap = 5;

            graphics2d.setColor(MICROSOFT_RED);
            graphics2d.fillRect(microsoftX, microsoftY, squareSize, squareSize);
            graphics2d.setColor(MICROSOFT_GREEN);
            graphics2d.fillRect(microsoftX + squareSize + squareGap, microsoftY, squareSize, squareSize);
            graphics2d.setColor(MICROSOFT_BLUE);
            graphics2d.fillRect(microsoftX, microsoftY + squareSize + squareGap, squareSize, squareSize);
            graphics2d.setColor(MICROSOFT_YELLOW);
            graphics2d.fillRect(microsoftX + squareSize + squareGap, microsoftY + squareSize + squareGap,
                    squareSize, squareSize);

            graphics2d.setColor(new Color(75, 87, 106));
            graphics2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            graphics2d.drawString("Quinnipiac x Microsoft", 12, 105);

            graphics2d.dispose();
        }
    }
}
