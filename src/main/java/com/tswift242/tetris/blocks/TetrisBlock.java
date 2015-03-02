package com.tswift242.tetris.blocks;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.tswift242.tetris.config.TetrisProperties;

public abstract class TetrisBlock
{
    // blocks are rotated 90 degrees --> 4 different orientations (max)
    protected static final int NUM_ORIENTATIONS = 4;
	// TetrisBlock orientations are logically constrained to fit inside square boxes of this size
	protected static final int BLOCK_BOX_SIZE = 4;

	// this blocks 4 different orientations; specified in subclasses
	private final List<BlockOrientation> orientations;

	// current block layout
    private BlockOrientation currOrientation;
    private int x, y, currOrientationIndex;
    private Color shapeColor;

	public TetrisBlock()
	{
		orientations = getOrientations();
		x = y = currOrientationIndex = 0;
        currOrientation = orientations.get(currOrientationIndex);
		shapeColor = TetrisProperties.BACKGROUND_COLOR;
	}

	public boolean[][] getCurrentOrientation()
	{
		return currOrientation.getOrientation();
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

	public void setX (int x)
	{
		this.x = x;
	}

	public void setY (int y)
	{
		this.y = y;
	}

	protected void setColor (Color c)
	{
		shapeColor = c;
	}

	public void rotate () {
        currOrientationIndex = getNextOrientationIndex();
        currOrientation = orientations.get(currOrientationIndex);
    }

    //TODO: delete? Check if we actually need this
	public boolean[][] getNextOrientation () {
        return orientations.get(getNextOrientationIndex()).getOrientation();
    }

    //TODO: this is a temp method inserted for now to enable current implementation of getCurrentOrientation(),
    // which returns the orientation impl, instead of the whole BlockOrientation. We should be returning the whole
    // BlockOrientation object, at which point this should be removed
    public int getDiffInNextOrientLengthFromCurrent() {
        return (currOrientation.length() - orientations.get(getNextOrientationIndex()).length());
    }



	protected abstract List<BlockOrientation> getOrientations();

    private int getNextOrientationIndex() {
        return ((currOrientationIndex + 1) % NUM_ORIENTATIONS);
    }

    // this is a utility method used by subclasses
    protected static void initOrientationToFalse(boolean[][] orientation) {
        for(boolean[] array : orientation) {
            Arrays.fill(array, false);
        }
    }
}
