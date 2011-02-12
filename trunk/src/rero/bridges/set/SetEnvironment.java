package rero.bridges.set;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.bridges.alias.*;

/**
 *  kind of dirty, implementation of sets just piggy backs off of the implementation of aliases.
 *  API's are different, however the data structures are the same and the unloading code can be shared this way.
**/

public class SetEnvironment extends AliasEnvironment
{
    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        ScriptedSet temp = null;

        boolean timestamp = true;

        if (name.charAt(name.length() - 1) == '!')
        {
            name = name.substring(0, name.length() - 1);
            timestamp = false; 
        }

        if (aliases.get(name) != null)
        {
            temp = (ScriptedSet)aliases.get(name);
        }

        ScriptedSet myset = new ScriptedSet(si, code, temp);
        myset.setTimeStamp(timestamp);

        aliases.put(name, myset);
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); // assuming the environment is shared. hah right

        env.put("set",   this);
    }

    public boolean isSet(String name)
    {
        return aliases.containsKey(name);
    }

    public boolean isTimeStamped(String name)
    {
        if (isSet(name))
        {
           return ((ScriptedSet)aliases.get(name)).isTimeStamped();
        }

        return false;
    }

    public String parseSet(String name, HashMap eventData)
    {
        if (isSet(name))
        {
           return ((ScriptedSet)aliases.get(name)).parseSet(eventData);
        }

        return null;
    }  
}
