package com.tswift242.tetris.blocks;

import java.awt.Color;

public class Iblock extends TetrisBlock
{
	public Iblock ()
	{
		super();
		setColor(Color.pink);
		template[0][0] = true;
		template[0][1] = true;
		template[0][2] = true;
		template[0][3] = true;
	}
	
	public void rotate ()
	{
		for (int i = 0; i < template.length; i++)
		{
			for (int j = 0; j < template[i].length; j++)
			{
				template[i][j] = false;
			}
		}

		if (orientation == 0)
		{
			curOrientLength = 4;
			nextOrientLength = 1;

			template[0][0] = true;
			template[1][0] = true;
			template[2][0] = true;
			template[3][0] = true;
		}

		if (orientation == 1)
		{
			curOrientLength = 1;
			nextOrientLength = 4;

			template[0][0] = true;
			template[0][1] = true;
			template[0][2] = true;
			template[0][3] = true;
		}

		if (orientation == 2)
		{
			curOrientLength = 4;
			nextOrientLength = 1;

			template[0][0] = true;
			template[1][0] = true;
			template[2][0] = true;
			template[3][0] = true;
		}

		if (orientation == 3)
		{
			curOrientLength = 1;
			nextOrientLength = 4;

			template[0][0] = true;
			template[0][1] = true;
			template[0][2] = true;
			template[0][3] = true;
		}

		orientation = ((orientation + 1) % 4);
	}

	public Boolean[][] getNextOrientArray ()
	{
		Boolean[][] a = new Boolean[4][4];

		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[i].length; j++)
			{
				a[i][j] = false;
			}
		}

		if (orientation == 0)
		{
			a[0][0] = true;
			a[1][0] = true;
			a[2][0] = true;
			a[3][0] = true;
		}

		if (orientation == 1)
		{
			a[0][0] = true;
			a[0][1] = true;
			a[0][2] = true;
			a[0][3] = true;
		}

		if (orientation == 2)
		{
			a[0][0] = true;
			a[1][0] = true;
			a[2][0] = true;
			a[3][0] = true;
		}

		if (orientation == 3)
		{
			a[0][0] = true;
			a[0][1] = true;
			a[0][2] = true;
			a[0][3] = true;
		}

		return a;
	}
}
