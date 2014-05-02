package moe.zenbutorrent.main.java.remote.torrent;

public interface RemoteTorrent
{
    public final static String WAITING = "waiting";
    public final static String CHECKING = "checking";
    public final static String DOWNLOADING = "downloading";
    public final static String SEEDING = "seeding";
    public final static String PAUSED = "paused";
    public final static String QUEUED = "queued";
    public final static String ERROR = "error";
    public final static String UNKOWN = "unkown";

    public String getStringId();
    public void setStringId(String infoHash);

    public Number getNumberId();
    public void setNumberId(Number numberId);

    public String getTitle();
    public void setTitle(String title);

    public String getDownloadDirectory();
    public void setDownloadDirectory(String directory);

    public String getStatus();
    public void setStatus(String status);

    public long getSize();
    public void setSize(long size);

    public double getProgress();
    public void setProgress(double progress);

    public long getDownloaded();
    public void setDownloaded(long downloaded);

    public long getUploaded();
    public void setUploaded(long uploaded);

    public double getRatio();
    public void setRatio(double ratio);

    public long getUploadSpeed();
    public void setUploadSpeed(long uploadSpeed);

    public long getDownloadSpeed();
    public void setDownloadSpeed(long downloadSpeed);

    public long getEta();
    public void setEta(long eta);

    @Override
    public boolean equals(Object object);
}
