package moe.zenbutorrent.main.java.local.peer.peerresponse;

import java.nio.ByteBuffer;
import java.util.Arrays;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.local.peer.peermessage.BitfieldMessage;
import moe.zenbutorrent.main.java.local.util.UrlEncoder;
import moe.zenbutorrent.main.java.logging.Log;

public class HandshakeResponse extends PeerResponse
{
    public HandshakeResponse(ByteBuffer receiveBuffer, Peer peer)
    {
        super(receiveBuffer, peer);
    }

    public void handle()
    {
        int length = (int) receiveBuffer.get();
        byte[] protocol = new byte[19];
        byte[] reserved = new byte[8];
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];


        receiveBuffer.get(protocol, 0, 19);
        receiveBuffer.get(reserved, 0, 8);
        receiveBuffer.get(infoHash, 0, 20);
        receiveBuffer.get(peerId, 0, 20);

        if(length != 19)
        {
            peer.drop();
            Log.error("Incorrect protocol length");
            return;
        }
        else if(!Arrays.equals(protocol, "BitTorrent protocol".getBytes()))
        {
            peer.drop();
            Log.error("Incorrect protocol identifier");
            return;
        }
        else if(!Arrays.equals(infoHash, peer.getPeerManager().getTorrent().getInfoHash().array()))
        {
            peer.drop();
            Log.error("Incorrect info hash");
            return;
        }

        peer.setPeerId(ByteBuffer.wrap(peerId));
        peer.sendMessage(new BitfieldMessage(peer));
        Log.info("Peer Id set: " + UrlEncoder.byteArrayToURLString(peerId));
    }
}        
