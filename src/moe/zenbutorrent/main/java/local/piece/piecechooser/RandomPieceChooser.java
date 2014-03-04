package moe.zenbutorrent.main.java.local.piece.piecechooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import moe.zenbutorrent.main.java.local.piece.Piece;
import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;

public class RandomPieceChooser implements PieceChooser
{
    private LocalTorrent torrent;

    public RandomPieceChooser(LocalTorrent torrent)
    {
        this.torrent = torrent;
    }

    public void choosePiece()
    {
        Piece piece;
        long seed = System.nanoTime();    
        ArrayList<Piece> notDownloaded = torrent.getPieceManager().getPiecesToDownload();

        Collections.shuffle(notDownloaded, new Random(seed));
        piece = notDownloaded.get(0); 

        torrent.getPieceManager().downloadPiece(piece);
    }        
}
