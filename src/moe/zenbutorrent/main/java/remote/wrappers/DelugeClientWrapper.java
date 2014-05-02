package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import moe.zenbutorrent.main.java.logging.Log;
import moe.zenbutorrent.main.java.remote.exceptions.RemoteTorrentConnectionException;
import moe.zenbutorrent.main.java.remote.exceptions.RemoteTorrentUnauthorizedException;
import moe.zenbutorrent.main.java.remote.torrent.DefaultRemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.file.RemoteTorrentFile;

import org.json.simple.JSONValue;

public class DelugeClientWrapper implements ClientWrapper
{
    private String API_URL = "http://127.0.0.1"; // Default address
    private int PORT = 8112; // Default port
    private String RPC = "/json";
    private String UPLOAD = "/upload";

    private String password = null;
    private String sessionId = null;

    public DelugeClientWrapper()
    {
        // Use default address and port
    }

    public DelugeClientWrapper(String password)
    {
        this.password = password;
    }

    public DelugeClientWrapper(String password, String url, int port)
    {
        this.password = password;
        this.API_URL = "http://" + url;
        this.PORT = port;
    }

    // Implemented methods
    public void addTorrent(String url) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        HashMap options = new HashMap();
        HashMap headers = new HashMap();

        params.add(url);
        params.add(options);
        params.add(headers);

        data.put("method", "core.add_torrent_url");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    public void addTorrent(File file) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        HashMap options = new HashMap();

        params.add(file.getName());
        params.add(base64EncodeFile(file));
        params.add(options);

