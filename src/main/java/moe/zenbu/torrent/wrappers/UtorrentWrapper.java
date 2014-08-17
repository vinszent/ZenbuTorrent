package moe.zenbu.torrent.wrappers;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import moe.zenbu.torrent.beans.BasicTorrent;
import moe.zenbu.torrent.beans.Torrent;
import moe.zenbu.torrent.exceptions.WrapperConnectException;
import moe.zenbu.torrent.exceptions.WrapperUnauthorisedException;
import moe.zenbu.torrent.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class UtorrentWrapper implements ClientWrapper
{
    private static final Logger log = LoggerFactory.getLogger(UtorrentWrapper.class);

    private final String NAME = "utorrent";

    private static String API_URL = "http://127.0.0.1";
    private static int PORT = 8080;
    private static String USERNAME = "";
    private static String PASSWORD = "";

    // RPC endpoints
    private static final String RPC_ENDPOINT = "/gui/";

    // RPC methods
    private static final String RPC_ADD_URL = "add-url";
    private static final String RPC_ADD_FILE = "add-file";
    private static final String RPC_PAUSE = "pause";
    private static final String RPC_RESUME = "unpause";
    private static final String RPC_REMOVE = "remove";
    private static final String RPC_REMOVE_DATA = "removedata";
    private static final String RPC_LIST = "list";

    private String authToken = "";

    private List<Torrent> torrents = new ArrayList<>();

    /**
     * Constructs a client wrapper for Deluge, uses default values for the url
     * and port.
     *
     * @param username Username for the Utorrent web ui
     * @param password Password for the Utorrent web ui
     */
    public UtorrentWrapper(final String username, final String password)
    {
        this(username, password, API_URL, PORT);
    }

    /**
     * Constructs a client wrapper for Utorrent, uses specified url and port.
     *
     * @param username Password for the Utorrent web ui
     * @param password Password for the Utorrent web ui
     * @param url URL for the Utorrent web ui
     * @param port Port for the Utorrent web ui
     */
    public UtorrentWrapper(final String username, final String password, final String url, final int port)
    {
        USERNAME = username;
        PASSWORD = password;
        if(!url.startsWith("http://"))
        {
            API_URL = "http://" + url;
        }
        else
        {
            API_URL = url;
        }
        PORT = port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTorrent(String url) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with url={}", url);

        Map<String, Object> body = new HashMap<>();

        body.put("action", RPC_ADD_URL);
        body.put("s", URLEncoder.encode(url));

        sendRequest(body);
    }

    /**
     * This is not supported by uTorrent API so the wrapper will throw an
     * exception.
     *
     * @param url URL to the torrent file
     * @param downloadPath Canonical path to the desired download directory
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     * @throws UnsupportedOperationException Not supported by uTorrent API
     */
    @Override
    public void addTorrent(String url, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        throw new UnsupportedOperationException("Adding a torrent with a specified path is not supported by the uTorrent API");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTorrent(File file) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with filepath={}", file.getPath());

        Map<String, Object> body = new HashMap<>();

        body.put("action", RPC_ADD_FILE);
        body.put("torrent_file", file);

        sendRequest(body);
    }

    /**
     * This is not supported by uTorrent API so the wrapper will throw an
     * exception.
     *
     * @param file File object of torrent
     * @param downloadPath Canonical path to the desired download directory
     * @throws WrapperConnectException Error with connection to client
     * @throws WrapperUnauthorisedException Unauthorised access to client
     * @throws UnsupportedOperationException Not supported by uTorrent API
     */
    @Override
    public void addTorrent(File file, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        throw new UnsupportedOperationException("Adding a torrent with a specified path is not supported by the uTorrent API");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Pausing torrent name={} id={}", torrent.getName(), torrent.getId());

        Map<String, Object> body = new HashMap<>();

        body.put("action", RPC_PAUSE);
        body.put("hash", (String) torrent.getId());

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Resuming torrent name={} id={}", torrent.getName(), torrent.getId());

        Map<String, Object> body = new HashMap<>();

        body.put("action", RPC_RESUME);
        body.put("hash", (String) torrent.getId());

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTorrent(Torrent torrent, boolean withData) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Removing torrent name={} id={} withData={}", torrent.getName(), torrent.getId(), withData);

        Map<String, Object> body = new HashMap<>();

        if(withData)
        {
            body.put("action", RPC_REMOVE_DATA);
        }
        else
        {
            body.put("action", RPC_REMOVE);
        }
        body.put("hash", (String) torrent.getId());

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Torrent> getTorrents() throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Getting list of torrents");

        Map<String, Object> body = new HashMap<>();

        body.put(RPC_LIST, "1");

        JSONObject root = sendRequest(body);
        JSONArray tors = root.getJSONArray("torrents");

        // Update
        torrents.forEach(t ->
        {
            for(int i = 0; i < tors.length(); i++)
            {
                JSONArray data = tors.getJSONArray(i);
                if(data.get(0).equals(t.getId()))
                {
                    t.setName(data.getString(2));
                    t.setProgress((double) data.getLong(4) / 1000.0);
                    t.setRatio(((double) data.getLong(7) / 1000.0));
                    t.setDownloadDirectory(data.getString(26));
                    t.setSize(data.getLong(3));
                    t.setUploadSpeed((double) data.getLong(8));
                    t.setDownloadSpeed((double) data.getLong(9));
                    t.setUploaded(data.getLong(6));
                    t.setDownloaded(data.getLong(5));
                    t.setEta(data.getLong(10));
                    t.setStatus(this.converStatus(BitSet.valueOf(new byte[]{(byte) data.getLong(1)}), t.getProgress()));
                }
            }
        });
        // Delete
        Iterator<Torrent> it = torrents.iterator();
        it.forEachRemaining(t ->
        {
            boolean found = false;

            for(int i = 0; i < tors.length(); i++)
            {
                JSONArray data = tors.getJSONArray(i);
                if(data.get(0).equals(t.getId()))
                {
                    found = true;
                }
            }

            if(!found)
            {
                torrents.remove(t);
            }
        });
        // Add
        for(int i = 0; i < tors.length(); i++)
        {
            JSONArray data = tors.getJSONArray(i);
            if(!torrents.parallelStream().anyMatch(t -> t.getId().equals(data.get(0))))
            {
                Torrent t = new BasicTorrent(this, data.get(0));

                t.setName(data.getString(2));
                t.setProgress((double) data.getLong(4) / 1000.0);
                t.setRatio(((double) data.getLong(7) / 1000.0));
                t.setDownloadDirectory(data.getString(26));
                t.setSize(data.getLong(3));
                t.setUploadSpeed((double) data.getLong(8));
                t.setDownloadSpeed((double) data.getLong(9));
                t.setUploaded(data.getLong(6));
                t.setDownloaded(data.getLong(5));
                t.setEta(data.getLong(10));
                t.setStatus(this.converStatus(BitSet.valueOf(new byte[]{(byte) data.getLong(1)}), t.getProgress()));

                torrents.add(t);
            }
        }

        return torrents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return NAME;
    }

    protected JSONObject sendRequest(final Map<String, Object> body) throws WrapperConnectException, WrapperUnauthorisedException
    {
        HttpResponse<JsonNode> response = Utils.executeSilently(Unirest.get(buildRpcUrl()).field("token", authToken).fields(body).basicAuth(USERNAME, PASSWORD), true);

        switch(response.getCode())
        {
            case HttpURLConnection.HTTP_OK:
                return response.getBody().getObject();
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new WrapperUnauthorisedException(this);
            default:
                throw new WrapperConnectException(this, response.getBody().toString());
        }
    }

    protected String getAuthToken() throws WrapperConnectException, WrapperUnauthorisedException
    {
        HttpResponse<String> response = Utils.executeSilently(Unirest.get(buildRpcUrl() + "token.html").basicAuth(USERNAME, PASSWORD), false);

        switch(response.getCode())
        {
            case HttpURLConnection.HTTP_OK:
                authToken = response.getBody().replaceAll("\\<.*?>", "");
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new WrapperUnauthorisedException(this);
            default:
                throw new WrapperConnectException(this, response.getBody());
        }

        log.debug("Set auth token to {}", authToken);

        return authToken;
    }

    private String converStatus(final BitSet status, final double progress)
    {
        if(status.get(0))
        {
            if(status.get(5))
            {
                return Torrent.PAUSED;
            }
            else if(progress == 1.0)
            {
                return Torrent.SEEDING;
            }
            else
            {
                return Torrent.DOWNLOADING;
            }
        }
        else if(status.get(1))
        {
            return Torrent.CHECKING;
        }
        else if(status.get(4))
        {
            return Torrent.ERROR;
        }
        else if(status.get(7))
        {
            return Torrent.QUEUED;
        }
        else
        {
            return Torrent.WAITING;
        }
    }

    private String buildRpcUrl()
    {
        return API_URL + ":" + PORT + RPC_ENDPOINT;
    }
}
