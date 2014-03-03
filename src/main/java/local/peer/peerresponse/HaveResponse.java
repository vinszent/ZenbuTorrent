package main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;

import main.java.logging.Log;
import main.java.local.peer.Peer;

public class HaveResponse extends PeerResponse
{
    public HaveResponse(ByteBuffer receiveBuffer, Peer peer)
    {
        super(receiveBuffer, peer);
    }

    public void handle()
    {
       int index = receiveBuffer.getInt(); 

       peer.setHasPiece(index);
    }        
}
