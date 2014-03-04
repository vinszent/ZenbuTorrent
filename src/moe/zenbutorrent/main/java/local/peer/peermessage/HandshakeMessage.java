package moe.zenbutorrent.main.java.local.peer.peermessage;

import java.nio.ByteBuffer;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;

public class HandshakeMessage extends PeerMessage
{
    private byte pstrlen;
    private ByteBuffer pstr;
    private ByteBuffer reserved;
    private ByteBuffer infoHashSha1;
    private ByteBuffer peerId;

    public HandshakeMessage(Peer peer)
    {
        sendBuffer = ByteBuffer.allocateDirect(68);

        pstrlen = 19;
        pstr = ByteBuffer.wrap("BitTorrent protocol".getBytes());
        reserved = ByteBuffer.wrap(new byte[8]);
        infoHashSha1 = peer.getPeerManager().getTorrent().getInfoHash();
        peerId = peer.getPeerManager().getTorrent().getPeerId();

        sendBuffer.put((byte) pstrlen);
        sendBuffer.put(pstr);
        sendBuffer.put(reserved);
        sendBuffer.put(infoHashSha1);
        sendBuffer.put(peerId);

        sendBuffer.rewind();

        this.socket = peer.getSocket();
    }
}
