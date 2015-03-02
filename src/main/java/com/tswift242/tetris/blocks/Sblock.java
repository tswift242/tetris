package com.tswift242.tetris.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Sblock extends TetrisBlock
{
	private static final List<BlockOrientation> SBLOCK_ORIENTATIONS = constructOrientations();

	public Sblock ()
	{
		super();
		setColor(Color.green);
	}

	private static List<BlockOrientation> constructOrientations() {
		List<BlockOrientation> blockOrientations = new ArrayList<>(4);

        boolean[][] orientation0Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation0Impl);
        orientation0Impl[0][1] = true;
        orientation0Impl[0][2] = true;
        orientation0Impl[1][0] = true;
        orientation0Impl[1][1] = true;
        BlockOrientation orientation0 = new BlockOrientation(orientation0Impl, 2);

        boolean[][] orientation1Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation1Impl);
        orientation1Impl[0][0] = true;
        orientation1Impl[1][0] = true;
        orientation1Impl[1][1] = true;
        orientation1Impl[2][1] = true;
        BlockOrientation orientation1 = new BlockOrientation(orientation1Impl, 3);

		blockOrientations.add(orientation0);
		blockOrientations.add(orientation1);
		// orientation2 == orientation0
		blockOrientations.add(orientation0);
		// orientation3 == orientation1
		blockOrientations.add(orientation1);

		return blockOrientations;
	}

    @Override
	protected List<BlockOrientation> getOrientations() {
		return SBLOCK_ORIENTATIONS;
	}
}
