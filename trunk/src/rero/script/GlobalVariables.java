/**
 * 
 *
 *
 **/

package rero.script;
 
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.ircfw.*;

import java.util.*;

public class GlobalVariables implements Variable
{
    protected HashMap data = new HashMap();
    protected Variable alternateVariables = null;
    

    public void setOtherVariables(Variable _alternateVariables)
    {
       alternateVariables = _alternateVariables;
    }

    public boolean scalarExists(String key)
    {
       if (alternateVariables != null && alternateVariables.scalarExists(key))
       {
          return true;
       }

       return data.containsKey(key);
    }

    public Scalar getScalar(String key)
    {
       if (alternateVariables != null && alternateVariables.scalarExists(key))
       {
          return alternateVariables.getScalar(key);
       }

       return (Scalar)data.get(key);
    }

    public Scalar putScalar(String key, Scalar value)
    {
       return (Scalar)data.put(key, value);
    }

    public void removeScalar(String key)
    {
       data.remove(key);
    }

    public Variable createLocalVariableContainer()     // create our variable container using our hashmap as the base of it.
    {                                                  // this way the user has access to all of the "local" variables for an
        return new LocalVariables();                   // event.  This should also do the $0 $1- $2-3 $-4 stuff
    }

    public Variable createInternalVariableContainer()
    {
       return new GlobalVariables();
    }
}
