package moe.zenbutorrent.main.java.local.tracker;

import java.util.ArrayList;

import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;

public class TrackerManager
{
    private ArrayList<Tracker> activeTrackers;
    private LocalTorrent torrent;

    public TrackerManager(LocalTorrent torrent)
    {
        this.torrent = torrent;

        activeTrackers = new ArrayList<>();
    }        

    public void addTracker(String url)
    {
        if(url.startsWith("http"))
        {
            activeTrackers.add(new TcpTracker(url, torrent));
        }
        else if(url.startsWith("udp"))
        {
            activeTrackers.add(new UdpTracker(url, torrent));
        }
    }        

    public void announce()
    {
        for(Tracker tracker : activeTrackers)
        {
            tracker.announce(); 
        }
    }
}
