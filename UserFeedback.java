
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/*
 * The UserFeedback class is a thread that is responsible for drawing
 * and timing for the flashes that occurs when a user presses a key or
 * misses a event
 */
public class UserFeedback implements Runnable {

	private static int hitTime = 0;
	private static int missTime = 0;
	private static int falseTime = 0;
	
	private static final int wait = 25;
	private static final int time = 250;
	
	
	private static Task t;
	public void run() {
		
		while(true) {
		
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if (hitTime > 0)
				hitTime -= wait;
		
			if (missTime > 0)
				missTime -= wait;
		
			if (falseTime > 0)
				falseTime -= wait;
		
			t.repaint();
			
			//System.out.println(barTime);
		}
	}
	
	
	
	public static void drawMiss(Graphics2D g2, int w, int h) {
		if (missTime <= 0)
			return;
		
		g2.setColor(Color.RED);
		g2.drawLine(w/2-100, h/2-100, w/2+100, h/2+100);
		g2.drawLine(w/2+100, h/2-100, w/2-100, h/2+100);

		g2.setStroke(new BasicStroke(10));
		
	}
	
	public static void setMiss() {
		missTime = time;
		Serial.sendPack();
	}
	

	
	
	
	public static void setTask (Task task) {
		t = task;
	}
}
