package Map;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Date;
import java.util.Vector;


/* Map
 * Represents the map the rover generates for navigation 
 * The map is the real world representation
 * all point coordinates are in cm relative to the start position 0,0
 */
public class Map 
{
	public Rover rover = new Rover();
	Point start = new Point(0,0);
	public Vector<Rectangle> obstacles = new Vector<Rectangle>();
	public Vector<MapPoint> waypoints = new Vector<MapPoint>();
	public Vector<MapPoint> positions = new Vector<MapPoint>();
	
	public Map()
	{
		start = new Point(0,0);
	}
	
	/*
	 * dummytestDataLoader 
	 * generates test data for software test without a real rover sending data
	 */
	public void dummytestDataLoader()
	{
		// Waypoints
		waypoints.add(new MapPoint(100,10, "RIDGE"));
		waypoints.add(new MapPoint(150,-10, "CRATER"));
		waypoints.add(new MapPoint(250,-70, "MOUNT SHARP"));
		waypoints.add(new MapPoint(280,-80, "TARGET"));
		
		// Sent positions
		positions.add(new MapPoint(20,0, new Date().toString()));
		positions.add(new MapPoint(40,0, new Date().toString()));
		positions.add(new MapPoint(60,0, new Date().toString()));
		positions.add(new MapPoint(80,5, new Date().toString()));
		positions.add(new MapPoint(100,10, new Date().toString()));
		
		// Obstacles
		obstacles.add(new Rectangle(120,70, 40,40));
		obstacles.add(new Rectangle(220,-70, 40,40));
		obstacles.add(new Rectangle(260, -120, 40,40));
		
		
	}
}
