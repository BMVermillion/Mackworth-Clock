
import java.io.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class FileIO {

//Outputs data to a file
public static void outputToFile( ArrayList<String> s, String name) {
								PrintWriter w = null;

								try {
																//Creates writer
																w = new PrintWriter(name, "utf-8");
								} catch (FileNotFoundException e) {
																e.printStackTrace();
																JOptionPane.showMessageDialog(new JFrame("Error"), new JLabel("<html><center>Failed to create file!</center></html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);

																return;
								} catch (UnsupportedEncodingException e) {
																e.printStackTrace();
																JOptionPane.showMessageDialog(new JFrame("Error"), new JLabel("<html><center>Failed to create file!</center></html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
																return;
								}

								//For all the strings provided, write them to the file
								for (String str : s)
																w.println(str);

								w.close();
}
}
