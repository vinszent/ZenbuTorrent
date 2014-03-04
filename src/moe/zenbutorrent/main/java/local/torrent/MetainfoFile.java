package moe.zenbutorrent.main.java.local.torrent;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import moe.zenbutorrent.main.java.local.util.Bencoder;
import moe.zenbutorrent.main.java.logging.Log;

public class MetainfoFile
{
    //Root map
    private HashMap root;

    //Root elements
    private HashMap info;
    private String announce;
    private ArrayList<ArrayList<ByteBuffer>> announceList;
    private String comment;
    private String createdBy;
    private long creationDate;

    //Info elements
    private long pieceLength;
    private ArrayList<byte[]> sha1Pieces;
    private ArrayList<HashMap> files;
    private String name;
    private long length;

    public MetainfoFile(InputStream is)
    {
        //Obtain root map
        root = (HashMap) Bencoder.bdecode(is);

        //Obtain root elements
        info = (HashMap) root.get(ByteBuffer.wrap("info".getBytes()));
        if(root.containsKey(ByteBuffer.wrap("announce".getBytes())))
        {
            announce = new String(((ByteBuffer) root.get(ByteBuffer.wrap("announce".getBytes()))).array());
            Log.info("Torrent contains 'announce' element");
        }
        if(root.containsKey(ByteBuffer.wrap("announce-list".getBytes())))
        {
            announceList = (ArrayList<ArrayList<ByteBuffer>>) root.get(ByteBuffer.wrap("announce-list".getBytes()));
            Log.info("Torrent contains 'announce-list' element");
        }
        if(root.containsKey(ByteBuffer.wrap("comment".getBytes())))
        {
            comment = new String(((ByteBuffer) root.get(ByteBuffer.wrap("comment".getBytes()))).array());
            Log.info("Torrent contains 'comment' element");
        }
        if(root.containsKey(ByteBuffer.wrap("created by".getBytes())))
        {
            createdBy = new String(((ByteBuffer) root.get(ByteBuffer.wrap("created by".getBytes()))).array());
            Log.info("Torrent contains 'created by' element");
        }
        if(root.containsKey(ByteBuffer.wrap("creation date".getBytes())))
        {
            creationDate = (long) root.get(ByteBuffer.wrap("creation date".getBytes()));
            Log.info("Torrent contains 'creation date' element");
        }

        //Obtain info elements
        pieceLength = (long) info.get(ByteBuffer.wrap("piece length".getBytes()));
        byte[] piecesByteString = ((ByteBuffer) info.get(ByteBuffer.wrap("pieces".getBytes()))).array();
        sha1Pieces = new ArrayList<byte[]>();
        for(int i = 0; i < piecesByteString.length; i += 20)
        {
           byte[] sha1Piece = new byte[20];
           System.arraycopy(piecesByteString, i, sha1Piece, 0, 20);
           sha1Pieces.add(sha1Piece);
        }
        name = new String(((ByteBuffer) info.get(ByteBuffer.wrap("name".getBytes()))).array());
        if(info.containsKey(ByteBuffer.wrap("length".getBytes())))
        {
            length = (long) info.get(ByteBuffer.wrap("length".getBytes()));
            Log.info("Torrent contains 'length' element");
        }
        if(info.containsKey(ByteBuffer.wrap("files".getBytes())))
        {
            files = (ArrayList<HashMap>) info.get(ByteBuffer.wrap("files".getBytes()));

            for(HashMap file : files)
            {
                length += (long) file.get(ByteBuffer.wrap("length".getBytes()));
            }

            Log.info("Torrent contains 'files' element");
        }
    }

    public ByteBuffer getInfoAsSha1()
    {
        byte[] infoBytes;
        MessageDigest md = null;

        infoBytes = Bencoder.bencode(info);
        try
        {
            md = MessageDigest.getInstance("SHA1");
        }
        catch(NoSuchAlgorithmException e)
        {
            Log.error("Could not find algorithm SHA1", e);
        }

        md.update(infoBytes);

        return ByteBuffer.wrap(md.digest());
    }        

    public ArrayList<String> getAnnounceList()
    {
        ArrayList<String> list = new ArrayList<>();
        
        list.add(announce);
        if(announceList != null)
        {
            list.remove(0);
            for(ArrayList<ByteBuffer> al : announceList)
            {
                for(ByteBuffer bb : al)
                {
                    list.add(new String(bb.array()));
                }
            }
        }

        return list;
    }        

    public ArrayList<byte[]> getSha1Pieces()
    {
        return sha1Pieces;        
    }        

    public long getLength()
    {
        return length;
    }        

    public long getPieceLength()
    {
        return pieceLength;
    }        

    public HashMap getRoot()
    {
        return root;
    }

    public HashMap getInfo()
    {
        return info;
    }        
}
