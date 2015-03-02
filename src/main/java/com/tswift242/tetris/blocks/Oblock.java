package com.tswift242.tetris.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Oblock extends TetrisBlock
{
	private static final List<BlockOrientation> OBLOCK_ORIENTATIONS = constructOrientations();

	public Oblock ()
	{
		super();
		setColor(Color.yellow);
	}

	public void rotate () {}

	private static List<BlockOrientation> constructOrientations() {
		List<BlockOrientation> blockOrientations = new ArrayList<>(4);

        boolean[][] orientation0Impl = new boolean[BLOCK_BOX_SIZE][BLOCK_BOX_SIZE];
        initOrientationToFalse(orientation0Impl);
        orientation0Impl[0][1] = true;
        orientation0Impl[0][2] = true;
        orientation0Impl[1][1] = true;
        orientation0Impl[1][2] = true;
        BlockOrientation orientation0 = new BlockOrientation(orientation0Impl, 2);

		blockOrientations.add(orientation0);
        // orientation1 == orientation0
        blockOrientations.add(orientation0);
        // orientation2 == orientation0
        blockOrientations.add(orientation0);
        // orientation3 == orientation0
        blockOrientations.add(orientation0);

		return blockOrientations;
	}

    @Override
	protected List<BlockOrientation> getOrientations() {
		return OBLOCK_ORIENTATIONS;
	}
}
