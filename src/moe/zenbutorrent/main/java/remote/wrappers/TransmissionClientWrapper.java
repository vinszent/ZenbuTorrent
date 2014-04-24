package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import moe.zenbutorrent.main.java.logging.Log;
import moe.zenbutorrent.main.java.remote.exceptions.RemoteTorrentConnectionException;
import moe.zenbutorrent.main.java.remote.exceptions.RemoteTorrentUnauthorizedException;
import moe.zenbutorrent.main.java.remote.torrent.DefaultRemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrentStatus;
import moe.zenbutorrent.main.java.remote.torrent.file.RemoteTorrentFile;

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
    public void addTorrent(String url) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
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
    public void addTorrent(File file) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
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
    public void pauseTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getNumberId());

        data.put("arguments", arguments);
        data.put("method", "torrent-stop");

        sendRequest(data);
    };

    @Override
    public void resumeTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getNumberId());

        data.put("arguments", arguments);
        data.put("method", "torrent-start");

        sendRequest(data);
    };

    @Override
    public void deleteTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getNumberId());

        data.put("arguments", arguments);
        data.put("method", "torrent-remove");

        sendRequest(data);
    }        

    @Override
    public void deleteTorrentAndData(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();

        arguments.put("id", remoteTorrent.getNumberId());
        arguments.put("delete-local-data", true);

        data.put("arguments", arguments);
        data.put("method", "torrent-remove");

        sendRequest(data);
    }        

    @Override
    public List<DefaultRemoteTorrent> getAllTorrents() throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();
        ArrayList<String> fields = new ArrayList<>();
        HashMap root;
        ArrayList<HashMap> torrents;

        ArrayList<DefaultRemoteTorrent> returned = new ArrayList<>();

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

            DefaultRemoteTorrent rt = new DefaultRemoteTorrent((int) id, title, filepath, size);
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
    }

    @Override
    public void updateAllTorrents(List<RemoteTorrent> userList, Class<? extends RemoteTorrent> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();
        ArrayList<String> fields = new ArrayList<>();
        HashMap root;
        ArrayList<HashMap> torrents;

        ArrayList<RemoteTorrent> temp = new ArrayList<>(userList);

        boolean exists = false;

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

            exists = false;

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

            for(RemoteTorrent rt : userList)
            {
                if(rt.getNumberId().longValue() == id)
                {
                    rt.setProgress(progress.doubleValue());
                    rt.setDownloaded(downloaded);
                    rt.setUploaded(uploaded);
                    rt.setDownloadSpeed(downloadSpeed);
                    rt.setUploadSpeed(uploadSpeed);
                    rt.setEta(eta);
                    rt.setRatio(ratio.doubleValue());
                    rt.setRemaining(remaining);
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
                rt.setNumberId(id);
                rt.setFilepath(filepath);
                rt.setSize(size);

                rt.setProgress(progress.doubleValue());
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio(ratio.doubleValue());
                rt.setRemaining(remaining);
                rt.setStatus(remoteTorrentStatus);

                userList.add(rt);
            }
        }

        for(RemoteTorrent rt : temp)
        {
            userList.remove(rt);
        }
    };

    @Override
    public void updateAllTorrents(List<RemoteTorrent> userList, Class<? extends RemoteTorrent> c, List<String> titles) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();
        ArrayList<String> fields = new ArrayList<>();
        HashMap root;
        ArrayList<HashMap> torrents;

        ArrayList<RemoteTorrent> temp = new ArrayList<>(userList);

        boolean exists = false;

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

            exists = false;

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

            for(RemoteTorrent rt : userList)
            {
                if(rt.getNumberId().longValue() == id)
                {
                    rt.setProgress(progress.doubleValue());
                    rt.setDownloaded(downloaded);
                    rt.setUploaded(uploaded);
                    rt.setDownloadSpeed(downloadSpeed);
                    rt.setUploadSpeed(uploadSpeed);
                    rt.setEta(eta);
                    rt.setRatio(ratio.doubleValue());
                    rt.setRemaining(remaining);
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
                rt.setNumberId(id);
                rt.setFilepath(filepath);
                rt.setSize(size);

                rt.setProgress(progress.doubleValue());
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio(ratio.doubleValue());
                rt.setRemaining(remaining);
                rt.setStatus(remoteTorrentStatus);

                userList.add(rt);
            }
        }

        for(RemoteTorrent rt : temp)
        {
            userList.remove(rt);
        }
    };

    @Override
    public List<RemoteTorrentFile> getFilesForTorrent(RemoteTorrent remoteTorrent, Class<? extends RemoteTorrentFile> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        ArrayList<RemoteTorrentFile> returned = new ArrayList<>();
        
        HashMap root;
        ArrayList<HashMap> torrents;

        JSONObject data = new JSONObject();
        HashMap arguments = new HashMap();
        ArrayList<String> fields = new ArrayList<>();

        fields.add("files");

        arguments.put("id", remoteTorrent.getNumberId());
        arguments.put("fields", fields);

        data.put("arguments", arguments);
        data.put("method", "torrent-get");

        root = (HashMap) JSONValue.parse(sendRequest(data));
        torrents = (ArrayList<HashMap>) ((HashMap) root.get("arguments")).get("torrents");

        for(HashMap hm : torrents)
        {
            ArrayList<HashMap> files = (ArrayList<HashMap>) hm.get("files");


            for(HashMap h : files)
            {
                RemoteTorrentFile rtf = null;

                String filename = (String) h.get("name");
                long filesize = (long) h.get("length");
                long downloaded = (long) h.get("bytesCompleted");

                try
                {
                    rtf = c.newInstance(); 
                }
                catch(Exception e)
                {
                    Log.error("Could not instantiate supplied remote torrent file class", e);
                }

                rtf.setFileName(filename);
                rtf.setFileSize(filesize);
                rtf.setDownloaded(downloaded);

                returned.add(rtf);
            }
        }

        return returned;
    }

    //Class specific methods
    private String sendRequest(JSONObject data) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
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
            getAuthToken();

            apiUrl = new URL("http://127.0.0.1:9090/transmission/rpc");

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setConnectTimeout(1000);
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
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting Transmission: " + response.toString());
                    throw new RemoteTorrentConnectionException();
            }
        }
        catch(RemoteTorrentUnauthorizedException x)
        {
            throw new RemoteTorrentUnauthorizedException();
        }
        catch(Exception e)
        {
            Log.error("Could not send request to Transmission", e);
            switch(responseCode)
            {
                case 401:
                    Log.error("Unauthorized to acess Utorrent API");
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting uTorrent API: " + response.toString());
                    throw new RemoteTorrentConnectionException();
            }
        }

        return returned;
    }

    private void getAuthToken() throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        URL apiUrl;
        HttpURLConnection conn = null;
        int responseCode = -1;

        try
        {
            apiUrl = new URL(API_URL);

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            responseCode = conn.getResponseCode();
            conn.getInputStream();
        }
        catch(IOException e)
        {
            if(responseCode == 409)
            {
                authToken = conn.getHeaderField("X-Transmission-Session-Id");
                Log.debug("Got auth token: " + authToken + " from Transmission");
            }
            else if(responseCode == 401)
            {
                Log.error("Unauthorized to acess Transmission");
                throw new RemoteTorrentUnauthorizedException();
            }
            else
            {
                Log.error("Could not get Transmission auth token", e);
                throw new RemoteTorrentConnectionException();
            }
        }
    }        

    public String getName()
    {
        return "Transmission";
    }        
}
