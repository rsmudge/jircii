package rero.ircfw;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;

import rero.ircfw.interfaces.FrameworkConstants;

import java.util.Iterator;
import java.util.Comparator;

public class User implements Comparable, FrameworkConstants
{
    protected String nickname = ""; /* user nickname */

    public void setNick(String n) { nickname = n; }
    public String getNick() { return nickname; }

    public User copy()
    {
       User temp = new User(nickname);
       temp.channels = new HashMap(channels);
       temp.address  = address;
       temp.idle     = idle;
       return temp;
    }

    public int compareTo(Object b)
    {
       User bb;
       bb = (User)b;

       if (bb == null)
          return -1;

       return (getNick().toUpperCase().compareTo(bb.getNick().toUpperCase()));
    }

    public User(String nick)
    {
        nickname = nick;
    }

    protected HashMap channels = new HashMap();  /* key=Channel value=UserMode */

    /** returned HashMap -> key=<ChannelObject> value=<UserMode> can be used to determine users mode on a channel */
    public HashMap getChannelData() { return channels; }
    public Set getChannels() { return channels.keySet(); }
  
    public int getModeFor(Channel ch)
    {
        return ((Integer)channels.get(ch)).intValue();
    }

    public void setModeFor(Channel ch, int modes)
    {
        channels.put(ch, new Integer(modes));
    }

    protected String address = ""; /* user address */

    public void setAddress(String a) { address = a; }
    public String getAddress() { return address; }

    public String getFullAddress()
    {
       return getNick() + "!" + getAddress();
    }

    protected long idle = System.currentTimeMillis(); 

    public void touch() { idle = System.currentTimeMillis(); }
 
    /** returns user idle time in seconds */
    public int getIdleTime() { return (int)((System.currentTimeMillis() - idle) / 1000); }

    public boolean isIdle()
    {
        return (System.currentTimeMillis() - idle) > (1000 * 60 * 5); // 5 minutes of idle time.
    }

    public String toString()
    {
       return getNick();
    }
}
