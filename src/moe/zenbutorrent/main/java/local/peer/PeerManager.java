package moe.zenbutorrent.main.java.local.peer;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import moe.zenbutorrent.main.java.local.peer.peermessage.HandshakeMessage;
import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;
import moe.zenbutorrent.main.java.logging.Log;

public class PeerManager
{
    private LocalTorrent torrent;

    private ArrayList<Peer> downloading;
    private ArrayList<Peer> peers;

    private TcpResponseListener tcpResponseListener;
    private Thread responseThread;

    public PeerManager(LocalTorrent torrent)
    {
        this.torrent = torrent;

        peers = new ArrayList<>();
        downloading = new ArrayList<>(60);

        tcpResponseListener = new TcpResponseListener(torrent);
        responseThread = new Thread(tcpResponseListener);
        responseThread.start();
    }

    public void initPeers()
    {
        
    }        

    public void addPeer(String[] peerAddress)
    {
        try
        {
            //UtpSocketChannel utp = UtpSocketChannel.open();
            //utp.connect(new InetSocketAddress(peerAddress[0], Integer.parseInt(peerAddress[1])));
            //Thread.sleep(1000);
            //Log.debug("Utp connected: " + utp.isConnected());
            //Log.debug("Utp state: " + utp.getState());
            
            //DatagramChannel dg = DatagramChannel.open();
            //dg.configureBlocking(true);
            //dg.socket().connect(new InetSocketAddress(peerAddress[0], Integer.parseInt(peerAddress[1])));
            //dg.configureBlocking(false);
            //Log.debug("Datagram: " + dg.isConnected());

            SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(true);
            socket.socket().connect(new InetSocketAddress(peerAddress[0], Integer.parseInt(peerAddress[1])), 300);
            socket.configureBlocking(false);

            Peer p = new Peer(socket, this);

            if(!peers.contains(p))
            {
                peers.add(p); 
                tcpResponseListener.register(socket);
                p.sendMessage(new HandshakeMessage(p));

                Log.debug("Adding peer: " + peerAddress[0] + ":" + peerAddress[1]);
            }
        }
        catch(Exception e)
        {
            //Log.warn("Could not open socket to peer");
            return;
        }
    }        

    public void addPeers(ArrayList<String[]> peerAddresses)
    {
        for(String[] sa : peerAddresses)
        {
            addPeer(sa);
        }
    }        

    public void dropPeer(Peer p)
    {
        peers.remove(p);
    }        

    public void handleResponse(SocketChannel socket)
    {
        for(Peer p : peers)
        {
            if(p.getSocket() == socket)
            {
                p.handleReponse();
                return;
            }
        }
    }        

    public TcpResponseListener getTcpResponseListener()
    {
        return tcpResponseListener;
    }        

    public ArrayList<Peer> getDownloading()
    {
        return downloading;
    }        

    public LocalTorrent getTorrent()
    {
        return torrent;
    }        
}
