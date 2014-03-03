package main.java.remote.wrappers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import main.java.logging.Log;
import main.java.remote.torrent.RemoteTorrent;

public class UtorrentClientWrapper implements ClientWrapper
{
    private String API_URL = "http://127.0.0.1:8080/gui/";

    private int port;
    private String username;
    private String password;
    private String cookie;
    private String basicAuth;

    public UtorrentClientWrapper(String username, String password)
    {
        this.username = username;
        this.password = password;

        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }

    public UtorrentClientWrapper(String username, String password, int port)
    {
        this.username = username;
        this.password = password;
        this.port = port;

        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

        API_URL = "http://127.0.0.1:" + port + "/gui/";
    }

    //Implemented methods
    public void addTorrent(String filepath)
    {
    }

    public void pauseTorrent(RemoteTorrent remoteTorrent)
    {
    }

    public void resumeTorrent(RemoteTorrent remoteTorrent)
    {
    }

    public ArrayList<RemoteTorrent> getAllTorrents()
    {
        System.out.println(sendRequest("list=1"));

        return new ArrayList<RemoteTorrent>(); 
    }        

    //Class specific methods
    public String sendRequest(String arg)
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
    public String getAuthToken()
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
           System.out.println(cookie);
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

       Log.debug("Auth token: " + authToken);
       return authToken;
    }        

    public void setConnectionSettings(String username, String password)
    {
        this.username = username;
        this.password = password;

        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }        

    public void setConnectionSettings(String username, String password, int port)
    {
        this.username = username;
        this.password = password;
        this.port = port;

        basicAuth = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

        API_URL = "http://127.0.0.1:" + port + "/gui/";
    }
}
