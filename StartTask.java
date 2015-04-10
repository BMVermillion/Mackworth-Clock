
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;

/*
 * This is where the task starts after the options menu closes. It reads in data, initializes other classes
 * handles the main loop of the program, records data, then outputs data.
 */
public class StartTask implements Runnable{
	
	//Main window
	private JFrame frame;
	//Draw frame
	private Task draw;
	
	//Important variables
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
	private double time;
	private boolean skipping = false;
	
	//Probability of skipping for training
	private final double trainProb = 0.3;
	
	//Set the variables
	public StartTask(String out, double evnRate, double sig, double time, boolean isBinary, boolean isTrain) {
		outputFile = out;
		eventRate = evnRate;
		signalTime = (int)(sig * 60 * 1000);
		binary = isBinary;
		train = isTrain;
		this.time = time*60*1000;
	}
	
	@Override
	//Thread that runs the task
	public void run() {

		/*init*/
		makeListeners();
		setFrame();		
		setDrawPane();
		UserFeedback.setTask(draw);
		new Thread(new UserFeedback()).start();
		////////
				
		frame.pack();
		frame.setVisible(true);
		
		//choose training notification
		if (train)
			Notifications.readyTrain();
		else
			Notifications.ready();
		
		//Start the task
		clockTimer();
	}
	
	private void clockTimer() {
		
		boolean first = true;
		boolean blockStart = true;
		
		//Timing variables
		long startTime = 0;						
		long relativeTime = 0;						//Time from start
		int timeCounter = 0;						//Counter for skip interval
		int skipTime = -1;							//Time to skip at
		int waitTime = (int)(60/eventRate*1000);	//Time for clock to wait
		
		ArrayList<String> output = new ArrayList<String>();
	
		//Send first serial pulse then wait
		Serial.sendPack();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//For random number generation
		Random random = new Random();
		
		
		while (true) {		
			
			//First run through of the loop, initialoze some variables
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
			
			//If training...
			if (train) {
				//Random chance at skipping
				if (random.nextDouble() <= trainProb) {
					draw.skip();
					skipping = true;
				}	
				else {
					draw.next();
					skipping = false;
				}
				
			}
			//Not training...
			else {
				//If counter becomes greater the the signal window...
				if (timeCounter >= signalTime || skipTime == -1 ) {
					//Generate a new skip time
					skipTime = (int)(random.nextDouble() * (signalTime/100))*100;
					
					//Reset counter and signal the start of a new block		Note: one skip appears per block
					timeCounter = 0;
					
					blockStart = true;
					skipping = false;
					draw.next();
				}
				//When counter hits skip time... Skip!
				else if (skipTime <= timeCounter) {
					blockStart = false;
					skipping = true;
					skipTime = signalTime;
					draw.skip();
				}
				//Normal operations...
				else {
					
					draw.next();
					blockStart = false;
					skipping = false;
				}
			}
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//If they miss a signal, draw the warning
			if (key_pressed != 1 && skipping)
				UserFeedback.setMiss();
			else if (binary && key_pressed == 0)
				UserFeedback.setMiss();
			
			//Update time variables
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

			
			//If the end of the task has been reached... end the loop
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
		
		frame = new JFrame("Mackworth Clock");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
				//Time of button press
				press_time = System.nanoTime()/1000000;
				
				//if a normal task (not binary)
				if (!binary && arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					Serial.sendPack();
					key_pressed = 1;
					
					//If pressed while not skipping, display error
					if (!skipping) {
						UserFeedback.setMiss();
					}
						
					draw.repaint();
				}	
				else if (binary) {
					//Settings for up key press
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_UP) {
						Serial.sendPack();
						key_pressed = 1;
						
						if (!skipping) {
							UserFeedback.setMiss();
						}
							
						draw.repaint();
					}
					//Settings for down key press 
					else if (arg0.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
						Serial.sendPack();
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