        data.put("method", "core.add_torrent_file");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    public void addTorrent(String url, String path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        HashMap options = new HashMap();
        HashMap headers = new HashMap();

        options.put("download_location", path);

        params.add(url);
        params.add(options);
        params.add(headers);

        data.put("method", "core.add_torrent_url");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    public void addTorrent(File file, String path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        HashMap options = new HashMap();

        options.put("download_location", path);

        params.add(file.getName());
        params.add(base64EncodeFile(file));
        params.add(options);

        data.put("method", "core.add_torrent_file");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    public void addTorrent(File file, File path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        HashMap options = new HashMap();

        try
        {
            options.put("download_location", file.getCanonicalPath());
        }
        catch(IOException e)
        {
            throw new RemoteTorrentConnectionException(e);
        }

        params.add(file.getName());
        params.add(base64EncodeFile(file));
        params.add(options);

        data.put("method", "core.add_torrent_file");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    @Override
    public void pauseTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();

        params.add(remoteTorrent.getStringId());

        data.put("method", "core.pause_torrent");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    @Override
    public void resumeTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();

        params.add(remoteTorrent.getStringId());

        data.put("method", "core.resume_torrent");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    @Override
    public void deleteTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();

        params.add(remoteTorrent.getStringId());
        params.add(false);

        data.put("method", "core.remove_torrent");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    @Override
    public void deleteTorrentAndData(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();

        params.add(remoteTorrent.getStringId());
        params.add(true);

        data.put("method", "core.remove_torrent");
        data.put("params", params);
        data.put("id", 2);

        sendRequest(data);
    }

    @Override
    public ArrayList<RemoteTorrent> getAllTorrents() throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        ArrayList<RemoteTorrent> returned = new ArrayList<>();

        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        ArrayList fields = new ArrayList();

        fields.add("name");
        fields.add("progress");
        fields.add("state");
        fields.add("eta");
        fields.add("total_done");
        fields.add("total_uploaded");
        fields.add("total_size");
        fields.add("save_path");
        fields.add("ratio");
        fields.add("download_payload_rate");
        fields.add("upload_payload_rate");
        
        params.add(fields);
        params.add(new ArrayList());

        data.put("method", "web.update_ui");
        data.put("params", params);
        data.put("id", 2);

        HashMap root = (HashMap) JSONValue.parse(sendRequest(data));
        HashMap torrents = (HashMap) ((HashMap) root.get("result")).get("torrents");
        for(Object key : torrents.keySet().toArray())
        {
            String id = (String) key;

            HashMap info = (HashMap) torrents.get(key);
            String title = (String) info.get("name");
            String downloadDir = (String) info.get("save_path");
            long size = (long) info.get("total_size");
            long uploadSpeed = (long) info.get("upload_payload_rate");
            long downloadSpeed = (long) info.get("download_payload_rate");
            double ratio = (double) info.get("ratio");
            double progress = (double) info.get("progress") / 100.0;
            long downloaded = (long)  info.get("total_done");
            long uploaded = (long) info.get("total_uploaded");
            long eta = (long) info.get("eta");
            String status = (String) info.get("state");

            String remoteTorrentStatus;
            switch(status)
            {
                case "Paused":
                        remoteTorrentStatus = RemoteTorrent.PAUSED;
                        break;
                case "Seeding":
                        remoteTorrentStatus = RemoteTorrent.SEEDING;
                        break;
                case "Downloading":
                case "Active":
                        remoteTorrentStatus = RemoteTorrent.DOWNLOADING; 
                        break;
                case "Checking":
                        remoteTorrentStatus = RemoteTorrent.CHECKING; 
                        break;
                case "Queued":
                        remoteTorrentStatus = RemoteTorrent.CHECKING;
                        break;
                default:
                        remoteTorrentStatus = RemoteTorrent.UNKOWN;
                        break;
            }

            DefaultRemoteTorrent rt = new DefaultRemoteTorrent(id, title, downloadDir, size);
            rt.setProgress(progress);
            rt.setDownloaded(downloaded);
            rt.setUploaded(uploaded);
            rt.setDownloadSpeed(downloadSpeed);
            rt.setUploadSpeed(uploadSpeed);
            rt.setEta(eta);
            rt.setRatio(ratio);
            rt.setStatus(remoteTorrentStatus);

            returned.add(rt);
        }

        return returned;
    }

    @Override
    public void updateAllTorrents(List<RemoteTorrent> updateList, Class<? extends RemoteTorrent> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        ArrayList fields = new ArrayList();

        ArrayList<RemoteTorrent> temp = new ArrayList<>(updateList);

        fields.add("name");
        fields.add("progress");
        fields.add("state");
        fields.add("eta");
        fields.add("total_done");
        fields.add("total_uploaded");
        fields.add("total_size");
        fields.add("save_path");
        fields.add("ratio");
        fields.add("download_payload_rate");
        fields.add("upload_payload_rate");
        
        params.add(fields);
        params.add(new ArrayList());

        data.put("method", "web.update_ui");
        data.put("params", params);
        data.put("id", 2);

        HashMap root = (HashMap) JSONValue.parse(sendRequest(data));
        HashMap torrents = (HashMap) ((HashMap) root.get("result")).get("torrents");
        for(Object key : torrents.keySet().toArray())
        {
            String id = (String) key;

            HashMap info = (HashMap) torrents.get(key);
            String title = (String) info.get("name");
            String downloadDir = (String) info.get("save_path");
            long size = (long) info.get("total_size");
            long uploadSpeed = (long) info.get("upload_payload_rate");
            long downloadSpeed = (long) info.get("download_payload_rate");
            double ratio = (double) info.get("ratio");
            double progress = (double) info.get("progress") / 100.0;
            long downloaded = (long)  info.get("total_done");
            long uploaded = (long) info.get("total_uploaded");
            long eta = (long) info.get("eta");
            String status = (String) info.get("state");

            String remoteTorrentStatus;
            switch(status)
            {
                case "Paused":
                        remoteTorrentStatus = RemoteTorrent.PAUSED;
                        break;
                case "Seeding":
                        remoteTorrentStatus = RemoteTorrent.SEEDING;
                        break;
                case "Downloading":
                case "Active":
                        remoteTorrentStatus = RemoteTorrent.DOWNLOADING; 
                        break;
                case "Checking":
                        remoteTorrentStatus = RemoteTorrent.CHECKING; 
                        break;
                case "Queued":
                        remoteTorrentStatus = RemoteTorrent.CHECKING;
                        break;
                default:
                        remoteTorrentStatus = RemoteTorrent.UNKOWN;
                        break;
            }

            boolean exists = false;

            for(RemoteTorrent rt : updateList)
            {
                if(rt.getStringId().equals(id))
                {
                    rt.setProgress(progress);
                    rt.setDownloaded(downloaded);
                    rt.setUploaded(uploaded);
                    rt.setDownloadSpeed(downloadSpeed);
                    rt.setUploadSpeed(uploadSpeed);
                    rt.setEta(eta);
                    rt.setRatio(ratio);
                    rt.setStatus(remoteTorrentStatus);

                    temp.remove(rt);

                    exists = true;
                }
            }

            if(!exists)
            {
                RemoteTorrent rt = null;

                try
                {
                    rt = c.newInstance();
                }
                catch(InstantiationException e)
                {
                    Log.error("Cannot instantiate RemoteTorrent class that was passed", e);
                }
                catch(IllegalAccessException x)
                {
                    Log.error("No access to RemoteTorrent class that was passed", x);
                }

                rt.setTitle(title);
                rt.setStringId(id);
                rt.setDownloadDirectory(downloadDir);
                rt.setSize(size);

                rt.setProgress(progress);
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio(ratio);
                rt.setStatus(remoteTorrentStatus);

                updateList.add(rt);
            }
        }

        for(RemoteTorrent rt : temp)
        {
            updateList.remove(rt);
        }
    }

    @Override
    public void updateAllTorrents(List<RemoteTorrent> updateList, Class<? extends RemoteTorrent> c, List<String> titles) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap data = new HashMap();
        ArrayList params = new ArrayList();
        ArrayList fields = new ArrayList();

        ArrayList<RemoteTorrent> temp = new ArrayList<>(updateList);

        fields.add("name");
        fields.add("progress");
        fields.add("state");
        fields.add("eta");
        fields.add("total_done");
        fields.add("total_uploaded");
        fields.add("total_size");
        fields.add("save_path");
        fields.add("ratio");
        fields.add("download_payload_rate");
        fields.add("upload_payload_rate");
        
        params.add(fields);
        params.add(new ArrayList());

        data.put("method", "web.update_ui");
        data.put("params", params);
        data.put("id", 2);

        HashMap root = (HashMap) JSONValue.parse(sendRequest(data));
        HashMap torrents = (HashMap) ((HashMap) root.get("result")).get("torrents");
        if(torrents == null)
        {
            return;
        }
        for(Object key : torrents.keySet().toArray())
        {
            String id = (String) key;

            HashMap info = (HashMap) torrents.get(key);
            String title = (String) info.get("name");
            String downloadDir = (String) info.get("save_path");
            long size = (long) info.get("total_size");
            long uploadSpeed = (long) info.get("upload_payload_rate");
            long downloadSpeed = (long) info.get("download_payload_rate");
            double ratio = (double) info.get("ratio");
            double progress = (double) info.get("progress") / 100.0;
            long downloaded = (long)  info.get("total_done");
            long uploaded = (long) info.get("total_uploaded");
            long eta = (long) info.get("eta");
            String status = (String) info.get("state");

            String remoteTorrentStatus;
            switch(status)
            {
                case "Paused":
                        remoteTorrentStatus = RemoteTorrent.PAUSED;
                        break;
                case "Seeding":
                        remoteTorrentStatus = RemoteTorrent.SEEDING;
                        break;
                case "Downloading":
                case "Active":
                        remoteTorrentStatus = RemoteTorrent.DOWNLOADING; 
                        break;
                case "Checking":
                        remoteTorrentStatus = RemoteTorrent.CHECKING; 
                        break;
                case "Queued":
                        remoteTorrentStatus = RemoteTorrent.CHECKING;
                        break;
                default:
                        remoteTorrentStatus = RemoteTorrent.UNKOWN;
                        break;
            }

            boolean exists = false;

            for(RemoteTorrent rt : updateList)
            {
                if(rt.getStringId().equals(id))
                {
                    rt.setProgress(progress);
                    rt.setDownloaded(downloaded);
                    rt.setUploaded(uploaded);
                    rt.setDownloadSpeed(downloadSpeed);
                    rt.setUploadSpeed(uploadSpeed);
                    rt.setEta(eta);
                    rt.setRatio(ratio);
                    rt.setStatus(remoteTorrentStatus);

                    temp.remove(rt);

                    exists = true;
                }
            }

            if(!exists && titles.contains(title))
            {
                RemoteTorrent rt = null;

                try
                {
                    rt = c.newInstance();
                }
                catch(InstantiationException e)
                {
                    Log.error("Cannot instantiate RemoteTorrent class that was passed", e);
                }
                catch(IllegalAccessException x)
                {
                    Log.error("No access to RemoteTorrent class that was passed", x);
                }

                rt.setTitle(title);
                rt.setStringId(id);
                rt.setDownloadDirectory(downloadDir);
                rt.setSize(size);

                rt.setProgress(progress);
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio(ratio);
                rt.setStatus(remoteTorrentStatus);

                updateList.add(rt);
            }
        }

        for(RemoteTorrent rt : temp)
        {
            updateList.remove(rt);
        }
    }

    @Override
    public List<? extends RemoteTorrentFile> getFilesForTorrent(RemoteTorrent remoteTorrent, Class<? extends RemoteTorrentFile> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        //TODO:
        return null;
    }

    public String getName()
    {
        return "Deluge";
    }

    // Class specific methods
    private HttpURLConnection buildURLConnetion()
    {
        URL apiUrl;
        HttpURLConnection conn = null;

        try
        {
            apiUrl = new URL(API_URL + ":" + PORT + RPC);

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return conn;
    }

    private String base64EncodeFile(File file)
    {
        String encoded = null;

        try
        {
            encoded = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(file.getCanonicalPath())));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return encoded;
    }        

    private String sendRequest(HashMap data) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        URL apiUrl;
        HttpURLConnection conn = null;
        String returned = null;

        int responseCode = -1;
        BufferedReader in;
        String line;
        StringBuilder response = new StringBuilder();
        DataOutputStream dos;

        try
        {
            if(sessionId == null)
            {
                HashMap auth;
                ArrayList params;

                auth = new HashMap();
                params = new ArrayList();

                params.add(password);

                auth.put("method", "auth.login");
                auth.put("params", params);
                auth.put("id", 1);

                conn = buildURLConnetion();

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(JSONValue.toJSONString(auth));
                dos.flush();

                for(String s : conn.getHeaderFields().get("Set-Cookie"))
                {
                    if(s.contains("_session_id"))
                    {
                        sessionId = s.split("=")[1];
                        sessionId = sessionId.substring(0, sessionId.indexOf(";"));
                        break;
                    }
                }
            }

            conn = buildURLConnetion();
            conn.setRequestProperty("Cookie", "_session_id=" + sessionId);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(JSONValue.toJSONString(data));
            dos.flush();
            dos.close();

            responseCode = conn.getResponseCode();
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));

            while((line = in.readLine()) != null)
            {
                response.append(line);
            }

            in.close();
            conn.disconnect();

            switch(responseCode)
            {
                case 200:
                case 201:
                case 202:
                    returned = response.toString();
                    break;
                case 401:
                    Log.error("Unauthorized to acess Deluge");
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting Deluge: " + response.toString());
                    throw new RemoteTorrentConnectionException();
            }
        }
        catch(RemoteTorrentConnectionException | RemoteTorrentUnauthorizedException x)
        {
            throw x;
        }
        catch(Exception e)
        {
            switch(responseCode)
            {
                case 401:
                    Log.error("Unauthorized to access Transmission");
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting Transmission: " + response.toString());
                    throw new RemoteTorrentConnectionException(e);
            }
        }

        return returned;
    }
}
