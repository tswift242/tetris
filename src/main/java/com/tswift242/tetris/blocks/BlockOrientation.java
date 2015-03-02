package com.tswift242.tetris.blocks;

public class BlockOrientation
{
    private final boolean[][] orientation;

    // orientation meta data
    //TODO: add leftMostCol and rightMostCol, then make length the difference of these
    //TODO: length is currently vertical length; it should be horizontal length
    private final int length;

    public BlockOrientation(boolean[][] orientation, int length) {
        this.orientation = orientation;
        this.length = length;
    }

    public boolean[][] getOrientation() {
        return orientation;
    }

    public int length() {
        return length;
    }
}