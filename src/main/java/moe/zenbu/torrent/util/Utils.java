package moe.zenbu.torrent.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

public class Utils
{
    public static String base64EncodeFile(final File file)
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

    public static HttpResponse<JsonNode> executeSilently(final RequestBodyEntity request)
    {
        HttpResponse<JsonNode> response = null;

        try
        {
            response = request.asJson();
        }
        catch(UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public static HttpResponse<JsonNode> executeSilently(final HttpRequestWithBody request)
    {
        HttpResponse<JsonNode> response = null;

        try
        {
            response = request.asJson();
        }
        catch(UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public static HttpResponse executeSilently(final GetRequest request, final boolean asJson)
    {
        HttpResponse response = null;

        try
        {
            if(asJson)
            {
                response = request.asJson();
            }
            else
            {
                response = request.asString();
            }
        }
        catch(UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }
}
