package moe.zenbutorrent.main.java.local.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import moe.zenbutorrent.main.java.logging.Log;

public class Bencoder
{
    private Bencoder()
    {
    }

    public static Object bdecode(InputStream is)
    {
        Object bdecodedObject;
        int readChar;

        bdecodedObject = new ArrayList();
        readChar = 0;

        try
        {
            is.mark(0);
            readChar = is.read();

            switch(readChar)
            {
                case 'i':
                    bdecodedObject = parseInteger(is);
                    break;
                case 'l':
                    bdecodedObject = parseList(is);
                    break;
                case 'd':
                    bdecodedObject = parseMap(is);
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    is.reset();
                    bdecodedObject = parseByteString(is);
                    break;
            }
        }
        catch(IOException e)
        {
            Log.error("Could not read char from input stream when bdecoding", e);
        }

        return bdecodedObject;
    }

    public static byte[] bencode(Object bdecodedObject)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bencode(bdecodedObject, os);

        return os.toByteArray();
    }

    public static void bencode(Object bdecodedObject, OutputStream os)
    {
        try
        {
            if(bdecodedObject instanceof Long)
            {
                os.write('i');
                os.write(((Long) bdecodedObject).toString().getBytes());
                os.write('e');
            }
            else if(bdecodedObject instanceof ByteBuffer)
            {
                byte[] byteString = ((ByteBuffer) bdecodedObject).array();

                os.write(Integer.toString(byteString.length).getBytes());
                os.write(':');
                for(int i = 0; i < byteString.length; i++)
                {
                    os.write(byteString[i]);
                }
            }
            else if(bdecodedObject instanceof ArrayList)
            {
                ArrayList list = (ArrayList) bdecodedObject;
                
                os.write('l');
                for(Object ob : list)
                {
                    bencode(ob, os);
                }
                os.write('e');
            }
            else if(bdecodedObject instanceof HashMap)
            {
                HashMap map = (HashMap) bdecodedObject;
                ArrayList<String> keys = new ArrayList<>();

                os.write('d');

                for(Object ob : map.keySet())
                {
                    keys.add(new String(((ByteBuffer) ob).array()));
                }
                Collections.sort(keys);
                for(String s : keys)
                {
                    Object key = ByteBuffer.wrap(s.getBytes());
                    Object value = map.get(ByteBuffer.wrap(s.getBytes()));
                    bencode(key, os);
                    bencode(value, os);
                }

                os.write('e');
            }
        }
        catch(IOException e)
        {
            Log.error("Failed to bencode object", e);
        }
    }

    public static long parseInteger(InputStream is)
    {
        StringBuilder parsedInteger;
        int readChar;

        parsedInteger = new StringBuilder();
        readChar = 0;

        try
        {
            readChar = is.read();

            while(readChar != 'e')
            {
                parsedInteger.append((char) readChar);
                readChar = is.read();
            }
            
        }
        catch(IOException e)
        {
            Log.error("Could not read char from input stream when parsing an integer", e);
        }

        return Long.parseLong(parsedInteger.toString());
    }

    public static ByteBuffer parseByteString(InputStream is)
    {
        byte[] parsedByteString = null;
        int readChar;
        int length;
        StringBuilder lengthBuilder;

        readChar = 0;
        lengthBuilder = new StringBuilder();

        try
        {
            readChar = is.read();

            while(readChar != ':')
            {
                lengthBuilder.append((char) readChar);
                readChar = is.read();
            }

            length = Integer.parseInt(lengthBuilder.toString());
            parsedByteString = new byte[length];

            is.read(parsedByteString, 0, length);
        }
        catch(IOException e)
        {
            Log.error("Could not read char from input stream when parsing a bytestring", e);
        }

        return ByteBuffer.wrap(parsedByteString);
    }

    public static ArrayList parseList(InputStream is)
    {
        ArrayList parsedList;
        int readChar;

        parsedList = new ArrayList();
        readChar = 0;

        try
        {
            is.mark(0);
            readChar = is.read();    

            while(readChar != 'e')
            {
                switch(readChar)
                {
                    case 'i':
                            parsedList.add(parseInteger(is));
                            break;
                    case 'l':
                            parsedList.add(parseList(is));
                            break;
                    case 'd':
                            parsedList.add(parseMap(is));
                            break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                            is.reset();
                            parsedList.add(parseByteString(is));
                            break;
                }

                is.mark(0);
                readChar = is.read();
            }
        }
        catch(IOException e)
        {
            Log.error("Could not read char from input stream when parsing a list", e);
        }

        return parsedList;
    }

    public static HashMap parseMap(InputStream is)
    {
        HashMap parsedMap;
        int readChar;

        parsedMap = new HashMap();
        readChar = 0;

        is.mark(0);

        while(readChar != 'e')
        {
            Object key = null;
            Object value = null;

            try
            {
                is.reset();

                //Get key
                key = parseByteString(is);

                //Get value
                is.mark(0);
                readChar = is.read();
                switch(readChar)
                {
                    case 'i':
                            value = parseInteger(is);
                            break;
                    case 'l':
                            value = parseList(is);
                            break;
                    case 'd':
                            value = parseMap(is);
                            break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                            is.reset();
                            value = parseByteString(is);
                            break;
                }

                parsedMap.put(key, value);

                is.mark(0);
                readChar = is.read();
            }
            catch(IOException e)
            {
                Log.error("Could not read char from input stream when parsing a map", e);
            }
        }

        return parsedMap;
    }
}
