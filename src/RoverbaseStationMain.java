import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.beans.XMLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import Comm.RoverCommunicationInterface;
import Map.Map;
import Map.MapPanel;
import Map.MapPoint;



public class RoverbaseStationMain implements ActionListener
{

	static int ROVER_UPDATE_TIME = 10;
	static int MAP_UPDATE_TIME = 15;

	
	JPanel commandP, remoteP, autonomousP;
	ImagePanel imageP;
	MapPanel mapP;
	JTabbedPane tab, tabC;
	JButton connectB, disconnectB , gotoB, lowerprobeB, raiseprobeB, stopB , stopB2 , fwdB, bwdB, leftB, righB , autB;
	JTextArea infoA;
	JTextField inputX, inputY, inputT, roverStatus;
	RoverCommunicationInterface comIf;
	Map map;
	Timer infotimer ;
	Timer maptimer ;
	
	public static void main(String[] args) 
	{
		new RoverbaseStationMain();
	}

	public RoverbaseStationMain()
	{
		
		// Build up GUI - Main Window
		JFrame mainW = new JFrame("Discount Mars Rover Base Station"); 
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
		scrollPane1.setBounds(10,400,500, 370);
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
		
		autonomousP = new JPanel();
		autonomousP.setLayout(null);
		autonomousP.setBackground(Color.black);
		tabC.addTab("Remote Control", remoteP);
		tabC.addTab("Autonomous Drive", autonomousP);
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
		
		
		
		// General Buttons
		
		
		lowerprobeB = new JButton("SENSOR LOW");
		//probeB.setEnabled(false);
		lowerprobeB.setBounds(10,340,130,30);
		lowerprobeB.addActionListener(this);
		commandP.add(lowerprobeB);

		
		raiseprobeB = new JButton("SENSOR UP");
		//probeB.setEnabled(false);
		raiseprobeB.setBounds(150,340,130,30);
		raiseprobeB.addActionListener(this);
		commandP.add(raiseprobeB);
		
		stopB = new JButton("STOP");
		//stopB.setEnabled(false);
		stopB.setBounds(300,340,130,30);
		stopB.addActionListener(this);
		commandP.add(stopB);
		

		
		
		mainW.setVisible(true);
		
		
		// Timer setup
		infotimer = new Timer(10000, this);
		infotimer.addActionListener(this);
		maptimer  = new Timer(10000, this);
		maptimer.addActionListener(this);
		
		/*
		SocketConnectionParameter c = new SocketConnectionParameter();
		c.setHost("10.11.12.1");
		c.setPort(8888);
		
	    XMLEncoder e;
		try {
			e = new XMLEncoder(
			           new BufferedOutputStream(
			               new FileOutputStream("connectionparameter.xml")));
			
			e.writeObject(c);
			e.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

	}

	private void updateRoverStatus() 
	{
		if(map == null)
			roverStatus.setText("No status information");
		else
		{	// ROVER DATA
			String status = "  ROV : X: " + map.rover.estX + " Y:  " + map.rover.estX + " Speed: " + map.rover.speed + " Turn: " +  map.rover.rotX; 
			if(map.rover.sensUP == 1)
				status = status + " SEN UP";
			else
				status = status + " SEN DOWN";
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
				infoA.append("Trying to connect to rover...\n");
				comIf = new RoverCommunicationInterface();
				comIf.connectToRover();
				infoA.append("Connection to rover established...\n");
				infoA.append("Starting timer for information update requests...\n");
				infotimer.start();
				maptimer.start();
				// Enable buttons
				lowerprobeB.setEnabled(true);
				raiseprobeB.setEnabled(true);
				gotoB.setEnabled(true);
				stopB.setEnabled(true);
				
			} 
			catch (Exception e1) 
			{
				// TODO Auto-generated catch block
				infoA.append("Connection to rover failed\n" + e1.getMessage() + "\n");
				e1.printStackTrace();
				
			}
			finally
			{
				//comIf.close();
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
				infotimer.stop();
				maptimer.stop();
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
			try {
				infoA.append("Command SENSOR DOWN sent...\n");
				comIf.lowerSensor();

			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure for sensor down command... \n" + e1.getMessage() + "\n");
			}
		}
		
		if(e.getSource() == raiseprobeB)
		{
			try {
				infoA.append("Command SENSOR UP sent...\n");
				comIf.raiseSensor();

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
				String res = comIf.requestStatusInfo();
				infoA.append(res);
				res = res.replace("\n", "");
				String[] line = res.split(":");
				map.rover.estX = Integer.parseInt(line[0]);
				map.rover.estY = Integer.parseInt(line[1]);
				map.rover.speed = Integer.parseInt(line[2]);
				map.rover.rotX = Integer.parseInt(line[3]);
				map.rover.sensUP = Integer.parseInt(line[4]);	
				map.rover.autonomousDrive = Integer.parseInt(line[5]);
				updateRoverStatus();
				updateGUIAutonomousDrive();
			} 
			catch (Exception e1) 
			{
				infoA.append("Communication failure during status request: \n" + e1.getMessage() + "\n");
			}

		}
		
		if(e.getSource() == maptimer)
		{
			try {
				String res = comIf.requestMap();
				if(res.length() < 1)
					return;
				infoA.append(res);
				
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
			catch (Exception e1) 
			{
				infoA.append("Communication failure during status request: \n" + e1.getMessage() + "\n");
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
}
