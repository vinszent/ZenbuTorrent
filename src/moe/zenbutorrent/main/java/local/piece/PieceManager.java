package moe.zenbutorrent.main.java.local.piece;

import java.util.ArrayList;
import java.util.BitSet;

import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;

public class PieceManager
{
    private ArrayList<Piece> cached = new ArrayList<>();
    private ArrayList<Piece> downloaded = new ArrayList<>();
    private ArrayList<Piece> notDownloaded = new ArrayList<>();
    private ArrayList<Piece> downloading = new ArrayList<>();
    private ArrayList<Piece> pieces = new ArrayList<>();

    private long pieceLength;

    public PieceManager(LocalTorrent torrent)
    {
        ArrayList<byte[]> sha1Pieces = torrent.getSha1Pieces();
        pieceLength = torrent.getPieceLength();

        for(int i = 0; i < sha1Pieces.size(); i++)
        {
            byte[] ba = sha1Pieces.get(i);

            Piece p = new Piece(i, pieceLength, ba);

            if(p.isDownloaded())
            {
                downloaded.add(p);
            }
            else
            {
                notDownloaded.add(p);
            }

            pieces.add(p);
        }
    }

    public void downloadPiece(Piece piece)
    {
        
    }        

    public ArrayList<Piece> getPiecesToDownload()
    {
       return notDownloaded;
    }        

    public BitSet getBitfield()
    {
        BitSet bitfield = new BitSet(pieces.size());

        for(int i = 0; i < pieces.size(); i++)
        {
            if(pieces.get(i).isDownloaded())
            {
                bitfield.set(i);
            }
            else
            {
                bitfield.clear(i);
            }
        }

        return bitfield;
    }
}
