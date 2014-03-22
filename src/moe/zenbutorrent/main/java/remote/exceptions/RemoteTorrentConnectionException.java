package moe.zenbutorrent.main.java.remote.exceptions;

public class RemoteTorrentConnectionException extends Exception
{
    public RemoteTorrentConnectionException()
    {
        super("Could not connect to remote torrent client");
    }
}
