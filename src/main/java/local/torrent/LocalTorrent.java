package main.java.local.torrent;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import main.java.logging.Log;
import main.java.local.peer.PeerManager;
import main.java.local.piece.PieceManager;
import main.java.local.tracker.TrackerManager;

public class LocalTorrent
{
    private MetainfoFile tf;

    private PeerManager peerManager;
    private PieceManager pieceManager;
    private TrackerManager trackerManager;

    private ArrayList<String> announceList;
    private ArrayList<byte[]> peerAddresses;
    private ArrayList<byte[]> sha1Pieces;
    private ByteBuffer infoHash;
    private ByteBuffer peerId;
    private long size;
    private long pieceLength;

    public LocalTorrent(MetainfoFile tf)
    {
        this.tf = tf;

        announceList = tf.getAnnounceList();
        sha1Pieces = tf.getSha1Pieces();
        infoHash = tf.getInfoAsSha1();
        size = tf.getLength();
        pieceLength = tf.getPieceLength();

        generatePeerId();

        Log.info("Torrent info hash: " + infoHash);
        Log.info("Torrent peerID: " + peerId);

    }

    public void init()
    {
        peerManager = new PeerManager(this);
        pieceManager = new PieceManager(this);
        trackerManager = new TrackerManager(this);

        for(String s : announceList)
        {
            Log.debug("Adding tracker: " + s);
            trackerManager.addTracker(s);
        }

        trackerManager.announce();
    }        

    public void generatePeerId()
    {
        Random random;
        byte[] temp = new byte[20];

        random = new Random(System.currentTimeMillis());

        random.nextBytes(temp);
        System.arraycopy("-ZT0001-".getBytes(), 0, temp, 0, 8);
        
        peerId = ByteBuffer.wrap(temp);
    }        

    public ByteBuffer getInfoHash()
    {
        return infoHash;
    }        

    public ByteBuffer getPeerId()
    {
        return peerId;
    }        

    public ArrayList<byte[]> getSha1Pieces()
    {
        return sha1Pieces;
    }        

    public long getSize()
    {
        return size;
    }        

    public long getPieceLength()
    {
        return pieceLength;
    }        

    public PeerManager getPeerManager()
    {
        return peerManager;
    }        

    public PieceManager getPieceManager()
    {
        return pieceManager;
    }        
}
