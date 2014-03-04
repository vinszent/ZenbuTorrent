package moe.zenbutorrent.main.java.local.peer.peerchooser;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.local.piece.Piece;

public interface PeerChooser
{
    public Peer choosePeer(Piece piece);
}
