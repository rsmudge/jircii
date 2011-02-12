package rero.bridges.subs;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import sleep.bridges.*;

public class SubroutineEnvironment implements Loadable, Environment
{
    public HashMap   func; // table to store the actual block associated with the function.

    public void scriptUnloaded (ScriptInstance si)
    {
        // My apoligies in advance.  It appears this code for unloading scripts is really ugly.
        // Indeed it is.  However it accomplishes my goals of reverting to a previously defined version of a
        // subroutine and purging out of memory code we don't want.

        Hashtable env = si.getScriptEnvironment().getEnvironment();

        String key;

        Enumeration en = env.keys();
        while (en.hasMoreElements())
        {
           key = (String)en.nextElement();
           if (key.charAt(0) == '&')
           {
              if (env.get(key) instanceof BasicSubroutine)
              {
                 //
                 // check if the sub routine inside of the environment is owned by this script
                 //
                 BasicSubroutine function = (BasicSubroutine)env.get(key);
                 if (!function.getOwner().isLoaded())
                 {
                     BasicSubroutine nextRoutine = getNextSafeSubroutine( function );

                     if (nextRoutine == null)
                     { 
                         // no other functions waiting to take its place
                         env.remove(key);
                     }
                     else
                     {
                         // the unload stack keeps track of the last version of this subroutine loaded into memory
                         env.put(key, nextRoutine);
                     }
                 }
              }
           }
        }
    }

    // It's a remote possibility that a script might have the following:
    // sub myfunc 
    // {
    //    if (someCondition)
    //    {
    //       sub myfunc
    //       {
    //          # replaces myfunc in the environment
    //       }
    //    }
    // }
    //
    // This code is kind of a work around for that condition.  Better safe than sorry, right?
    //
    protected BasicSubroutine getNextSafeSubroutine(BasicSubroutine function)
    {
        if (function.getOwner().isLoaded())
        {
           return function;
        }

        if (!function.getUnloadStack().isEmpty())
        {
           BasicSubroutine temp = (BasicSubroutine)function.getUnloadStack().pop();
           return getNextSafeSubroutine(temp);
        }

        return null;
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); // assuming the environment is shared. hah right

        //
        // tell the environment that we want subroutine's to be bound here
        //
        env.put("sub",    this);
        env.put("inline", this);
    }

    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); // assuming the environment is shared. hah right

        if (type.equals("sub"))
        {
           Stack unloadStack;

           if (env.get("&"+name) != null && env.get("&"+name) instanceof BasicSubroutine)
           {
               BasicSubroutine temp = (BasicSubroutine)env.get("&"+name);
               unloadStack = temp.getUnloadStack();

               unloadStack.push(temp); // make this instance of BasicSubroutine available in case the main one gets unloaded
           }
           else
           {
               unloadStack = new Stack();
           }

           env.put("&"+name, new BasicSubroutine(si, code, unloadStack));
        }
        else if (type.equals("inline"))
        {
           env.put("^&"+name, code); /* add an inline function, very harmless */
        }
    }
}
