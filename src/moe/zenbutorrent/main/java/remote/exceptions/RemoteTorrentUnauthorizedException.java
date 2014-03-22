package moe.zenbutorrent.main.java.remote.exceptions;

public class RemoteTorrentUnauthorizedException extends Exception
{
    public RemoteTorrentUnauthorizedException()
    {
        super("Unauthorized to access remote torrent client, are credentials incorrect?");
    }
}
