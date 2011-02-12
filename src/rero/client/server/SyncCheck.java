package rero.client.server;

import java.util.HashMap;

public class SyncCheck
{
    protected HashMap joinInProgress = new HashMap();

    public void addChannel(String channel)
    {
       joinInProgress.put(channel, new Long(System.currentTimeMillis()));
    }

    public boolean isSyncing(String channel)
    {
       return joinInProgress.containsKey(channel);
    }

    public long getSyncTime(String channel)
    {
       long time = ((Long)joinInProgress.get(channel)).longValue();
       joinInProgress.remove(channel);
   
       time = System.currentTimeMillis() - time;

       return time;
    }    
}

