package rero.bridges.alias;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.script.*;

/** implementation of subroutines for the DefaultEnvironment class */
public class ScriptAlias implements rero.ircfw.interfaces.FrameworkConstants
{
    protected Block                   code;
    protected ScriptAlias      predecessor;
    protected ScriptInstance         owner; // owner of this alias.
    protected Hashtable                env;

    public ScriptAlias(ScriptInstance si, Block _code)
    {
       this(si, _code, null);
    }

    public ScriptAlias(ScriptInstance si, Block _code, ScriptAlias _predecessor)
    {
       code        =        _code;
       predecessor = _predecessor;
       owner       =           si;

       env = si.getScriptEnvironment().getEnvironment(); // assuming the environment is shared.
    }

    public boolean isValid()
    {
       return owner.isLoaded();
    }

    public ScriptAlias getPredecessor()
    {
       return predecessor;
    }

    public void runAlias(String name, String parameters)
    {
       synchronized (owner.getScriptVariables())
       {
          ScriptVariables vars = owner.getScriptVariables();

          vars.pushLocalLevel();
 
          Variable localLevel = vars.getLocalVariables();

          //
          // setup the parameters variable
          //
          HashMap eventData = new HashMap();
          eventData.put($PARMS$, parameters);
          eventData.put($DATA$, name + " " + parameters);

          LocalVariables locals = (LocalVariables)localLevel;
          locals.setDataSource(eventData);

          //
          // execute the block of code
          //
          SleepUtils.runCode(code, owner.getScriptEnvironment());
 
          vars.popLocalLevel();
       }
    }
}
