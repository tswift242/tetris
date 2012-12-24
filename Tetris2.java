import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

public class Tetris2 extends JApplet
{

	public void init()
	{
		getContentPane().add(new TetrisPanel());
		/*setPreferredSize(new Dimensions(400, 800));*/
	}

	public static void main (String[] args) 
	{
		JFrame frame = new JFrame ("Tetris");
		/*frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);*/
		Tetris2 t = new Tetris2();
		t.init();
		frame.add(t);
		frame.pack();
		/*frame.setSize(400, 800);*/
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
}