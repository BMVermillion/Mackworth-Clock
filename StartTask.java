
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
 * This is where the task starts after the options menu closes. It reads in data, initializes other classes
 * handles the main loop of the program, records data, then outputs data.
 */
public class StartTask implements Runnable{
	//Main window
	private JFrame frame;
	//Draw frame
	private Task draw;
	
	//Data structs for input and output
	private ArrayList<Pair> input_data;
	private ArrayList<String> output_data;
	
	//Important variables
	private String inputFile;
	private String outputFile;
	private double eventRate;
	private int signalTime;
	
	//Listeners
	private KeyListener buttonBindings;
	private MouseListener cellClick;
	
	//Variables for timing and such
	private int key_pressed = 0;
	private long press_time = 0;
	private boolean binary;
	private boolean train;
	private int counter = 0;
	private double time;
	private boolean skipping = false;
	
	private final double trainProb = 0.3;
	
	public StartTask(String out, double evnRate, double sig, double time, boolean isBinary, boolean isTrain) {
		outputFile = out;
		eventRate = evnRate;
		signalTime = (int)(sig * 60 * 1000);
		binary = isBinary;
		train = isTrain;
		this.time = time*60*1000;
		
		output_data = new ArrayList<String>();
	}
	
	@Override
	//Thread that runs the task
	public void run() {

		/*init*/
		makeListeners();
		setFrame();		
		setDrawPane();
		////////
				
		frame.pack();
		frame.setVisible(true);
		
		UserFeedback.setTask(draw);
		
		Thread t = new Thread(new UserFeedback());
		t.start();
		
		if (train)
			Notifications.readyTrain();
		else
			Notifications.ready();
		
		clockTimer();
	}
	
	private void clockTimer() {
		
		boolean first = true;
		boolean blockStart = true;
		
		//Timing variables
		long startTime = 0;				
		long relativeTime = 0;
		int timeCounter = 0;
		int skipTime = -1;
		int waitTime = (int)(60/eventRate*1000);
		
		ArrayList<String> output = new ArrayList<String>();
	
		Serial.sendPack();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Random random = new Random();
		
		while (true) {		
			
			if (first) {
					startTime = System.nanoTime()/1000000;
					Serial.sendPack();
					
					relativeTime = 0;
					first = false;
					output.add("Participant Number: " + outputFile);
					output.add("Event Rate, " + eventRate);
					output.add("Block start, Skip, Key Press, Overall Time, Press Time");
			}
			else 
					relativeTime = System.nanoTime()/1000000 - startTime;
			
			if (train) {
				if (random.nextDouble() <= trainProb) {
					draw.skip();
					skipping = true;
				}	
				else {
					draw.next();
					skipping = false;
				}
				
			}
			else {
				if (timeCounter >= signalTime || skipTime == -1 ) {
					skipTime = (int)(random.nextDouble() * (signalTime/100))*100;
					System.out.println(skipTime);
					timeCounter = 0;
					blockStart = true;
					draw.next();
				}
				else if (skipTime <= timeCounter) {
					skipping = true;
					skipTime = signalTime;
					draw.skip();
				}
				else {
					blockStart = false;
					draw.next();
					skipping = false;
				}
			}
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			if (key_pressed != 1 && skipping)
				UserFeedback.setMiss();
			else if (binary && key_pressed == 0)
				UserFeedback.setMiss();
			
			time -= waitTime;
			timeCounter += waitTime;
				
			
			//Record output
		
			output.add(
					(blockStart ? 1 : 0) + ", " +
					(skipping ? 1 : 0) + ", " +
					key_pressed + ", " +
					relativeTime + ", " +
					((key_pressed == 1) ? press_time - startTime - relativeTime: 0)
					);
			
			key_pressed = 0;
			counter++;
			
			if (time <= 0) {
				break;
			}
		}
		
		//Records all output to a file
		if (!train)
			FileIO.outputToFile(output, outputFile);
		
		Notifications.finished();
		System.exit(0);
	}
	
	
	//Sets default options for the Window
	private void setFrame() {
		
		frame = new JFrame("Viglence Test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		//frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.addMouseListener(cellClick);
		frame.addKeyListener(buttonBindings);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.BLACK);
	}
	
	private void makeListeners() {
		
		buttonBindings = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				press_time = System.nanoTime()/10000000;
				if (!binary && arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					key_pressed = 1;
					
					if (!skipping) {
						UserFeedback.setMiss();
					}
						
					draw.repaint();
			
				}	
				else if (binary) {
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_UP) {
						key_pressed = 1;
						
						if (!skipping) {
							UserFeedback.setMiss();
						}
							
						draw.repaint();
					}
					else if (arg0.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
						key_pressed = 2;
						
						if (skipping) {
							UserFeedback.setMiss();;
						}
							
						draw.repaint();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	
	//Makes the pane that will do the drawing
	private void setDrawPane() {
		draw = new Task(Toolkit.getDefaultToolkit().getScreenSize());		
		frame.setContentPane(draw);
	}
	
}
