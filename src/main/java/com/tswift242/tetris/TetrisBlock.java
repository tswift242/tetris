package com.tswift242.tetris;

import java.awt.Color;

public class TetrisBlock
{
	protected Boolean[][] template;
	protected int x, y, orientation, curOrientLength, nextOrientLength;
	protected Color shapeColor;

	public TetrisBlock()
	{
		template = new Boolean[4][4];
		
		for (int i = 0; i < template.length; i++)
		{
			for (int j = 0; j < template[i].length; j++)
			{
				template[i][j] = false;
			}
		}

		x = y = orientation = curOrientLength = nextOrientLength = 0;
		shapeColor = Color.black;
	}

	public Boolean[][] getTemplate ()
	{
		return template;
	}

	public int getX ()
	{
		return x;
	}

	public int getY ()
	{
		return y;
	}

	public Color getColor ()
	{
		return shapeColor;
	}

	public int getCurOrientLength ()
	{
		return curOrientLength;
	}

	public int getNextOrientLength ()
	{
		return nextOrientLength;
	}

	public void setX (int x)
	{
		this.x = x;
	}

	public void setY (int y)
	{
		this.y = y;
	}

	public void setColor (Color c)
	{
		shapeColor = c;
	}

	public void rotate () {}

	public Boolean[][] getNextOrientArray () {return template;}
}
