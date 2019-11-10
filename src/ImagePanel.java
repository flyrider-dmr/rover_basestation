import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Image currImg;
	
	public ImagePanel()
	{
		super();
		currImg = 	Toolkit.getDefaultToolkit().getImage(getClass().getResource("default.jpg"));
		JLabel label = new JLabel(new ImageIcon(currImg));
		this.add(label);
	}
	
	protected void paintComponent(Graphics g) 
	{	super.paintComponent(g); 
		g.drawImage(currImg, 0,0,this);

	}
}
