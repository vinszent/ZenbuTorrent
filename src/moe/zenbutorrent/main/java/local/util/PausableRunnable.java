package moe.zenbutorrent.main.java.local.util;

import moe.zenbutorrent.main.java.logging.Log;

public abstract class PausableRunnable implements Runnable
{
    protected Object lock = new Object();
    protected boolean stopped = false;
    protected boolean paused = false;

    public void run()
    {

    }

    public void pause()
    {
        paused = true;
    }

    public void resume()
    {
        paused = false;
        synchronized(lock)
        {
            lock.notifyAll();
        }
    }

    public void stop()
    {
        paused = true;
        stopped = true;

        synchronized(lock)
        {
            lock.notifyAll();
        }
    }
}
