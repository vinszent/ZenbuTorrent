package moe.zenbutorrent.main.java.remote.torrent;

public interface RemoteTorrent
{
    public String getStringId();
    public void setStringId(String infoHash);

    public int getIntId();
    public void setIntId(int intId);

    public String getTitle();
    public void setTitle(String title);

    public String getFilepath();
    public void setFilepath(String filepath);

    public RemoteTorrentStatus getStatus();
    public void setStatus(RemoteTorrentStatus status);

    public long getSize();
    public void setSize(long size);

    public double getProgress();
    public void setProgress(double progress);

    public long getRemaining();
    public void setRemaining(long remaining);

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
