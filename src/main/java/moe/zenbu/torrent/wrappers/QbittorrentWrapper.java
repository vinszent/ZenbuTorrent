package moe.zenbu.torrent.wrappers;

import java.io.File;
import java.util.List;

import moe.zenbu.torrent.beans.Torrent;
import moe.zenbu.torrent.exceptions.WrapperConnectException;
import moe.zenbu.torrent.exceptions.WrapperUnauthorisedException;

public class QbittorrentWrapper implements ClientWrapper
{

    @Override
    public void addTorrent(String url) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTorrent(String url, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTorrent(File file) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTorrent(File file, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void pauseTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void resumeTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTorrent(Torrent torrent, boolean withData) throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Torrent> getTorrents() throws WrapperConnectException, WrapperUnauthorisedException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
