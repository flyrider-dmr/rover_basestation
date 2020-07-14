import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import Comm.RoverCommunicationInterface;
import Comm.SocketConnectionParameter;
import Map.Map;
import Map.MapPanel;
import Map.MapPoint;

import org.eclipse.paho.client.mqttv3.*;


public class RoverbaseStationMain implements ActionListener, MqttCallback
{

	static int ROVER_UPDATE_TIME = 10;
	static int MAP_UPDATE_TIME = 15;

	
	JPanel commandP, remoteP, autonomousP ,mastCamPanel;
	ImagePanel imageP;
	MapPanel mapP;
	JTabbedPane tab, tabC;
	JButton connectB, disconnectB , gotoB, lowerprobeB, raiseprobeB, stopB , stopB2 , fwdB, bwdB, leftB, righB , autB;
	JButton mastLeft, mastRight, mastUp, mastDown;
	JTextArea infoA;
	JTextField inputX, inputY, inputT, roverStatus, drillOffX, drillOffY, mastAngle;
	RoverCommunicationInterface comIf;
	Map map;
	Timer infotimer, maptimer, imagetimer ;
	JFrame mainW;
	
	public static void main(String[] args) 
	{
		new RoverbaseStationMain();
	}

	public RoverbaseStationMain()
	{
		
		// Build up GUI - Main Window
	    mainW = new JFrame("Discount Mars Rover Base Station"); 
		mainW.setSize(1600,860);
		mainW.setResizable(false);
		mainW.setLocation(100,100);
		mainW.setLayout(null);
		mainW.getContentPane().setBackground(Color.BLACK);
		mainW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Tab pane
		JTabbedPane tab = new JTabbedPane();
		tab.setBounds(20,20,1000,800);
		mainW.add(tab);
		
		//Panels - Panel to display images sent from the rover
		imageP = new ImagePanel();
		imageP.setBackground(Color.BLACK);
		
		// map panel
		//Panels - Panel to display images sent from the rover
		map = new Map();
		
		
		
		// ********************** DUMMY ****************** REMOVE BEFORE FLIGHT **************
		//map.dummytestDataLoader();
		// ********************** DUMMY ****************** REMOVE BEFORE FLIGHT **************
		
		
		
		mapP = new MapPanel(map);
        JScrollPane scrollPane = new JScrollPane (mapP,  ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		// Panel für the command buttons and info display
		commandP = new JPanel();
		commandP.setBackground(Color.BLACK);
		commandP.setBounds(1040,40,540,780);
		commandP.setLayout(null);
		
		roverStatus = new JTextField();
		roverStatus.setBounds(10,50,500,50);
		roverStatus.setEditable(false);
		roverStatus.setFont(new Font("Arial", 0 , 18));
		roverStatus.setForeground(Color.black);
		commandP.add(roverStatus);
		updateRoverStatus();
		
		infoA = new JTextArea();
        JScrollPane scrollPane1 = new JScrollPane (infoA,  ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane1.setBounds(10,440,500, 330);
		commandP.add(scrollPane1);
		mainW.add(commandP);
		
		// build the tab view
		tab.addTab("Rover Image View", imageP);
		tab.addTab("Map Overview", scrollPane);
		
		// Buttons for commands
		connectB = new JButton("CONNECT");
		connectB.setBounds(10,0,130,30);
		connectB.addActionListener(this);
		commandP.add(connectB);
		
		disconnectB = new JButton("DISCONNECT");
		disconnectB.setBounds(150,0,130,30);
		disconnectB.addActionListener(this);
		commandP.add(disconnectB);
		
		autB = new JButton("AUTONOMOUS ON / OFF");
		autB.setBounds(300,0,200,30);
		autB.addActionListener(this);
		commandP.add(autB);
		// Command panel Tab
		
		tabC = new JTabbedPane();
		tabC.setBounds(10,120, 500, 200);
		
		remoteP = new JPanel();
		remoteP.setBackground(Color.black);
		remoteP.setLayout(null);
		
		mastCamPanel = new JPanel();
		mastCamPanel.setBackground(Color.black);
		mastCamPanel.setLayout(null);
		
		autonomousP = new JPanel();
		autonomousP.setLayout(null);
		autonomousP.setBackground(Color.black);
		tabC.addTab("Remote Control", remoteP);
		tabC.addTab("Autonomous Drive", autonomousP);
		tabC.addTab("Camera Mast Control", mastCamPanel);
		
		commandP.add(tabC);
		// Buttons for autonomous drive
		
		JLabel lab = new JLabel("WAYPOINT    X :            Y :           Name:"	);
		lab.setFont(new Font("Arial", 0 , 18));
		lab.setForeground(Color.WHITE);
		lab.setBounds(10,10,500,40);
		autonomousP.add(lab);
		
		gotoB = new JButton("SET WAYPOINT");
		//gotoB.setEnabled(false);
		gotoB.setBounds(120,100,230,30);
		gotoB.addActionListener(this);
		autonomousP.add(gotoB);

		inputX = new JTextField();
		inputX.setBounds(120,50,50,30);
		autonomousP.add(inputX);
		
		inputY = new JTextField();
		inputY.setBounds(200,50,50,30);
		autonomousP.add(inputY);
		
		inputT = new JTextField();
		inputT.setBounds(270,50,200,30);
		autonomousP.add(inputT);
		
		commandP.add(tabC);
		
		// Buttons for remote control drive
		
		fwdB = new JButton("FORWARD");
		//probeB.setEnabled(false);
		fwdB.setBounds(150,10,130,30);
		fwdB.addActionListener(this);
		remoteP.add(fwdB);
		
		leftB = new JButton("LEFT");
		//probeB.setEnabled(false);
		leftB.setBounds(10,70,130,30);
		leftB.addActionListener(this);
		remoteP.add(leftB);
		
		stopB2 = new JButton("STOP");
		//probeB.setEnabled(false);
		stopB2.setBounds(150,70,130,30);
		stopB2.addActionListener(this);
		remoteP.add(stopB2);
		
		righB = new JButton("RIGHT");
		//probeB.setEnabled(false);
		righB.setBounds(300,70,130,30);
		righB.addActionListener(this);
		remoteP.add(righB);
		
		bwdB = new JButton("BACKWARD");
		//probeB.setEnabled(false);
		bwdB.setBounds(150,130,130,30);
		bwdB.addActionListener(this);
		remoteP.add(bwdB);
		
		
		// Camera mast Remote Control
		
		mastLeft = new JButton("LEFT");
		mastLeft.setBounds(10,70,130,30);
		mastLeft.addActionListener(this);
		mastCamPanel.add(mastLeft);
		
		mastRight = new JButton("RIGHT");
		mastRight.setBounds(300,70,130,30);
		mastRight.addActionListener(this);
		mastCamPanel.add(mastRight);

		
		mastUp = new JButton("UP");
		mastUp.setBounds(150,10,130,30);
		mastUp.addActionListener(this);
		mastCamPanel.add(mastUp);
		
		mastDown = new JButton("DOWN");
		mastDown.setBounds(150,130,130,30);
		mastDown.addActionListener(this);
		mastCamPanel.add(mastDown);
		
		mastAngle = new JTextField();
		mastAngle.setBounds(190,70,50,30);
		mastCamPanel.add(mastAngle);
		
		// General Buttons and Textfields
		
		
		lowerprobeB = new JButton("DRILL LOW");
		//probeB.setEnabled(false);
		lowerprobeB.setBounds(10,340,130,30);
		lowerprobeB.addActionListener(this);
		commandP.add(lowerprobeB);

		
		raiseprobeB = new JButton("DRILL UP");
		//probeB.setEnabled(false);
		raiseprobeB.setBounds(150,340,130,30);
		raiseprobeB.addActionListener(this);
		commandP.add(raiseprobeB);
		
		stopB = new JButton("STOP");
		//stopB.setEnabled(false);
		stopB.setBounds(300,340,130,30);
		stopB.addActionListener(this);
		commandP.add(stopB);
		
		JLabel lab1 = new JLabel("DRILL OFFSET   X :");
		lab1.setBounds(10,390,170,30);
		lab1.setFont(new Font("Arial", 0 , 18));
		lab1.setForeground(Color.WHITE);
		commandP.add(lab1);
		
		JLabel lab2 = new JLabel(" Y : ");
		lab2.setBounds(250,390,170,30);
		lab2.setFont(new Font("Arial", 0 , 18));
		lab2.setForeground(Color.WHITE);
		commandP.add(lab2);
		
		drillOffX = new JTextField();
		drillOffX.setBounds(190,390,50,30);
		commandP.add(drillOffX);
		
		drillOffY = new JTextField();
		drillOffY.setBounds(300,390,50,30);
		commandP.add(drillOffY);
		
		setControlsEnabled(false);
		mainW.setVisible(true);
		
		
		// Timer setup
		infotimer = new Timer(7000, this);
		infotimer.addActionListener(this);
		maptimer  = new Timer(11000, this);
		maptimer.addActionListener(this);
		imagetimer  = new Timer(13000, this);
		imagetimer.addActionListener(this);
		
		
		
		// MQTT Subscriber
		// read serialized infos for connecting to the rover
	    try {
	    	comIf = new RoverCommunicationInterface();
	    	
			XMLDecoder d = new XMLDecoder( new BufferedInputStream( new FileInputStream("connectionparameter.xml")));
			SocketConnectionParameter connParams = (SocketConnectionParameter) d.readObject();
			d.close();
			String broker = "tcp://" + connParams.getHost() + ":" + connParams.getPort(); 
			
			MqttClient client = new MqttClient(broker,"RoverMain");
			client.setCallback(this);
			MqttConnectOptions mqOptions=new MqttConnectOptions();
			 mqOptions.setCleanSession(true);
			 client.connect(mqOptions);      //connecting to broker 
			 client.subscribe("marsrover/map"); //subscribing to the topic name  test/topic
			 client.subscribe("marsrover/statusinfo"); //subscribing to the topic name  test/topic
			 client.subscribe("marsrover/image"); //subscribing to the topic name  test/topic
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
		/*
		SocketConnectionParameter c = new SocketConnectionParameter();
		c.setHost("10.11.12.1");
		c.setPort(8888);
		
	    XMLEncoder e;
		try {
			e = new XMLEncoder( new BufferedOutputStream( new FileOutputStream("connectionparameter.xml")));
			e.writeObject(c);
			e.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
	
	}

	private void setControlsEnabled (boolean stat)
	{
		fwdB.setEnabled(stat);
		bwdB.setEnabled(stat);
		stopB.setEnabled(stat);
		stopB2.setEnabled(stat);
		righB.setEnabled(stat);
		leftB.setEnabled(stat);
		lowerprobeB.setEnabled(stat);
		raiseprobeB.setEnabled(stat);
		
	}
	
	private void updateRoverStatus() 
	{
		if(map == null)
			roverStatus.setText("No status information");
		else
		{	// ROVER DATA
			String status = "PoX: " + map.rover.estX + " PoY:  " + map.rover.estX + " Sp: " + map.rover.speed + " TA: " +  map.rover.rotX; 
			if(map.rover.sensUP == 1)
				status = status + " DR UP";
			else
				status = status + " DR DOWN";
			status = status + " CaT: " + map.rover.mastTurn + " CaP: " + map.rover.mastPitch;
			roverStatus.setText(status);
			
			// POSITION
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");  
            String strDate = dateFormat.format( new Date());  
			map.positions.add(new MapPoint( map.rover.estX,  map.rover.estY, strDate));
		}
			
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == connectB)
		{
			try 
			{
				// Comm Interface
				comIf = new RoverCommunicationInterface();
				infoA.append("Starting timer for information update requests...\n");
				infotimer.start();
				maptimer.start();
				imagetimer.start();
				// Enable buttons
				lowerprobeB.setEnabled(true);
				raiseprobeB.setEnabled(true);
				setControlsEnabled(true);
				comIf.requestStatusInfo();
				
			} 
			catch (Exception e1) 
			{
				infoA.append("Timer start failed\n" + e1.getMessage() + "\n");
				
			}
		}
		
		if(e.getSource() == autB)
		{
			int stat = map.rover.autonomousDrive;

			try {
				if(stat == 1)
				{
					infoA.append("Command ENABLE AUTONOMOUS DRIVE sent...\n");
					comIf.setToAutonomous();
				}
				else
				{
					infoA.append("Command DISABLE AUTONOMOUS DRIVE sent...\n");
					comIf.resetAutonomous();
				}
				
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for RIGHT command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == stopB || e.getSource() == stopB2) 
		{
			try {
				infoA.append("Rover STOP command sent...\n");
				comIf.stopRover();
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure during stop command\n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == leftB)
		{
			try {
				infoA.append("Command LEFT TURN sent...\n");
				comIf.turnLeft();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for LEFT command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == righB)
		{
			try {
				infoA.append("Command RIGHT TURN sent...\n");
				comIf.turnRight();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for RIGHT command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == fwdB)
		{
			try {
				infoA.append("Command FORWARD sent...\n");
				comIf.forward();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for FORWARD command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == bwdB)
		{
			try {
				infoA.append("Command BACKWARD sent...\n");
				comIf.backward();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for BACKWARD command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == lowerprobeB)
		{
			try 
			{
				infoA.append("Command DRILL DOWN sent...\n");
				int x,y =  0;
				
				try {
					x = Integer.parseInt(drillOffX.getText());
				} catch (Exception e1) 
				{
					x = 0;
				}
				
				try {
					y = Integer.parseInt(drillOffY.getText());
				} catch (Exception e1) {
					y = 0; 
				}
				comIf.lowerDrill(x,y);

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for sensor down command... \n" + e1.getMessage() + "\n");
			}
		}
		try
		{
			// MAST REMOTE CONTROL
			if(e.getSource() == mastLeft)
			{
				int ang = Integer.parseInt(mastAngle.getText());
				if(Math.abs(ang) > 45)
				{
					JOptionPane.showMessageDialog(commandP, "Maximum turn angle ist 45 degrees");
					return;
				}
				mastAngle.setText("");
				comIf.mastCamLeft(ang);
			}
			if(e.getSource() == mastRight)
			{
				int ang = Integer.parseInt(mastAngle.getText());
				if(Math.abs(ang) > 45)
				{
					JOptionPane.showMessageDialog(commandP, "Maximum turn angle ist 45 degrees");
					return;
				}
				mastAngle.setText("");
				comIf.mastCamRight(ang);
			}
			if(e.getSource() == mastUp)
			{
				int ang = Integer.parseInt(mastAngle.getText());
				if(Math.abs(ang) > 30)
				{
					JOptionPane.showMessageDialog(commandP, "Maximum tilt up angle ist 30 degrees");
					return;
				}
				mastAngle.setText("");
				comIf.mastCamUp(ang);
			}
			if(e.getSource() == mastDown)
			{
				int ang = Integer.parseInt(mastAngle.getText());
				if(Math.abs(ang) > 30)
				{
					JOptionPane.showMessageDialog(commandP, "Maximum tilt up angle ist 30 degrees");
					return;
				}
				mastAngle.setText("");
				comIf.mastCamDown(ang);
			}
		}
		catch (Exception e1) 
		{
			JOptionPane.showMessageDialog(commandP, "Please insert angle for camera operation");
			return;
		}
		// DRILL REMOTE CONTROL
		if(e.getSource() == raiseprobeB)
		{
			try {
				infoA.append("Command DRILL UP sent...\n");
				comIf.raiseDrill();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for sensor up  command \n" + e1.getMessage() + "\n");
			}
		}
		if(e.getSource() == gotoB)
		{
			try 
			{
				int x = Integer.parseInt(inputX.getText());
				int y = Integer.parseInt(inputX.getText());
				String text = inputT.getText();
				comIf.sendWayPoint(x,y, text);
				infoA.append("New waypoint " + x + " " + y + " "  + text + " sent to rover...\n");
			} 
			catch (NumberFormatException e4) 
			{
				infoA.append("Please insert values for waypoint coordinates\n" + e4.getMessage() + "\n");
			}
			catch (Exception e1) 
			{
				infoA.append("Communication failure\n" + e1.getMessage() + "\n");
			}
		}
		if(e.getSource() == infotimer)
		{
			
			try {
				comIf.requestStatusInfo();
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure during status request: \n" + e1.getMessage() + "\n");
			}

		}
		
		if(e.getSource() == maptimer)
		{
			try {
				comIf.requestMap();
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure during map request: \n" + e1.getMessage() + "\n");
			}
		}
		if(e.getSource() == imagetimer)
		{
			try {
				comIf.requestImage();
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure during image request: \n" + e1.getMessage() + "\n");
			}
		}
	}

	private void updateGUIAutonomousDrive() 
	{
		int status = map.rover.autonomousDrive;
		if(status == 1)
		{
			// RUNNING IN AutonomousDrive
			roverStatus.setBackground(Color.RED);
			tabC.setSelectedIndex(1);
			tabC.setEnabledAt(1, true);
			tabC.setEnabledAt(0, false);
		}
		else
		{
			// RUNNING with Remote Control
			roverStatus.setBackground(Color.GREEN);
			tabC.setSelectedIndex(0);
			tabC.setEnabledAt(1, true);
			tabC.setEnabledAt(0, false);
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception 
	{
		String res = message.toString();
		
		// INFO MESSAGE 
		if(topic.compareTo("marsrover/statusinfo") == 0)
		{
			infoA.append(res + "\n");
			res = res.replace("\n", "");
			String[] line = res.split(":");
			map.rover.estX = Integer.parseInt(line[0]);
			map.rover.estY = Integer.parseInt(line[1]);
			map.rover.speed = Integer.parseInt(line[2]);
			map.rover.rotX = Integer.parseInt(line[3]);
			map.rover.sensUP = Integer.parseInt(line[4]);	
			map.rover.autonomousDrive = Integer.parseInt(line[5]);
			map.rover.mastTurn = Integer.parseInt(line[6]);
			map.rover.mastPitch = Integer.parseInt(line[7]);
			
			updateRoverStatus();
			updateGUIAutonomousDrive();
		}
		// MAP UPDATE 
		if(topic.compareTo("marsrover/map") == 0)
		{
			if(res.length() < 1)
				return;
			infoA.append("New map received ... \n");
			
			// Clear the current map 
			map.obstacles.clear();
			map.waypoints.clear();
			
			res = res.replace("\n", ";");
			String[] lines = res.split(";");
			for(int i = 0; i < lines.length; i++)
			{
				String[] parts = lines[i].split(":");
				if(parts[0].compareTo("W") == 0)
				{
					int x = Integer.parseInt(parts[1]);
					int y = Integer.parseInt(parts[2]);
					String name = parts[3];
					map.waypoints.add(new MapPoint(x,y,name));
				}
				else // needs to be an obstacle
				{
					int x = Integer.parseInt(parts[1]);
					int y = Integer.parseInt(parts[2]);
					map.obstacles.add(new Rectangle(x,y, 40,40));
				}
			}
			updateRoverStatus();
			mapP.repaint();
		}
		
		if(topic.compareTo("marsrover/image") == 0)
		{  
			byte[] data = message.getPayload();
		    ByteArrayInputStream bis = new ByteArrayInputStream(data);
		    BufferedImage bImage2 = ImageIO.read(bis);
		    Date date = Calendar.getInstance().getTime();  
		    
		    imageP.updateImage(bImage2);
		    DateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hhmm_ss");  
		    String strDate = dateFormat.format(date);
		    ImageIO.write(bImage2, "jpg", new File("images/marsrover_" + strDate + ".jpg") );
		    
		}
	}
}
