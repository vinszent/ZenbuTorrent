package moe.zenbutorrent.main.java.remote.torrent;

public class DefaultRemoteTorrent implements RemoteTorrent
{
    private String stringId = null;
    private Number numberId = null;
    private String title;
    private String downloadDir;
    private String status;
    private long size; //In bytes
    private double progress; //In promille ex: 0.0523 = 5.23%
    private long downloaded; //In bytes
    private long uploaded; //In bytes
    private double ratio; //In promille
    private long uploadSpeed; //In bytes per second
    private long downloadSpeed; //In bytes per second
    private long eta; //In seconds

    public DefaultRemoteTorrent(String stringId, String title, String downloadDir, long size)
    {
        this.stringId = stringId;
        this.title = title;
        this.downloadDir = downloadDir;
        this.size = size;
    }

    public DefaultRemoteTorrent(Number numberId, String title, String downloadDir, long size)
    {
        this.numberId = numberId;
        this.title = title;
        this.downloadDir = downloadDir;
        this.size = size;
    }

    public DefaultRemoteTorrent()
    {
        // Do nothing 
    }        

    public String getStringId()
    {
        return stringId;
    }

    public void setStringId(String infoHash)
    {
        this.stringId = infoHash;
    }

    public Number getNumberId()
    {
        return numberId;
    }

    public void setNumberId(Number numberId)
    {
        this.numberId = numberId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDownloadDirectory()
    {
        return downloadDir;
    }

    public void setDownloadDirectory(String downloadDir)
    {
        this.downloadDir = downloadDir;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public double getProgress()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress = progress;
    }

    public long getDownloaded()
    {
        return downloaded;
    }

    public void setDownloaded(long downloaded)
    {
        this.downloaded = downloaded;
    }

    public long getUploaded()
    {
        return uploaded;
    }

    public void setUploaded(long uploaded)
    {
        this.uploaded = uploaded;
    }

    public double getRatio()
    {
        return ratio;
    }

    public void setRatio(double ratio)
    {
        this.ratio = ratio;
    }

    public long getUploadSpeed()
    {
        return uploadSpeed;
    }

    public void setUploadSpeed(long uploadSpeed)
    {
        this.uploadSpeed = uploadSpeed;
    }

    public long getDownloadSpeed()
    {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed)
    {
        this.downloadSpeed = downloadSpeed;
    }

    public long getEta()
    {
        return eta;
    }

    public void setEta(long eta)
    {
        this.eta = eta;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean same = false;

        if(object != null && object instanceof DefaultRemoteTorrent)
        {
            DefaultRemoteTorrent rt = (DefaultRemoteTorrent) object;

            if((rt.getNumberId() != null && rt.getNumberId() == this.getNumberId()) || (rt.getStringId() != null && rt.getStringId() == this.getStringId()))
            {
                same = true;
            }
        }

        return same;
    }
}
