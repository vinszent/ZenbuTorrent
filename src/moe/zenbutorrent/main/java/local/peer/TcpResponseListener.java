package moe.zenbutorrent.main.java.local.peer;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import moe.zenbutorrent.main.java.local.torrent.LocalTorrent;
import moe.zenbutorrent.main.java.local.util.PausableRunnable;
import moe.zenbutorrent.main.java.logging.Log;

public class TcpResponseListener extends PausableRunnable
{
    private Selector selector;
    private LocalTorrent torrent;

    public TcpResponseListener(LocalTorrent torrent)
    {
        this.torrent = torrent;
        try
        {
            selector = Selector.open();
        }
        catch(IOException e)
        {
            Log.error("Could not open selector", e);
        }
    }

    public void register(SocketChannel socket)
    {
        try
        {
            super.pause();
            selector.wakeup();
            socket.register(selector, SelectionKey.OP_READ);
            super.resume();
        }
        catch(ClosedChannelException e)
        {
            Log.error("Could not register response listener selector", e);
        }
    }        

    @Override
    public void run()
    {
       while(!stopped) 
       {
           while(!paused)
           {
               try
               {
                   selector.wakeup();
                   selector.select();
               }
               catch(IOException e)
               {
                   Log.error("Could not select keys", e);
               }

               for(SelectionKey key : selector.selectedKeys())
               {
                   if(key.isReadable())
                   {
                       key.cancel(); //Unregister selector until channel is done handling current data
                       torrent.getPeerManager().handleResponse((SocketChannel) key.channel());
                   }
               }
           }
           

           synchronized(lock)
           {
               try
               {
                   lock.wait();
               }
               catch(InterruptedException e)
               {
                   Log.error("TcpResponseListener interrupted while sleeping", e);
                   Thread.currentThread().interrupt();
                   return;
               }
           }
       }
    }        
}
