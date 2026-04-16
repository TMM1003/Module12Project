import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingPractice {
    public static void main(String[] args) {
        // create JFrame
        JFrame frame = new JFrame("Swing Practice");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // make it so when JFrame is closed via the x button, the entire program stops
        frame.setLocationRelativeTo(null); // make JFrame open center screen
        frame.setResizable(false);
        
        // create main panel (canvas)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null); // no layout so you can put things wherever you want
        mainPanel.setBackground(Color.blue);

        // create label
        JLabel helloLabel = new JLabel("Hello!!!");
        helloLabel.setSize(100, 100);
        helloLabel.setLocation(50, 50);
        helloLabel.setForeground(Color.white);
        mainPanel.add(helloLabel);

        // create button
        JButton submitButton = new JButton("Submit");
        submitButton.setSize(100, 50);
        submitButton.setLocation(50, 200);
        
        // when button is clicked, run the code inside the actionPerformed method
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("You clicked the button!");
                helloLabel.setText("Goodbye!!");
            }
        });
        mainPanel.add(submitButton);

        // add mainPanel to JFrame
        frame.setContentPane(mainPanel);

        // show JFrame
        frame.setVisible(true);
    }
}