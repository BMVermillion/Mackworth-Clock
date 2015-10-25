
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/*
 * A class to hold all of the notifications the program generates
 */
public class Notifications {

public static void finished() {
								JOptionPane.showMessageDialog(new JFrame(), new JLabel("<html><center>All Done!</center> Press ok to exit</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
								Serial.close();
}

public static void ready() {
								JOptionPane.showMessageDialog(new JFrame(), new JLabel("<html><center>Ready?</center> Press ok to start the task.</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
}

public static int exit() {
								return JOptionPane.showConfirmDialog(new JFrame(), new JLabel("<html><center>Are you sure you want to quit?</center> Press ok to quit the task.</html>", JLabel.CENTER), "Finished", JOptionPane.OK_CANCEL_OPTION);
}

public static void readyTrain() {
								JOptionPane.showMessageDialog(new JFrame(), new JLabel("<html><center>Ready to start the training?</center> Press ok to start.</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
}

public static void errorOutputFile() {
								JOptionPane.showMessageDialog(new JFrame("Error"), "Output file can not be empty.");
								Serial.close();
}

public static void errorInputFile() {
								JOptionPane.showMessageDialog(new JFrame("Error"), "Input file does not exist.");
}

public static void errorPort() {
								JOptionPane.showMessageDialog(new JFrame("Error"), "Could not connect to port.");

}

public static void errorWrite() {
								JOptionPane.showMessageDialog(new JFrame("Error"), "Could not write to port.");
}
}
