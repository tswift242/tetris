package com.tswift242.tetris;

import javax.swing.JFrame;

public class Tetris
{
	public static void main (String[] args) 
	{
		JFrame frame = new JFrame ("Tetris");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new TetrisPanel());
		frame.pack();
		frame.setVisible(true);
	}
}
