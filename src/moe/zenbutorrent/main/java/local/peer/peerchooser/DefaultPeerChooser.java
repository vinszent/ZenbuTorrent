package moe.zenbutorrent.main.java.local.peer.peerchooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import moe.zenbutorrent.main.java.local.peer.Peer;
import moe.zenbutorrent.main.java.local.piece.Piece;
import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;

public class DefaultPeerChooser implements PeerChooser
{
    private LocalTorrent torrent;

    public DefaultPeerChooser(LocalTorrent torrent)
    {
        this.torrent = torrent;   
    }        

    public Peer choosePeer(Piece piece)
    {
       
        return null;
    }        
}
