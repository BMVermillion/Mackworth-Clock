
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * This class is responsible for drawing the entire screen. Data is not handled here
 * except for what is being displayed on the screen.
 */
public class Task extends JPanel{

	
	
	//Table formating variables
	private int width;
	private int height;
	private int dot;
	
	
	
	
	public Task(Dimension d) {
		
		
		this.setPreferredSize(new Dimension((int)d.getHeight()-50, (int)d.getWidth()));
		super.setBackground(Color.BLACK);
		
		//Variables to help draw the table
		width = (int) d.getWidth();
		height = (int) d.getHeight()-50;
		
		dot = 0;
		
	}
	
	
	//Draws data to the screen
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
	
		
		g2.setColor(Color.LIGHT_GRAY);		//Default line and text color
		//g2.setStroke(new BasicStroke(5));	//Line width
		
		drawCircles(g2);
		UserFeedback.drawMiss(g2, width, height);
		

	}
	
	private void drawCircles(Graphics2D g) {
		//System.out.println("Draw circles");
		int slice = 360/60;
		int x = width/2;
		int y = height/2;
		int r = 240;
		//g.drawOval(x-r, y-r, r*2, r*2);
		
		//System.out.println("x:"+x+" y:"+y+" r:"+r+" dot:"+dot);
		int rs = 10;
		for (int i=0; i<60; i++) 
			g.drawOval(x + (int)(r*Math.cos(slice*i*Math.PI/180)) - rs/2, y + (int)(r*Math.sin(slice*i*Math.PI/180)) - rs/2, rs, rs);
		
		if (dot >= 0)
			g.fillOval(x + (int)(r*Math.cos(slice*dot*Math.PI/180)) - rs/2, y + (int)(r*Math.sin(slice*dot*Math.PI/180)) - rs/2, rs, rs);
		
	}
	
	public void next() {
		dot++;
		this.repaint();
	}
	
	public void skip() {
		dot += 2;
		this.repaint();
	}

	
	}