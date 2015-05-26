import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/*
 * Contains the main thread of the program which creates a options menu
 * for settings that builds the task. Pressing start disposes the window and
 * starts a new thread that runs the task.
 */
public class Main{
	public static JFrame settings;
	
	//Fields and buttons for the start dialog
	private static JTextField outText;
	private static JTextField evnText;
	private static JTextField sigText;
	private static JTextField timeText;
	private static JTextField pText;
	private static JRadioButton serial;
	private static JRadioButton binary;
	private static JRadioButton train;
	private static JRadioButton feedback;

	
	private static final int boxWidth = 150;
	
	public static void main(String[] args) {
		
		//Make window
		settings = new JFrame("Settings");
		settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Container for window components
		Container c = new Container();
		c.setLayout(new BoxLayout(c,BoxLayout.PAGE_AXIS));
		
		//Footer of the window
		Container footer = new Container();
		footer.setLayout(new BoxLayout(footer,BoxLayout.LINE_AXIS));
		
		
		//Radio buttons for the footer
		serial = new JRadioButton("Serial");
		binary = new JRadioButton("Binary");
		train = new JRadioButton("Training");
		feedback = new JRadioButton("Feedback");
		
		serial.setSelected(true);
		feedback.setSelected(true);
		
		//Button to start the task
		JButton button = new JButton("Start");
		button.addActionListener(buttonPress);
		
		//Make footer
		footer.add(serial);
		footer.add(binary);
		footer.add(train);
		footer.add(feedback);
		footer.add(Box.createHorizontalGlue());
		footer.add(button);

		//Make other fields
		c.add( buildRow(outText = new JTextField(), "Output File:", "Participant_00.txt") );
		c.add( buildRow(evnText = new JTextField(), "Event Rate (per min):", "60") );
		c.add( buildRow(sigText = new JTextField(), "Signal Spacing (min):", "2") );
		c.add( buildRow(timeText = new JTextField(), "Time (min):", "40") );
		c.add( buildRow(pText = new JTextField(), "Port:", "COM1") );
		c.add(footer);

		//Put everything together
		settings.setContentPane(c);
		settings.pack();
		settings.setVisible(true);	
		
	}
	
	//Helper function that builds a row for the dialog box
	private static Container buildRow(JTextField text, String label, String box) {
		Container t = new Container();
		t.setLayout(new BoxLayout(t,BoxLayout.LINE_AXIS));
		
		//Text on the left
		JLabel lab = new JLabel(label);
		
		//Text field on the right
		text.setPreferredSize(new Dimension(boxWidth,0));
		text.setMaximumSize(new Dimension(250,25));
		text.setText(box);
		
		t.add(lab);
		t.add(Box.createHorizontalGlue());
		t.add(text);
		return t;
	}
	
	//On button press...
	private static ActionListener buttonPress = new ActionListener() {

		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//Check serial settings if selected
			if(serial.isSelected()) {
				try {
					if ( !Serial.connect(pText.getText()) ) {
						Notifications.errorPort();
						return;
					}
			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.errorPort();
					return;
				}
			}

			//Check for empty output file
			if (outText.getText().equals("")) {
				Notifications.errorOutputFile();
				return;
			}
			
			//Build the task
			StartTask task = new StartTask(
					outText.getText(), 
					Double.valueOf(evnText.getText()),
					Double.valueOf(sigText.getText()),
					Double.valueOf(timeText.getText()),
					binary.isSelected(),
					train.isSelected(),
					feedback.isSelected());
			
			//Destroy the window
			settings.dispose();
			
			//Start the task
			Thread t = new Thread(task);
			t.start();
			
		}
			
	};
	


}
