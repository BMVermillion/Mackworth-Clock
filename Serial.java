
import gnu.io.*;
import java.io.*;

/*
 * The Serial class handles all of the serial communication for the program. Everything is
 * static, so first call connect() then you can use sendPack(). sendPack() will not throw a error
 * if you don't connect first, it just won't send anything. Please close when you are done :)
 */
public class Serial {

//Serial port variables
private static SerialPort serialPort;
private static OutputStream out;

//Packet to send over serial
private final static byte[] pack1 = { 0x56, 0x5A, 0x00, 0x01, 0x01, 0x01, (byte) 0xFC };
private final static byte[] pack2 = { 0x56, 0x5A, 0x00, 0x02, 0x01, 0x00, 0x01, (byte) 0xFB };


//Trys to connect to a given port
public static boolean connect ( String port ) throws Exception
{
							CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);

							if ( portIdentifier.isCurrentlyOwned() )
							{
															System.out.println("Error: Port is currently in use");
															return false;
							}
							else
							{
															CommPort commPort = portIdentifier.open("CERN",2000);

															if ( commPort instanceof SerialPort )
															{
																							serialPort = (SerialPort) commPort;
																							serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
																							out = serialPort.getOutputStream();

																							return true;
															}
															else
																							return false;
							}
}

//Sends packet, does nothing if not connected
public static void sendPack1() {
								//System.out.print(1);
								if (out == null)
																return;

								try {
																out.write(pack1, 0, pack1.length);
																out.flush();
								} catch (IOException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
																Notifications.errorWrite();
								}
}

//Sends packet, does nothing if not connected
public static void sendPack2() {
								//System.out.print(2);
								if (out == null)
																return;

								try {
																out.write(pack2, 0, pack2.length);
																out.flush();
								} catch (IOException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
																Notifications.errorWrite();
								}
}

//Closes the connection
public static void close() {
								if (serialPort != null)
																serialPort.close();
}
}
