package moe.zenbutorrent.main.java.remote.torrent.file;

public class DefaultRemoteTorrentFile implements RemoteTorrentFile
{
    private String fileName;
    private long fileSize;
    private long downloaded;

    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String arg0)
    {
        fileName = arg0;
    }

    public long getFileSize()
    {
        return fileSize;
    }
    public void setFileSize(long arg0)
    {
        fileSize = arg0;
    }

    public long getDownloaded()
    {
        return downloaded;
    }
    public void setDownloaded(long arg0)
    {
        downloaded = arg0;
    }
}
