
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/*
 * This class is responsible for drawing the entire screen. Data is not handled here
 * except for what is being displayed on the screen.
 */

@SuppressWarnings("serial")
public class Task extends JPanel {

//Screen formating variables
private int width;
private int height;
private int dot;
private int slice;
private int rs;
private int r;
private int x;
private int y;

public Task(Dimension d) {

								//Get screen dimensions and set size
								this.setPreferredSize(new Dimension((int)d.getHeight()-50, (int)d.getWidth()));
								super.setBackground(Color.BLACK);

								//Variables to help draw the table
								width = (int) d.getWidth();
								height = (int) d.getHeight()-50;

								//clock start point (0 degrees of a circle)
								dot = 0;

								//Divide the circle into 60 parts
								slice = 360/60;

								//Radius of the large circle
								r = 240;

								//Get center point of the screen
								x = width/2;
								y = height/2;

								//Radius of small circle
								rs = 10;

}


//Draws data to the screen
public void paintComponent(Graphics g) {
								super.paintComponent(g);
								Graphics2D g2 = (Graphics2D) g;


								g2.setColor(Color.LIGHT_GRAY); //Default line and text color

								drawCircles(g2);
								UserFeedback.drawMiss(g2, width, height);


}

private void drawCircles(Graphics2D g) {
								//Draw all the points for the clock
								for (int i=0; i<60; i++)
																g.drawOval(x + (int)(r*Math.cos(slice*i*Math.PI/180)) - rs/2, y + (int)(r*Math.sin(slice*i*Math.PI/180)) - rs/2, rs, rs);

								//Draw clock position
								if (dot >= 0)
																g.fillOval(x + (int)(r*Math.cos(slice*dot*Math.PI/180)) - rs/2, y + (int)(r*Math.sin(slice*dot*Math.PI/180)) - rs/2, rs, rs);

}

//Advance to next dot
public void next() {
								dot++;
								this.repaint();
}

//Advance to next dot, skipping one
public void skip() {
								dot += 2;
								this.repaint();
}


}
