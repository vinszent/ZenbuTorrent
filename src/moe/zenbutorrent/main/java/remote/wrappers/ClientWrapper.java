package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.File;
import java.util.List;

import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;

public interface ClientWrapper
{
    public void addTorrent(String url);

    public void addTorrent(File file);

    public void pauseTorrent(RemoteTorrent remoteTorrent);

    public void resumeTorrent(RemoteTorrent remoteTorrent);

    public void deleteTorrent(RemoteTorrent remoteTorrent);

    public void deleteTorrentAndData(RemoteTorrent remoteTorrent);

    public List<? extends RemoteTorrent> getAllTorrents();

    public void updateAllTorrents(List<RemoteTorrent> torrents, Class<? extends RemoteTorrent> c);
}
