
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * This is where the task starts after the options menu closes. It reads in data, initializes other classes
 * handles the main loop of the program, records data, then outputs data.
 */
public class StartTask extends TimerTask{
	
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
	private boolean exit = false;
	private boolean feedback;
	private String caseLogic;
	private long startTime;
	private long relativeTime;
	private int timeCounter;
	private int skipTime;
	private int waitTime;
	private boolean first;
	private boolean blockStart;
	
	private ArrayList<String> output;
	Random random;
	
	//Probability of skipping for training
	private final double trainProb = 0.3;
	
	long serialAcu = 0;
	long outAcu = 0;
	long logicAcu = 0;
	int counter = 0;
	long serialTime = 0;
	
	//Set the variables
	public StartTask(StartPack pack) {
		outputFile = pack.out;
		eventRate = pack.evnRate;
		signalTime = (int)(pack.sig * 60 * 1000);
		binary = pack.isBinary;
		train = pack.isTrain;
		this.time = pack.time*60*1000;
		feedback = pack.isFeedback;
		
		
		/*init*/
		makeListeners();
		setFrame();		
		setDrawPane();
		UserFeedback.setTask(draw);
		new Thread(new UserFeedback()).start();
		///////////////////////////////////////
				
		frame.pack();
		frame.setVisible(true);
		
		//choose training notification
		if (train)
			Notifications.readyTrain();
		else
			Notifications.ready();
		
		
		first = true;
		blockStart = true;
		
		//Timing variables
		startTime = 0;						
		relativeTime = 0;						//Time from start
		timeCounter = 0;						//Counter for skip interval
		skipTime = -1;							//Time to skip at
		waitTime = (int)(60/eventRate*1000);	//Time for clock to wait
		
		output = new ArrayList<String>();
	
		
		//For random number generation
		random = new Random();
		caseLogic = "START";
	}
	
	
	

