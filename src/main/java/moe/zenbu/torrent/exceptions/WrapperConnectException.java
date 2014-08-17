package moe.zenbu.torrent.exceptions;

import moe.zenbu.torrent.wrappers.ClientWrapper;

public class WrapperConnectException extends Exception
{
    private static final long serialVersionUID = 3297507600586626550L;

    public WrapperConnectException(final ClientWrapper wrapper, final String message)
    {
        super("Connect exception to " + wrapper.getName() + " caused by: " + message);
    }

    public WrapperConnectException(final ClientWrapper wrapper, final Exception cause)
    {
        super("Connect exception to " + wrapper.getName() + " caused by: ", cause);
    }
}
