package moe.zenbutorrent.main.java.local.piece;

import java.util.ArrayList;

public class Piece
{
    private static final int BLOCK_LENGTH = 16384;

    private int index;
    private long size;
    private byte[] sha1Hash;

    private ArrayList<byte[]> blocks;
    private long trailingBlockLength;
    private ArrayList<Long> cacheTimes;

    private boolean downloaded = false;
    private boolean downloading = false;
    private boolean cached = false;

    public Piece(int index, long size, byte[] sha1Hash)
    {
        this.index = index;
        this.size = size;
        this.sha1Hash = sha1Hash;

        int blockCount = (int) Math.floor((double) (size / BLOCK_LENGTH));
        if(blockCount * BLOCK_LENGTH != size)
        {
            trailingBlockLength = size % blockCount;
            blocks = new ArrayList<>(blockCount + 1);
            cacheTimes = new ArrayList<>(blockCount + 1);
        }
        else
        {
            blocks = new ArrayList<>(blockCount);
            cacheTimes = new ArrayList<>(blockCount);
        }

        //If resuming check if data exists
    }

    public boolean cacheBlock(byte[] block)
    {
        //TODO: Initially cache block for required time, set current time as cache time.

        cached = true;

        return true;
    }        

    public byte[] getBlock()
    {
        if(!cached)
        {
            readData();
        }

        return new byte[8];
    }        

    public void writeData()
    {
        //TODO: Write data to file
        
        cached = false;
    }        

    public void readData()
    {
        //TODO: Read data from file, arraylist of blacks
        
        cached = true;
    }        

    public int getIndex()
    {
        return index;
    }        

    public boolean isDownloaded()
    {
        return downloaded;
    }        
}
