package main.java.local.peer.peermessage;

import java.nio.ByteBuffer;
import java.util.BitSet;

import main.java.local.peer.Peer;

public class BitfieldMessage extends PeerMessage
{
    private byte len;
    private byte id;
    private BitSet bitfield;

    public BitfieldMessage(Peer peer)
    {
        bitfield = peer.getPeerManager().getTorrent().getPieceManager().getBitfield();

        len = (byte) (1 + bitfield.length());
        id = 5;

        sendBuffer = ByteBuffer.allocateDirect(1 + len);

        sendBuffer.put(len);
        sendBuffer.put(id);
        sendBuffer.put(bitfield.toByteArray());

        sendBuffer.rewind();
        
        this.socket = peer.getSocket();
    }
}
