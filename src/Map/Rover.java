package Map;

/*	Rover
 *  Represents the rover
 *  estX : estimated X coordinate in cm from starting point
 *  estY : estimated Y coordinate in cm from starting point
 *  speed: current speed setting on the rover (ranges from 0 to 255)
 *  rotX : current rotation angle of the X axis compared to the starting direction
 *  sensUP : Position of the humidity sensor sensUp = 1 is UP, 0 is DOWN
 *  autonomousDrive : Rover is currently in autonomous  mode -> 1 , else -> 0
 */
public class Rover 
{

	public int estX,estY, speed, rotX, sensUP, autonomousDrive;
	
	public Rover()
	{
		estX = 0;
		estY = 0;
		speed = 0;
		rotX = 0;
		sensUP = 1;
		autonomousDrive = 0;
	}

}
