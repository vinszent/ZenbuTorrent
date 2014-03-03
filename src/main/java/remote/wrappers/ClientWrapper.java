package main.java.remote.wrappers;

import java.util.ArrayList;

import main.java.remote.torrent.RemoteTorrent;

public interface ClientWrapper
{
    public void addTorrent(String filepath);

    public void pauseTorrent(RemoteTorrent remoteTorrent);

    public void resumeTorrent(RemoteTorrent remoteTorrent);

    public ArrayList<RemoteTorrent> getAllTorrents();
}
