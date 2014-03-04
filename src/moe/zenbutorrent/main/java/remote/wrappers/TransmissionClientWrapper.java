package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import moe.zenbutorrent.main.java.logging.Log;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrentStatus;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TransmissionClientWrapper implements ClientWrapper
{
    //TODO: Complete
    private String API_URL = "http://127.0.0.1:9091/transmission/rpc";

    private String authToken = null;
    private String basicAuth;
    
    public TransmissionClientWrapper()
    {
        //No auth
    }

    public TransmissionClientWrapper(int port)
    {
        API_URL = "http://127.0.0.1:" + port + "/transmission/rpc";
    }

    public TransmissionClientWrapper(String username, String password)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }

    public TransmissionClientWrapper(String username, String password, int port)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

        API_URL = "http://127.0.0.1:" + port + "/transmission/rpc";
    }

    //Implemented methods
    @Override
    public void addTorrent(String url)
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        try
        {
            arguments.put("filename", url);
        }
        catch(Exception e)
        {
            Log.error("Could not get filepath to torrent file", e);
        }

        data.put("arguments", arguments);
        data.put("method", "torrent-add");

        sendRequest(data);
    };

    @Override
    public void addTorrent(File file)
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        try
        {
            arguments.put("filename", file.getCanonicalPath());
        }
        catch(Exception e)
        {
            Log.error("Could not get filepath to torrent file", e);
        }

        data.put("arguments", arguments);
        data.put("method", "torrent-add");

        sendRequest(data);
    };

    @Override
    public void pauseTorrent(RemoteTorrent remoteTorrent)
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getIntId());

        data.put("arguments", arguments);
        data.put("method", "torrent-stop");

        sendRequest(data);
    };

    @Override
    public void resumeTorrent(RemoteTorrent remoteTorrent)
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getIntId());

        data.put("arguments", arguments);
        data.put("method", "torrent-start");

        sendRequest(data);
    };

    @Override
    public ArrayList<RemoteTorrent> getAllTorrents()
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();
        ArrayList<String> fields = new ArrayList<>();
        HashMap root;
        ArrayList<HashMap> torrents;

        ArrayList<RemoteTorrent> returned = new ArrayList<>();

        fields.add("id");
        fields.add("status");
        fields.add("name");
        fields.add("percentDone");
        fields.add("downloadedEver");
        fields.add("uploadedEver");
        fields.add("uploadRatio");
        fields.add("sizeWhenDone");
        fields.add("leftUntilDone");
        fields.add("rateDownload");
        fields.add("rateUpload");
        fields.add("eta");
        fields.add("downloadDir");

        arguments.put("fields", fields);

        data.put("arguments", arguments);
        data.put("method", "torrent-get");

        root = (HashMap) JSONValue.parse(sendRequest(data));
        torrents = (ArrayList<HashMap>) ((HashMap) root.get("arguments")).get("torrents");

        for(HashMap hm : torrents)
        {
            long id = (long) hm.get("id");
            long status = (long) hm.get("status");
            String title = (String) hm.get("name");
            String filepath = (String) hm.get("downloadDir");
            long size = (long) hm.get("sizeWhenDone");
            Number progress = (Number) hm.get("percentDone");
            long downloaded = (long) hm.get("downloadedEver");
            long uploaded = (long) hm.get("uploadedEver");
            Number ratio = (Number) hm.get("uploadRatio");
            long uploadSpeed = (long) hm.get("rateUpload");
            long downloadSpeed = (long) hm.get("rateDownload");
            long eta = (long) hm.get("eta");
            long remaining = (long) hm.get("leftUntilDone");

            RemoteTorrentStatus remoteTorrentStatus = null;
            switch((int) status)
            {
                case 0:
                        remoteTorrentStatus = RemoteTorrentStatus.PAUSED;
                        break;
                case 1:
                        remoteTorrentStatus = RemoteTorrentStatus.WAITING;
                        break;
                case 2:
                        remoteTorrentStatus = RemoteTorrentStatus.CHECKING;
                        break;
                case 3:
                        remoteTorrentStatus = RemoteTorrentStatus.QUEUED;
                        break;
                case 4:
                        remoteTorrentStatus = RemoteTorrentStatus.DOWNLOADING;
                        break;
                case 5:
                        remoteTorrentStatus = RemoteTorrentStatus.QUEUED;
                        break;
                case 6:
                        remoteTorrentStatus = RemoteTorrentStatus.SEEDING;
                        break;
                default:
                        remoteTorrentStatus = RemoteTorrentStatus.UNKOWN;
                        break;
            }

            RemoteTorrent rt = new RemoteTorrent((int) id, title, filepath, size);
            rt.setProgress(progress.doubleValue()); //Convert to promille
            rt.setDownloaded(downloaded);
            rt.setUploaded(uploaded);
            rt.setDownloadSpeed(downloadSpeed);
            rt.setUploadSpeed(uploadSpeed);
            rt.setEta(eta);
            rt.setRatio(ratio.doubleValue()); //Convert to promille
            rt.setRemaining(remaining);
            rt.setStatus(remoteTorrentStatus);

            returned.add(rt);
            //Log.debug("Torrent: " + rt.getTitle() + " : " + rt.getStatus());
        }

        return returned;
    };

    //Class specific methods
    private String sendRequest(JSONObject data)
    {
        URL apiUrl;
        HttpURLConnection conn = null;
        String returned = null;

        int responseCode;
        BufferedReader in;
        String line;
        StringBuilder response = new StringBuilder();

        DataOutputStream dos;

        try
        {
            getAuthToken();

            apiUrl = new URL("http://127.0.0.1:9090/transmission/rpc");

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("X-Transmission-Session-Id", authToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setDoOutput(true);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(JSONValue.toJSONString(data));
            dos.flush();
            dos.close();

            responseCode = conn.getResponseCode();
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while((line = in.readLine()) != null)
            {
                response.append(line);
            }
            in.close();

            switch(responseCode)
            {
                case 200:
                case 201:
                case 202:
                    returned = response.toString();
                    break;
                case 401:
                    Log.error("Unauthorized to acess Transmission");
                    break;
                default:
                    Log.error("Error contacting Transmission: " + response.toString());
                    break;
            }
        }
        catch(Exception e)
        {
            Log.error("Could not send request to Transmission", e);
            return null;
        }

        return returned;
    }

    private void getAuthToken()
    {
        URL apiUrl;
        HttpURLConnection conn = null;

        try
        {
            apiUrl = new URL("http://127.0.0.1:9090/transmission/rpc");

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream();
        }
        catch(IOException e)
        {
            try
            {
                if(conn.getResponseCode() == 409)
                {
                    authToken = conn.getHeaderField("X-Transmission-Session-Id");
                    Log.debug("Got auth token: " + authToken + " from Transmission");
                }
                else
                {
                    Log.error("Could not get Transmission auth token", e);
                }
            }
            catch(Exception x)
            {
                Log.error("Could not get Transmission auth token", x);
            }
        }
    }        
}
