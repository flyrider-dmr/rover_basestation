import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Image currImg;
	JLabel label;
	
	public ImagePanel()
	{
		super();
		setLayout(null);
		currImg = 	Toolkit.getDefaultToolkit().getImage(getClass().getResource("default.jpg"));
		label = new JLabel();
		label.setBounds(0,100,1000,600);
		label.setIcon(new ImageIcon(currImg));
		this.add(label);
	}
	
	protected void paintComponent(Graphics g) 
	{	super.paintComponent(g); 
		g.drawImage(currImg, 0,0,this);

	}
	
	public void updateImage(Image image)
	{
		label.setIcon(new ImageIcon(image));
		repaint();
	}
}
