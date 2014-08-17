package moe.zenbu.torrent.beans;

import java.util.Date;

import moe.zenbu.torrent.wrappers.ClientWrapper;

public interface Torrent
{
    public final static String WAITING = "waiting";
    public final static String CHECKING = "checking";
    public final static String DOWNLOADING = "downloading";
    public final static String SEEDING = "seeding";
    public final static String PAUSED = "paused";
    public final static String QUEUED = "queued";
    public final static String ERROR = "error";
    public final static String UNKOWN = "unkown";

    // Getters
    public ClientWrapper getWrapper();

    public Object getId();

    public String getName();

    public String getDownloadDirectory();

    public String getStatus();

    public long getSize();

    public double getProgress();

    public long getDownloaded();

    public long getUploaded();

    public double getRatio();

    public double getDownloadSpeed();

    public double getUploadSpeed();

    public long getEta();

    // Setters
    public void setWrapper(final ClientWrapper wrapper);

    public void setId(final Object id);

    public void setName(final String name);

    public void setDownloadDirectory(final String downloadDirectory);

    public void setStatus(final String status);

    public void setSize(final long size);

    public void setProgress(final double progress);

    public void setDownloaded(final long downloaded);

    public void setUploaded(final long uploaded);

    public void setRatio(final double ratio);

    public void setDownloadSpeed(final double downloadSpeed);

    public void setUploadSpeed(final double uploadSpeed);

    public void setEta(final long eta);

    public void pause();

    public void resume();
}
