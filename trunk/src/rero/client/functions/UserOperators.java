package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;

import rero.client.*;
import rero.ircfw.data.*;
import rero.ircfw.*;

import rero.util.*;

import java.util.*;

public class UserOperators extends Feature implements Predicate, Function, Loadable
{
   protected InternalDataList data;
 
   public void init()
   {
      getCapabilities().getScriptCore().addBridge(this);
      
      data = (InternalDataList)getCapabilities().getDataStructure("clientInformation");
   }

   public void scriptLoaded(ScriptInstance script)
   {
      String[] contents = new String[] { 
          "&searchAddressList",
          "&getChannels",
          "&getAddress",
          "&getIdleTime",
          "-isidle",
      };

      for (int x = 0; x < contents.length; x++)
      {
         script.getScriptEnvironment().getEnvironment().put(contents[x], this);
      }       
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   public Scalar evaluate(String function, ScriptInstance script, Stack locals)
   {
      User user = null;

      if (function.equals("&searchAddressList"))
      {
         if (locals.size() != 1)
         {
            return null;
         }

         String pattern = ((Scalar)locals.pop()).getValue().toString();

         Set rv = new HashSet();

         Iterator i = data.getAllUsers().iterator();
         while (i.hasNext())
         {
            User temp = (User)i.next();
            if ( StringUtils.iswm(pattern, temp.getFullAddress()) )
            {
               rv.add(temp.getNick());
            }
         }
         return SleepUtils.getArrayWrapper(rv);
      }

      if (locals.size() != 1)
      {
         user = data.getMyUser();
      }
      else
      {
         String nick = ((Scalar)locals.pop()).getValue().toString();
         if (data.isUser(nick))
         {
            user = data.getUser(nick);
         }
         else
         {
            return SleepUtils.getEmptyScalar();
         }
      }
 
      if (function.equals("&getChannels"))
      {
         Set rv = new HashSet();
         Iterator i = user.getChannels().iterator();
         while (i.hasNext())
         {
            rv.add(((Channel)i.next()).getName());
         }
         return SleepUtils.getArrayWrapper(rv);
      }

      if (function.equals("&getAddress"))
      {
         return SleepUtils.getScalar(user.getAddress());
      }

      if (function.equals("&getIdleTime"))
      {
         return SleepUtils.getScalar(user.getIdleTime());
      }

      return null;
   }

   public boolean decide(String predicate, ScriptInstance script, Stack terms)
   {
      if (terms.size() != 1)
      {
         return false;
      }

      String nick    = ((Scalar)terms.pop()).getValue().toString();
   
      if (predicate.equals("-isidle") && data.isUser(nick))
      {
         return (data.getUser(nick).getIdleTime() > (60 * 5));
      }

      return false;
   }
}
