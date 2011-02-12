package rero.ircfw;

import java.util.*;

import rero.ircfw.interfaces.FrameworkConstants;
import rero.ircfw.data.*;

public class Channel implements FrameworkConstants, Comparator
{
    protected String topic = ""; /* channel topic */

    public void setTopic(String t) { topic = t; }
    public String getTopic() { return topic; }

    protected int    limit; /* channel user limit */

    public void setLimit(int l) { limit = l; }
    public int getLimit() { return limit; }

    protected String   key; /* channel key */

    public void setKey(String k) { key = k; }
    public String getKey() { return key; }

    protected GenericMode mode = new GenericMode(); /* channel mode */

    public void setMode(String m) { mode = new GenericMode(m); }
    public GenericMode getMode() { return mode; }

    protected String  name; /* name of this #channel */

    public String getName() { return name; }
  
    public Channel(String name)
    {
       this.name = name;
    }

    protected SortedSet allusers  = Collections.synchronizedSortedSet(new TreeSet(this));  // keep this synchronized or bad things will happen

    public int compare(Object aa, Object bb)
    {
       User a = (User)aa;
       User b = (User)bb;

       if (a == null || b == null) 
          return 0;

       int aM = a.getModeFor(this);
       int bM = b.getModeFor(this);

       int result = bM - aM;

       if (result == 0)
       {
          return a.compareTo(b);
       }

       return result;
    }

    public Set getAllUsers()
    {
       return allusers; /* for now */
    }

    public String toString()
    {
       StringBuffer temp = new StringBuffer();
       temp.append(getName());
       temp.append(" \"");
       temp.append(getTopic());
       temp.append("\", ");

       temp.append(getMode());
 
       if (getMode().isSet('k'))
       {
          temp.append(" key="+key);
       }

       if (getMode().isSet('l'))
       {
          temp.append(" limit="+limit);
       }

       temp.append(", ");

       Iterator iter = getAllUsers().iterator();
       while (iter.hasNext())
       {
           User tempa = (User)iter.next();
           temp.append("?");
           temp.append(tempa.getNick());
           temp.append(" ");
       }

       return temp.toString();
    }
}
