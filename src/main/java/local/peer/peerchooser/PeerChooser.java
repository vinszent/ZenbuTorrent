package main.java.local.peer.peerchooser;

import main.java.local.peer.Peer;
import main.java.local.piece.Piece;

public interface PeerChooser
{
    public Peer choosePeer(Piece piece);
}
