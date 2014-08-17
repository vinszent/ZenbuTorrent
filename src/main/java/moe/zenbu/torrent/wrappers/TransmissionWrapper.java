package moe.zenbu.torrent.wrappers;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

public class TransmissionWrapper implements ClientWrapper
{
    private static final Logger log = LoggerFactory.getLogger(TransmissionWrapper.class);

    private final String NAME = "transmission";

    private static String API_URL = "http://127.0.0.1";
    private static int PORT = 9090;
    private static String USERNAME = "";
    private static String PASSWORD = "";

    private String authToken = "";

    // Endpoints
    private static final String RPC_ENDPOINT = "/transmission/rpc";

    // RPC methods
    private static final String RPC_ADD = "torrent-add";
    private static final String RPC_PAUSE = "torrent-stop";
    private static final String RPC_RESUME = "torrent-resume";
    private static final String RPC_REMOVE = "torrent-remove";
    private static final String RPC_LIST = "torrent-get";

    private List<Torrent> torrents = new ArrayList<>();

    /**
     * Constructs a client wrapper for Deluge, uses default values for the url and port.
     *
     * @param username Username for the Transmission web ui
     * @param password Password for the Transmission web ui
     */
    public TransmissionWrapper(final String username, final String password)
    {
        this(username, password, API_URL, PORT);
    }

    /**
     * Constructs a client wrapper for Transmission, uses specified url and port.
     *
     * @param username Password for the Transmission web ui
     * @param password Password for the Transmission web ui
     * @param url URL for the Transmission web ui
     * @param port Port for the Transmission web ui
     */
    public TransmissionWrapper(final String username, final String password, final String url, final int port)
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

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("filename", url);

