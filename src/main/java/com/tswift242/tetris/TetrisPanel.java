package com.tswift242.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tswift242.tetris.blocks.Iblock;
import com.tswift242.tetris.blocks.Jblock;
import com.tswift242.tetris.blocks.Lblock;
import com.tswift242.tetris.blocks.Oblock;
import com.tswift242.tetris.blocks.Sblock;
import com.tswift242.tetris.blocks.Tblock;
import com.tswift242.tetris.blocks.TetrisBlock;
import com.tswift242.tetris.blocks.Zblock;
import com.tswift242.tetris.config.TetrisProperties;

public class TetrisPanel extends JPanel
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final int NUM_UNIQUE_TETRIS_BLOCKS = 7;
	// window size
	private static final int WIDTH = 400;
	private static final int HEIGHT = 800;
	// grid size
	private static final int ROWS = 20;
	private static final int COLUMNS = 10;
	private static final int INIT_DELAY = 400; //timer delay
	// TODO: make this value formulaic instead of fixed in the future
	private static final int SCORE_INCREMENT_VALUE = 100;
	private static final String HIGH_SCORES_FILENAME = "TetrisHighScores.txt";
	// maximum number of high scores to save
	private static final int MAX_HIGH_SCORES = 10;
	private static final String MUSIC_FILE = "/12-hans_zimmer-time.wav";
	private static final Color TEXT_COLOR = Color.white;

	private Timer timer;
	private DirectionListener DL;
	private PauseListener PL;
	private MusicListener musicListener;
	private NewGameListener NGL;
	private int moveY, blockWidth, blockHeight, boardWidth, boardHeight, score, currentDelay, level;
	private boolean colLeft, colRight, colDown, ableToRotate, onTopOfBlockR, paused, musicOn, newGame, GAMEOVERflag, speedIncreased;
	private TetrisBlock block;
	private GridBlock[][] Board;
	private Font scoreFont, gameOverFont;
	private int[] HighScores;
	private String[] initials;
	private Clip music;
	private Random rand;

	public TetrisPanel ()
	{
		currentDelay = INIT_DELAY;
		score = 0;
		level = 1;
		paused = false;
		musicOn = true;
		newGame = false;
		GAMEOVERflag = false;
		speedIncreased = false;
		scoreFont = new Font("SansSerif", Font.PLAIN, 20);
		gameOverFont = new Font("SansSerif", Font.BOLD, 62);
		rand = new Random();
		logger.info("Initializing board with {} rows and {} columns", ROWS, COLUMNS);
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
		HighScores = new int[MAX_HIGH_SCORES];
		Arrays.fill(HighScores, 0);
		initials = new String[MAX_HIGH_SCORES];
		Arrays.fill(initials, "xxx");

		//sets up music file to be played
		try
		{
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(this.getClass().getResource(MUSIC_FILE));
            music = AudioSystem.getClip();
            music.open(audioStream);
			music.loop(Clip.LOOP_CONTINUOUSLY);
			// this can be closed now that it has been loaded into Clip
			audioStream.close();
			logger.info("Playing music file {}", MUSIC_FILE);
		} catch(UnsupportedAudioFileException e) {
			logger.error("Audio file " + MUSIC_FILE + " not supported", e);
		} catch(IOException e) {
            logger.error("Exception while playing audio file " + MUSIC_FILE, e);
        } catch(LineUnavailableException e) {
            logger.error("Audio line unavailable", e);
        }

		//sets up panel
		logger.info("Setting window size to be {} x {}", WIDTH, HEIGHT);
		setPreferredSize (new Dimension(WIDTH, HEIGHT));
		setBackground (TetrisProperties.BACKGROUND_COLOR);
		setFocusable(true);

        // add listeners
		DL = new DirectionListener();
		addKeyListener (DL);
		PL = new PauseListener();
		addKeyListener (PL);
		musicListener = new MusicListener();
		addKeyListener(musicListener);
		/*NGL = new NewGameListener();
		addKeyListener (NGL);*/

        // start game
        newBlock(); // create initial block
        timer = new Timer(INIT_DELAY, new TetrisListener());
		timer.start();
	}

	public void newBlock ()
	{
		int randBlock = rand.nextInt(NUM_UNIQUE_TETRIS_BLOCKS);

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

        //TODO: we may want to tweak what we're subtracting off here. We want to make sure the initial X is a multiple
        // of blockWidth (getWidth()/COLUMNS)
		block.setX((getWidth()/2) - 2*(getWidth()/COLUMNS));
		block.setY(0);
	}

	public int getBoardRow (int blockRow)
	{
		return (block.getY() + blockRow*blockHeight)/blockHeight + 1;
	}
		
	public int getBoardCol (int blockCol)
	{
		return (block.getX() + blockCol*blockWidth)/blockWidth;
	}

    //TODO: store leftMostCol as meta data in BlockOrientation to make this much easier
	public int getLeftmostX ()
	{
		int LeftmostCol = 0;
		int LeftmostX;
		boolean foundLeftmostCol = false;

		for (int i = 0; i < block.getCurrentOrientation().length; i++)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[j][i])
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

    //TODO: store rightMostCol as meta data in BlockOrientation to make this much easier
	public int getRightmostX ()
	{
		int RightmostCol = 3;
		int RightmostX;
		boolean foundRightmostCol = false;

		for (int i = block.getCurrentOrientation().length - 1; i >= 0; i--)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[j][i])
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
			for (int i = 0; i < block.getCurrentOrientation().length; i++)
			{
				if ((block.getCurrentOrientation())[i][a])
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
			for (int i = 0; i < block.getCurrentOrientation().length; i++)
			{
				if ((block.getCurrentOrientation())[i][a])
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

		for (int j = (block.getCurrentOrientation())[0].length - 1; j >= 0; j--)
		{
			if ((block.getCurrentOrientation())[j][0])
			{
				if (!foundLowestCol1)
					lowestCol1 = j;

				foundLowestCol1 = true;
				col1NotEmpty = true;
			}
		}

		r1 = getBoardRow(lowestCol1);
		c1 = getBoardCol(0);

		for (int j = (block.getCurrentOrientation())[1].length - 1; j >= 0; j--)
		{
			if ((block.getCurrentOrientation())[j][1])
			{
				if (!foundLowestCol2)
					lowestCol2 = j;

				foundLowestCol2 = true;
				col2NotEmpty = true;
			}
		}

		r2 = getBoardRow(lowestCol2);
		c2 = getBoardCol(1);

		for (int j = (block.getCurrentOrientation())[2].length - 1; j >= 0; j--)
		{
			if ((block.getCurrentOrientation())[j][2])
			{
				if (!foundLowestCol3)
					lowestCol3 = j;

				foundLowestCol3 = true;
				col3NotEmpty = true;
			}
		}

		r3 = getBoardRow(lowestCol3);
		c3 = getBoardCol(2);

		for (int j = (block.getCurrentOrientation())[3].length - 1; j >= 0; j--)
		{
			if ((block.getCurrentOrientation())[j][3])
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
		for (int i = 0; i < (block.getCurrentOrientation()).length; i++)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[i][j])
				{
					int r = getBoardRow(i);
					int c = getBoardCol(j);

					Board[r][c].fill();
					Board[r][c].setColor(block.getColor());
				}
			}
		}
	}

	//only used when about to get a Game Over
	public void addToBoardGameOver ()
	{
		boolean rowInitiallyEmpty = false;

		for (int i = 0; i < (block.getCurrentOrientation()).length; i++)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[i][j])
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

        //TODO: this logic currently checks vertical length difference; it should be based off of horizontal length,
        // in which case if would be next-curr
		if ((block.getDiffInNextOrientLengthFromCurrent()) > (Board[0].length - 1 - getBoardCol(a)))
			ableToRotate = false;					

		return ableToRotate;
	}

	//used to check for Game Over
	public boolean onTopOfBlock ()
	{
		boolean onTopOfBlock = false;

		for (int i = 0; i < (block.getCurrentOrientation()).length; i++)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[i][j])
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
	public boolean onTopOfBlock (boolean[][] a)
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
		logger.info("Clearing row {} and incrementing score by {}", r, SCORE_INCREMENT_VALUE);
		for (int i = r ; i >= 1; i--)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				Board[i][j] = Board[i - 1][j];
			}
		}

		for (int j = 0; j < Board[0].length; j++)
			Board[0][j] = new GridBlock();

		score += SCORE_INCREMENT_VALUE;
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

		logger.info("Increasing speed by setting timer delay to {}", currentDelay);
		timer.setDelay(currentDelay);
	}

	// TODO: this should hook into logic in TetrisPanel constructor
	//called when a new game is started
	public void newGame ()
	{
		timer.stop();
		addToBoard();
        newBlock();

		//resets variables
		score = 0;
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

		for (int j = 0; j < Board[ROWS+1].length; j++)
		{
			Board[ROWS+1][j].fill();
		}

		//restarts music
        music.loop(Clip.LOOP_CONTINUOUSLY);

		//starts timer again if timer is stopped
		/*if (!timer.isRunning())
			timer.start();*/

		if (GameOver());
		{
			addKeyListener (DL);
			addKeyListener (PL);
			addKeyListener(musicListener);
		}
		timer.start();	
	}

	public void updateHighScores () throws IOException
	{
		logger.info("Updating high scores");
		//reads in current high scores from the TetrisHighScores file
		try
		{
			Scanner scan = new Scanner(new File(HIGH_SCORES_FILENAME));

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
			logger.error("Input file not found", e);
		}
		catch (NumberFormatException e)
		{
			logger.error("Error in the format of the input file", e);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			logger.error("There's an error in the numbering of the high scores in the input file, causing an array " +
						"index to be out of bounds", e);
		}

		// TODO: get this to work
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
		PrintWriter outFile = new PrintWriter (new FileWriter(HIGH_SCORES_FILENAME));

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

		page.setColor(TEXT_COLOR);
		page.setFont(scoreFont);
		page.drawString("Score: " + score, 0, 15);
		page.drawString("Level: " + level, 320, 15);

		//draws all gridBlocks in Board background array
		for (int i = 1; i < Board.length - 1; i++)
		{
			for (int j = 0; j < Board[i].length; j++)
			{
				if (Board[i][j].isFilled())
				{
					page.setColor(Board[i][j].getColor());
					page.fillRect(j*blockWidth, (i - 1)*blockHeight, blockWidth, blockHeight);
					page.setColor(TetrisProperties.BACKGROUND_COLOR);
					page.drawRect(j*blockWidth, (i - 1)*blockHeight, blockWidth, blockHeight);
				}
			}
		}

		//draws newly created block at top, center of screen
		for (int i = 0; i < (block.getCurrentOrientation()).length; i++)
		{
			for (int j = 0; j < (block.getCurrentOrientation())[i].length; j++)
			{
				if ((block.getCurrentOrientation())[i][j])
				{
					page.setColor(block.getColor());
					page.fillRect((block.getX() + (j)*blockWidth), (block.getY() + i*blockHeight), blockWidth, 
							blockHeight);
					page.setColor(TetrisProperties.BACKGROUND_COLOR);
					page.drawRect((block.getX() + (j)*blockWidth), (block.getY() + i*blockHeight), blockWidth, 
							blockHeight);
				}
			}
		}

		colLeft = collisionLeft();
		colRight = collisionRight();
		colDown = collisionDown();

		//checks to see if on top of another block, if so, adds current block to board and creates a new one
		if (colDown) {
            addToBoard();
            newBlock();
            if (onTopOfBlock()) {
                addToBoardGameOver();
            }
        }

        //TODO: move this logic into DirectionListener, and store in local vars instead of fields
		//checks to see if block can rotate
		ableToRotate = canRotate();
		if (ableToRotate)
			onTopOfBlockR = onTopOfBlock(block.getNextOrientation());

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
			music.stop();
			removeKeyListener (DL);
			page.setColor(TEXT_COLOR);
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
                    music.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}

		//executes when the player gets Game Over
		if (!GAMEOVERflag)
		{
			if (GameOver())
			{
				logger.info("Game over!");
				GAMEOVERflag = true;

				timer.stop();
				music.stop();
				music.close();

				//paints top row
				for (int j = 0; j < Board[1].length; j++)
				{
					if (Board[1][j].isFilled())
					{
						page.setColor(Board[1][j].getColor());
						page.fillRect(j*blockWidth, (0)*blockHeight, blockWidth, blockHeight);
						page.setColor(TetrisProperties.BACKGROUND_COLOR);
						page.drawRect(j*blockWidth, (0)*blockHeight, blockWidth, blockHeight);
					}
				}

				removeKeyListener (DL);
				removeKeyListener (PL);
				removeKeyListener(musicListener);
				page.setColor(TEXT_COLOR);
				page.setFont(gameOverFont);
				page.drawString("GAME OVER!", 0, boardHeight/2);

				try
				{
					updateHighScores();
				}
				catch (IOException e)
				{
					logger.error("Exception updating high scores", e);
				}
			}
		}

		//starts a new game if "n" key is pressed
		if (newGame)
			newGame();
	}

	// listens for a timer to go off, indicating that the current block needs to be moved down
	private class TetrisListener implements ActionListener 
	{
		public void actionPerformed (ActionEvent event) 
		{
			if ((block != null) && (!colDown)) //TODO: fix null check
				block.setY(block.getY() + moveY);

			repaint();
		}
	}

	// listens for directional key input from the user, either to move the current block or to rotate it
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

	// listens for the "p" key to be pressed, which pauses the game
	private class PauseListener implements KeyListener
	{
		public void keyPressed (KeyEvent event)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_P:
					logger.info("Paused state changed");
					paused = !paused;
					break;
			}
			repaint();
		}

		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {}
	}

	// listens for the "v" key to be pressed, which toggles the in-game music
	private class MusicListener implements KeyListener
	{
		public void keyPressed (KeyEvent event)
		{
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_V:
					if (musicOn)
					{
						logger.info("Music stopped");
						music.stop();
					}
					else
					{
						logger.info("Music started");
                        music.loop(Clip.LOOP_CONTINUOUSLY);
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
					logger.info("New game selected");
					newGame = true;
					break;
			}			
			repaint();
		}

		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {}
	}
}
