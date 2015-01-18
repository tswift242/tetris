package com.tswift242.tetris.blocks;

import java.awt.Color;

public class Tblock extends TetrisBlock
{
	public Tblock ()
	{
		super();
		setColor(Color.magenta);
		template[0][1] = true;
		template[1][0] = true;
		template[1][1] = true;
		template[1][2] = true;
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
			curOrientLength = 3;
			nextOrientLength = 2;

			template[0][0] = true;
			template[1][0] = true;
			template[1][1] = true;
			template[2][0] = true;
		}

		if (orientation == 1)
		{
			curOrientLength = 2;
			nextOrientLength = 3;

			template[0][0] = true;
			template[0][1] = true;
			template[0][2] = true;
			template[1][1] = true;
		}

		if (orientation == 2)
		{
			curOrientLength = 3;
			nextOrientLength = 2;

			template[0][1] = true;
			template[1][0] = true;
			template[1][1] = true;
			template[2][1] = true;
		}

		if (orientation == 3)
		{
			curOrientLength = 2;
			nextOrientLength = 3;

			template[0][1] = true;
			template[1][0] = true;
			template[1][1] = true;
			template[1][2] = true;
		}

		orientation = ((orientation + 1) % BLOCK_BOX_SIZE);
	}

	public Boolean[][] getNextOrientArray ()
	{
		Boolean[][] a = new Boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];

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
			a[1][1] = true;
			a[2][0] = true;
		}

		if (orientation == 1)
		{
			a[0][0] = true;
			a[0][1] = true;
			a[0][2] = true;
			a[1][1] = true;
		}

		if (orientation == 2)
		{
			a[0][1] = true;
			a[1][0] = true;
			a[1][1] = true;
			a[2][1] = true;
		}

		if (orientation == 3)
		{
			a[0][1] = true;
			a[1][0] = true;
			a[1][1] = true;
			a[1][2] = true;
		}

		return a;
	}
}
