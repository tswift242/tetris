import java.awt.Color;

public class gridBlock
{
	private boolean filled;
	private Color gridBlockColor;

	public gridBlock ()
	{
		filled = false;
	}

	public void fill ()
	{
		filled = true;
	}

	public boolean isFilled ()
	{
		return filled;
	}

	public Color getColor ()
	{
		return gridBlockColor;
	}

	public void setColor (Color c)
	{
		gridBlockColor = c;
	}
}