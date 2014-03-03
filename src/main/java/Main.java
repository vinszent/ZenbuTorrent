package main.java;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import main.java.logging.Log;
import main.java.remote.wrappers.UtorrentClientWrapper;
import main.java.local.torrent.MetainfoFile;

public class Main
{
    public static void main(String[] args)
    {
        //Log.set(Log.LEVEL_ERROR);
        Log.set(Log.LEVEL_DEBUG);

        try
        {
            MetainfoFile tf = new MetainfoFile(new BufferedInputStream(new FileInputStream("/home/vincent/test6.torrent")));

            UtorrentClientWrapper test = new UtorrentClientWrapper("abc", "123");
            //test.resumeTorrent(null);
            test.addTorrent("/home/vincent/Downloads/[Hatsuyuki]_Maken-ki!_Two_-_00_[1280x720][BAF450C1].mkv.torrent");
            //test.getAllTorrents();
            //Torrent t = new Torrent(tf);
            //t.init();
            
            //Bencoder.bencode(tf.getRoot(), os);
            //String s = Bencoder.bencode(tf.getRoot());
        }
        catch(FileNotFoundException e)
        {
            Log.error("Could not find file", e);
        }
    }
}
