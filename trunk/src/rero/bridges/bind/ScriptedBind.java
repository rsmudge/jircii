package rero.bridges.bind;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.script.*;
import rero.bridges.alias.*;

public class ScriptedBind extends ScriptAlias
{
    public ScriptedBind(ScriptInstance si, Block _code)
    {
       super(si, _code, null);
    }

    public ScriptedBind(ScriptInstance si, Block _code, ScriptAlias _predecessor)
    {
       super(si, _code, _predecessor);
    }

    public void process()
    {
       synchronized (owner.getScriptVariables())
       {
          SleepUtils.runCode(code, owner.getScriptEnvironment());
       }
    }
}