        body.put("arguments", params);
        body.put("method", RPC_ADD);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTorrent(String url, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with url={} and download path={}", url, downloadPath);

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("filename", url);
        params.put("download-dir", downloadPath);

        body.put("arguments", params);
        body.put("method", RPC_ADD);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTorrent(File file) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with filepath={}", file.getPath());

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        try
        {
            params.put("filename", file.getCanonicalPath());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        body.put("arguments", params);
        body.put("method", RPC_ADD);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTorrent(File file, String downloadPath) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with filepath={} and download path={}", file.getPath(), downloadPath);

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        try
        {
            params.put("filename", file.getCanonicalPath());
            params.put("download-dir", downloadPath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        body.put("arguments", params);
        body.put("method", RPC_ADD);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Pausing torrent name={} id={}", torrent.getName(), torrent.getId());

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("id", (int) torrent.getId());

        body.put("arguments", params);
        body.put("method", RPC_PAUSE);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Resuming torrent name={} id={}", torrent.getName(), torrent.getId());

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("id", (int) torrent.getId());

        body.put("arguments", params);
        body.put("method", RPC_RESUME);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTorrent(Torrent torrent, boolean withData) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Removing torrent name={} id={} withData={}", torrent.getName(), torrent.getId(), withData);

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("id", (int) torrent.getId());
        params.put("delete-local-body", withData);

        body.put("arguments", params);
        body.put("method", RPC_REMOVE);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Torrent> getTorrents() throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Getting list of torrents");

        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();
        JSONArray fields = new JSONArray();

        fields.put("id");
        fields.put("status");
        fields.put("name");
        fields.put("percentDone");
        fields.put("downloadedEver");
        fields.put("uploadedEver");
        fields.put("uploadRatio");
        fields.put("sizeWhenDone");
        fields.put("rateDownload");
        fields.put("rateUpload");
        fields.put("eta");
        fields.put("downloadDir");

        params.put("fields", fields);

        body.put("arguments", params);
        body.put("method", RPC_LIST);

        JSONObject root = sendRequest(body);
        JSONArray tors = root.getJSONObject("arguments").getJSONArray("torrents");

        // Update
        torrents.forEach(t ->
        {
            for(int i = 0; i < tors.length(); i++)
            {
                JSONObject data = tors.getJSONObject(i);
                if(data.get("id").equals(t.getId()))
                {
                    t.setName(data.getString("name"));
                    t.setProgress(data.getDouble("percentDone"));
                    t.setDownloadDirectory(data.getString("downloadDir"));
                    t.setSize(data.getLong("sizeWhenDone"));
                    t.setUploadSpeed((double) data.getLong("rateUpload"));
                    t.setDownloadSpeed((double) data.getLong("rateDownload"));
                    t.setUploaded(data.getLong("uploadedEver"));
                    t.setDownloaded(data.getLong("downloadedEver"));
                    t.setEta(data.getLong("eta"));
                    t.setStatus(convertStatus(data.getInt("status")));
                    t.setRatio(data.getDouble("uploadRatio"));
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
                JSONObject data = tors.getJSONObject(i);
                if(data.get("id").equals(t.getId()))
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
            JSONObject data = tors.getJSONObject(i);
            if(!torrents.parallelStream().anyMatch(t -> t.getId().equals(data.getLong("id"))))
            {
                Torrent t = new BasicTorrent(this, data.getLong("id"));

                t.setName(data.getString("name"));
                t.setProgress(data.getDouble("percentDone"));
                t.setDownloadDirectory(data.getString("downloadDir"));
                t.setSize(data.getLong("sizeWhenDone"));
                t.setUploadSpeed((double) data.getLong("rateUpload"));
                t.setDownloadSpeed((double) data.getLong("rateDownload"));
                t.setUploaded(data.getLong("uploadedEver"));
                t.setDownloaded(data.getLong("downloadedEver"));
                t.setEta(data.getLong("eta"));
                t.setStatus(convertStatus(data.getInt("status")));
                t.setRatio(data.getDouble("uploadRatio"));

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

    protected JSONObject sendRequest(final JSONObject body) throws WrapperConnectException, WrapperUnauthorisedException
    {
        if(authToken == null)
        {
            getAuthToken();
        }

        log.info("Sending request to {} with body {}", NAME, body.toString());

        HttpResponse<JsonNode> response = Utils.executeSilently(Unirest.post(buildRpcUrl()).basicAuth(USERNAME, PASSWORD).basicAuth(USERNAME, PASSWORD).header("X-Transmission-Session-Id", authToken).body(body.toString()));

        log.info("{} replied with body {} and headers {}", NAME, response.getBody().toString(), response.getHeaders());

        switch(response.getCode())
        {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new WrapperUnauthorisedException(this);
            case HttpURLConnection.HTTP_CONFLICT:
                authToken = response.getHeaders().get("x-transmission-session-id").get(0);
                break;
            case HttpURLConnection.HTTP_OK:
                return response.getBody().getObject();
            default:
                throw new WrapperConnectException(this, "Unkown error code=" + response.getCode() + " body=" + response.getBody() + " headers=" + response.getHeaders());
        }

        return response.getBody().getObject();
    }

    protected String getAuthToken() throws WrapperConnectException, WrapperUnauthorisedException
    {
        HttpResponse<String> response = Utils.executeSilently(Unirest.get(buildRpcUrl()).basicAuth(USERNAME, PASSWORD), false);

        log.info("{} replied with body {} and headers {}", NAME, response.getBody().toString(), response.getHeaders());

        switch(response.getCode())
        {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new WrapperUnauthorisedException(this);
            case HttpURLConnection.HTTP_CONFLICT:
                authToken = response.getHeaders().get("x-transmission-session-id").get(0);
                break;
            default:
                throw new WrapperConnectException(this, "Unkown error");
        }

        log.info("Setting auth token={}", authToken);

        return authToken;
    }

    private String convertStatus(final int status)
    {
        switch(status)
        {
            case 0:
                    return Torrent.PAUSED;
            case 1:
                    return Torrent.WAITING;
            case 2:
                    return Torrent.CHECKING;
            case 3:
                    return Torrent.QUEUED;
            case 4:
                    return Torrent.DOWNLOADING;
            case 5:
                    return Torrent.QUEUED;
            case 6:
                    return Torrent.SEEDING;
            default:
                    return Torrent.UNKOWN;
        }
    }

    private String buildRpcUrl()
    {
        return API_URL + ":" + PORT + RPC_ENDPOINT;
    }

    @Override
    public String toString()
    {
        return "TransmissionWrapper [NAME=" + NAME + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((NAME == null) ? 0 : NAME.hashCode());
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
        TransmissionWrapper other = (TransmissionWrapper) obj;
        if(NAME == null)
        {
            if(other.NAME != null)
                return false;
        }
        else if(!NAME.equals(other.NAME))
            return false;
        return true;
    }
}
