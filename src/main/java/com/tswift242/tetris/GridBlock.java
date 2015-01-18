package com.tswift242.tetris;

import java.awt.Color;

public class GridBlock
{
	private boolean filled;
	private Color gridBlockColor;

	public GridBlock()
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
