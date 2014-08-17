package moe.zenbu.torrent.wrappers;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

public class DelugeWrapper implements ClientWrapper
{
    private static final Logger log = LoggerFactory.getLogger(DelugeWrapper.class);
    
    private final String NAME = "deluge";

    private static String API_URL = "http://127.0.0.1";
    private static int PORT = 8112;
    private static String PASSWORD = "deluge";

    // Endpoints
    private static final String RPC_ENDPOINT = "/json";

    // RPC methods
    private static final String RPC_LOGIN = "auth.login";
    private static final String RPC_ADD_URL = "core.add_torrent_url";
    private static final String RPC_ADD_FILE = "core.add_torrent_file";
    private static final String RPC_PAUSE = "core.pause_torrent";
    private static final String RPC_RESUME = "core.resume_torrent";
    private static final String RPC_REMOVE = "core.remove_torrent";
    private static final String RPC_LIST = "web.update_ui";

    private String sessionId;
    private Date sessionExpiry;

    private static final DateFormat DELUGE_DATA = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

    private List<Torrent> torrents = new ArrayList<>();

    /**
     * Constructs a client wrapper for Deluge, uses default values for the url and port.
     *
     * @param password Password for the Deluge web ui
     */
    public DelugeWrapper(final String password)
    {
        this(password, API_URL, PORT);
    }

    /**
     * Constructs a client wrapper for Deluge, uses specified url and port.
     *
     * @param password Password for the Deluge web ui
     * @param url URL for the Deluge web ui
     * @param port Port for the Deluge web ui
     */
    public DelugeWrapper(final String password, final String url, final int port)
    {
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
    public void addTorrent(String url) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with url={}", url);

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        params.put(url);
        params.put(new JSONObject());
        params.put(new JSONObject());

        body.put("method", RPC_ADD_URL);
        body.put("params", params);

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
        JSONArray params = new JSONArray();
        JSONObject options = new JSONObject();

        options.put("download_location", downloadPath);

        params.put(url);
        params.put(options);
        params.put(new JSONObject());

        body.put("method", RPC_ADD_URL);
        body.put("params", params);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    public void addTorrent(File file) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Adding torrent with filepath={}", file.getPath());

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        params.put(file.getName());
        params.put(Utils.base64EncodeFile(file));
        params.put(new JSONObject());

        body.put("method", RPC_ADD_FILE);
        body.put("params", params);

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
        JSONArray params = new JSONArray();
        JSONObject options = new JSONObject();

        options.put("download_location", downloadPath);

        params.put(file.getName());
        params.put(Utils.base64EncodeFile(file));
        params.put(options);

        body.put("method", RPC_ADD_FILE);
        body.put("params", params);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    public void pauseTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Pausing torrent name={} id={}", torrent.getName(), torrent.getId());

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        params.put((String) torrent.getId());

        body.put("method", RPC_PAUSE);
        body.put("params", params);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    public void resumeTorrent(Torrent torrent) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Resuming torrent name={} id={}", torrent.getName(), torrent.getId());

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        params.put((String) torrent.getId());

        body.put("method", RPC_RESUME);
        body.put("params", params);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTorrent(Torrent torrent, final boolean withData) throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Removing torrent name={} id={} withData={}", torrent.getName(), torrent.getId(), withData);

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        params.put((String) torrent.getId());
        params.put(withData);

        body.put("method", RPC_REMOVE);
        body.put("params", params);

        sendRequest(body);
    }

    /**
     * {@inheritDoc}
     */
    public List<Torrent> getTorrents() throws WrapperConnectException, WrapperUnauthorisedException
    {
        log.info("Getting list of torrents");

        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();
        JSONArray fields = new JSONArray();

        fields.put("name");
        fields.put("progress");
        fields.put("state");
        fields.put("eta");
        fields.put("total_done");
        fields.put("total_uploaded");
        fields.put("total_size");
        fields.put("save_path");
        fields.put("ratio");
        fields.put("download_payload_rate");
        fields.put("upload_payload_rate");

        params.put(fields);
        params.put(new JSONArray());

        body.put("method", RPC_LIST);
        body.put("params", params);

        JSONObject root = sendRequest(body);
        JSONObject tors = root.getJSONObject("result").getJSONObject("torrents");

        // Update
        torrents.parallelStream().filter(t -> tors.has((String) t.getId())).forEach(t ->
        {
            JSONObject data = tors.getJSONObject((String) t.getId());

            t.setName(data.getString("name"));
            t.setProgress(data.getDouble("progress") / 100.0);
            t.setDownloadDirectory(data.getString("save_path"));
            t.setSize(data.getLong("total_size"));
            t.setUploadSpeed((double) data.getLong("upload_payload_rate"));
            t.setDownloadSpeed((double) data.getLong("download_payload_rate"));
            t.setUploaded(data.getLong("total_uploaded"));
            t.setDownloaded(data.getLong("total_done"));
            t.setEta(data.getLong("eta"));
            t.setStatus(convertStatus(data.getString("state")));
            t.setRatio(data.getDouble("ratio"));
        });
        // Delete
        torrents.removeIf(t -> !tors.has((String) t.getId()));
        // Add
        Iterator keys = tors.keys();
        while(keys.hasNext())
        {
            Object key = keys.next();
            JSONObject data = tors.getJSONObject((String) key);

            Torrent t = new BasicTorrent(this, key);
            t.setName(data.getString("name"));
            t.setProgress(data.getDouble("progress") / 100.0);
            t.setDownloadDirectory(data.getString("save_path"));
            t.setSize(data.getLong("total_size"));
            t.setUploadSpeed((double) data.getLong("upload_payload_rate"));
            t.setDownloadSpeed((double) data.getLong("download_payload_rate"));
            t.setUploaded(data.getLong("total_uploaded"));
            t.setDownloaded(data.getLong("total_done"));
            t.setEta(data.getLong("eta"));
            t.setStatus(convertStatus(data.getString("state")));
            t.setRatio(data.getDouble("ratio"));

            torrents.add(t);
        };

        return torrents;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NAME;
    }

