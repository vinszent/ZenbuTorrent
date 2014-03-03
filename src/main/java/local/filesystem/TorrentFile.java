package main.java.local.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import main.java.logging.Log;

public class TorrentFile
{
    private final static long BLOCK_LENGTH = 16384;

    private String filepath;
    private RandomAccessFile file;
    private TorrentFileManager torrentFileManager;

    private long length;
    private long begin;
    private long end;

    public TorrentFile(TorrentFileManager torrentFileManager, String filepath, long length, long begin)
    {
        this.filepath = filepath;
        try
        {
            file = new RandomAccessFile(filepath, "rw");
        }
        catch(FileNotFoundException e)
        {
            Log.error("Could not open file: " + filepath, e);
        }

        this.torrentFileManager = torrentFileManager;

        this.length = length;
        this.begin = begin;
        this.end = begin + length;
    }

    public void writeBlock(byte[] data, int index, long offset)
    {
       long fileOffset = (index * torrentFileManager.getTorrent().getPieceLength()) + offset; 
    
       try
       {
           file.seek(fileOffset);
           file.write(data);
       }
       catch(IOException e)
       {
           Log.error("Could not write block to file: " + filepath, e);
       }
    }        

    public long getBegin()
    {
        return begin;
    }        

    public long getEnd()
    {
        return end; 
    }        
}
