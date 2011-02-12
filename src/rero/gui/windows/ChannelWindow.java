package rero.gui.windows;

import rero.ircfw.*;
import rero.ircfw.data.*;
import rero.ircfw.interfaces.*;

import rero.client.*;
import javax.swing.*;
import text.*;
import text.event.*;
import java.awt.*;
import java.awt.event.*;

import rero.bridges.menu.*;

import text.list.*;

import java.util.*;

import rero.config.*;

public class ChannelWindow extends StatusWindow implements ChannelDataWatch
{
   protected Channel         channel;
   protected ListDisplay     listbox;
   protected ChannelListData data;
   protected Capabilities    capabilities;
   protected MouseAdapter    mouseListener; // for nicklist popups.
   protected ListBoxOptions  watcher;

   public ListDisplay getListbox()
   {
      return listbox;
   }

   public void cleanup()
   {
      ((InternalDataList)capabilities.getDataStructure("clientInformation")).installChannelWatch(channel.getName(), null);
      watcher      = null;
      listeners.clear();  // this can't hurt to do.
      super.cleanup();
   }

   public ChannelWindow(Channel _channel)
   {
      channel = _channel;
   }

   public void init(ClientWindow _temp)
   {
      super.init(_temp);

      data = new ChannelListData(channel);
      listbox = new ListDisplay(data);

      watcher = new ListBoxOptions(this, listbox);

      mouseListener = new MouseAdapter()
      {
          public void mousePressed(MouseEvent ev)
          {
             maybeShowPopupNicklist(ev, "nicklist");
          }

          public void mouseReleased(MouseEvent ev)
          {
             maybeShowPopupNicklist(ev, "nicklist");
          }

          public void mouseClicked(MouseEvent ev)
          {
             if (ev.getClickCount() >= 2 && !ev.isPopupTrigger() && (ev.getButton() & MouseEvent.BUTTON3) != MouseEvent.BUTTON3)
             {
                fireClickEvent(ev);
             }
             else
             {
                maybeShowPopupNicklist(ev, "nicklist");
             }
          }
      };

      listbox.addMouseListener(mouseListener);
   }

   protected void maybeShowPopupNicklist(MouseEvent ev, String desc)
   {
      JPopupMenu menu = getPopupMenu(desc, null);

      if (ev.isPopupTrigger() && menu != null)
      {
         ListElement element = listbox.getSelectedElement();
         if (element != null)
         {
            HashMap eventData = new HashMap();
            User user = (User)element.getSource();
            eventData.put("$snick", user.getNick());

            Iterator i = listbox.getSelectedElements().iterator();
            StringBuffer values = new StringBuffer();

            while (i.hasNext())
            {
               User temp = (User)((ListElement)i.next()).getSource();

               values.append(temp.getNick());
               if (i.hasNext())
                 values.append(" ");
            }
            eventData.put("$data", getName() + " " + values.toString());

            ScriptedPopupMenu.SetMenuData(eventData);
         }

         menu.show((JComponent)ev.getComponent(), ev.getX(), ev.getY());
         ev.consume();
      }
   }

   // called when a ChannelWindow object already exists and you just joined a channel...
   public void createChannel(Channel c)
   {
      channel = c;
      data.installCapabilities(capabilities);
      data.updateChannel(channel);
   }

   public void userAdded(User u)
   {
      listbox.repaint();
   }

   public void userRemoved(User u)
   {
      data.removeUser(u);
      listbox.repaint();
   }

   public void userChanged()
   {
      listbox.repaint();
   }

   public void touch()
   {
      super.touch();
      listbox.repaint();
   }

   public void installCapabilities(Capabilities c)
   {
      capabilities = c;

      super.installCapabilities(c);
      data.installCapabilities(c);

      ((InternalDataList)c.getDataStructure("clientInformation")).installChannelWatch(channel.getName(), this);
   }

   public ImageIcon getImageIcon()
   {
      if (icon == null)
      {
         icon = new ImageIcon(ClientState.getClientState().getResource("channel.gif"));
      }

      return icon;
   }

   public String getQuery()
   {
      return channel.getName();
   }

   public String getName()
   {
      if (channel != null)
         return channel.getName();
      return "";
   }

   public String getWindowType()
   {
      return "channel";
   }

   protected LinkedList listeners = new LinkedList();

   public void addClickListener(ClickListener l)
   {
      listeners.add(l);
   }

   public void fireClickEvent(MouseEvent mev)
   {
      String target = "";

      ListElement element = listbox.getSelectedElement();
      if (element != null)
      {
          User user = (User)element.getSource();
          target = user.getNick();

          ClickEvent ev = new ClickEvent(target, target, mev);

          ListIterator i = listeners.listIterator();
          while (i.hasNext() && !ev.isConsumed())
          {
             ClickListener l = (ClickListener)i.next();
             l.wordClicked(ev);
          }

          if (!ev.isConsumed())
          {
             capabilities.getUserInterface().openQueryWindow(target, true);
          }
      }
   }

   public int compareWindowType()
   {
      return 2;
   }   
}
