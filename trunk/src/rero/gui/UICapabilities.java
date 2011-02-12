package rero.gui;

import rero.gui.windows.*;
import rero.ircfw.*;

import java.util.*;

import rero.gui.script.*; // for the WindowDataListener

public class UICapabilities
{
   protected IRCSession         clientSession;
   protected WindowDataListener listeners     = null; // we're only going to have one listener and that is the collapsed 
   protected BuiltInLogger      logger        = null;
                                                      // listener for the scripts.
   public UICapabilities(IRCSession _session)
   {
      clientSession = _session;
      logger        = new BuiltInLogger(_session);
   }   

   public void logMessage(String window, String text)
   {
      if (logger.isEnabled())
        logger.logMessage(window, text);
   }

   public void setQuery(String query)
   {
      clientSession.getStatusWindow().setQuery(query);
   }

   public String getQuery()
   {
      return clientSession.getActiveWindow().getQuery();
   }

   public boolean isActive(String title)
   {
      return (clientSession.getStatusWindow().getQuery().toLowerCase().equals(title.toLowerCase()) || clientSession.isWindow(title));
   }

   public boolean isWindow(String title)
   {
      return clientSession.isWindow(title);
   }

   /** checks the listeners to make sure text is approved by on window events, also makes sure text is not null */
   protected boolean shouldContinue(String window, String text)
   {
      return (text != null && (listeners == null || listeners.shouldContinue(window, text)));
   }

   /** prints text to the active window, if no window is active text is printed to the status window */
   public void printActive(String text)
   {
      if (shouldContinue(clientSession.getActiveWindow().getQuery(), text))
      {
         clientSession.getActiveWindow().flag();
         clientSession.getActiveWindow().getDisplay().addText(text);

         if (logger.isEnabled())
           logger.logMessage(clientSession.getActiveWindow().getName(), text);
      }
   }

   /** prints text to all open windows for the current server.  By all I mean *all* open windows. Window buttons are not flagged. */
   public void printAll(String text)
   {
      Iterator i = clientSession.getAllWindows().iterator();
      while (i.hasNext())
      {
         StatusWindow temp = (StatusWindow)i.next();
         if (temp.isLegalWindow() && shouldContinue(temp.getQuery(), text))
         {
            temp.getDisplay().addText(text);

            if (logger.isEnabled())
              logger.logMessage(temp.getName(), text);
         }
      }

//      printStatus(text);
   }

   /** prints text directly to the status window. */
   public void printStatus(String text)
   {
      if (shouldContinue(clientSession.getStatusWindow().getQuery(), text) && clientSession.getStatusWindow().getDisplay() != null)
      {
         clientSession.getStatusWindow().flag();
         clientSession.getStatusWindow().getDisplay().addText(text);

         if (logger.isEnabled())
           logger.logMessage(StatusWindow.STATUS_NAME, text);
      }
   }

   /** prints text to the specified window.  If the window doesn't exist the text goes to the status window, simple enough */
   public void printNormal(String window, String text)
   {
      if (shouldContinue(window, text))
      {
          StatusWindow temp = clientSession.getWindow(window);
          temp.flag();
          temp.getDisplay().addText(text);

          if (logger.isEnabled())
            logger.logMessage(temp.getName(), text);
      }
   }

   /** prints a message to the window for each target (if it exists), if there is no window for any of the targets output 
      goes to the status window, if any of the targets are handled in the status window the text is echo'd at most once to 
      the status window */
   public void printToTargets(Set targets, String text, boolean alwaysStatus)
   {
      boolean echoToStatus   = false;
      boolean echoHasOccured = false;

      Iterator i = targets.iterator();

      String query = getQuery();

      while (i.hasNext())
      {
         String target = (String)i.next();
         if (clientSession.isWindow(target))
         {
            printNormal(target, text);
            echoHasOccured = true;

            if (target.equals(query))
               alwaysStatus = false;
         }
         else
         {
            echoToStatus = true;
         }
      }

      if (echoToStatus || !echoHasOccured || alwaysStatus)
      {
         printStatus(text);
      }
   }

   /** prints a chunk of text to the specified window, a special case used for /names formatting */
   public void printChunk(String window, String normal, String chunks[], double percentage)
   {
      if (shouldContinue(window, normal))
      {
         clientSession.getWindow(window).getDisplay().addTextTable(normal, chunks, percentage);

         if (logger.isEnabled())
           logger.logMessage(clientSession.getWindow(window).getName(), normal);
      }
   }

   public void printRaw(String window, String text)
   {
      StatusWindow temp = clientSession.getWindow(window);

      if (window != null)
      {
         temp.flag();
         temp.getDisplay().addText(text);

         if (logger.isEnabled())
           logger.logMessage(temp.getName(), text);
      }
   }

   public void clearScreen(String window)
   {
      if (window == null || window.length() == 0)
      {
         clientSession.getActiveWindow().getDisplay().clear();
      }
      else if ("%ALL%".equals(window))
      {
         clientSession.getStatusWindow().getDisplay().clear();
         clientSession.getStatusWindow().unflag();

         Iterator i = clientSession.getAllWindows().iterator();          
         while (i.hasNext())
         {
            StatusWindow swindow = (StatusWindow)i.next();

            if (swindow.isLegalWindow())
            {
               swindow.getDisplay().clear();
               swindow.unflag();
            }
         }
      }
      else
      {
         clientSession.getWindow(window).getDisplay().clear();
         clientSession.getWindow(window).unflag();
      }
   }

   public void openQueryWindow(String nickname, boolean selected)
   {
      if (! clientSession.isWindow(nickname))
      {
         QueryWindow temp = clientSession.createQueryWindow(nickname, selected);
      }
   }

   public void closeWindow(String window)
   {
      if (clientSession.isWindow(window))
      {
         clientSession.getWindow(window).getWindow().closeWindow();
      }
   }

   public void openListWindow()
   {
      clientSession.createListWindow();
   }

   public void openDCCWindow()
   {
      clientSession.createDCCWindow();
   }
 
   public void openChannelWindow(Channel channel)
   {
      if (! clientSession.isWindow(channel.getName()))
      {
         ChannelWindow temp = clientSession.createChannelWindow(channel);
      }
   }

   public void notifyActiveWindow()
   {
      // Tells the window that something important hath happened.  Update its state.
      clientSession.getActiveWindow().touch();
   }

   public void notifyWindow(String window)
   {
      // Tells the window that something important hath happened.  Update its state.
      clientSession.getWindow(window).touch();
   }

   public void renameWindow(String old, String newtitle)
   {
      clientSession.renameWindow(old, newtitle);
   }

   public void setListener(WindowDataListener l)
   {
      listeners = l;
   }

   public void showSearchDialog(String window)
   {
      StatusWindow temp = clientSession.getWindow(window);

      if (temp.isLegalWindow())
      {
         if (window.equals("%STATUS%")) { window = "Status"; }

         temp.getDisplay().showSearchDialog("Search " + window);
      }
   }
}
