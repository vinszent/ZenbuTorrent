package main.java.local.peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import main.java.logging.Log;
import main.java.local.util.PausableRunnable;

public class TcpConnectionListener extends PausableRunnable
{
    private ServerSocketChannel peerListeningSocket;
    private Selector peerListeningSelector;

    public TcpConnectionListener()
    {
        try
        {
            peerListeningSocket = ServerSocketChannel.open();
            peerListeningSocket.configureBlocking(false);
            peerListeningSocket.socket().bind(new InetSocketAddress(6881));
            
            peerListeningSelector = Selector.open();
            peerListeningSocket.register(peerListeningSelector, SelectionKey.OP_ACCEPT);
        }
        catch(IOException e)
        {
        }
    }


    public void run()
    {
       while(true) 
       {
           try
           {
               peerListeningSelector.select();

               Set<SelectionKey> keys = peerListeningSelector.selectedKeys();
               for(SelectionKey key : keys)
               {
                   if(key.isAcceptable())
                   {
                       SocketChannel peerChannel = peerListeningSocket.accept();
                       peerChannel.configureBlocking(false);
                   }
               }
           }
           catch(IOException e)
           {
               e.printStackTrace();
           }

       }
    }        
}
