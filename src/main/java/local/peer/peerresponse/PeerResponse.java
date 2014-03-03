package main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;

import main.java.local.peer.Peer;

public abstract class PeerResponse
{
    protected ByteBuffer receiveBuffer;
    protected Peer peer;

    public PeerResponse(ByteBuffer receiveBuffer, Peer peer)
    {
        this.receiveBuffer = receiveBuffer;
        this.peer = peer;
    }

    public void handle()
    {

    }        
}
