package moe.zenbutorrent.main.java;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import main.java.remote.client.RemoteClient;
import moe.zenbutorrent.main.java.local.torrent.MetainfoFile;
import moe.zenbutorrent.main.java.logging.Log;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;
import moe.zenbutorrent.main.java.remote.wrappers.TransmissionClientWrapper;

public class Main
{
    public static void main(String[] args)
    {
        //Log.set(Log.LEVEL_ERROR);
        Log.set(Log.LEVEL_DEBUG);

        try
        {
            MetainfoFile tf = new MetainfoFile(new BufferedInputStream(new FileInputStream("/home/vincent/test6.torrent")));

            TransmissionClientWrapper test = new TransmissionClientWrapper(9090);

            
            //Bencoder.bencode(tf.getRoot(), os);
            //String s = Bencoder.bencode(tf.getRoot());
        }
        catch(FileNotFoundException e)
        {
            Log.error("Could not find file", e);
        }
        catch(Exception x)
        {
            x.printStackTrace();
        }
    }
}
