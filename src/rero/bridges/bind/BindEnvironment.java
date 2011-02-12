package rero.bridges.bind;
 
import java.util.*;
import java.io.*;

import java.awt.event.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.bridges.alias.*;

/**
 *  kind of dirty, implementation of sets just piggy backs off of the implementation of aliases.
 *  API's are different, however the data structures are the same and the unloading code can be shared this way.
**/

public class BindEnvironment extends AliasEnvironment
{
    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        ScriptedBind temp = null;

        if (aliases.get(name) != null)
        {
            temp = (ScriptedBind)aliases.get(name);
        }

        ScriptedBind mybind = new ScriptedBind(si, code, temp);

        aliases.put(name.toUpperCase(), mybind);
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); // assuming the environment is shared. hah right

        env.put("bind",   this);
    }

    public ScriptedBind getBinding(String description)
    {
        return (ScriptedBind)aliases.get(description.toUpperCase());
    }

    public boolean isBound(String description)
    {
        return getBinding(description) != null;
    }

    public void processEvent(String description)
    {
        getBinding(description).process();
    }  
}
