package rero.client.output;

import rero.ircfw.*;
import rero.ircfw.data.*;

import rero.bridges.event.*;
import rero.bridges.set.*;

import rero.client.*;

import java.util.*;

import rero.client.listeners.*;

import rero.gui.*;
import rero.gui.windows.*;
import text.*;

import rero.util.*;

import rero.config.*;

public class OutputCapabilities extends Feature implements ClientStateListener
{
   protected SetEnvironment      sets;
   protected UICapabilities      gui;
   protected InternalDataList    data;

   protected boolean             doTimestamp;
 
   public void init() 
   {
      sets    = (SetEnvironment)getCapabilities().getDataStructure("setBridge");
      gui     = getCapabilities().getUserInterface(); 
      data    = (InternalDataList)getCapabilities().getDataStructure("clientInformation");

      doTimestamp = ClientState.getClientState().isOption("option.timestamp", ClientDefaults.option_timestamp);

      ClientState.getClientState().addClientStateListener("option.timestamp", this);
   }

   public void propertyChanged(String varname, String parm)
   {
      doTimestamp = ClientState.getClientState().isOption("option.timestamp", ClientDefaults.option_timestamp);
   }

   /** sets the query to be the next non /window'd channel */
   public void cycleQuery()
   {
      Set mychans = data.getMyUser().getChannels();

      boolean  pastQuery = false;
 
      if (gui.getQuery().length() == 0)
      {
         pastQuery = true;
      }

      Iterator i         = mychans.iterator();
      while (i.hasNext())
      {
         Channel temp = (Channel)i.next();
         if (pastQuery && !gui.isActive(temp.getName()))
         {
            gui.setQuery(temp.getName());
            return;
         }

         if (temp.getName().equals(gui.getQuery()))
         {
            pastQuery = true;
         }
      }

      i         = mychans.iterator();
      while (i.hasNext())
      {
         Channel temp = (Channel)i.next();
         if (!gui.isActive(temp.getName()))
         {
            gui.setQuery(temp.getName());
            return;
         }
      }

      gui.setQuery(""); 
   }

   public void fireSetActive(HashMap event, String setName)
   {
      gui.printActive(getSet(event, setName));
   }

   /** fires a set for a query */
   public void fireSetQuery(HashMap event, String from, String target, String setName)
   {
      if (target.charAt(0) == '@' || target.charAt(0) == '+' || target.charAt(0) == '%')
      {
         target = target.substring(1, target.length());
      }

      boolean toActive = ClientState.getClientState().isOption("active.query", ClientDefaults.active_option);

      if (ClientUtils.isChannel(target))
      {
         fireSetTarget(event, target, setName);
      }
      else if (getCapabilities().getUserInterface().isWindow(from))
      {
         fireSetTarget(event, from, setName);
      }
      else if (toActive)
      {
         fireSetActive(event, setName);
      }
      else
      {
         fireSetStatus(event, setName);
      }
   }

   /** fires a set for a "confusing" situation...  */
   public void fireSetConfused(HashMap event, String target, String setType, String setName)
   {
      if (target != null && target.length() > 0 && (target.charAt(0) == '@' || target.charAt(0) == '+' || target.charAt(0) == '%'))
      {
         target = target.substring(1, target.length());
      }

      boolean toActive = ClientState.getClientState().isOption("active."+setType, ClientDefaults.active_option);

      if (target != null && getCapabilities().getUserInterface().isWindow(target))
      {
         fireSetTarget(event, target, setName);
      }
      else if (toActive)
      {
         fireSetActive(event, setName);
      }
      else
      {
         fireSetStatus(event, setName);
      }
   }

   /** analyzes variable and determines if user has chosen for event to go to status or active window.  Fires appropriate set 
       based on users chosen value of variable */
   public void fireSetOption(HashMap event, String variable, String setName)
   {
      gui.printStatus(getSet(event, setName));
   }

   public void fireSetTarget(HashMap event, String target, String setName)
   {
      gui.printNormal(target, getSet(event, setName));
   }

   public void fireSetAllDeadTarget(HashMap event, String target, String setName)
   {
      Set temp = data.getChannelsFromPriorLife(target);
      gui.printToTargets(temp, getSet(event, setName), false);
   }

   public void fireSetAllTarget(HashMap event, String target, String setName)
   {
      echoToTarget(target, getSet(event, setName), false);
   }

   public void fireSetAllTarget2(HashMap event, String target, String setName)
   {
      echoToTarget(target, getSet(event, setName), true);
   }

   /** fires set echoing to status window */
   public void fireSetStatus(HashMap event, String setName)
   {
      gui.printStatus(getSet(event, setName));
   }

   /** fires set echoing to all active windows */
   public void fireSetAll(HashMap event, String setName)
   {
       gui.printAll(getSet(event, setName));
   }

   public String chooseSet(String target, String setNameActive, String setNameInActive)
   {
      if (gui.isActive(target))
      {
         return setNameActive;
      }
      return setNameInActive;
   }

   public String getSet(HashMap event, String setName)
   {
  //    System.out.println("--- \"" + setName + "\" ---");
  //    System.out.println(event);

      if (sets.isSet(setName))
      {
          String setData = sets.parseSet(setName, event);

          if (setData == null || setData.equals(""))
          {
              return null;
          }

          if (doTimestamp && sets.isTimeStamped(setName) && sets.isSet("TIMESTAMP"))
          {
              return sets.parseSet("TIMESTAMP", event) + setData;
          }
          else
          {
              return setData;
          }
      }

      return null;
   }

   public String parseSet(HashMap event, String set_name)
   {
      return sets.parseSet(set_name, event);
   }

   public boolean isSet(String setName)
   {
      return sets.isSet(setName);
   }

   public void echoToTarget(String nickname, String text, boolean alwaysStatys)
   {
      Set targets = new HashSet();

      Iterator i = data.getUser(nickname).getChannels().iterator();
      while (i.hasNext())
      {
         Channel temp = (Channel)i.next();
         targets.add(temp.getName());
      }

      gui.printToTargets(targets, text, alwaysStatys);
   }

   public void echo(String window, String text[], double percentage)
   {
      StringBuffer temp = new StringBuffer(text[0].length() * text.length); // pretty accurate guess at size.
      for (int x = 0; x < text.length; x++)
      {
         temp.append(text[x]);
      }

      gui.printChunk(window, temp.toString(), text, percentage);
   }
}


