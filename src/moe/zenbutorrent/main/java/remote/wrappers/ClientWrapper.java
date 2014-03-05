package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.File;
import java.util.ArrayList;

import moe.zenbutorrent.main.java.remote.torrent.DefaultRemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;

public interface ClientWrapper
{
    public void addTorrent(String url);

    public void addTorrent(File file);

    public void pauseTorrent(DefaultRemoteTorrent remoteTorrent);

    public void resumeTorrent(DefaultRemoteTorrent remoteTorrent);

    public ArrayList<DefaultRemoteTorrent> getAllTorrents();

    public void updateAllTorrents(ArrayList<RemoteTorrent> torrents, Class<? extends RemoteTorrent> c);
}
