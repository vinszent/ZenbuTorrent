package main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;
import java.util.BitSet;

import main.java.logging.Log;
import main.java.local.peer.Peer;
import main.java.local.util.ArrayUtils;

public class BitfieldResponse extends PeerResponse
{
    public BitfieldResponse(ByteBuffer receiveBuffer, Peer peer)
    {
        super(receiveBuffer, peer);
    }

    public void handle()
    {
        BitSet bitfield = BitSet.valueOf(ArrayUtils.split(receiveBuffer));
        peer.setBitfield(bitfield);
    }        
}
