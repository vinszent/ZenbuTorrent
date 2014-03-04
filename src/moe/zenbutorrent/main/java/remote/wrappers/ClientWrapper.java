package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.File;
import java.util.ArrayList;

import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;

public interface ClientWrapper
{
    public void addTorrent(String url);

    public void addTorrent(File file);

    public void pauseTorrent(RemoteTorrent remoteTorrent);

    public void resumeTorrent(RemoteTorrent remoteTorrent);

    public ArrayList<RemoteTorrent> getAllTorrents();
}
