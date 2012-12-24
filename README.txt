My name: Thomas Swift
My e-mail: tswift@u.rochester.edu
Assignment Description: A program that runs the puzzle video game known as Tetris.

Game objective: Different colored blocks, known as tetronimoes, fall from the top of the board until they either hit the bottom of the 
		board or another block located directly below them. These blocks can be moved left or right across the screen and they can 		also be rotated in 90 degree increments. The goal of the game is to obtain as high a score as possible. Your score starts 			at 0 and is incremented by 100 each time that a horizontal row on the game screen is completely filled with these colored 			blocks (i.e. there are no black spaces). Completely filling a horizontal row with blocks yields the additonal benefit of 			that row of blocks being cleared and all the blocks situated above that row falling down one row, thus decreasing the 				height of the accumulated stack of fallen blocks. The blocks will start to fall at a faster rate when certain scores are 			achieved, thus automatically increasing the game's difficulty. The game ends when a falling block goes off screen, which 			occurs when the stack of already fallen blocks is so high that a newly created fallen block has no place to go except off 			screen. 

Game instructions: 
Left arrow key = move the currently falling block to the left
Right arrow key = move the currently falling block to the right
Down arrow key = move the currently falling block down (i.e. make it fall faster)
Up arrow key = rotate the currently falling block 90 degrees clockwise
"P" letter key = pause/unpause the game
"V" letter key =  turn the background music on/off

My solution: Firstly, I created a generic Shapes. I then created individual classes for each of the 7 different types of tetronimoes, 				which all inherit the Shapes class. Each tetronimoe, or block, is represented as a 2-D array of booleans, with specified 			elements set to true to indicate the shape of the block. I then created a separate array to represent the game board. This 
		board is an array of gridBlocks, another class that I made. Each gridBlock contains the boolean filled, which is 
		initialized to false, but can be set to true with the fill() method. Additionally, each gridBlock contains a color, which
		can be set with the setColor() of the gridBlock class. The TetrisPanel does most of the work for the program. It randomly
		creates a block at the top-middle of the screen, which appears to fall down with the help of a timer. This class contains
		a collisonDown method, which checks to see if the block is going to hit the bottom of the screen or another block as it 
		falls. If this method returns true, the block stops falling and the elements on the board that correspond to the true
		elements of the falling block's array (based on it's current position) are filled and there color is set to the color of
		the block. Since the timer continuously calls the paintComponent method, the board array is constantly being looped 
		through and its filled elements have rectangles drawn at their corresponding positon on the board in their assigned color.
		Then, after the fallen block has been "added" to the board, another block starts falling from the top of the screen. The
		rowIsFull and clearRow methods check to see if a horizontal row on the board is full of blocks, and if it is that row is
		cleared, all blocks above that row fall down one row, and the score is incremented by 100. The game only ends when an 
		element in the invisible top row is filled, which happens if any of the space in which a new falling block would be drawn
		is already occupied by another block, thus causing the new block to go off screen. 

My references: "Tetris." Wikipedia, the free encyclopedia. Web. 6 Dec. 2009. <http://en.wikipedia.org/wiki/Tetris>

Included Files: Shapes.java and Shapes.class which contain the super class from which the 7 blocks inherit. Iblock.java, Iblock.class, 
		Jblock.java, Jblock.class, Lblock.java, Lblock.class, Oblock.java, Oblock.class, Sblock.java, Sblock.class, Tblock.java,
		Tblock.class, Zblock.java, and Zblock.class which contain all the information pertaining to the 7 different tetronimoes.
		gridBlock.java and gridBlock.class which contain information on gridBlocks, which compose the background board array.
		TetrisPanel.java, TetrisPanel.class, TetrisPanel$1.class, TetrisPanel$DirectionListener.class, 
		TetrisPanel$NewGameListener.class, TetrisPanel$PauseListener.class, and TetrisPanel$TetrisListener.class, which contain
		the panel used in the frame that does most of the work in the application, as well as all the listeners contained in the 
		panel. Tetris.java and Tetris.class which contain the frame that contains the frame. Finally, there is 
		TetrisHighScores.txt which contains the top 10 high scores, and Final Fantasy Theme.wav, which contains the music file
		use in the program.

Extra features: I included a pause button, the "p" key, that allows the user to pause the game and then unpause the game later when they
		wish to continue. 