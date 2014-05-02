package moe.zenbutorrent.main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
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

import org.json.simple.JSONValue;

public class UtorrentClientWrapper implements ClientWrapper
{
    private String API_URL = "http://127.0.0.1:8080/gui/";

    private String cookie;
    private String basicAuth;

    public UtorrentClientWrapper()
    {
        // No auth
    }

    public UtorrentClientWrapper(String username, String password)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }

    public UtorrentClientWrapper(String username, String password, String ip, int port)
    {
        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

        API_URL = "http://" + ip + ":" + port + "/gui/";
    }

    //Implemented methods
    @Override
    public void addTorrent(String url) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        url = URLEncoder.encode(url);
        sendRequest("action=add-url&s=" + url);
    }

    @Override
    public void addTorrent(File file) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        uploadFile(file);
    }        

    @Override
    public void addTorrent(String url, String path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        throw new UnsupportedOperationException("Adding a torrent with a specified path is not supported by the uTorrent API");
    }        

    @Override
    public void addTorrent(File file, File path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        throw new UnsupportedOperationException("Adding a torrent with a specified path is not supported by the uTorrent API");
    }

    @Override
    public void addTorrent(File file, String path) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        throw new UnsupportedOperationException("Adding a torrent with a specified path is not supported by the uTorrent API");
    }

    @Override
    public void pauseTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        sendRequest("action=pause&hash=" + remoteTorrent.getStringId());
        Log.info("Paused torrent: " + remoteTorrent.getTitle() + " on uTorrent");
    }

    @Override
    public void resumeTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        sendRequest("action=unpause&hash=" + remoteTorrent.getStringId());
        Log.info("Resumed torrent: " + remoteTorrent.getTitle() + " on uTorrent");
    }

    @Override
    public void deleteTorrent(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        sendRequest("action=remove&hash=" + remoteTorrent.getStringId());
        Log.info("Deleted torrent: " + remoteTorrent.getTitle() + " on uTorrent");
    }        

    @Override
    public void deleteTorrentAndData(RemoteTorrent remoteTorrent) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        sendRequest("action=removedata&hash=" + remoteTorrent.getStringId());
        Log.info("Deleted torrent and data: " + remoteTorrent.getTitle() + " on uTorrent");
    }        

    @Override
    public ArrayList<RemoteTorrent> getAllTorrents() throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        ArrayList<RemoteTorrent> returned = new ArrayList<>();

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
            String downloadDir = (String) al.get(26);

            //Convert status
            String remoteTorrentStatus;
            if(status.get(0))
            {
                if(status.get(5))
                {
                    remoteTorrentStatus = RemoteTorrent.PAUSED;
                }
                else if(downloaded == 1000)
                {
                    remoteTorrentStatus = RemoteTorrent.SEEDING;
                }
                else
                {
                    remoteTorrentStatus = RemoteTorrent.DOWNLOADING;
                }
            }
            else if(status.get(1))
            {
                remoteTorrentStatus = RemoteTorrent.CHECKING;
            }
            else if(status.get(4))
            {
                remoteTorrentStatus = RemoteTorrent.ERROR;
            }
            else if(status.get(7))
            {
                remoteTorrentStatus = RemoteTorrent.QUEUED;
            }
            else
            {
                remoteTorrentStatus = RemoteTorrent.WAITING;
            }

            DefaultRemoteTorrent rt = new DefaultRemoteTorrent(id, title, downloadDir, size);
            rt.setProgress((double) progress / 1000.0); //Convert to promille
            rt.setDownloaded(downloaded);
            rt.setUploaded(uploaded);
            rt.setDownloadSpeed(downloadSpeed);
            rt.setUploadSpeed(uploadSpeed);
            rt.setEta(eta);
            rt.setRatio((double) ratio / 1000.0); //Convert to promille
            rt.setStatus(remoteTorrentStatus);

            returned.add(rt);
        }

        return returned; 
    }        

    @Override
    public void updateAllTorrents(List<RemoteTorrent> userList, Class<? extends RemoteTorrent> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap root;
        ArrayList<ArrayList> torrents;

        root = (HashMap) JSONValue.parse(sendRequest("list=1"));
        torrents = (ArrayList<ArrayList>) root.get("torrents");

        ArrayList<RemoteTorrent> temp = new ArrayList<>(userList);

        boolean exists = false;

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
            String downloadDir = (String) al.get(26);

            exists = false;

            //Convert status
            String remoteTorrentStatus;
            if(status.get(0))
            {
                if(status.get(5))
                {
                    remoteTorrentStatus = RemoteTorrent.PAUSED;
                }
                else if(downloaded == 1000)
                {
                    remoteTorrentStatus = RemoteTorrent.SEEDING;
                }
                else
                {
                    remoteTorrentStatus = RemoteTorrent.DOWNLOADING;
                }
            }
            else if(status.get(1))
            {
                remoteTorrentStatus = RemoteTorrent.CHECKING;
            }
            else if(status.get(4))
            {
                remoteTorrentStatus = RemoteTorrent.ERROR;
            }
            else if(status.get(7))
            {
                remoteTorrentStatus = RemoteTorrent.QUEUED;
            }
            else
            {
                remoteTorrentStatus = RemoteTorrent.WAITING;
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

                rt.setProgress((double) progress / 1000.0); //Convert to promille
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio((double) ratio / 1000.0); //Convert to promille
                rt.setStatus(remoteTorrentStatus);

                userList.add(rt);
            }
        }

        for(RemoteTorrent rt : temp)
        {
            userList.remove(rt);
        }
    }        

    @Override
    public void updateAllTorrents(List<RemoteTorrent> userList, Class<? extends RemoteTorrent> c, List<String> titles) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        HashMap root;
        ArrayList<ArrayList> torrents;

        root = (HashMap) JSONValue.parse(sendRequest("list=1"));
        torrents = (ArrayList<ArrayList>) root.get("torrents");

        ArrayList<RemoteTorrent> temp = new ArrayList<>(userList);

        boolean exists = false;

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
            String downloadDir = (String) al.get(26);

            exists = false;

            //Convert status
            String remoteTorrentStatus;
            if(status.get(0))
            {
                if(status.get(5))
                {
                    remoteTorrentStatus = RemoteTorrent.PAUSED;
                }
                else if(downloaded == 1000)
                {
                    remoteTorrentStatus = RemoteTorrent.SEEDING;
                }
                else
                {
                    remoteTorrentStatus = RemoteTorrent.DOWNLOADING;
                }
            }
            else if(status.get(1))
            {
                remoteTorrentStatus = RemoteTorrent.CHECKING;
            }
            else if(status.get(4))
            {
                remoteTorrentStatus = RemoteTorrent.ERROR;
            }
            else if(status.get(7))
            {
                remoteTorrentStatus = RemoteTorrent.QUEUED;
            }
            else
            {
                remoteTorrentStatus = RemoteTorrent.WAITING;
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

                rt.setProgress((double) progress / 1000.0); //Convert to promille
                rt.setDownloaded(downloaded);
                rt.setUploaded(uploaded);
                rt.setDownloadSpeed(downloadSpeed);
                rt.setUploadSpeed(uploadSpeed);
                rt.setEta(eta);
                rt.setRatio((double) ratio / 1000.0); //Convert to promille
                rt.setStatus(remoteTorrentStatus);

                userList.add(rt);
            }

        }

        for(RemoteTorrent rt : temp)
        {
            userList.remove(rt);
        }
    }        

    @Override
    public List<RemoteTorrentFile> getFilesForTorrent(RemoteTorrent remoteTorrent, Class<? extends RemoteTorrentFile> c) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        ArrayList<RemoteTorrentFile> returned = new ArrayList<>();

        HashMap root;
        ArrayList<ArrayList> files;

        root = (HashMap) JSONValue.parse(sendRequest("action=getfiles&hash=" + remoteTorrent.getStringId()));
        files = (ArrayList<ArrayList>) ((ArrayList) root.get("files")).get(1);

        for(ArrayList info : files)
        {
            RemoteTorrentFile rtf = null;

            String filename = (String) info.get(0);
            long filesize = (long) info.get(1);
            long downloaded = (long) info.get(2);

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

        return returned;
    }

    //Class specific methods
    private void uploadFile(File file) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        String authToken;
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
            authToken = getAuthToken();
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
                    Log.info("Sent file: " + file.getName() + "to uTorrent");
                    break;
                case 401:
                    Log.error("Unauthorized to acess Utorrent API");
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting uTorrent API: " + response.toString());
                    throw new RemoteTorrentConnectionException();
            }
        }
        catch(RemoteTorrentConnectionException | RemoteTorrentUnauthorizedException x)
        {
            throw x;
        }
        catch(Exception e)
        {
            Log.error("Could not send file to uTorrent");
            throw new RemoteTorrentConnectionException();
        }
    }        

    private String sendRequest(String arg) throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
    {
        String authToken;
        URL apiUrl;
        HttpURLConnection conn;

        int responseCode;
        BufferedReader in;
        String line;
        StringBuilder response = new StringBuilder();
        String returned = null;
        
        try
        {
            authToken = getAuthToken();
            apiUrl = new URL(API_URL + "?token=" + authToken + "&" + arg);

            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setConnectTimeout(1000);
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
                    throw new RemoteTorrentUnauthorizedException();
                default:
                    Log.error("Error contacting uTorrent API: " + response.toString());
                    throw new RemoteTorrentConnectionException();
            }
        }
        catch(RemoteTorrentConnectionException | RemoteTorrentUnauthorizedException x)
        {
            throw x;
        }
        catch(IOException e)
        {
            Log.error("Could not send request to uTorrent");
            throw new RemoteTorrentConnectionException(e);
        }

        return returned;
    }        

    private String getAuthToken() throws RemoteTorrentConnectionException, RemoteTorrentUnauthorizedException
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
           conn.setConnectTimeout(1000);
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

       switch(responseCode)
       {
           case 200:
           case 201:
           case 202:
               authToken = response.toString().replaceAll("\\<.*?>", "").trim();
               break;
           case 401:
               Log.error("Unauthorized to acess Utorrent API");
               throw new RemoteTorrentUnauthorizedException();
           default:
               Log.error("Error contacting uTorrent API: " + response.toString());
               throw new RemoteTorrentConnectionException();
       }

       Log.debug("Got uTorrent auth token: " + authToken);
       return authToken;
    }        

    public String getName()
    {
        return "uTorrent";
    }        

    @Override
    public String toString()
    {
        return getName(); 
    }        
}
