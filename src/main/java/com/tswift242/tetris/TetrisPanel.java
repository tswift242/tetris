package com.tswift242.tetris;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.io.*;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Scanner;
import java.util.Arrays;

public class TetrisPanel extends JPanel
{
	private final int WIDTH = 400, HEIGHT = 800; //window size
	private final int INIT_DELAY = 400; //timer delay
	private final int ROWS = 20, COLUMNS = 10; //grid size
	private final int NUM_HIGH_SCORES = 10;
	private final String MUSIC_FILE = "/12-hans_zimmer-time.wav";

	private Timer timer;
	private DirectionListener DL;
	private PauseListener PL;
	private NewGameListener NGL;
	private int moveY, blockWidth, blockHeight, boardWidth, boardHeight, score, currentDelay, level;
	private boolean startOfGame, colLeft, colRight, colDown, ableToRotate, onTopOfBlockR, paused, musicOn, newGame, GAMEOVERflag, speedIncreased;
	private Shapes block;
	private GridBlock[][] Board;
	private Font scoreFont, gameOverFont;
	private int[] HighScores;
	private String[] initials;
	private AudioClip tetrisMusic;
	private Random rand;

	public TetrisPanel ()
	{
		timer = new Timer(INIT_DELAY, new TetrisListener());

		currentDelay = INIT_DELAY;
		score = 0;
		level = 1;
		startOfGame = true;
		paused = false;
		musicOn = true;
		newGame = false;
		GAMEOVERflag = false;
		speedIncreased = false;
		scoreFont = new Font("SansSerif", Font.PLAIN, 20);
		gameOverFont = new Font("SansSerif", Font.BOLD, 62);
		rand = new Random();
		Board = new GridBlock[ROWS + 2][COLUMNS];
		for (int i = 0; i < Board.length; i++)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				Board[i][j] = new GridBlock();
			}
		}

		//fill invisible bottom row
		for (int j = 0; j < Board[ROWS+1].length; j++)
		{
			Board[ROWS+1][j].fill();
		}

		//keep track of high scores
		HighScores = new int[NUM_HIGH_SCORES];
		Arrays.fill(HighScores, 0);
		initials = new String[NUM_HIGH_SCORES];
		Arrays.fill(initials, "xxx");

		//sets up music file to be played
		URL url1 = null;
		try
		{
			url1 = new URL ("file", "localhost", MUSIC_FILE);
			tetrisMusic = JApplet.newAudioClip (url1);
			tetrisMusic.loop();
		}
		catch (Exception exception) {}

		//sets up panel
		setPreferredSize (new Dimension(WIDTH, HEIGHT));
		setBackground (Color.black);
		setFocusable(true);
		DL = new DirectionListener();
		addKeyListener (DL);
		PL = new PauseListener();
		addKeyListener (PL);
		/*NGL = new NewGameListener();
		addKeyListener (NGL);*/
		timer.start();
	}

	public void newBlock ()
	{
		int randBlock = rand.nextInt(7);

		switch (randBlock)
		{
			case 0:
				block = new Iblock();
				break;
			case 1:
				block = new Jblock();
				break;
			case 2:
				block = new Lblock();
				break;
			case 3:
				block = new Oblock();
				break;
			case 4:
				block = new Sblock();
				break;
			case 5:
				block = new Tblock();
				break;
			case 6:
				block = new Zblock();
				break;
		}

		block.setX((getWidth()/2) - 2*blockWidth);
		block.setY(0);

		if (onTopOfBlock())
			addToBoardGameOver();
	}

	public int getBoardRow (int blockRow)
	{
		return (block.getY() + blockRow*blockHeight)/blockHeight + 1;
	}
		
	public int getBoardCol (int blockCol)
	{
		return (block.getX() + blockCol*blockWidth)/blockWidth;
	}

	public int getLeftmostX ()
	{
		int LeftmostCol = 0;
		int LeftmostX;
		boolean foundLeftmostCol = false;

		for (int i = 0; i < block.getTemplate().length; i++)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[j][i])
				{
					if (!foundLeftmostCol)
						LeftmostCol = i;

					foundLeftmostCol = true;
				}
			}
		}

		LeftmostX = block.getX() + LeftmostCol*blockWidth;

		return LeftmostX;
	}
	
	public int getRightmostX ()
	{
		int RightmostCol = 3;
		int RightmostX;
		boolean foundRightmostCol = false;

		for (int i = block.getTemplate().length - 1; i >= 0; i--)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[j][i])
				{
					if (!foundRightmostCol)
						RightmostCol = i;

					foundRightmostCol = true;
				}
			}
		}

		RightmostX = block.getX() + (RightmostCol + 1)*blockWidth;

		return RightmostX;
	}

	//checks if block can move left
	public boolean collisionLeft ()
	{
		boolean colLeft = false;

		int a = (getLeftmostX() - block.getX())/blockWidth;
		int c = getBoardCol(a);

		if (c > 0)
		{
			for (int i = 0; i < block.getTemplate().length; i++)
			{
				if ((block.getTemplate())[i][a])
				{
					int r = getBoardRow(i);

					if (Board[r][c - 1].isFilled())
						colLeft = true;
				}
			}
		}

		if (getLeftmostX() <= 0)
			colLeft = true;

		return colLeft;
	}

	//checks if block can move right
	public boolean collisionRight ()
	{
		boolean colRight = false;
		
		int a = (getRightmostX() - block.getX())/blockWidth - 1;
		int c = getBoardCol(a);

		if (c < Board[0].length - 1)
		{
			for (int i = 0; i < block.getTemplate().length; i++)
			{
				if ((block.getTemplate())[i][a])
				{
					int r = getBoardRow(i);
	
					if (Board[r][c + 1].isFilled())
						colRight = true;
				}
			}
		}

		if (getRightmostX() >= getWidth())
			colRight = true;

		return colRight;
	}

	//checks if block can move down, or it it's on top of another block
	public boolean collisionDown ()
	{
		boolean doneFalling, foundLowestCol1, foundLowestCol2, foundLowestCol3, foundLowestCol4, 
				col1NotEmpty, col2NotEmpty, col3NotEmpty, col4NotEmpty;
		doneFalling = foundLowestCol1 = foundLowestCol2 = foundLowestCol3 = foundLowestCol4 = false;
		col1NotEmpty = col2NotEmpty = col3NotEmpty = col4NotEmpty = false;
		int lowestCol1, lowestCol2, lowestCol3, lowestCol4;
		lowestCol1 = lowestCol2 = lowestCol3 = lowestCol4 = 0;
		int r1, r2, r3, r4, c1, c2, c3, c4;

		for (int j = (block.getTemplate())[0].length - 1; j >= 0; j--)
		{
			if ((block.getTemplate())[j][0])
			{
				if (!foundLowestCol1)
					lowestCol1 = j;

				foundLowestCol1 = true;
				col1NotEmpty = true;
			}
		}

		r1 = getBoardRow(lowestCol1);
		c1 = getBoardCol(0);

		for (int j = (block.getTemplate())[1].length - 1; j >= 0; j--)
		{
			if ((block.getTemplate())[j][1])
			{
				if (!foundLowestCol2)
					lowestCol2 = j;

				foundLowestCol2 = true;
				col2NotEmpty = true;
			}
		}

		r2 = getBoardRow(lowestCol2);
		c2 = getBoardCol(1);

		for (int j = (block.getTemplate())[2].length - 1; j >= 0; j--)
		{
			if ((block.getTemplate())[j][2])
			{
				if (!foundLowestCol3)
					lowestCol3 = j;

				foundLowestCol3 = true;
				col3NotEmpty = true;
			}
		}

		r3 = getBoardRow(lowestCol3);
		c3 = getBoardCol(2);

		for (int j = (block.getTemplate())[3].length - 1; j >= 0; j--)
		{
			if ((block.getTemplate())[j][3])
			{
				if (!foundLowestCol4)
					lowestCol4 = j;

				foundLowestCol4 = true;
				col4NotEmpty = true;
			}
		}

		r4 = getBoardRow(lowestCol4);
		c4 = getBoardCol(3);

		if (col1NotEmpty && Board[r1 + 1][c1].isFilled())
			doneFalling = true;
		if (col2NotEmpty && Board[r2 + 1][c2].isFilled())
			doneFalling = true;
		if (col3NotEmpty && Board[r3 + 1][c3].isFilled())
			doneFalling = true;
		if (col4NotEmpty && Board[r4 + 1][c4].isFilled())
			doneFalling = true;

		return doneFalling;
	}

	public void addToBoard ()
	{
		for (int i = 0; i < (block.getTemplate()).length; i++)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[i][j])
				{
					int r = getBoardRow(i);
					int c = getBoardCol(j);

					Board[r][c].fill();
					Board[r][c].setColor(block.getColor());
				}
			}
		}

		newBlock();
	}

	//only used when about to get a Game Over
	public void addToBoardGameOver ()
	{
		boolean rowInitiallyEmpty = false;

		for (int i = 0; i < (block.getTemplate()).length; i++)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[i][j])
				{
					int r = getBoardRow(i);
					int c = getBoardCol(j);

					if (rowIsEmpty(r - 1) && (r == 2))
						rowInitiallyEmpty = true;
					if (rowInitiallyEmpty)
						Board[r - 1][c].setColor(block.getColor());	
					Board[r - 1][c].fill();
				}
			}
		}
	}

	public boolean rowIsEmpty (int r)
	{
		boolean rowIsEmpty = true;

		for (int j = 0; j < Board[r].length; j++)
		{
			if (Board[r][j].isFilled())
				rowIsEmpty = false;
		}

		return rowIsEmpty;
	}

	//checks to see if right boundary interferes with rotation
	public boolean canRotate ()
	{
		boolean ableToRotate = true;

		int a = (getRightmostX() - block.getX())/blockWidth - 1;

		if ((block.getCurOrientLength() - block.getNextOrientLength()) > (Board[0].length - 1 - getBoardCol(a)))
			ableToRotate = false;					

		return ableToRotate;
	}

	//used to check for Game Over
	public boolean onTopOfBlock ()
	{
		boolean onTopOfBlock = false;

		for (int i = 0; i < (block.getTemplate()).length; i++)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[i][j])
				{
					int c = getBoardCol(j);
					int r = getBoardRow(i);

					if (Board[r][c].isFilled())
						onTopOfBlock = true;
				}
			}
		}

		return onTopOfBlock;
	}

	//used to check if block can rotate or if another block is interfering
	public boolean onTopOfBlock (Boolean[][] a)
	{
		boolean onTopOfBlock = false;

		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[i].length; j++)
			{
				if (a[i][j])
				{
					int c = getBoardCol(j);
					int r = getBoardRow(i);

					if (r < Board.length)
						if (Board[r][c].isFilled())
							onTopOfBlock = true;
				}
			}
		}

		return onTopOfBlock;
	}

	//checks if row r is full
	public boolean rowIsFull (int r)
	{
		boolean rowIsFull = true;

		for (int j = 0; j < Board[r].length; j++)
		{
			if (!(Board[r][j].isFilled()))
				rowIsFull = false;
		}

		return rowIsFull;
	}

	//shifts blocks down and increments score, called when rowIsFull
	public void clearRow (int r)
	{
		for (int i = r ; i >= 1; i--)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				Board[i][j] = Board[i - 1][j];
			}
		}

		for (int j = 0; j < Board[0].length; j++)
			Board[0][j] = new GridBlock();

		score += 100;
	}

	public boolean GameOver ()
	{
		boolean gameOver = false;

		for (int j = 0; j < Board[0].length; j++)
		{
			if (Board[0][j].isFilled())
				gameOver = true;
		}

		return gameOver;
	}

	public void speedIncrease ()
	{
		if (score == 1000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 2000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 5000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 10000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 15000)
		{
			currentDelay -= 50;
			level++;
		}
		else if (score == 20000)
		{
			currentDelay -= 50;
			level++;
		}
		else if (score == 25000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 30000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 35000)
		{
			currentDelay -= 25;
			level++;
		}
		else if (score == 40000)
		{
			currentDelay -= 25;
			level++;
		}

		speedIncreased = true;

		timer.setDelay(currentDelay);
	}

	//called when a new game is started
	public void newGame ()
	{
		timer.stop();
		addToBoard();

		//resets variables
		score = 0;
		startOfGame = true;
		paused = false;
		newGame = false;

		//resets board
		Board = new GridBlock[ROWS + 2][COLUMNS];
		for (int i = 0; i < Board.length; i++)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				Board[i][j] = new GridBlock();
			}
		}

		for (int j = 0; j < Board[21].length; j++)
		{
			Board[21][j].fill();
		}

		//restarts music
		tetrisMusic.loop();

		//starts timer again if timer is stopped
		/*if (!timer.isRunning())
			timer.start();*/

		if (GameOver());
		{
			addKeyListener (DL);
			addKeyListener (PL);
		}
		timer.start();	
	}

	public void updateHighScores () throws IOException
	{
		//reads in current high scores from the TetrisHighScores file
		try
		{
			Scanner scan = new Scanner(new File("TetrisHighScores.txt"));

			String s1 = scan.next();
			String s2 = scan.next();
			String s3 = scan.next();

			for (int i = 0; i < HighScores.length; i++)
			{
				int index = Integer.parseInt(scan.next());
				String HSInitials = scan.next();
				int highScore = Integer.parseInt(scan.next());

				HighScores[index - 1] = highScore;
				initials[index - 1] = HSInitials;
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Input file not found: " + e);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Error in the format of the input file:" + e);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("There's an error in the numbering of the high scores in the input file, causing an array " +
						"index to be out of bounds:" + e);
		}

		//makes a JOptionPane appearing, allowing the user to enter in their initials or their name
		/*String userInitials;
		userInitials = JOptionPane.showInputDialog(this, "Enter your initials or your name", "tgs");
		JOptionPane.showMessageDialog(this, "Thank you for playing");*/

		//compares current score against high scores and adjusts high scores accordingly
		if (score > HighScores[0])
		{
			for (int i = HighScores.length - 1; i >= 1; i--)
			{
				HighScores[i] = HighScores[i - 1];
				initials[i] = initials[i - 1];
			}

			HighScores[0] = score;
			/*initials[0] = userInitials;*/
		}
		else
		{
			for (int i = HighScores.length - 1; i >= 1; i--)
			{
				if ((score > HighScores[i]) && (score <= HighScores[i - 1]))
				{
					for (int j = HighScores.length - 1; j >= (i + 1); j--)
					{
						HighScores[j] = HighScores[j - 1];
						initials[j] = initials[j - 1];
					}

					HighScores[i] = score;
					/*initials[i] = userInitials;*/
				}
			}
		}

		//writes high scores to file TetrisHighScores
		PrintWriter outFile = new PrintWriter (new FileWriter("TetrisHighScores.txt"));

		//mini-header
		outFile.println("Tetris High Scores");
		outFile.println();
		outFile.println();
		outFile.println();

		for (int i = 0; i < HighScores.length; i++)
			outFile.println((i + 1) + "\t" + initials[i] + "\t" + HighScores[i]);

		outFile.close();

		/*System.exit(0);*/
	}

	public void paintComponent (Graphics page)
	{
		super.paintComponent (page);

		boardWidth = getWidth();
		boardHeight = getHeight();
		blockWidth = boardWidth/COLUMNS;
		blockHeight = boardHeight/ROWS;
		moveY = blockHeight;

		page.setColor(Color.white);
		page.setFont(scoreFont);
		page.drawString("Score: " + score, 0, 15);
		page.drawString("Level: " + level, 320, 15);

		if (startOfGame)
			newBlock();

		//draws all gridBlocks in Board background array
		for (int i = 1; i < Board.length - 1; i++)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				if (Board[i][j].isFilled())
				{
					page.setColor(Board[i][j].getColor());
					page.fillRect(j*blockWidth, (i - 1)*blockHeight, blockWidth, blockHeight);
					page.setColor(Color.black);
					page.drawRect(j*blockWidth, (i - 1)*blockHeight, blockWidth, blockHeight);
				}
			}
		}

		//draws newly created block at top, center of screen
		for (int i = 0; i < (block.getTemplate()).length; i++)
		{
			for (int j = 0; j < (block.getTemplate())[i].length; j++)
			{
				if ((block.getTemplate())[i][j])
				{
					page.setColor(block.getColor());
					page.fillRect((block.getX() + (j)*blockWidth), (block.getY() + i*blockHeight), blockWidth, 
							blockHeight);
					page.setColor(Color.black);
					page.drawRect((block.getX() + (j)*blockWidth), (block.getY() + i*blockHeight), blockWidth, 
							blockHeight);
				}
			}
		}

		colLeft = collisionLeft();
		colRight = collisionRight();
		colDown = collisionDown();

		//checks to see if on top of another block, if so, adds current block to board and creates a new one
		if (colDown)
			addToBoard();

		//checks to see if block can rotate
		ableToRotate = canRotate();
		if (ableToRotate)
			onTopOfBlockR = onTopOfBlock(block.getNextOrientArray());

		//loops through board to check for any completely filled ROWS
		for (int i = (Board.length - 2); i >= 1; i--)
		{
			if (rowIsFull(i))
				clearRow(i);
		}

		//decreases timer delay and makes blocks fall faster when score reaches certain values
		if (!speedIncreased && ((score == 1000) || (score == 2000) || (score == 5000) || 
		(score == 10000) || (score == 15000) || (score == 20000) || (score == 25000) || 
		(score == 30000) || (score == 35000) || (score == 40000)))
			speedIncrease();
		else if ((score % 1000) != 0)
			speedIncreased = false;

		//pauses or unpauses the game when "p" key is pressed
		if (paused)
		{
			timer.stop();
			tetrisMusic.stop();
			removeKeyListener (DL);
			page.setColor(Color.white);
			page.setFont(gameOverFont);
			page.drawString("PAUSED", boardWidth/5, boardHeight/2);
		}
		else
		{
			if (!timer.isRunning() && !GameOver())
			{
				timer.start();
				addKeyListener (DL);

				if (musicOn)
					tetrisMusic.loop();
			}
		}

		//executes when the player gets Game Over
		if (!GAMEOVERflag)
		{
			if (GameOver())
			{
				GAMEOVERflag = true;

				timer.stop();
				tetrisMusic.stop();

				//paints top row
				for (int j = 0; j < Board[1].length; j++)
				{
					if (Board[1][j].isFilled())
					{
						page.setColor(Board[1][j].getColor());
						page.fillRect(j*blockWidth, (0)*blockHeight, blockWidth, blockHeight);
						page.setColor(Color.black);
						page.drawRect(j*blockWidth, (0)*blockHeight, blockWidth, blockHeight);
					}
				}

				removeKeyListener (DL);
				removeKeyListener (PL);
				page.setColor(Color.white);
				page.setFont(gameOverFont);
				page.drawString("GAME OVER!", 0, boardHeight/2);

				try
				{
					System.out.println("updating high scores");
					updateHighScores();
				}
				catch (IOException e)
				{
					System.out.println(e);
				}
			}
		}

		//starts a new game if "n" key is pressed
		if (newGame)
			newGame();
	}

	private class TetrisListener implements ActionListener 
	{
		public void actionPerformed (ActionEvent event) 
		{
			startOfGame = false;

			if ((block != null) && (!colDown)) //TODO: fix null check
				block.setY(block.getY() + moveY);

			repaint();
		}
	}

	private class DirectionListener implements KeyListener
	{
		public void keyPressed (KeyEvent event)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
					if (!colLeft)
						block.setX(block.getX() - blockWidth);
					break;
				case KeyEvent.VK_RIGHT:
					if (!colRight)
						block.setX(block.getX() + blockWidth);
					break;
				case KeyEvent.VK_DOWN:
					if (!colDown)
						block.setY(block.getY() + moveY);
					break;
				case KeyEvent.VK_UP:
					if (ableToRotate)
					{
						if (!onTopOfBlockR)
							block.rotate();
					}
					break;
			}

			repaint();
		}

		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {}
	}

	private class PauseListener implements KeyListener
	{
		public void keyPressed (KeyEvent event)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_P:
					paused = !paused;
					break;
				case KeyEvent.VK_V:
					if (musicOn)
					{
						tetrisMusic.stop();
					}
					else
					{
						tetrisMusic.loop();
					}
					musicOn = !musicOn;
					break;
			}
			repaint();
		}

		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {}
	}

	private class NewGameListener implements KeyListener
	{
		public void keyPressed (KeyEvent event)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_N:
					newGame = true;
					break;
			}			
			repaint();
		}

		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {}
	}
}
