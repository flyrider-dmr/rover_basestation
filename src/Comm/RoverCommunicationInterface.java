package Comm;

import java.awt.Image;
import java.beans.XMLDecoder;
import java.io.*;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
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
 *  12 : lower drill
 *  13 : raise drill
 *  14 : drive forward
 *  15 : drive backward
 *  16 : turn left
 *  17 : turn right  
 *  21 : stop the rover
*/

public class RoverCommunicationInterface 
{
	String broker;
	String topicName = "marsrover/command";
	int qos = 1;
	
	public RoverCommunicationInterface()  throws Exception
	{
		// read serialized infos for connecting to the rover
	    XMLDecoder d = new XMLDecoder( new BufferedInputStream( new FileInputStream("connectionparameter.xml")));
	    SocketConnectionParameter connParams = (SocketConnectionParameter) d.readObject();
	    d.close();
		broker = "tcp://" + connParams.host + ":" + connParams.port; 
	}
	
	
	/*
	 *  COMMUNICATION TO THE ROVER : SENDING COMMANDS 
	 */
	
	
	public void requestStatusInfo() throws Exception
	{
		sendMessage("1:"); 
	}
	
	public void requestImage() throws Exception
	{
		sendMessage("2:"); 
        
	}
	
	public void requestMap() throws Exception
	{
		sendMessage("3:"); 
	}
	
	/*
	 *  COMMUNICATION TO THE ROVER : SENDING COMMANDS 
	 */
	
	public void setToAutonomous() throws Exception
	{
		 sendMessage("4:"); 
	}
	
	public void sendWayPoint(int x, int y, String text) throws Exception
	{
		sendMessage("5:" + x + ":" + y + ":" + text); 
	}
	
	public void resetAutonomous() throws Exception
	{
		 sendMessage("6:"); 
	}
	
	public void setToDirectContol() throws Exception
	{
		 sendMessage("11:"); 
	}
	public void lowerDrill() throws Exception
	{
		 sendMessage("12:"); 
	}
	
	public void raiseDrill() throws Exception
	{
		 sendMessage("13:"); 
	}
	
	public void forward() throws Exception
	{
		 sendMessage("14:"); 
	}
	
	public void backward() throws Exception
	{
		 sendMessage("15:"); 
	}
	
	public void turnLeft() throws Exception
	{
		 sendMessage("16:"); 
	}
	
	public void turnRight() throws Exception
	{
		 sendMessage("17:"); 
	}
	
	public void stopRover() throws Exception
	{
		 sendMessage("21:"); 
	}

	private void sendMessage(String command) throws Exception
	{
		MqttClient client = new MqttClient(broker,"Rover1");
		MqttConnectOptions mqOptions=new MqttConnectOptions();
	    mqOptions.setCleanSession(true);
	    client.connect(mqOptions);      //connecting to broker 
        mqOptions.setCleanSession(true); //no persistent session 
        mqOptions.setKeepAliveInterval(1000);
		MqttMessage message = new MqttMessage(command.getBytes());
		
		 
		message.setQos(qos);     //sets qos level 1
		message.setRetained(true); //sets retained message 
		
		MqttTopic topic = client.getTopic(topicName);
		
		topic.publish(message);    // publishes the message to the topic(test/topic)
	}
	
}
