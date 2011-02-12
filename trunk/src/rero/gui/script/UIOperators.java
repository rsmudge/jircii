package rero.gui.script;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.input.*;
import rero.ircfw.*;

import rero.config.*;

import sleep.engine.*; 
import sleep.runtime.*; 
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import java.awt.*;
import java.util.*;

import text.*;
import text.list.*;

public class UIOperators implements Function, Loadable
{
   protected IRCSession session;

   public UIOperators(IRCSession _session)
   {
      session = _session;
   }

   public void scriptLoaded(ScriptInstance script)
   {
      String[] contents = new String[] { 
          "&refreshMenubar",
          "&showOptionDialog",
          "&showHelpDialog",
          "&showAboutDialog",
          "&showSearchDialog"
      };

      for (int x = 0; x < contents.length; x++)
      {
         script.getScriptEnvironment().getEnvironment().put(contents[x], this);
      }       

      script.getScriptEnvironment().getEnvironment().put("&showSortedList", new openSortedWindow());
      script.getScriptEnvironment().getEnvironment().put("&refreshData", new refreshData());
   }

   private class openSortedWindow implements Function
   {
      public Scalar evaluate(String name, ScriptInstance script, Stack locals)
      {
          if (locals.size() < 3)
             return SleepUtils.getEmptyScalar();
     
          String title = locals.pop().toString();
          String hook  = locals.pop().toString();
          Object data  = locals.pop();

          return SleepUtils.getScalar(session.createSortedWindow(title, hook, data, extractData(locals)));
      }
   }

   private static LinkedList extractData(Stack locals)
   {
       LinkedList data = new LinkedList();

       while (!locals.isEmpty())
          data.add(locals.pop().toString());

       return data;
   }

   private static class refreshData implements Function
   {
      public Scalar evaluate(String name, ScriptInstance script, Stack locals)
      {
          ScriptedListDialog dialog = (ScriptedListDialog)BridgeUtilities.getObject(locals);

          dialog.refreshData();

          return SleepUtils.getEmptyScalar();
      }
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   public Scalar evaluate(String function, ScriptInstance script, Stack locals)
   {
      if (function.equals("&refreshMenubar"))
      {
          ClientState.getClientState().fireChange("loaded.scripts", null);
      }
      else if (function.equals("&showOptionDialog"))
      {
          session.getCapabilities().getGlobalCapabilities().showOptionDialog(BridgeUtilities.getString(locals, ""));         
      }
      else if (function.equals("&showHelpDialog"))
      {
          session.getCapabilities().getGlobalCapabilities().showHelpDialog(BridgeUtilities.getString(locals, ""));         
      }
      else if (function.equals("&showAboutDialog"))
      {
          session.getCapabilities().getGlobalCapabilities().showAboutDialog();         
      }
      else if (function.equals("&showSearchDialog"))
      {
          session.getCapabilities().getUserInterface().showSearchDialog(BridgeUtilities.getString(locals, "%STATUS%"));
      }
  
      return SleepUtils.getEmptyScalar();
   }
}
