package moe.zenbu.torrent.beans;

import java.util.Date;

import moe.zenbu.torrent.wrappers.ClientWrapper;

public class BasicTorrent implements Torrent
{
    private ClientWrapper wrapper;

    private Object id;

    private String name;

    private String downloadDirectory;

    private String status;

    private long size;

    private double progress;

    private long downloaded;

    private long uploaded;

    private double ratio;

    private double downloadSpeed;

    private double uploadSpeed;

    private long eta;

    public BasicTorrent(final ClientWrapper wrapper, final Object id)
    {
        this.wrapper = wrapper;
        this.id = id;
    }

    public ClientWrapper getWrapper()
    {
        return wrapper;
    }

    public void setWrapper(ClientWrapper wrapper)
    {
        this.wrapper = wrapper;
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDownloadDirectory()
    {
        return downloadDirectory;
    }

    public void setDownloadDirectory(String downloadDirectory)
    {
        this.downloadDirectory = downloadDirectory;
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

    public double getDownloadSpeed()
    {
        return downloadSpeed;
    }

    public void setDownloadSpeed(double downloadSpeed)
    {
        this.downloadSpeed = downloadSpeed;
    }

    public double getUploadSpeed()
    {
        return uploadSpeed;
    }

    public void setUploadSpeed(double uploadSpeed)
    {
        this.uploadSpeed = uploadSpeed;
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
    public void pause()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString()
    {
        return "BasicTorrent [wrapper=" + wrapper + ", id=" + id + ", name=" + name + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((wrapper == null) ? 0 : wrapper.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        BasicTorrent other = (BasicTorrent) obj;
        if(id == null)
        {
            if(other.id != null)
                return false;
        }
        else if(!id.equals(other.id))
            return false;
        if(wrapper == null)
        {
            if(other.wrapper != null)
                return false;
        }
        else if(!wrapper.equals(other.wrapper))
            return false;
        return true;
    }
}
