package main.java.local.tracker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.logging.Log;
import main.java.local.peer.PeerManager;
import main.java.local.torrent.LocalTorrent;
import main.java.local.util.Bencoder;
import main.java.local.util.IpDecoder;
import main.java.local.util.UrlEncoder;

public class TcpTracker implements Tracker
{
    private String announceUrl;
    private LocalTorrent torrent;

    public TcpTracker(String announceUrl, LocalTorrent torrent)
    {
        this.announceUrl = announceUrl;
        this.torrent = torrent;
    }

    public void announce()
    {
       String requestUrl = "" ;
       HttpURLConnection conn = null;
       ArrayList<String[]> peerAddresses = null;

       requestUrl += announceUrl;
       requestUrl += "?info_hash=" + UrlEncoder.byteArrayToURLString(torrent.getInfoHash().array());
       requestUrl += "&peer_id=" + UrlEncoder.byteArrayToURLString(torrent.getPeerId().array());
       requestUrl += "&port=" + 6881;
       requestUrl += "&uploaded=" + 0;
       requestUrl += "&downloaded=" + 0;
       requestUrl += "&left=" + torrent.getSize();
       requestUrl += "&event=" + "started";

       System.out.println("URL: " + requestUrl);

       try
       {
           conn = (HttpURLConnection) new URL(requestUrl).openConnection();
           conn.setRequestProperty("User-Agent", "ZenbuTorrent");

           HashMap response = (HashMap) Bencoder.bdecode(new BufferedInputStream(conn.getInputStream()));
           Object peers = response.get(ByteBuffer.wrap("peers".getBytes()));

           if(peers instanceof ByteBuffer)
           {
               peerAddresses = IpDecoder.decodeByteString(((ByteBuffer) peers).array());
           }
           else if(peers instanceof ArrayList)
           {
               peerAddresses = new ArrayList<>();
               ArrayList al = (ArrayList) peers;

               for(int i = 0; i < al.size(); i++)
               {
                   HashMap hm = (HashMap) al.get(i);
                   String ip = new String(((ByteBuffer) hm.get(ByteBuffer.wrap("ip".getBytes()))).array());
                   String port = String.valueOf((long) hm.get(ByteBuffer.wrap("port".getBytes())));

                   peerAddresses.add(new String[]{ip, port});
               }
           }

           torrent.getPeerManager().addPeers(peerAddresses);   
       }
       catch(IOException e)
       {
           Log.error("Could not announce on tracker: " + announceUrl, e);
       }
    }        
}
