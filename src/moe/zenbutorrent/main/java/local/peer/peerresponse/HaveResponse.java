package moe.zenbutorrent.main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.logging.Log;

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