	/*
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
	
		
		//For random number generation
		Random random = new Random();
		
		//Send first serial pulse then wait
		Serial.sendPack2();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		long serialAcu = 0;
		long outAcu = 0;
		long logicAcu = 0;
		int counter = 0;
		long serialTime = 0;
		while (true) {
			System.out.println("BEGINGIN LOOP");
			System.out.println(logicAcu);
			long logicTime = System.nanoTime();
			if (exit) {
				System.out.println("EXIT");
				if (Notifications.exit() == JOptionPane.OK_OPTION)
					break;
				else {
					exit = false;
					output.add("Program paused");
				}
			}
				
			//First run through of the loop, initialize some variables
			if (first) {
				System.out.println("FIRST RUN");
				startTime = System.nanoTime()/1000000;
				Serial.sendPack2();
					
				relativeTime = 0;
				first = false;
				output.add("Participant Number: " + outputFile);
				output.add("Event Rate, " + eventRate);
				output.add("Block start, Skip, Key Press, Overall Time, Press Time");
			}
			else {
				relativeTime = System.nanoTime()/1000000 - startTime;
			}
			
			//If training...
			if (train) {
				System.out.println("IN TRAINING");
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
			logicTime = System.nanoTime() - logicTime;
			System.out.print("Logic time = " + logicTime);
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//If they miss a signal, draw the warning
			if (key_pressed != 1 && skipping && !binary) 
				if (feedback)
					UserFeedback.setMiss();
			
			else if (binary && key_pressed == 0)
				if (feedback)
					UserFeedback.setMiss();

			
			
			//Update time variables
			time -= waitTime;
			timeCounter += waitTime;
				
			
			long outputTime = System.nanoTime();
			//Record output
			output.add(
					(blockStart ? 1 : 0) + ", " +
					(skipping ? 1 : 0) + ", " +
					key_pressed + ", " +
					relativeTime + ", " +
					((key_pressed == 1 || key_pressed == 2) ? press_time - startTime - relativeTime: 0)
					);
			
			outputTime = System.nanoTime() - outputTime;
			System.out.print(" | Output diff: " + outputTime);
			
			//if ( press_time - startTime - relativeTime < 0 && key_pressed != 0)
				//System.out.println(press_time +"\n" +startTime+ "\n" + relativeTime + "\n" + (press_time - startTime - relativeTime) + "\n");
			
			serialTime = System.nanoTime();
			Serial.sendPack2();
			key_pressed = 0;
			
			serialTime = System.nanoTime() - serialTime;
			System.out.println(" | Serial diff: " + serialTime);
			serialAcu += serialTime;
			outAcu += outputTime;
			logicAcu = logicAcu + logicTime;
			counter++;
			
			//If the end of the task has been reached... end the loop
			if (time <= 0) {
				break;
			}
			
			System.out.println(logicAcu);
			System.out.println("END LOOP");
		}
		
		System.out.println("Total Serial time: " + serialAcu + "ns " + (serialAcu/1000000) + "ms " + (int)(serialAcu/1000000000) + "s" );
		System.out.println("Average Serial Time: " + (int)(serialAcu/counter));
		System.out.println("Total Output time: " + outAcu + "ns " + (outAcu/1000000) + "ms " + (int)(outAcu/1000000000) + "s" );
		System.out.println("Average Output Time: " + (int)(outAcu/counter));
		System.out.println("Total Logic time: " + logicAcu + "ns " + (logicAcu/1000000) + "ms " + (int)(logicAcu/1000000000) + "s" );
		System.out.println("Average Logic Time: " + (int)(logicAcu/counter));

		
		//Records all output to a file
		if (!train)
			FileIO.outputToFile(output, outputFile);
		
		Notifications.finished();
		//System.exit(0);
	}
	
private void clockTimerOptimized() {
		
		boolean first = true;
		boolean blockStart = true;
		
		//Timing variables
		long startTime = 0;						
		long relativeTime = 0;						//Time from start
		int timeCounter = 0;						//Counter for skip interval
		int skipTime = -1;							//Time to skip at
		int waitTime = (int)(60/eventRate*1000);	//Time for clock to wait
		
		ArrayList<String> output = new ArrayList<String>();
	
		
		//For random number generation
		Random random = new Random();
		
		//Send first serial pulse then wait
		Serial.sendPack2();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		long serialAcu = 0;
		long outAcu = 0;
		long logicAcu = 0;
		int counter = 0;
		long serialTime = 0;
		
		String caseLogic = "START";
		while (true) {
			
			System.out.print("__");
			//System.out.println("BEGINGIN LOOP");
			//System.out.println(logicAcu);
			
			long logicTime = System.nanoTime();
			switch (caseLogic) {
			
			case "EXIT": 
				System.out.println("EXIT");
				if (Notifications.exit() == JOptionPane.OK_OPTION)
					break;
				else {
					exit = false;
					output.add("Program paused");
				}
				break;
				
			case "START": 
				//First run through of the loop, initialize some variables
				System.out.println("FIRST RUN");
				startTime = System.nanoTime()/1000000;
				Serial.sendPack2();
					
				relativeTime = 0;
				first = false;
				output.add("Participant Number: " + outputFile);
				output.add("Event Rate, " + eventRate);
				output.add("Block start, Skip, Key Press, Overall Time, Press Time");
				
				if (train)
					caseLogic = "TRAIN";
				else
					caseLogic = "RUN";
				break;
				
			case "TRAIN":
				System.out.println("IN TRAINING");
				//Random chance at skipping
				if (random.nextDouble() <= trainProb) {
					draw.skip();
					skipping = true;
				}	
				else {
					draw.next();
					skipping = false;
				}
				break;
			case "RUN":
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
					System.out.print("S");
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

			relativeTime = System.nanoTime()/1000000 - startTime;
			
			logicTime = System.nanoTime() - logicTime;
			//System.out.print("Logic time = " + logicTime);
			
			System.out.print("Z");
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//If they miss a signal, draw the warning
			if (key_pressed != 1 && skipping && feedback) 
				if (feedback)
					UserFeedback.setMiss();
			
			else if (binary && key_pressed == 0 && feedback)
					UserFeedback.setMiss();

			
			
			//Update time variables
			time -= waitTime;
			timeCounter += waitTime;
				
			
			long outputTime = System.nanoTime();
			//Record output
			System.out.print("O");
			output.add(
					(blockStart ? 1 : 0) + ", " +
					(skipping ? 1 : 0) + ", " +
					key_pressed + ", " +
					relativeTime + ", " +
					((key_pressed == 1 || key_pressed == 2) ? press_time - startTime - relativeTime: 0)
					);
			
			outputTime = System.nanoTime() - outputTime;
			//System.out.print(" | Output diff: " + outputTime);
			
			//if ( press_time - startTime - relativeTime < 0 && key_pressed != 0)
				//System.out.println(press_time +"\n" +startTime+ "\n" + relativeTime + "\n" + (press_time - startTime - relativeTime) + "\n");
			
			serialTime = System.nanoTime();
			Serial.sendPack2();
			key_pressed = 0;
			
			serialTime = System.nanoTime() - serialTime;
			//System.out.println(" | Serial diff: " + serialTime);
			serialAcu += serialTime;
			outAcu += outputTime;
			logicAcu = logicAcu + logicTime;
			counter++;
			
			//If the end of the task has been reached... end the loop
			if (time <= 0) {
				break;
			}
			
			//System.out.println(logicAcu);
			//System.out.println("END LOOP");
		}
		System.out.println();
		System.out.println("Total Serial time: " + serialAcu + "ns " + (serialAcu/1000000) + "ms " + (int)(serialAcu/1000000000) + "s" );
		System.out.println("Average Serial Time: " + (int)(serialAcu/counter));
		System.out.println("Total Output time: " + outAcu + "ns " + (outAcu/1000000) + "ms " + (int)(outAcu/1000000000) + "s" );
		System.out.println("Average Output Time: " + (int)(outAcu/counter));
		System.out.println("Total Logic time: " + logicAcu + "ns " + (logicAcu/1000000) + "ms " + (int)(logicAcu/1000000000) + "s" );
		System.out.println("Average Logic Time: " + (int)(logicAcu/counter));

		
		//Records all output to a file
		if (!train)
			FileIO.outputToFile(output, outputFile);
		
		Notifications.finished();
		System.exit(0);
	}
	
	*/
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
				if (key_pressed != 0)
					return;
				
