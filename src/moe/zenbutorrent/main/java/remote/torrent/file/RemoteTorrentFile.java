package moe.zenbutorrent.main.java.remote.torrent.file;

public interface RemoteTorrentFile
{
    public String getFileName();
    public void setFileName(String arg0);

    public long getFileSize();
    public void setFileSize(long arg0);

    public long getDownloaded();
    public void setDownloaded(long arg0);
}
