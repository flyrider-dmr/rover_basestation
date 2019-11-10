package Map;

import java.awt.Point;

public class MapPoint extends Point
{
	String text;
	public MapPoint(int x, int y, String text)
	{
		super(x,y);
		this.text = text;
	}
}
