
import java.io.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


//Data structure to hold the strings and integers read in from a input file
class Pair {
	public String[] str;
	public int [] in;
	
	public Pair() {
		//str = IP, Port, IP, Port
		str = new String[4];
		//in = true/false, section
		in = new int[2];
	}
	
}

public class FileIO {
	
	//Reads in from a file 
	public static ArrayList<Pair> getInputFile(String file) {
		
		ArrayList<Pair> list= new ArrayList<Pair>();
		
		File f = new File(file);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		while (s.hasNext()) {
			Pair p = new Pair();
			list.add(p);
			
			for (int i=0; i<4; i++ ) 
				p.str[i] = s.next();
			
			p.in[0] = s.nextInt();
			p.in[1] = s.nextInt();
			
		}
		
		s.close();
		return list;
		
	}
	
	//Gets only the strings from the list of pairs
	public static String[][] getStrings( ArrayList<Pair> list ) {
		String[][] s = new String[list.size()][4];
		
		int i=0;
		for (Pair p: list) 
			s[i++] = p.str.clone();
		
	
		return s;
	}
	
	public static void outputToFile( ArrayList<String> s, String name) {
		PrintWriter w = null;
		
		try {
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
		
		for (String str : s) 
			w.println(str);
		
		w.close();
		
	}
	
	public static boolean testInputFile(String file) {
		File f = new File(file);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
		
			return true;
		}
		
		s.close();
		return false;
	}
}
