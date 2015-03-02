package com.tswift242.tetris.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tblock extends TetrisBlock
{
	private static final List<BlockOrientation> TBLOCK_ORIENTATIONS = constructOrientations();

	public Tblock ()
	{
		super();
		setColor(Color.magenta);
	}

	private static List<BlockOrientation> constructOrientations() {
		List<BlockOrientation> blockOrientations = new ArrayList<>(4);

        boolean[][] orientation0Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation0Impl);
        orientation0Impl[0][1] = true;
        orientation0Impl[1][0] = true;
        orientation0Impl[1][1] = true;
        orientation0Impl[1][2] = true;
        BlockOrientation orientation0 = new BlockOrientation(orientation0Impl, 2);

        boolean[][] orientation1Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation1Impl);
        orientation1Impl[0][0] = true;
        orientation1Impl[1][0] = true;
        orientation1Impl[1][1] = true;
        orientation1Impl[2][0] = true;
        BlockOrientation orientation1 = new BlockOrientation(orientation1Impl, 3);

        boolean[][] orientation2Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation2Impl);
        orientation2Impl[0][0] = true;
        orientation2Impl[0][1] = true;
        orientation2Impl[0][2] = true;
        orientation2Impl[1][1] = true;
        BlockOrientation orientation2 = new BlockOrientation(orientation2Impl, 2);

        boolean[][] orientation3Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation3Impl);
        orientation3Impl[0][1] = true;
        orientation3Impl[1][0] = true;
        orientation3Impl[1][1] = true;
        orientation3Impl[2][1] = true;
        BlockOrientation orientation3 = new BlockOrientation(orientation3Impl, 3);

		blockOrientations.add(orientation0);
		blockOrientations.add(orientation1);
		blockOrientations.add(orientation2);
		blockOrientations.add(orientation3);

		return blockOrientations;
	}

    @Override
	protected List<BlockOrientation> getOrientations() {
		return TBLOCK_ORIENTATIONS;
	}
}
