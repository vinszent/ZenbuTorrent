package main.java.local.filesystem;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.local.torrent.LocalTorrent;

public class TorrentFileManager
{
    private ArrayList<HashMap> torrentFiles = new ArrayList<>();
    private LocalTorrent torrent;

    public TorrentFileManager(LocalTorrent torrent)
    {
        this.torrent = torrent;
    }

    public LocalTorrent getTorrent()
    {
        return torrent;
    }        
}
