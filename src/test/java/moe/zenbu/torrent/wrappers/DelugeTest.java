package moe.zenbu.torrent.wrappers;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import moe.zenbu.torrent.beans.Torrent;
import moe.zenbu.torrent.exceptions.WrapperConnectException;
import moe.zenbu.torrent.exceptions.WrapperUnauthorisedException;

import org.junit.Test;

public class DelugeTest
{
    private static final DelugeWrapper d = new DelugeWrapper("deluge", "192.168.254.25", 8112); 

    //@Test
    //public void testSessionId()
    //{
        //try
        //{
            //String sessionId = d.getSessionId();

            //assertNotNull(sessionId);
        //}
        //catch(WrapperUnauthorisedException | WrapperConnectException e)
        //{
            //e.printStackTrace();
        //}
    //}

    //@Test
    //public void testAddTorrent()
    //{
        //try
        //{
            ////d.addTorrent("https://torguard.net/torrentip/checkMyTorrentIp.png.torrent");
            //d.addTorrent(new File("/home/vincent/Downloads/checkMyTorrentIp.png.torrent"));
        //}
        //catch(WrapperUnauthorisedException | WrapperConnectException e)
        //{
            //e.printStackTrace();
        //}
    //}

    //@Test
    //public void testGetTorrents()
    //{
        //try
        //{
            //List<Torrent> torrents = d.getTorrents();
            //Torrent t = torrents.get(0);
        //}
        //catch(Exception e)
        //{
            //e.printStackTrace();
        //}
    //}
}
