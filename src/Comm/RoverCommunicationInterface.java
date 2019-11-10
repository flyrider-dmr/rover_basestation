package Comm;

import java.awt.Image;
import java.beans.XMLDecoder;
import java.io.*;
import java.net.Socket;

/* RoverCommunicationInterface
 * Interface o send commands to the rover and to receive info messages and images
 *  
 *  Command structure:
 *  
 *  1 : request for status information
 *  2 : request picture
 *  3 : request map
 *  4 : enable autonomous drive mode 
 *  5 : send new waypoint
 *  6 : disable autonomous drive mode 
 *  
 *  11 : set direct control mode
 *  12 : lower sensor
 *  13 : raise sensor
 *  14 : drive forward
 *  15 : drive backward
 *  16 : turn left
 *  17 : turn right  
 *  21 : stop the rover
*/
public class RoverCommunicationInterface 
{
	SocketConnectionParameter connParams;
	Socket socket = null;
	OutputStream out;
	InputStream in;
	PrintStream psout;
	
	public RoverCommunicationInterface()  throws Exception
	{
		// read serialized infos for connecting to the rover
	    XMLDecoder d = new XMLDecoder( new BufferedInputStream( new FileInputStream("connectionparameter.xml")));
	    connParams = (SocketConnectionParameter) d.readObject();
	    d.close();
	}
	
	public void connectToRover() throws Exception
	{
		socket = new Socket(connParams.host, connParams.port);
		out = socket.getOutputStream(); 
		in = socket.getInputStream();
	    psout = new PrintStream(out, true);
	}
	
	public void disconnectToRover() throws Exception
	{
		socket.close();
	}
	
	/*
	 *  COMMUNICATION TO THE ROVER : REQUEST INFORMATION FROM ROVER
	 */
	
	
	public String requestStatusInfo() throws Exception
	{
		psout.print(1); 
        BufferedReader buff = new BufferedReader(new InputStreamReader(in));
        String out =  buff.readLine() + "\n";
        return out;
	}
	
	public Image requestImage() throws Exception
	{
		psout.print(2); 
        BufferedReader buff = new BufferedReader(new InputStreamReader(in));
        /*
        byte[] resultBuff = new byte[0];
        byte[] buff = new byte[1024];
        int k = -1;
        while((k = socket.getInputStream().read(buff, 0, buff.length)) > -1) 
        {
            byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
            System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
            System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
            resultBuff = tbuff; // call the temp buffer as your result buff
        }
        System.out.println(resultBuff.length + " bytes read.");
        return null;
        */
        
        String out =  buff.readLine() + "\n";
        System.out.println(out);
        return null;
        
	}
	
	public String requestMap() throws Exception
	{
		 psout.print(3); 
        BufferedReader buff = new BufferedReader(new InputStreamReader(in));
        String out =  buff.readLine() + "\n";
        return out;
	}
	
	/*
	 *  COMMUNICATION TO THE ROVER : SENDING COMMANDS FOR AUTONOMOUS MODE
	 */
	
	public void setToAutonomous() throws Exception
	{
		 psout.print(4); 
	}
	
	public void sendWayPoint(int x, int y, String text) throws Exception
	{
		psout.print("5:" + x + ":" + y + ":" + text); 
	}
	
	public void resetAutonomous() throws Exception
	{
		 psout.print(6); 
	}
		
	/*
	 *  COMMUNICATION TO THE ROVER : SENDING COMMANDS FOR REMOTE CONTROL  MODE
	 */
	
	public void setToDirectContol() throws Exception
	{
		 psout.print(11); 
	}
	public void lowerSensor() throws Exception
	{
		 psout.print(12); 
	}
	
	public void raiseSensor() throws Exception
	{
		 psout.print(13); 
	}
	
	public void forward() throws Exception
	{
		 psout.print(14); 
	}
	
	public void backward() throws Exception
	{
		 psout.print(15); 
	}
	
	public void turnLeft() throws Exception
	{
		 psout.print(16); 
	}
	
	public void turnRight() throws Exception
	{
		 psout.print(17); 
	}
	
	public void stopRover() throws Exception
	{
		 psout.print(21); 
	}
	
	// Close socket

	public void close() throws Exception
	{ 
		socket.close();
	}
	
	
	
}
