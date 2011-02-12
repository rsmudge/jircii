package rero.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.gui.windows.*;

import rero.client.*;
import rero.client.user.*;

import rero.ircfw.*;

import rero.bridges.menu.*;

import rero.gui.script.*;

import rero.gui.mdi.*;
import rero.gui.toolkit.*;
import rero.gui.sdi.*;

import rero.dcc.*;

import rero.config.*;

import rero.client.user.UserHandler;

public class IRCSession
{
   protected WindowManager            desktop;
   protected InternetRelayChatClient  client;

   protected StatusWindow            status;  // status window isn't referred to by name, it is just the default
   protected HashMap                 windows; // key=String value=StatusWindow

   protected UserHandler           userInput;
   protected UIScriptBridge        scriptBridge; // UI hooks for the scripting engine

   protected ClientWindowStuff     windowListener;
   protected ClickableURLHandler   urlListener;

   public void cleanup()
   {
      getCapabilities().cleanup();
   }

   public Capabilities getCapabilities()
   {
      return client.getCapabilities();
   }

   public void executeCommand(String command)  
   {
      ((UserHandler)getCapabilities().getDataStructure("commands")).processCommand(command);
   }

   public IRCSession()
   {
      client  = new InternetRelayChatClient();
      client.init(new UICapabilities(this));   // UI Capabilities provides interface for stable irc code to this lame gui code

      userInput = (UserHandler)client.getCapabilities().getDataStructure("commands");

      // init our gui stuff...

      if (ClientState.getClientState().isOption("ui.sdi", ClientDefaults.ui_sdi))
      {
         desktop = new ClientPanel();
      }
      else
      {
         desktop = new ClientDesktop();
         ((ClientDesktop)desktop).addMouseListener(new PopupManager());
      }

      windows = new HashMap();

      urlListener    = new ClickableURLHandler();
      urlListener.installCapabilities(getCapabilities());

      windowListener = new ClientWindowStuff();

      scriptBridge = new UIScriptBridge(this);
      status  = createStatusWindow();
      scriptBridge.windowCreated(status);
      status.getDisplay().addClickListener(urlListener);
   }

   public boolean isWindow(String name)
   {
      return (windows.get(name.toUpperCase()) != null || name.equals(StatusWindow.STATUS_NAME));
   }

   public StatusWindow getWindow(String name)
   {
      if (name == null || windows.get(name.toUpperCase()) == null)
      {
         return status;
      }
      return (StatusWindow)windows.get(name.toUpperCase());
   }

   public Collection getAllWindows()
   {
      return desktop.getAllWindows();
   }

   public InternetRelayChatClient getClient()
   {
      return client;
   }

   public StatusWindow getStatusWindow()
   {
      return status;
   }

   public boolean isStatus(StatusWindow window)
   {
      return window == status;
   }

   public WindowManager getDesktop()
   {
      return desktop;
   }

   public StatusWindow getActiveWindow()
   {
      StatusWindow temp = desktop.getActiveWindow();
      if (temp == null || !temp.isLegalWindow())
      {
         return getStatusWindow();
      }
      return temp;
   }

   public StatusWindow getRealActiveWindow()
   {
      return desktop.getActiveWindow();
   }

   public void createAboutWindow()
   {
      AboutWindow contents = new AboutWindow();
      desktop.addWindow(contents, true);

      Thread fred = new Thread(contents);
      fred.start();
   }

   public void renameWindow(String name, String newname)
   {
      StatusWindow temp  = (StatusWindow)windows.get(name.toUpperCase());
      StatusWindow temp2 = (StatusWindow)windows.get(newname.toUpperCase());

      if (temp != null && temp2 == null)
      {
         windows.remove(name.toUpperCase());
         windows.put(newname.toUpperCase(), temp);
         temp.setName(newname);
      }
   }

   private DCCListDialog dccListDialog = null;
  
   public void createDCCWindow()
   {
      if (dccListDialog == null)
      {
         dccListDialog = new DCCListDialog();
         dccListDialog.installCapabilities(client.getCapabilities());

         desktop.addWindow(dccListDialog, true);

         windows.put(dccListDialog.getName().toUpperCase(), dccListDialog);
         scriptBridge.windowCreated(dccListDialog); 
         dccListDialog.getWindow().addWindowListener(windowListener);
      }
      else if (!dccListDialog.isOpen())
      {
         desktop.addWindow(dccListDialog, true);

         windows.put(dccListDialog.getName().toUpperCase(), dccListDialog);
         scriptBridge.windowCreated(dccListDialog); 
         dccListDialog.getWindow().addWindowListener(windowListener);
      }
   }

   public ScriptedListDialog createSortedWindow(String title, String hook, Object data, LinkedList columns)
   {
      ScriptedListDialog contents = new ScriptedListDialog(title, hook, data, columns);
      contents.installCapabilities(client.getCapabilities());

      desktop.addWindow(contents, false);

      windows.put(title.toUpperCase(), contents);
      scriptBridge.windowCreated(contents); 
      contents.getWindow().addWindowListener(windowListener);

      return contents;
   }

