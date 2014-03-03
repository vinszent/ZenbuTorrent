package main.java.local.peer.peerresponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import main.java.logging.Log;
import main.java.local.peer.Peer;
import main.java.local.util.PausableRunnable;

public class PeerResponseHandler implements Runnable
{
    private Peer peer;
    private SocketChannel socket;
    private boolean shookHands;
    private ByteBuffer receiveBuffer;
    private long bytesRead = 0;

    public PeerResponseHandler(Peer peer)
    {
        this.peer = peer;
        this.socket = peer.getSocket();
        this.shookHands = peer.getShookHands();

        //receiveBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        receiveBuffer = ByteBuffer.allocate(4);
    }

    public void read()
    {
        try
        {
            while(receiveBuffer.hasRemaining())
            {
               bytesRead += socket.read(receiveBuffer);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        receiveBuffer.rewind();
    }        

    @Override
    public void run()
    {
        int length;
        byte id;

        if(!shookHands)
        {
            Log.debug("Handshake received");

            peer.setShookHands(true);

            receiveBuffer = ByteBuffer.allocate(68);

            read();

            System.out.println(receiveBuffer.limit());

            //TODO: Handshake
            new HandshakeResponse(receiveBuffer, peer).handle();

            peer.getPeerManager().getTcpResponseListener().register(socket); //Re-register this channel

            return; 
        }

        read();

        length = receiveBuffer.getInt(0);

        Log.debug("Length: " + length);

        receiveBuffer = ByteBuffer.allocate(length);

        read();

        id = receiveBuffer.get();

        Log.debug("Id: " + id);

        switch(id)
        {
            case -1:
                //TODO: Keep alive
                break;
            case 0:
                //TODO: Choke
                break;
            case 1:
                //TODO: Unchoke
                break;
            case 2:
                //TODO: Interested
                break;
            case 3:
                //TODO: Not interested
                break;
            case 4:
                //TODO: Have
                new HaveResponse(receiveBuffer, peer).handle();
                break;
            case 5:
                //TODO: Bitfield
                new BitfieldResponse(receiveBuffer, peer).handle();
                break;
            case 6:
                //TODO: Request
                break;
            case 7:
                //TODO: Piece
                break;
            case 8:
                //TODO: Cancel
                break;
            case 9:
                //TODO: Node
                break;
        }

        peer.setDownSpeed(bytesRead); //Set current down speed
        peer.getPeerManager().getTcpResponseListener().register(socket); //Re-register this channel
    }
}
