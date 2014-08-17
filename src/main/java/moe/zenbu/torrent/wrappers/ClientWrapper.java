package moe.zenbu.torrent.wrappers;

import java.io.File;
import java.util.List;

import moe.zenbu.torrent.beans.Torrent;
import moe.zenbu.torrent.exceptions.WrapperConnectException;
import moe.zenbu.torrent.exceptions.WrapperUnauthorisedException;

public interface ClientWrapper
{
    /**
     * Adds a torrent to download, downloads to the clients default directory.
     * 
     * @param url URL to the torrent file
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void addTorrent(final String url) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Adds a torrent to download, downloads to the specified download directory.
     *
     * @param url URL to the torrent file
     * @param downloadPath Canonical path to the desired download directory
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void addTorrent(final String url, final String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Adds a torrent to download.
     *
     * @param file File object of torrent
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void addTorrent(final File file) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Adds a torrent to download, downloads to the specified download directory.
     *
     * @param file File object of torrent
     * @param downloadPath Canonical path to the desired download directory
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void addTorrent(final File file, final String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Pauses the given torrent.
     *
     * @param torrent Torrent to pause
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void pauseTorrent(final Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Resumes the given torrent.
     *
     * @param torrent Torrent to resume
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public void resumeTorrent(final Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Removes the torrent from the client. The client will no longer continue to download the torrent.
     *
     * @param torrent Torrent to remove
     * @param withData Remove downloaded data as well
     */
    public void removeTorrent(final Torrent torrent, final boolean withData) throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Get a list of all torrents managed by the client
     *
     * @return List of torrents, default implementation is {@link moe.zenbu.torrent.beans.BasicTorrent}
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     */
    public List<Torrent> getTorrents() throws WrapperConnectException, WrapperUnauthorisedException;

    /**
     * Get the name of the client wrapper
     *
     * @return name of the client wrapper
     */
    public String getName();
}
