package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import moe.zenbutorrent.main.java.logging.Log;
import moe.zenbutorrent.main.java.remote.torrent.DefaultRemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrent;
import moe.zenbutorrent.main.java.remote.torrent.RemoteTorrentStatus;

import org.json.simple.JSONValue;

public class UtorrentClientWrapper implements ClientWrapper
{
    private String API_URL = "http://127.0.0.1:8080/gui/";

    private String cookie;
    private String basicAuth;

    public UtorrentClientWrapper(String username, String password)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }

    public UtorrentClientWrapper(String username, String password, int port)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

        API_URL = "http://127.0.0.1:" + port + "/gui/";
    }

    //Implemented methods
    @Override
    public void addTorrent(String url)
    {
        sendRequest("action=add-url&s=" + url);
    }

    public void addTorrent(File file)
    {
        uploadFile(file);
    }        

    @Override
    public void pauseTorrent(DefaultRemoteTorrent remoteTorrent)
    {
        sendRequest("action=pause&hash=" + remoteTorrent.getStringId());
        Log.info("Paused torrent: " + remoteTorrent.getTitle() + "on uTorrent");
    }

    @Override
    public void resumeTorrent(DefaultRemoteTorrent remoteTorrent)
    {
        sendRequest("action=resume&hash=" + remoteTorrent.getStringId());
        Log.info("Resumed torrent: " + remoteTorrent.getTitle() + "on uTorrent");
    }

    @Override
    public ArrayList<DefaultRemoteTorrent> getAllTorrents()
    {
        ArrayList<DefaultRemoteTorrent> returned = new ArrayList<>();

        HashMap root;
        ArrayList<ArrayList> torrents;

        root = (HashMap) JSONValue.parse(sendRequest("list=1"));
        torrents = (ArrayList<ArrayList>) root.get("torrents");

        for(ArrayList al : torrents)
        {
            String id = (String) al.get(0);
            BitSet status = BitSet.valueOf(new byte[]{(byte) ((Long) al.get(1)).intValue()});
            String title = (String) al.get(2);
            long size = (long) al.get(3);
            long progress = (long) al.get(4);
            long downloaded = (long) al.get(5);
            long uploaded = (long) al.get(6);
            long ratio = (long) al.get(7);
            long uploadSpeed = (long) al.get(8);
            long downloadSpeed = (long) al.get(9);
            long eta = (long) al.get(10);
            long remaining = (long) al.get(18);
            String filepath = (String) al.get(26);

            //Convert status
            RemoteTorrentStatus remoteTorrentStatus;
            if(status.get(0))
            {
                if(status.get(5))
                {
                    remoteTorrentStatus = RemoteTorrentStatus.PAUSED;
                }
                else if(downloaded == 1000)
                {
                    remoteTorrentStatus = RemoteTorrentStatus.SEEDING;
                }
                else
                {
                    remoteTorrentStatus = RemoteTorrentStatus.DOWNLOADING;
                }
            }
            else if(status.get(1))
            {
                remoteTorrentStatus = RemoteTorrentStatus.CHECKING;
            }
            else if(status.get(4))
            {
                remoteTorrentStatus = RemoteTorrentStatus.ERROR;
            }
            else if(status.get(7))
            {
                remoteTorrentStatus = RemoteTorrentStatus.QUEUED;
            }
            else
            {
                remoteTorrentStatus = RemoteTorrentStatus.WAITING;
            }

            DefaultRemoteTorrent rt = new DefaultRemoteTorrent(id, title, filepath, size);
            rt.setProgress((double) progress / 1000.0); //Convert to promille
            rt.setDownloaded(downloaded);
            rt.setUploaded(uploaded);
            rt.setDownloadSpeed(downloadSpeed);
            rt.setUploadSpeed(uploadSpeed);
            rt.setEta(eta);
            rt.setRatio((double) ratio / 1000.0); //Convert to promille
            rt.setRemaining(remaining);
            rt.setStatus(remoteTorrentStatus);

            returned.add(rt);
        }

        return returned; 
    }        

    @Override
    public void updateAllTorrents(ArrayList<RemoteTorrent> userList)
    {
        HashMap root;
        ArrayList<ArrayList> torrents;

        root = (HashMap) JSONValue.parse(sendRequest("list=1"));
        torrents = (ArrayList<ArrayList>) root.get("torrents");

        for(ArrayList al : torrents)
        {
            String id = (String) al.get(0);
            BitSet status = BitSet.valueOf(new byte[]{(byte) ((Long) al.get(1)).intValue()});
            String title = (String) al.get(2);
            long size = (long) al.get(3);
            long progress = (long) al.get(4);
            long downloaded = (long) al.get(5);
            long uploaded = (long) al.get(6);
            long ratio = (long) al.get(7);
            long uploadSpeed = (long) al.get(8);
            long downloadSpeed = (long) al.get(9);
            long eta = (long) al.get(10);
            long remaining = (long) al.get(18);
            String filepath = (String) al.get(26);

            //Convert status
            RemoteTorrentStatus remoteTorrentStatus;
            if(status.get(0))
            {
                if(status.get(5))
                {
                    remoteTorrentStatus = RemoteTorrentStatus.PAUSED;
                }
                else if(downloaded == 1000)
                {
                    remoteTorrentStatus = RemoteTorrentStatus.SEEDING;
                }
                else
                {
                    remoteTorrentStatus = RemoteTorrentStatus.DOWNLOADING;
                }
            }
            else if(status.get(1))
            {
                remoteTorrentStatus = RemoteTorrentStatus.CHECKING;
            }
            else if(status.get(4))
            {
                remoteTorrentStatus = RemoteTorrentStatus.ERROR;
            }
            else if(status.get(7))
            {
                remoteTorrentStatus = RemoteTorrentStatus.QUEUED;
            }
            else
            {
                remoteTorrentStatus = RemoteTorrentStatus.WAITING;
            }

            for(RemoteTorrent rt : userList)
            {
                if(rt.getStringId().equals(id))
                {
                    rt.setProgress((double) progress / 1000.0); //Convert to promille
                    rt.setDownloaded(downloaded);
                    rt.setUploaded(uploaded);
                    rt.setDownloadSpeed(downloadSpeed);
                    rt.setUploadSpeed(uploadSpeed);
                    rt.setEta(eta);
                    rt.setRatio((double) ratio / 1000.0); //Convert to promille
                    rt.setRemaining(remaining);
                    rt.setStatus(remoteTorrentStatus);
                }
            }
        }
    }        

    //Class specific methods
    private void uploadFile(File file)
    {
        String authToken = getAuthToken();
        URL apiUrl;
        HttpURLConnection conn;

        int responseCode;
        BufferedReader in;
        String line;
        StringBuilder response = new StringBuilder();

        FileInputStream fis;
        DataOutputStream dos;
        byte[] bytes = new byte[1024];
        int bytesRead;

        String boundary = "*****";
        String delimiter = "--";
        String newline = "\r\n";
        
        try
        {
            apiUrl = new URL(API_URL + "?token=" + authToken + "&action=add-file");
            conn = (HttpURLConnection) apiUrl.openConnection();

            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(delimiter + boundary + newline);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + "torrent_file" + "\";filename=\"" + file.getName() + "\"" + newline);
            dos.writeBytes(newline);

            fis = new FileInputStream(file);

            while((bytesRead = fis.read(bytes)) != -1)
            {
                dos.write(bytes, 0, bytesRead);
                dos.flush();
            }
            
            dos.writeBytes(newline);
            dos.writeBytes(delimiter + boundary + newline);

            dos.flush();
            dos.close();
            fis.close();

            responseCode = conn.getResponseCode();
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
                    Log.info("Uploaded file: " + file.getName() + "to uTorrent");
                    break;
                case 401:
                    Log.error("Unauthorized to acess Utorrent API");
                    break;
                default:
                    Log.error("Error contacting uTorrent API: " + response.toString());
                    break;
            }
        }
        catch(Exception e)
        {
            Log.error("Could not get uTorrent auth token", e);
        }
    }        

    private String sendRequest(String arg)
    {
        String authToken = getAuthToken();
        URL apiUrl;
        HttpURLConnection conn;

        int responseCode;
        BufferedReader in;
        String line;
        StringBuilder response = new StringBuilder();
        String returned = null;
        
        try
        {
            apiUrl = new URL(API_URL + "?token=" + authToken + "&" + arg);

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("Cookie", cookie);

            responseCode = conn.getResponseCode();
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
                    Log.error("Unauthorized to acess Utorrent API");
                    break;
                default:
                    Log.error("Error contacting uTorrent API: " + response.toString());
                    break;
            }
        }
        catch(Exception e)
        {
            Log.error("Could not get uTorrent auth token", e);
            return null;
        }

        return returned;
    }        

    private String getAuthToken()
    {
       String authToken = null;
       URL apiUrl;
       HttpURLConnection conn;

       int responseCode = -1;
       BufferedReader in;
       String line;
       StringBuilder response = new StringBuilder();

       try
       {
           apiUrl = new URL(API_URL + "token.html");

           conn = (HttpURLConnection) apiUrl.openConnection();
           conn.setRequestMethod("GET");
           conn.setRequestProperty("Authorization", "Basic " + basicAuth);

           responseCode = conn.getResponseCode();
           in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

           while((line = in.readLine()) != null)
           {
               response.append(line);
           }
           in.close();

           cookie = conn.getHeaderFields().get("Set-Cookie").get(0);
           Log.debug("Set uTorrent cookie: " + cookie);
       }
       catch(Exception e)
       {
           Log.error("Could not get uTorrent auth token", e);
           return null;
       }

       switch(responseCode)
       {
           case 200:
           case 201:
           case 202:
               authToken = response.toString().replaceAll("\\<.*?>", "").trim();
               break;
           case 401:
               Log.error("Unauthorized to acess Utorrent API");
               break;
           default:
               Log.error("Error contacting uTorrent API: " + response.toString());
               break;
       }

       Log.debug("Got uTorrent auth token: " + authToken);
       return authToken;
    }        
}