    protected JSONObject sendRequest(final JSONObject body) throws WrapperConnectException, WrapperUnauthorisedException
    {
        if(!isSessionValid())
        {
            getSessionId();
        }

        int requestId = (int) Math.round(Math.random() * 100000000);
        
        body.put("id", requestId);

        log.debug("Sending request to {} with body {}", NAME, body.toString());

        HttpResponse<JsonNode> response = Utils.executeSilently(Unirest.post(buildRpcUrl()).header("Cookie", "_session_id=" + sessionId).body(body.toString()));

        log.debug("{} replied with body {} and headers {}", NAME, response.getBody().toString(), response.getHeaders());

        int responseId = response.getBody().getObject().getInt("id");

        if(responseId != requestId)
        {
            throw new WrapperConnectException(this, "Invalid response id, expected= " + requestId + " received=" + responseId);
        }
        if(response.getBody().getObject().get("error") != JSONObject.NULL)
        {
            JSONObject error = response.getBody().getObject().getJSONObject("error");

            throw new WrapperConnectException(this, "Returned unexpected response '" + error.getString("message") + "'");
        }

        return response.getBody().getObject();
    }

    protected String getSessionId() throws WrapperUnauthorisedException, WrapperConnectException
    {
        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        int requestId = (int) Math.round(Math.random() * 100000000);

        params.put(PASSWORD);

        body.put("method", RPC_LOGIN);
        body.put("params", params);
        body.put("id", requestId);

        HttpResponse<JsonNode> response = Utils.executeSilently(Unirest.post(buildRpcUrl()).body(body.toString()));

        log.debug("{} replied with body {} and headers {}", NAME, response.getBody().toString(), response.getHeaders());

        if(response.getBody().getObject().getInt("id") != requestId)
        {
            throw new WrapperConnectException(this, "Invalid response id, expected= " + requestId + " received=" + response.getBody().getObject().getInt("id"));
        }

        if(response.getBody().getObject().getBoolean("result") == false)
        {
            throw new WrapperUnauthorisedException(this);
        }
        else
        {
            List<String> cookies = response.getHeaders().get("set-cookie");
            String sessionCookie = cookies.stream().filter(s -> s.startsWith("_session_id")).findFirst().get();

            sessionId = sessionCookie.split("=")[1];
            sessionId = sessionId.substring(0, sessionId.indexOf(";"));

            String expiryCookie = sessionCookie.split("Expires=")[1];
            expiryCookie = expiryCookie.substring(0, expiryCookie.indexOf(";"));

            try
            {
                sessionExpiry = DELUGE_DATA.parse(expiryCookie);
            }
            catch(ParseException e)
            {
                throw new WrapperConnectException(this, e);
            }
        }

        log.debug("Setting session id={} and session expiry={}", sessionId, sessionExpiry);

        return sessionId;
    }

    private boolean isSessionValid()
    {
        return sessionId != null && sessionExpiry != null && new Date().before(sessionExpiry);
    }

    private String convertStatus(final String status)
    {
        switch(status)
        {
            case "Paused":
                    return Torrent.PAUSED;
            case "Seeding":
                    return Torrent.SEEDING;
            case "Downloading":
            case "Active":
                    return Torrent.DOWNLOADING; 
            case "Checking":
                    return Torrent.CHECKING; 
            case "Queued":
                    return Torrent.CHECKING;
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
        return "DelugeWrapper [NAME=" + NAME + "]";
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
        DelugeWrapper other = (DelugeWrapper) obj;
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
