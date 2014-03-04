package moe.zenbutorrent.main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;
import java.util.BitSet;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.local.util.ArrayUtils;
import moe.zenbutorrent.main.java.logging.Log;

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
