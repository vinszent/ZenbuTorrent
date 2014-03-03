package main.java.local.peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.ThreadPoolExecutor;

import main.java.logging.Log;
import main.java.local.peer.peermessage.PeerMessage;
import main.java.local.peer.peerresponse.PeerResponseHandler;
import main.java.local.piece.Piece;

public class Peer
{
    private SocketChannel socket;
    private PeerManager peerManager;
    private ByteBuffer peerId;

    private ExecutorService sendingExecutor;
    private ExecutorService receivingExecutor;

    private boolean shookHands = false;

    private boolean amChoking = true; // We are choking this peer
    private boolean amInterested = false; // We are interested in this peer
    private boolean peerChoking = true; // This peer is choking us
    private boolean peerInterested = false; // This peer is interested in us

    private BitSet bitfield;

    private long downSpeed;
    private long lastDownUpdate;
    private long upSpeed;
    private long lastUpUpdate;

    public Peer(SocketChannel socket, PeerManager peerManager)
    {
        this.socket = socket;
        this.peerManager = peerManager;
        
        sendingExecutor = Executors.newSingleThreadExecutor();
        receivingExecutor = Executors.newSingleThreadExecutor();
    }

    public void setPeerId(ByteBuffer peerId)
    {
       this.peerId = peerId; 
    }        

    public void setBitfield(BitSet bitfield)
    {
        this.bitfield = bitfield;
    }        

    public BitSet getBitfield()
    {
        return bitfield;
    }        

    public void setHasPiece(int index)
    {
        bitfield.set(index);
    }        

    public void clearHasPieces(int index)
    {
        bitfield.clear(index);
    }        

    public void sendMessage(PeerMessage message)
    {
        sendingExecutor.execute(message);
    }

    public void handleReponse()
    {
        PeerResponseHandler peerResponseHandler = new PeerResponseHandler(this);
        receivingExecutor.execute(peerResponseHandler);
    }        

    public boolean getShookHands()
    {
        return shookHands;
    }        

    public void setShookHands(boolean shookHands)
    {
        this.shookHands = shookHands;
    }        

    public SocketChannel getSocket()
    {
        return socket;    
    }        

    public void drop()
    {
        peerManager.dropPeer(this);
        try
        {
            socket.close();
        }
        catch(IOException e)
        {
            Log.error("Could not close socket to peer");
        }
    }        

    public boolean hasPiece(Piece piece)
    {
        return bitfield.get(piece.getIndex());
    }        

    public boolean isQueueEmpty()
    {
        return ((ThreadPoolExecutor) sendingExecutor).getQueue().size() == 0;
    }

    public void setDownSpeed(long downBytes)
    {
        long now = System.nanoTime();

        downSpeed = Math.round(downBytes / Math.pow(now - lastDownUpdate, -9));
    }        

    public long getDownSpeed()
    {
        return downSpeed;
    }

    public PeerManager getPeerManager()
    {
        return peerManager;
    }        
}
