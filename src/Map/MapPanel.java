package Map;

import java.awt.*;
import java.util.Enumeration;
import javax.swing.JPanel;

/* MapPanel
 * this panel visualizes the current navigation map 
 * it refers to the map object and displays all 
 * objects in the map:
 *  waypoints are green dots
 *  positions sent from the rover are black dots
 *  the rover is represented as image
 */
public class MapPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	int scale = 2; // one cm is represented by "scale" pixel
	Map map;
	Point start = new Point(40, 500);
	
	public MapPanel(Map map)
	{
		super();
		setBackground(new Color(194,161,126));
		this.map = map;
	}
	
	protected void paintComponent(Graphics g) 
	{	super.paintComponent(g); 
		drawMap(g);
	}
	
	public void rescale(int newscale)
	{
		scale = newscale;
	}
	private void drawMap(Graphics g)
	{
		// draw scale as size comparison
		drawScale(g);
		// Draw all elements from the map
		g.drawString("SCALE :  " + scale, 50, 50);
		// Draw starting point

		drawWayPoint(g, map.start, "START");
		
		
		// Draw all waypoints
		Enumeration<MapPoint> ewp = map.waypoints.elements();
		while(ewp.hasMoreElements())
		{
			MapPoint p = ewp.nextElement();
			drawWayPoint(g, p, p.text);
		}
		
		
		// Draw positions
		Enumeration<MapPoint> epp = map.positions.elements();
		while(epp.hasMoreElements())
		{	
			MapPoint p = epp.nextElement();
			drawPositionPoint(g, p, p.text);
		}
		
		// Draw obstacles
		Enumeration<Rectangle> eob = map.obstacles.elements();
		while(eob.hasMoreElements())
		{	
			Rectangle r = eob.nextElement();
			drawObstacle(g, r);
		}
		// Draw rover

		int x = (map.rover.estY * scale) + start.y;
		int y = (map.rover.estX * scale) + start.x;
		g.setColor(Color.BLUE);
		g.fillOval(x-10, y-10, 20, 20);
		
	}
	
	private void drawScale(Graphics g)
	{
		int start = 10;
		for(int i = 1; i < 10; i++)
		{	g.setColor(Color.BLACK);
			g.fillRect(10, start, 20, 100 * scale);
			start = start + 100 * scale;
			g.setColor(Color.WHITE);
			g.fillRect(10, start, 20, 100 * scale);
			start = start + 100 * scale;
		}
	}
	private void drawWayPoint(Graphics g, Point p, String text)
	{
		// Calculate pixel position
		// ATTENTION: In the real world X is left / right to the start position and Y is front/back to start position
		// In the map X changes to Y and Y becomes X
		int x = (p.y * scale) + start.y;
		int y = (p.x * scale) + start.x;
		g.setColor(Color.GREEN);
		g.fillOval(x-10, y-10, 20, 20);
		
		// Draw text 
		g.drawString(text, x + 30, y);
		
	}
	
	private void drawPositionPoint(Graphics g, Point p, String text)
	{
		// Calculate pixel position
		int x = (p.y * scale) + start.y;
		int y = (p.x * scale) + start.x;
		g.setColor(Color.BLACK);
		g.fillOval(x-5, y-5, 10, 10);
		
		// Draw text 
		g.drawString(text, x + 30, y);
		
	}
	
	private void drawObstacle(Graphics g, Rectangle p)
	{
		// Calculate pixel position
		int x = (p.y * scale) + start.y;
		int y = (p.x * scale) + start.x;
		g.setColor(Color.RED);
		g.fillRect(x-20, y-20, 40, 40);

		
	}
}
