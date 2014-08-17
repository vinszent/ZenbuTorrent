package moe.zenbu.torrent.exceptions;

import moe.zenbu.torrent.wrappers.ClientWrapper;

public class WrapperUnauthorisedException extends Exception
{
    private static final long serialVersionUID = -8031012877428450884L;

    public WrapperUnauthorisedException(final ClientWrapper wrapper)
    {
        super("Unauthorised access to torrent client: " + wrapper.getName());
    }
}
