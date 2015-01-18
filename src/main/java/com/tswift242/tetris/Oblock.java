package com.tswift242.tetris;

import java.awt.Color;

public class Oblock extends Shapes
{
	public Oblock ()
	{
		super();
		setColor(Color.yellow);
		template[0][1] = true;
		template[0][2] = true;
		template[1][1] = true;
		template[1][2] = true;
		curOrientLength = 2;
		nextOrientLength = 2;
	}

	public void rotate () {}

	public Boolean[][] getNextOrientArray () {return template;}
}
