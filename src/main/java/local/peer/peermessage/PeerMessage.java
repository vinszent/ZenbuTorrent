package main.java.local.peer.peermessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import main.java.logging.Log;
import main.java.local.util.PausableRunnable;

public abstract class PeerMessage extends PausableRunnable
{
    protected SocketChannel socket;
    protected ByteBuffer sendBuffer;

    @Override
    public void run()
    {
        while(sendBuffer.hasRemaining())
        {
            Log.debug("Writing to peer");
            try
            {
                socket.write(sendBuffer); 
            }
            catch(IOException e)
            {
                Log.warn("Could not complete sending of message to peer", e);
            }
        }
    }        
}