   public void createListWindow()
   {
      ChannelListDialog contents = new ChannelListDialog();
      contents.installCapabilities(client.getCapabilities());

      desktop.addWindow(contents, true);

      windows.put(contents.getName().toUpperCase(), contents);
      scriptBridge.windowCreated(contents); 
      contents.getWindow().addWindowListener(windowListener);
   }

   public StatusWindow createStatusWindow()
   {
      StatusWindow contents = new StatusWindow();

      desktop.addWindow(contents, true);

      contents.getInput().addInputListener(userInput); // we'll just do the input assignment ourselves thank you very much.
      contents.installCapabilities(client.getCapabilities());

      return contents;
   }

   public QueryWindow createQueryWindow(String user, boolean selected)
   {
      QueryWindow contents = new QueryWindow(user);

      desktop.addWindow(contents, selected);

      contents.getInput().addInputListener(userInput); // we'll just do the input assignment ourselves thank you very much.
      contents.installCapabilities(client.getCapabilities());
      windows.put(user.toUpperCase(), contents);

      scriptBridge.windowCreated(contents);
      contents.getDisplay().addClickListener(urlListener);

      contents.getWindow().addWindowListener(windowListener); // must be added last, so this is the last event to fire when 
                                                              // stuff happens

      return contents;
   }

   public ChannelWindow createChannelWindow(rero.ircfw.Channel channel)
   {
      final ChannelWindow contents = new ChannelWindow(channel);

      desktop.addWindow(contents, true);

      contents.getInput().addInputListener(userInput); // we'll just do the input assignment ourselves thank you very much.
      contents.installCapabilities(client.getCapabilities());
      windows.put(channel.getName().toUpperCase(), contents);

      scriptBridge.windowCreated(contents); 
      contents.getDisplay().addClickListener(urlListener);

      contents.getWindow().addWindowListener(windowListener);

      return contents;
   }

   public StatusWindow resolveClientWindow(ClientWindow temp)
   {
      Iterator i = windows.values().iterator();
      while (i.hasNext())
      {
         StatusWindow x = (StatusWindow)i.next();
         if (x.getWindow() == temp)
         {
            return x;
         }
      }

      return status;
   }

   // perform actions related to the window closing...
   public void postProcessWindow(StatusWindow window)
   {
      if (window instanceof ChannelWindow)
      {
         if (ClientState.getClientState().isOption("auto.part", ClientDefaults.auto_option))
         {
             InternalDataList ircData = (InternalDataList)getCapabilities().getDataStructure(DataStructures.InternalDataList);

             if (ircData.getChannel(window.getName()) != null)
             {
                getCapabilities().sendln("PART " + window.getName());
             }
         }
      }
      else if (window.getName().charAt(0) == '=' && window.isLegalWindow())
      {
         if (ClientState.getClientState().isOption("auto.chatclose", ClientDefaults.auto_option))
         {
             DataDCC dccData = (DataDCC)getCapabilities().getDataStructure("dcc");
             dccData.closeChat(window.getName().substring(1, window.getName().length()));
         }
      }
   }

   protected class ClientWindowStuff implements ClientWindowListener
   {

      public void onActive(ClientWindowEvent ev) { }
      public void onOpen(ClientWindowEvent ev) { }
      public void onInactive(ClientWindowEvent ev) { }
      public void onMinimize(ClientWindowEvent ev) { }
      public void onClose(ClientWindowEvent ev) 
      { 
         ClientWindow temp = (ClientWindow)ev.getSource();
         Iterator i = windows.values().iterator();
         while (i.hasNext())
         {
            StatusWindow x = (StatusWindow)i.next();
            if (x.getWindow() == temp)
            {
               postProcessWindow(x);
               i.remove();
               return;
            }
         }
      }
   }

   protected class PopupManager extends MouseAdapter
   {
      public void maybeShowPopup(MouseEvent ev)
      {
         if (ev.isPopupTrigger())
         {
             MenuBridge bridge = (MenuBridge)client.getCapabilities().getDataStructure("menuBridge");
             JPopupMenu menu = bridge.getPopupMenu("background", null);
     
             if (menu == null)
             {
                return;
             }

             menu.show((JComponent)ev.getComponent(), ev.getX(), ev.getY());
             ev.consume();
         }
      }

      public void mouseClicked(MouseEvent e)
      {
         maybeShowPopup(e);
      }

      public void mousePressed(MouseEvent e)
      {
         maybeShowPopup(e);
      }

      public void mouseReleased(MouseEvent e)
      {
         maybeShowPopup(e);
      }
   }

   protected void finalize()
   {
      System.out.println("FINALIZING IRC SESSION y0");
   }
}