				press_time =  System.nanoTime()/1000000;
				
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					exit = true;
					caseLogic = "EXIT";
				}
				
				//if a normal task (not binary)
				if (!binary && arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					Serial.sendPack1();
					key_pressed = 1;
					
					//If pressed while not skipping, display error
					if (!skipping && feedback) {
						UserFeedback.setMiss();
					}
						
					draw.repaint();
				}	
				else if (binary) {
					//Settings for up key press
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_A) {
						Serial.sendPack1();
						key_pressed = 1;
						
						if (!skipping && feedback) {
							UserFeedback.setMiss();
						}
							
						draw.repaint();
					}
					//Settings for down key press 
					else if (arg0.getExtendedKeyCode() == KeyEvent.VK_L) {
						Serial.sendPack1();
						key_pressed = 2;
						
						if (skipping) {
							UserFeedback.setMiss(); 
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




	@Override
	public void run() {
		
	long loopSTime = System.nanoTime()/1000000 ;
	
	if (caseLogic != "START") {
		output.add(
				(blockStart ? 1 : 0) + ", " +
				(skipping ? 1 : 0) + ", " +
				key_pressed + ", " +
				relativeTime + ", " +
				((key_pressed == 1 || key_pressed == 2) ? press_time - startTime - relativeTime: 0)
				);
		
		//If they miss a signal, draw the warning
		if (key_pressed != 1 && skipping && feedback) { 
			//System.out.println("Test");
				UserFeedback.setMiss();
		}
		
		else if (binary && key_pressed == 0 && feedback)
				UserFeedback.setMiss();
		
		time -= waitTime;
		timeCounter += waitTime;
	}
	
	relativeTime = loopSTime - startTime;
	Serial.sendPack2();
		
	key_pressed = 0;
	
	
	
	switch (caseLogic) {
		
		case "EXIT": 
			//System.out.println("EXIT");
			if (Notifications.exit() != JOptionPane.OK_OPTION) {
				caseLogic = "RUN";
				break;
			}
	
		case "DONE":
			if (!train)
				FileIO.outputToFile(output, outputFile);
			
			Notifications.finished();
			System.exit(0);
			break;
			
		case "START": 
			//First run through of the loop, initialize some variables
			//System.out.println("FIRST RUN");
			startTime = System.nanoTime()/1000000;
			draw.next();
			//Serial.sendPack2();
				
			relativeTime = 0;
			//first = false;
			output.add("Participant Number: " + outputFile);
			output.add("Event Rate, " + eventRate);
			output.add("Block start, Skip, Key Press, Overall Time, Press Time");
			
			if (train)
				caseLogic = "TRAIN";
			else
				caseLogic = "RUN";
			
			
			//Generate first skip time
			skipTime = (int)(random.nextDouble() * (signalTime/100))*100;
			
			//Set counter and signal		Note: one skip appears per block
			timeCounter = 0;
			
			blockStart = true;
			skipping = false;
			
			break;
			
		case "TRAIN":
			//System.out.println("IN TRAINING");
			//Random chance at skipping
			if (random.nextDouble() <= trainProb) {
				draw.skip();
				skipping = true;
			}	
			else {
				draw.next();
				skipping = false;
			}
			break;
		case "RUN":
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
				//System.out.print("S");
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

		if (time <= 0) {
			caseLogic = "DONE";
		}
	
	}
	
}
