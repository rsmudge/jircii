package rero.gui.script;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.input.*;
import rero.ircfw.*;

import rero.gui.mdi.*;

import sleep.engine.*; 
import sleep.runtime.*; 
import sleep.interfaces.*;

import javax.swing.*;

import java.awt.*;
import java.util.*;

import text.*;
import text.list.*;

import rero.util.*;

public class WindowManagementOperators implements Function, Loadable
{
   protected IRCSession session;

   public WindowManagementOperators(IRCSession _session)
   {
      session = _session;
   }

   public void scriptLoaded(ScriptInstance script)
   {
      String[] contents = new String[] { 
         "&openWindow",
         "&closeWindow",
         "&setWindowState", 
         "&getWindowState",
         "&activateWindow",
         "&getActiveWindow",
         "&getCurrentWindow",
         "&getWindows",
         "&tileWindows",
         "&cascadeWindows"
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
      if (function.equals("&openWindow"))
      {
          String parms = locals.pop().toString();
          InternalDataList ircData = (InternalDataList)session.getCapabilities().getDataStructure("clientInformation");

          if (ClientUtils.isChannel(parms))
          {
             session.getCapabilities().getUserInterface().openChannelWindow(ircData.getChannel(parms));
          }
          else
          {
             // any kind of second parameter means we don't want the query window active
             session.getCapabilities().getUserInterface().openQueryWindow(parms, locals.isEmpty()); // for now
          }
      }
      else if (function.equals("&closeWindow"))
      {
          String window = locals.pop().toString();

          if (session.isWindow(window))
          {
             session.getWindow(window).getWindow().closeWindow();
          }
      }
      else if (function.equals("&setWindowState") && locals.size() == 2)
      {
          String window = locals.pop().toString();
          String state  = locals.pop().toString().toUpperCase();

          if (session.isWindow(window))
          {
             if (state.equals("NORMAL"))
             {
                session.getWindow(window).getWindow().setMaximum(false);
                session.getWindow(window).getWindow().setIcon(false);
             }
             else if (state.equals("MAXIMIZED"))
             {
                session.getWindow(window).getWindow().setIcon(false);
                session.getWindow(window).getWindow().setMaximum(true);
             }
             else if (state.equals("MINIMIZED"))
             {
                session.getWindow(window).getWindow().setIcon(true);
             }
          }
      }
      else if (function.equals("&setWindowState") && locals.size() == 1)
      {
          String state  = locals.pop().toString().toUpperCase();

          JFrame window = SessionManager.getGlobalCapabilities().getFrame();

             if (state.equals("NORMAL"))
             {
                window.setExtendedState(JFrame.NORMAL);
             }
             else if (state.equals("MAXIMIZED"))
             {
                window.setExtendedState(JFrame.MAXIMIZED_BOTH);
             }
             else if (state.equals("MINIMIZED"))
             {
                window.setExtendedState(JFrame.ICONIFIED);
             }
      }
      else if (function.equals("&getWindowState") && locals.size() == 0)
      {
          JFrame window = SessionManager.getGlobalCapabilities().getFrame();
          int    state  = window.getExtendedState();

             if ((state & JFrame.ICONIFIED) == JFrame.ICONIFIED)
             {
                return SleepUtils.getScalar("MINIMIZED");
             }
             else if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH)
             {
                return SleepUtils.getScalar("MAXIMIZED");
             }
             else
             {
                return SleepUtils.getScalar("NORMAL");
             }
      }
      else if (function.equals("&getWindowState") && locals.size() == 1)
      {
          String window = locals.pop().toString();

          if (session.isWindow(window))
          {
             if (session.getWindow(window).getWindow().isMaximum())
             {
                return SleepUtils.getScalar("MAXIMIZED");
             }
             else if (session.getWindow(window).getWindow().isIcon())
             {
                return SleepUtils.getScalar("MINIMIZED");
             }
             else
             {
                return SleepUtils.getScalar("NORMAL");
             }
          }
      }
      else if (function.equals("&getActiveWindow"))
      {
          if (session.getRealActiveWindow() == null)
              return SleepUtils.getScalar(StatusWindow.STATUS_NAME); 

          return SleepUtils.getScalar(session.getRealActiveWindow().getName());
      }
      else if (function.equals("&getCurrentWindow"))
      {
          return SleepUtils.getScalar(session.getActiveWindow().getName());
      }
      else if (function.equals("&getWindows"))
      {
          Set returnValue = new LinkedHashSet();
          Iterator i = session.getAllWindows().iterator();
          while (i.hasNext())
          {
             StatusWindow temp = (StatusWindow)i.next();
             returnValue.add(temp.getName());
          }

          return SleepUtils.getArrayWrapper(returnValue); 
      }
      else if (function.equals("&activateWindow") && locals.size() == 1)
      {
          String window = locals.pop().toString();

          if (session.isWindow(window))
          {
             session.getWindow(window).getWindow().activate();
          }
      }
      else if (function.equals("&tileWindows"))
      {
          if (session.getDesktop() instanceof ClientDesktop)
          {
             ClientUtils.invokeLater(new Runnable() { public void run() {
                ((ClientDesktop)session.getDesktop()).tileWindows();
             } });
          }
      }
      else if (function.equals("&cascadeWindows"))
      {
          if (session.getDesktop() instanceof ClientDesktop)
          {
             ClientUtils.invokeLater(new Runnable() { public void run() {
                ((ClientDesktop)session.getDesktop()).cascadeWindows();
             } });
          }
      }

      return SleepUtils.getEmptyScalar();
   }
}
